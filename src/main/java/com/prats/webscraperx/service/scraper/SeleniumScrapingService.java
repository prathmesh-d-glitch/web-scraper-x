package com.prats.webscraperx.service.scraper;

import com.prats.webscraperx.config.ScraperConfig;
import com.prats.webscraperx.model.Entity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SeleniumScrapingService {
    private WebDriver driver;
    private WebDriverWait wait;
    private JSONObject scrapedData;
    private List<Entity> entities;

    public SeleniumScrapingService(boolean headlessMode) {
        ScraperConfig scraperConfig = new ScraperConfig();
        this.driver = scraperConfig.setUpWebDriver(headlessMode);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // 10 seconds timeout
        this.scrapedData = new JSONObject();
        this.entities = new ArrayList<>();
    }

    public void executeTemplate(String template) {
        JSONArray jsonTemplate = new JSONArray(template);

        for(int i=0;i<jsonTemplate.length();i++) {
            JSONObject step = jsonTemplate.getJSONObject(i);
            String action = step.getString("action");
            System.out.println("Executing action: " + action);

            try {
                switch (action) {
                    case "navigate":
                        String url = step.getString("url");
                        driver.get(url);
                        break;
                    case "type":
                        WebElement inputElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(step.getString("selector"))));
                        inputElement.clear();
                        inputElement.sendKeys(step.getString("text"));
                        break;
                    case "click":
                        WebElement clickElement = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(step.getString("selector"))));
                        clickElement.click();
                        break;
                    case "wait":
                        WebDriverWait waitForElement = new WebDriverWait(driver, Duration.ofSeconds(step.getInt("timeout")));
                        waitForElement.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(step.getString("selector"))));
                        break;
                    case "scrape":
                        Thread.sleep(5000);
                        handleScraping(step.getJSONObject("target"));
                        break;
                        default:
                            System.out.println("Unknown action: " + action);

                }
                System.out.println("Action executed successfully: " + action);
            }
            catch (Exception e) {
                System.err.println("Error executing action: " + action + " - " + e.getMessage());
            }
        }
    }

    private void handleScraping(JSONObject target) {
        Document doc = Jsoup.parse(driver.getPageSource());

        for(String key : target.keySet()) {
            String selector = target.getString(key);
            Elements selectedElements = doc.select(selector);
            List<String> elementTexts = new ArrayList<>();
            for (Element element : selectedElements) {
                elementTexts.add(element.text());
            }
            this.scrapedData.put(key, new JSONArray(elementTexts));
        }
    }
}
