package com.nkborba.superpowers;

import static org.openqa.selenium.support.locators.RelativeLocator.with;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.Test;

/**
 * Focused demos on the-internet.herokuapp.com — one scenario per Selenium superpower.
 */
public class SeleniumSuperpowersTest extends BaseTest {

    private static final String BASE = "https://the-internet.herokuapp.com";

    @Test
    public void windowsPageOpensAndClosesSecondaryWindow() {
        driver.get(BASE + "/windows");

        String originalHandle = driver.getWindowHandle();
        wait.until(ExpectedConditions.elementToBeClickable(By.partialLinkText("Click Here"))).click();

        // Window-handle switching: native WebDriver capability; Cypress cannot drive multiple windows.
        wait.until(d -> d.getWindowHandles().size() == 2);
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(originalHandle)) {
                driver.switchTo().window(handle);
                break;
            }
        }

        assertEquals(wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h3"))).getText(),
                "New Window");

        driver.close();
        driver.switchTo().window(originalHandle);
        assertTrue(driver.getTitle().contains("The Internet"));
    }

    @Test
    public void loginWithRelativeLocator() {
        driver.get(BASE + "/login");

        // Selenium 4 relative locators — spatial relationships; Cypress/Playwright use different selector models.
        By username = By.id("username");
        By password = with(By.tagName("input")).below(username);

        wait.until(ExpectedConditions.visibilityOfElementLocated(username)).sendKeys("tomsmith");
        wait.until(ExpectedConditions.visibilityOfElementLocated(password)).sendKeys("SuperSecretPassword!");
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']"))).click();

        WebElement flash = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("flash")));
        assertTrue(flash.getText().contains("You logged into a secure area!"));
    }

    @Test
    public void nestedFrames() {
        driver.get(BASE + "/nested_frames");

        // Frame switching — WebDriver crosses frame boundaries; Cypress cannot leave its single-origin sandbox.
        driver.switchTo().frame("frame-top");
        driver.switchTo().frame("frame-left");
        assertEquals(driver.findElement(By.tagName("body")).getText(), "LEFT");

        driver.switchTo().defaultContent();
        driver.switchTo().frame("frame-bottom");
        assertEquals(driver.findElement(By.tagName("body")).getText(), "BOTTOM");
    }

    @Test
    public void javascriptConfirmAlert() {
        driver.get(BASE + "/javascript_alerts");

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Click for JS Confirm']"))).click();

        // Native browser dialogs — WebDriver talks to the OS dialog; Cypress stubs alerts in-process.
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals(alert.getText(), "I am a JS Confirm");
        alert.accept();

        WebElement result = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("result")));
        assertEquals(result.getText(), "You clicked: Ok");
    }

    @Test
    public void dynamicLoadingExplicitWait() {
        driver.get(BASE + "/dynamic_loading/1");

        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#start > button"))).click();

        // Explicit waits poll the DOM via WebDriver; no fixed sleeps, no Cypress-style automatic retry layer.
        WebElement hello = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#finish h4")));
        assertEquals(hello.getText(), "Hello World!");
    }
}
