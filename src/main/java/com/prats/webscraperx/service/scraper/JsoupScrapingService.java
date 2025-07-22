package com.prats.webscraperx.service.scraper;

import com.prats.webscraperx.model.AttachmentObject;
import com.prats.webscraperx.model.Entity;
import com.prats.webscraperx.model.Pattern;
import com.prats.webscraperx.model.PatternObject;
import com.prats.webscraperx.service.EntityService;
import com.prats.webscraperx.service.PatternService;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JsoupScrapingService {

    private final int timeout;

    private EntityService entityService;
    private PatternService patternService;

    private JSONObject httpResponse = new JSONObject();
    private int scrapeCount = 0;

    @Autowired
    public JsoupScrapingService(EntityService entityService, PatternService patternService) {
        this.timeout = 15000; // Default timeout of 15 seconds
        this.entityService = entityService;
        this.patternService = patternService;
    }

    public JsoupScrapingService(int timeout, EntityService entityService, PatternService patternService) {
        this.timeout = timeout;
        this.entityService = entityService;
        this.patternService = patternService;
    }

    public String startScraping(String patternName, Pattern pattern) throws Exception {
        return scan(patternName);
    }

    private String scan(String patternName) throws Exception {

        Optional<Pattern> oPattern = patternService.getPatternByName(patternName);
        if(oPattern.isEmpty()) {
            httpResponse.put("patternFound", "false");
            throw new Exception("Pattern not found: " + patternName);
        }

        Pattern pattern = oPattern.get();
        httpResponse.put("patternFound", "true");

        try {
            Document doc = Jsoup.connect(pattern.getUrl()).timeout(timeout).get();
            httpResponse.put("mainConnectionStatus", "success");

            if (pattern.getHasPrescraping()) {
                Elements prescraping = doc.select(pattern.getTagForPrescraping());
                int page = 1;;
                for (Element pagination : prescraping) {
                    if (page == 1) {
                        Elements firstPage = doc.select(pattern.getTagForBody());
                        paginationScrape(firstPage, pattern);
                    }
                    else {
                        try {
                            String href = pagination.attr("href");
                            String resolved = resolveUrl(pattern.getUrl(), href);
                            Document paginatedDoc = Jsoup.connect(resolved).timeout(timeout).get();
                            httpResponse.put("paginationStatus", "success");
                            paginationScrape(paginatedDoc.select(pattern.getTagForBody()), pattern);
                        }
                        catch (Exception e) {
                            httpResponse.put("paginationStatus", "failed");
                            System.err.println("Error during pagination: " + e.getMessage());
                        }
                        page++;
                    }
                }
            }
            else {
                Elements bodyElements = doc.select(pattern.getTagForBody());
                paginationScrape(bodyElements,pattern);
            }
        }
        catch (Exception e) {
            httpResponse.put("mainConnectionStatus", "failed");
            System.err.println("Error connecting to URL: " + e.getMessage());
        }

        httpResponse.put("scrapeCount", scrapeCount);
        return httpResponse.toString(2);

    }

    // Outer page Scrape
    private void paginationScrape(Elements bodyElements, Pattern pattern) throws Exception {
        // Determine the base URL to resolve relative links
        String baseUrl = extractBaseUrl(pattern.getUrl());

        // Iterate through each item in the main body (product, article, etc.)
        for(Element element:bodyElements){
            Entity entity=new Entity(); // Create a new Entity for each item
            scrapeCount++; // Increment the counter to track progress

            // Extract Entity ID
            try {
                if(pattern.getEntityId()!=null){
                    Elements idElements=element.select(pattern.getEntityId());
                    if(!idElements.isEmpty()){
                        entity.setEntityId(idElements.get(0).attr(pattern.getAttrForEntityId()));
                    }
                }
            }
            catch(Exception e){
                System.err.println("Error extracting entity ID: "+e.getMessage());
            }

            // Extract the path to the entity (usually a link to the inner page)
            try {
                if(pattern.getEntityPath()!=null){
                    Elements pathElements=element.select(pattern.getEntityPath());
                    if(!pathElements.isEmpty()){
                        entity.setPath(pathElements.get(0).attr("href"));
                    }
                }
            }
            catch(Exception e){
                System.err.println("Error extracting entity path: "+e.getMessage());
            }

            // Extract additional custom data
            if(pattern.getPatternObjects()!=null&&!pattern.getPatternObjects().isEmpty()){
                for(PatternObject patternObject:pattern.getPatternObjects()){
                    try {
                        Elements elements=element.select(patternObject.getTagForElementToScrape());
                        if(!elements.isEmpty()){
                            if(patternObject.getMethodForElementToScrape()){
                                entity.setEntityContent(
                                        patternObject.getElementToScrape(),
                                        elements.text());
                            }
                            else{
                                entity.setEntityContent(
                                        patternObject.getElementToScrape(),
                                        elements.get(0).attr(patternObject.getAttrForElementToScrape()));
                            }
                        }
                    }
                    catch(Exception e){
                        System.err.println("Error extracting pattern object: "+e.getMessage());
                    }
                }
            }

            // Extract attachments like images or documents
            try {
                if(pattern.getHasAttachments()
                        &&pattern.getAttachmentObject()!=null
                        &&!pattern.getAttachmentObject().isEmpty()){
                    for(AttachmentObject attachmentObject:pattern.getAttachmentObject()){
                        Elements attachmentElements=element.select(attachmentObject.getTagForElementToScrape());
                        if(!attachmentElements.isEmpty()){
                            String attachmentUrl=attachmentElements.get(0).attr(attachmentObject.getAttrForElementToScrape());
                            String resolvedUrl=resolveUrl(baseUrl,attachmentUrl);
                            UUID uuid=entityService.saveAttachment(
                                    resolvedUrl,
                                    entity.getBasePath()+safe(entity.getPath()),
                                    baseUrl);
                            if(uuid!=null){
                                entity.addAttachmentId(uuid);
                            }
                        }
                    }
                }
            }
            catch(Exception e){
                System.err.println("Error extracting attachments: "+e.getMessage());
            }

            // Set metadata like timestamps and base URL
            entity.setLastScaping(Date.from(Instant.now()));
            entity.setBasePath(baseUrl);

            // Handle deep scraping if inner pages need to be explored
            if(pattern.getHaveToExplore()){
                String path=safe(entity.getPath());
                String targetUrl=path.startsWith("http://")||path.startsWith("https://")
                        ?path
                        :resolveUrl(baseUrl,path);
                try {
                    Document doc=Jsoup.connect(targetUrl).timeout(timeout).get();
                    innerScrape(doc.select(pattern.getTagForInnerBody()),entity,pattern);
                }
                catch(Exception e){
                    System.err.println("Error during inner scrape: "+e.getMessage());
                }
            }
            else{
                // Save or update the entity if no inner page scraping is required
                entityService.updateScraping(entity,baseUrl);
            }
        }
    }

    private void innerScrape(Elements pagintion, Entity entity, Pattern pattern) throws Exception {

        for(Element element:pagintion){

            List<PatternObject> patternObjects = pattern.getInnerPatternObjects();
            if(patternObjects != null && !patternObjects.isEmpty()) {
                for(PatternObject patternObject:patternObjects) {
                    try {
                        Elements elements = element.select(patternObject.getTagForElementToScrape());
                        if(!elements.isEmpty()) {
                            if(patternObject.getMethodForElementToScrape()) {
                                entity.setEntityContent(
                                        patternObject.getElementToScrape(),
                                        elements.text());
                            } else {
                                entity.setEntityContent(
                                        patternObject.getElementToScrape(),
                                        elements.get(0).attr(patternObject.getAttrForElementToScrape()));
                            }
                        }
                    } catch(Exception e) {
                        System.err.println("Error extracting inner pattern object: " + e.getMessage());
                    }
                }
            }

            if(pattern.getHasInnerAttachments() && !pattern.getAttachmentInnerObject().isEmpty()) {
                for(AttachmentObject attachmentObject:pattern.getAttachmentInnerObject()) {
                    try {
                        Elements attachmentElements = element.select(attachmentObject.getTagForElementToScrape());
                        if(!attachmentElements.isEmpty()) {
                            String attachmentUrl = attachmentElements.get(0).attr(attachmentObject.getAttrForElementToScrape());
                            String resolvedUrl = resolveUrl(entity.getBasePath(), attachmentUrl);
                            UUID uuid = entityService.saveAttachment(
                                    resolvedUrl,
                                    entity.getBasePath() + safe(entity.getPath()),
                                    entity.getBasePath());
                            if(uuid != null) {
                                entity.addAttachmentId(uuid);
                            }
                        }
                    } catch(Exception e) {
                        System.err.println("Error extracting inner attachments: " + e.getMessage());
                    }
                }
            }
            entityService.updateScraping(entity, entity.getBasePath());
        }
    }


    private String extractBaseUrl(String url) {
        if(url == null || url.isBlank()) {
            return "";
        }

        try {
            URI uri = URI.create(url);
            String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
            String host = uri.getHost();
            int port = uri.getPort();
            StringBuilder sb = new StringBuilder();
            sb.append(scheme).append("://").append(host);
            if (port > 0 && port != 80 && port != 443) {
                sb.append(":").append(port);
            }
            return sb.toString();
        }
        catch (Exception e) {
            System.err.println("Error extracting base URL: " + e.getMessage());
            return url;
        }
    }

    private String resolveUrl(String baseUrl, String link) {
        if(link == null || link.isBlank()) {
            return baseUrl;
        }
        if(link.startsWith("http://") || link.startsWith("https://")) {
            return link;
        }
        if(!baseUrl.endsWith("/")&& !link.startsWith("/")) {
            return baseUrl + "/" + link;
        }
        return baseUrl + link;
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
