package com.prats.webscraperx.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ScraperConfig {

    public WebDriver setUpWebDriver(boolean headlessMode) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);  // Use EAGER to load the page as quickly as possible without waiting for all resources to load

        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-gpu");

        //Helps to avoid basic bot detection mechanisms
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        if(headlessMode) {
            options.addArguments("--headless=new");
        }

        WebDriver driver = new ChromeDriver(options);
        return driver;
    }
}
