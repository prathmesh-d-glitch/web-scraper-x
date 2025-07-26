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
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);

        if(headlessMode) {
            options.addArguments("--headless=new");
        }

        WebDriver driver = new ChromeDriver(options);
        return driver;
    }
}
