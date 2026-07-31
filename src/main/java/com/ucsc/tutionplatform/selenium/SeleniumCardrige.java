package com.ucsc.tutionplatform.selenium;

import com.ucsc.tutionplatform.core.DriverManager;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class SeleniumCardrige {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    public WebDriver driver() {
        return DriverManager.getDriver();
    }

    public void navigateTo(String url) {
        driver().get(url);
    }

    public void refresh() {
        driver().navigate().refresh();
    }

    public void back() {
        driver().navigate().back();
    }

    public void forward() {
        driver().navigate().forward();
    }

    public String getCurrentUrl() {
        return driver().getCurrentUrl();
    }

    public String getTitle() {
        return driver().getTitle();
    }

    public WebElement findElement(By locator) {
        return driver().findElement(locator);
    }

    public List<WebElement> findElements(By locator) {
        return driver().findElements(locator);
    }

    public WebElement waitUntilPresent(By locator) {
        return waitUntilPresent(locator, DEFAULT_TIMEOUT);
    }

    public WebElement waitUntilPresent(By locator, Duration timeout) {
        return wait(timeout).until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public WebElement waitUntilVisible(By locator) {
        return waitUntilVisible(locator, DEFAULT_TIMEOUT);
    }

    public WebElement waitUntilVisible(By locator, Duration timeout) {
        return wait(timeout).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public List<WebElement> waitUntilAllVisible(By locator) {
        return wait(DEFAULT_TIMEOUT).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    public boolean waitUntilInvisible(By locator) {
        return wait(DEFAULT_TIMEOUT).until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public WebElement waitUntilClickable(By locator) {
        return waitUntilClickable(locator, DEFAULT_TIMEOUT);
    }

    public WebElement waitUntilClickable(By locator, Duration timeout) {
        return wait(timeout).until(ExpectedConditions.elementToBeClickable(locator));
    }

    public boolean waitUntilTextToBe(By locator, String text) {
        return wait(DEFAULT_TIMEOUT).until(ExpectedConditions.textToBe(locator, text));
    }

    public boolean waitUntilTextContains(By locator, String text) {
        return wait(DEFAULT_TIMEOUT).until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    public boolean waitUntilUrlContains(String urlText) {
        return wait(DEFAULT_TIMEOUT).until(ExpectedConditions.urlContains(urlText));
    }

    public boolean waitUntilTitleContains(String titleText) {
        return wait(DEFAULT_TIMEOUT).until(ExpectedConditions.titleContains(titleText));
    }

    public void click(By locator) {
        waitUntilClickable(locator).click();
    }

    public void click(WebElement element) {
        wait(DEFAULT_TIMEOUT).until(ExpectedConditions.elementToBeClickable(element)).click();
    }

    public void jsClick(By locator) {
        executeScript("arguments[0].click();", waitUntilPresent(locator));
    }

    public void type(By locator, String value) {
        WebElement element = waitUntilVisible(locator);
        element.clear();
        element.sendKeys(value);
    }

    public void type(WebElement element, String value) {
        element.clear();
        element.sendKeys(value);
    }

    public void appendText(By locator, String value) {
        waitUntilVisible(locator).sendKeys(value);
    }

    public void pressKey(By locator, Keys key) {
        waitUntilVisible(locator).sendKeys(key);
    }

    public void clear(By locator) {
        waitUntilVisible(locator).clear();
    }

    public String getText(By locator) {
        return waitUntilVisible(locator).getText();
    }

    public String getValue(By locator) {
        return getAttribute(locator, "value");
    }

    public String getAttribute(By locator, String attributeName) {
        return waitUntilPresent(locator).getAttribute(attributeName);
    }

    public String getCssValue(By locator, String propertyName) {
        return waitUntilPresent(locator).getCssValue(propertyName);
    }

    public boolean isDisplayed(By locator) {
        List<WebElement> elements = findElements(locator);
        return !elements.isEmpty() && elements.get(0).isDisplayed();
    }

    public boolean isEnabled(By locator) {
        return waitUntilPresent(locator).isEnabled();
    }

    public boolean isSelected(By locator) {
        return waitUntilPresent(locator).isSelected();
    }

    public int getElementCount(By locator) {
        return findElements(locator).size();
    }

    public void selectByVisibleText(By locator, String visibleText) {
        select(locator).selectByVisibleText(visibleText);
    }

    public void selectByValue(By locator, String value) {
        select(locator).selectByValue(value);
    }

    public void selectByIndex(By locator, int index) {
        select(locator).selectByIndex(index);
    }

    public String getSelectedText(By locator) {
        return select(locator).getFirstSelectedOption().getText();
    }

    public void hover(By locator) {
        actions().moveToElement(waitUntilVisible(locator)).perform();
    }

    public void doubleClick(By locator) {
        actions().doubleClick(waitUntilClickable(locator)).perform();
    }

    public void rightClick(By locator) {
        actions().contextClick(waitUntilClickable(locator)).perform();
    }

    public void dragAndDrop(By sourceLocator, By targetLocator) {
        actions()
                .dragAndDrop(waitUntilVisible(sourceLocator), waitUntilVisible(targetLocator))
                .perform();
    }

    public void scrollToElement(By locator) {
        executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", waitUntilPresent(locator));
    }

    public void scrollToTop() {
        executeScript("window.scrollTo(0, 0);");
    }

    public void scrollToBottom() {
        executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    public void switchToDefaultContent() {
        driver().switchTo().defaultContent();
    }

    public void switchToFrame(By locator) {
        wait(DEFAULT_TIMEOUT).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator));
    }

    public void switchToFrame(int index) {
        driver().switchTo().frame(index);
    }

    public void switchToWindowByTitle(String title) {
        String currentWindow = driver().getWindowHandle();

        for (String windowHandle : driver().getWindowHandles()) {
            driver().switchTo().window(windowHandle);

            if (driver().getTitle().equals(title)) {
                return;
            }
        }

        driver().switchTo().window(currentWindow);
        throw new IllegalArgumentException("Window not found with title: " + title);
    }

    public void switchToWindowByUrl(String urlText) {
        String currentWindow = driver().getWindowHandle();

        for (String windowHandle : driver().getWindowHandles()) {
            driver().switchTo().window(windowHandle);

            if (driver().getCurrentUrl().contains(urlText)) {
                return;
            }
        }

        driver().switchTo().window(currentWindow);
        throw new IllegalArgumentException("Window not found with url text: " + urlText);
    }

    public void closeCurrentWindow() {
        driver().close();
    }

    public Alert waitUntilAlertPresent() {
        return wait(DEFAULT_TIMEOUT).until(ExpectedConditions.alertIsPresent());
    }

    public String getAlertText() {
        return waitUntilAlertPresent().getText();
    }

    public void acceptAlert() {
        waitUntilAlertPresent().accept();
    }

    public void dismissAlert() {
        waitUntilAlertPresent().dismiss();
    }

    public void typeInAlert(String value) {
        waitUntilAlertPresent().sendKeys(value);
    }

    public byte[] takeScreenshotAsBytes() {
        return ((TakesScreenshot) driver()).getScreenshotAs(OutputType.BYTES);
    }

    public String takeScreenshotAsBase64() {
        return ((TakesScreenshot) driver()).getScreenshotAs(OutputType.BASE64);
    }

    public Object executeScript(String script, Object... args) {
        return ((JavascriptExecutor) driver()).executeScript(script, args);
    }

    private Select select(By locator) {
        return new Select(waitUntilVisible(locator));
    }

    private Actions actions() {
        return new Actions(driver());
    }

    private WebDriverWait wait(Duration timeout) {
        return new WebDriverWait(driver(), timeout);
    }
}
