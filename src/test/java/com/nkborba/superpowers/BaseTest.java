package com.nkborba.superpowers;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Shared WebDriver lifecycle for all tests. Not a Page Object — only setup and teardown.
 * ChromeDriver binaries are resolved automatically by Selenium Manager (no WebDriverManager).
 */
public abstract class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        if ("true".equalsIgnoreCase(System.getProperty("headless"))) {
            options.addArguments("--headless=new");
        }

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
