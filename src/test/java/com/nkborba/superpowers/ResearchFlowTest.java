package com.nkborba.superpowers;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.Test;

/**
 * End-to-end research flow on Wikipedia demonstrating multi-tab orchestration.
 */
public class ResearchFlowTest extends BaseTest {

    private static final By SEARCH_INPUT = By.id("searchInput");
    private static final By ARTICLE_HEADING = By.id("firstHeading");
    private static final By FIRST_PARAGRAPH = By.cssSelector(
            "#mw-content-text .mw-parser-output > p:not(.mw-empty-elt)");

    @Test
    public void researchSeleniumRelatedTopicsAcrossTabs() {
        driver.get("https://www.wikipedia.org/");

        WebElement search = wait.until(ExpectedConditions.elementToBeClickable(SEARCH_INPUT));
        search.sendKeys("Selenium (software)");
        search.sendKeys(Keys.ENTER);

        WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(ARTICLE_HEADING));
        assertEquals(heading.getText(), "Selenium (software)");

        // Actions API scroll — Selenium drives real browser input events; Cypress scrolls its own app window.
        WebElement articleBody = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("mw-content-text")));
        new Actions(driver).scrollToElement(articleBody).perform();

        String originalHandle = driver.getWindowHandle();

        new Actions(driver).scrollToElement(
                wait.until(ExpectedConditions.presenceOfElementLocated(By.id("Selenium_WebDriver")))).perform();

        // Multi-tab: Cypress and Playwright run one tab per browser context; Selenium switches handles freely.
        ArticleSnapshot webDriverArticle = openArticleSectionInNewTab(
                "https://en.wikipedia.org/wiki/Selenium_(software)#Selenium_WebDriver", "Selenium_WebDriver");
        assertTrue(webDriverArticle.firstParagraph().toLowerCase().contains("webdriver"),
                "WebDriver section should discuss WebDriver");

        driver.switchTo().window(originalHandle);

        ArticleSnapshot cypressArticle = openArticleInNewTab("https://en.wikipedia.org/wiki/Cypress_(software)");
        assertEquals(cypressArticle.heading(), "Cypress (software)");
        assertTrue(cypressArticle.firstParagraph().toLowerCase().contains("selenium"),
                "Cypress article compares itself to Selenium");

        closeExtraTabs(originalHandle);
        driver.switchTo().window(originalHandle);

        assertEquals(driver.getTitle(), "Selenium (software) - Wikipedia");
        assertEquals(
                wait.until(ExpectedConditions.visibilityOfElementLocated(ARTICLE_HEADING)).getText(),
                "Selenium (software)");
    }

    private ArticleSnapshot openArticleInNewTab(String url) {
        driver.switchTo().newWindow(WindowType.TAB);
        driver.get(url);
        return snapshotCurrentArticle();
    }

    private ArticleSnapshot openArticleSectionInNewTab(String url, String sectionId) {
        driver.switchTo().newWindow(WindowType.TAB);
        driver.get(url);

        WebElement section = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(sectionId)));
        new Actions(driver).scrollToElement(section).perform();

        String heading = wait.until(ExpectedConditions.visibilityOfElementLocated(ARTICLE_HEADING)).getText();
        String paragraph = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id='" + sectionId + "']/ancestor::div[contains(@class,'mw-heading')]"
                        + "/following-sibling::p[1]"))).getText();

        return new ArticleSnapshot(heading, paragraph);
    }

    private ArticleSnapshot snapshotCurrentArticle() {
        String heading = wait.until(ExpectedConditions.visibilityOfElementLocated(ARTICLE_HEADING)).getText();
        String paragraph = wait.until(ExpectedConditions.visibilityOfElementLocated(FIRST_PARAGRAPH)).getText();
        return new ArticleSnapshot(heading, paragraph);
    }

    private void closeExtraTabs(String handleToKeep) {
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(handleToKeep)) {
                driver.switchTo().window(handle);
                driver.close();
            }
        }
    }

    private record ArticleSnapshot(String heading, String firstParagraph) {
    }
}
