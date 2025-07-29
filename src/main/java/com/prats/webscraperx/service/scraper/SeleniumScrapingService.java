package com.prats.webscraperx.service.scraper;

import com.prats.webscraperx.config.ScraperConfig;
import com.prats.webscraperx.model.Entity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
                    case "waitForText":
                        WebElement elementWithText = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(step.getString("selector"))));
                        wait.until(ExpectedConditions.textToBePresentInElement(elementWithText, step.getString("text")));
                        break;
                    case "hover":
                        WebElement hoverElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(step.getString("selector"))));
                        new Actions(driver).moveToElement(hoverElement).perform();
                        break;
                    case "screenshot":
                        takeScreenshot(step.getString("filePath"));
                        break;
                    case "scroll":
                        handleScroll(step);
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

    private void takeScreenshot(String filePath) throws IOException {
        File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Path destination = Paths.get(filePath);
        Files.createDirectories(destination.getParent());
        Files.copy(srcFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Screenshot saved to: " + filePath);
    }

    private void handleScroll(JSONObject step) {


        //Alternative scroll handling using Actions class testing
//        Actions actions = new Actions(driver);
//        String type = step.optString("type", "bottom");
//
//        switch (type) {
//            case "toElement":
//                WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(step.getString("selector"))));
//                actions.moveToElement(element).perform();
//                break;
//
//            case "byAmount":
//                int amount = step.getInt("amount");
//                // Scroll down by pressing Page Down key multiple times if amount is large
//                int scrolls = Math.abs(amount) / 100;
//                for (int i = 0; i < scrolls; i++) {
//                    if (amount > 0) {
//                        actions.sendKeys(Keys.PAGE_DOWN).perform();
//                    } else {
//                        actions.sendKeys(Keys.PAGE_UP).perform();
//                    }
//                }
//                break;
//
//            case "bottom":
//            default:
//                // Scroll to bottom using Ctrl + End
//                actions.keyDown(Keys.CONTROL).sendKeys(Keys.END).keyUp(Keys.CONTROL).perform();
//                break;
//        }



        JavascriptExecutor js = (JavascriptExecutor) driver;
        String type = step.optString("type", "bottom");

        switch (type) {
            case "toElement":
                WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(step.getString("selector"))));
                js.executeScript("arguments[0].scrollIntoView(true);", element);
                break;
            case "byAmount":
                js.executeScript("window.scrollBy(0," + step.getInt("amount") + ")");
                break;
            case "bottom":
            default:
                js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
                break;
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
