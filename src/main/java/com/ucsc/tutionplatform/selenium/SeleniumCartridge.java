package com.ucsc.tutionplatform.selenium;

import com.ucsc.tutionplatform.core.DriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The low-level Selenium façade ("cartridge"). Everything the framework does to a browser
 * goes through here.
 *
 * Design rules for this class - keep them:
 *  1. NO assertions. Assertions belong in keyword libraries; this layer only drives the browser.
 *  2. NO reporting/logging side effects. The executor owns reporting.
 *  3. NO test data / business logic. It knows about By and String, nothing about "login".
 *  4. Every read/interaction is guarded by an explicit wait. There are no unguarded findElement
 *     calls in test flows - that is the single biggest source of flakiness.
 *  5. Stateless and thread-safe: state lives in DriverManager's ThreadLocal.
 */
public class SeleniumCartridge {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration POLLING = Duration.ofMillis(250);
    private static final int STALE_RETRIES = 2;

    // ------------------------------------------------------------------ driver

    public WebDriver driver() {
        return DriverManager.getDriver();
    }

    public JavascriptExecutor js() {
        return (JavascriptExecutor) driver();
    }

    // ------------------------------------------------------------------ finders

    /** Raw find - use only when you already know the element is settled. */
    public WebElement findElement(By locator) {
        return driver().findElement(locator);
    }

    public List<WebElement> findElements(By locator) {
        return driver().findElements(locator);
    }

    /** The finder you should actually use in keywords. */
    public WebElement find(By locator) {
        return waitUntilElementIsVisible(locator);
    }

    public int count(By locator) {
        return findElements(locator).size();
    }

    // ------------------------------------------------------------------ waits

    public WebDriverWait waitFor(){
        return new WebDriverWait(driver(), DEFAULT_TIMEOUT);
    }

    public WebDriverWait waitFor(Duration timeout){
        return new WebDriverWait(driver(), timeout);
    }

    public WebElement waitUntilElementIsPresent(By locator) {
        return waitUntilElementIsPresent(locator, DEFAULT_TIMEOUT);
    }

    public WebElement waitUntilElementIsPresent(By locator, Duration timeout) {
        return waitFor(timeout).until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public WebElement waitUntilElementIsVisible(By locator) {
        return waitUntilElementIsVisible(locator, DEFAULT_TIMEOUT);
    }

    public WebElement waitUntilElementIsVisible(By locator, Duration timeout) {
        return waitFor(timeout).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public List<WebElement> waitUntilAllElementsAreVisible(By locator) {
        return waitFor().until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    public WebElement waitUntilElementIsClickable(By locator) {
        return waitUntilElementIsClickable(locator, DEFAULT_TIMEOUT);
    }

    public WebElement waitUntilElementIsClickable(By locator, Duration timeout) {
        return waitFor(timeout).until(ExpectedConditions.elementToBeClickable(locator));
    }

    public boolean waitUntilElementIsInvisible(By locator) {
        return waitUntilElementIsInvisible(locator, DEFAULT_TIMEOUT);
    }

    public boolean waitUntilElementIsInvisible(By locator, Duration timeout) {
        return waitFor(timeout).until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public boolean waitUntilTextIsPresent(By locator, String text) {
        return waitFor().until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    public boolean waitUntilAttributeIs(By locator, String attribute, String value) {
        return waitFor().until(ExpectedConditions.attributeToBe(locator, attribute, value));
    }

    public boolean waitUntilUrlContains(String fragment) {
        return waitFor().until(ExpectedConditions.urlContains(fragment));
    }

    public boolean waitUntilTitleContains(String fragment) {
        return waitFor().until(ExpectedConditions.titleContains(fragment));
    }

    /** Blocks until document.readyState == complete (and jQuery is idle, if jQuery is on the page). */
    public void waitUntilPageIsReady() {
        waitFor().until((ExpectedCondition<Boolean>) d -> {
            Object ready = ((JavascriptExecutor) d).executeScript("return document.readyState");
            boolean domDone = "complete".equals(ready);
            Object jq = ((JavascriptExecutor) d)
                    .executeScript("return (window.jQuery === undefined) ? 0 : jQuery.active");
            boolean ajaxDone = jq == null || ((Number) jq).intValue() == 0;
            return domDone && ajaxDone;
        });
    }

    /** Waits for the DOM to stop changing - the pragmatic answer to animated/SPA pages. */
    public void waitUntilDomIsStable(Duration quietPeriod, Duration timeout) {
        long deadline = System.currentTimeMillis() + timeout.toMillis();
        String previous = "";
        long stableSince = System.currentTimeMillis();
        while (System.currentTimeMillis() < deadline) {
            String current = String.valueOf(js().executeScript("return document.body.innerHTML.length"));
            if (!current.equals(previous)) {
                previous = current;
                stableSince = System.currentTimeMillis();
            } else if (System.currentTimeMillis() - stableSince >= quietPeriod.toMillis()) {
                return;
            }
            sleep(Duration.ofMillis(100));
        }
        throw new TimeoutException("DOM did not stabilise within " + timeout);
    }

    // ------------------------------------------------------------------ navigation

    public void open(String url) {
        driver().get(url);
        waitUntilPageIsReady();
    }

    public void refresh()  { driver().navigate().refresh(); waitUntilPageIsReady(); }
    public void back()     { driver().navigate().back();    waitUntilPageIsReady(); }
    public void forward()  { driver().navigate().forward(); waitUntilPageIsReady(); }

    public String currentUrl() { return driver().getCurrentUrl(); }
    public String title()      { return driver().getTitle(); }
    public String pageSource() { return driver().getPageSource(); }

    // ------------------------------------------------------------------ interactions

    public void click(By locator) {
        retryOnStale(() -> {
            WebElement el = waitUntilElementIsClickable(locator);
            scrollIntoView(el);
            el.click();
            return null;
        });
    }

    /**
     * Clicks, falling back to a JS click when the element is obscured by an overlay/sticky header.
     * Use deliberately: a JS click bypasses real user-visibility checks.
     */
    public void clickWithJsFallback(By locator) {
        try {
            click(locator);
        } catch (ElementClickInterceptedException | TimeoutException e) {
            jsClick(locator);
        }
    }

    public void jsClick(By locator) {
        WebElement el = waitUntilElementIsPresent(locator);
        js().executeScript("arguments[0].click();", el);
    }

    public void doubleClick(By locator) {
        new Actions(driver()).doubleClick(waitUntilElementIsClickable(locator)).perform();
    }

    public void rightClick(By locator) {
        new Actions(driver()).contextClick(waitUntilElementIsClickable(locator)).perform();
    }

    public void hover(By locator) {
        new Actions(driver()).moveToElement(waitUntilElementIsVisible(locator)).perform();
    }

    public void type(By locator, String text) {
        retryOnStale(() -> {
            WebElement el = waitUntilElementIsVisible(locator);
            scrollIntoView(el);
            el.clear();
            el.sendKeys(text == null ? "" : text);
            return null;
        });
    }

    /** Types without clearing - for masked/formatted inputs that reject clear(). */
    public void append(By locator, String text) {
        waitUntilElementIsVisible(locator).sendKeys(text);
    }

    public void clear(By locator) {
        WebElement el = waitUntilElementIsVisible(locator);
        el.clear();
        // Some React/Angular inputs ignore clear(); force it and fire the events the app listens for.
        if (!el.getAttribute("value").isEmpty()) {
            el.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        }
    }

    public void pressKey(By locator, Keys key) {
        waitUntilElementIsVisible(locator).sendKeys(key);
    }

    public void pressKey(Keys key) {
        new Actions(driver()).sendKeys(key).perform();
    }

    public void submit(By locator) {
        waitUntilElementIsVisible(locator).submit();
    }

    public void uploadFile(By fileInputLocator, String absolutePath) {
        // Do NOT click the input; sendKeys straight to the <input type=file>.
        waitUntilElementIsPresent(fileInputLocator).sendKeys(absolutePath);
    }

    public void dragAndDrop(By source, By target) {
        new Actions(driver())
                .dragAndDrop(waitUntilElementIsVisible(source), waitUntilElementIsVisible(target))
                .perform();
    }

    public void scrollIntoView(WebElement element) {
        js().executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'center', behavior:'instant'});",
                element);
    }

    public void scrollIntoView(By locator) {
        scrollIntoView(waitUntilElementIsPresent(locator));
    }

    public void scrollToBottom() {
        js().executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    // ------------------------------------------------------------------ reads

    public String getText(By locator) {
        return retryOnStale(() -> waitUntilElementIsVisible(locator).getText().trim());
    }

    public List<String> getTexts(By locator) {
        return retryOnStale(() -> waitUntilAllElementsAreVisible(locator).stream()
                .map(WebElement::getText)
                .map(String::trim)
                .collect(Collectors.toList()));
    }

    public String getAttribute(By locator, String attribute) {
        return retryOnStale(() -> waitUntilElementIsPresent(locator).getAttribute(attribute));
    }

    /** Prefer this over getAttribute("value") - it reads the live DOM property, not the HTML attribute. */
    public String getValue(By locator) {
        return retryOnStale(() -> waitUntilElementIsPresent(locator).getDomProperty("value"));
    }

    public String getCssValue(By locator, String property) {
        return waitUntilElementIsPresent(locator).getCssValue(property);
    }

    /** Non-throwing presence check. This is the ONLY place a short timeout is acceptable. */
    public boolean isDisplayed(By locator) {
        try {
            return waitUntilElementIsVisible(locator, Duration.ofSeconds(3)).isDisplayed();
        } catch (TimeoutException | NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    public boolean isPresent(By locator) {
        return !findElements(locator).isEmpty();
    }

    public boolean isEnabled(By locator) {
        return waitUntilElementIsPresent(locator).isEnabled();
    }

    public boolean isSelected(By locator) {
        return waitUntilElementIsPresent(locator).isSelected();
    }

    // ------------------------------------------------------------------ checkboxes / radios

    public void setCheckbox(By locator, boolean checked) {
        WebElement el = waitUntilElementIsClickable(locator);
        if (el.isSelected() != checked) {
            scrollIntoView(el);
            el.click();
        }
    }

    // ------------------------------------------------------------------ dropdowns

    private Select select(By locator) {
        return new Select(waitUntilElementIsVisible(locator));
    }

    public void selectByVisibleText(By locator, String text) { select(locator).selectByVisibleText(text); }
    public void selectByValue(By locator, String value)      { select(locator).selectByValue(value); }
    public void selectByIndex(By locator, int index)         { select(locator).selectByIndex(index); }

    public String getSelectedOption(By locator) {
        return select(locator).getFirstSelectedOption().getText().trim();
    }

    public List<String> getSelectOptions(By locator) {
        return select(locator).getOptions().stream()
                .map(WebElement::getText).map(String::trim).collect(Collectors.toList());
    }

    // ------------------------------------------------------------------ frames

    public void switchToFrame(By locator) {
        driver().switchTo().frame(waitUntilElementIsPresent(locator));
    }

    public void switchToFrame(int index)   { driver().switchTo().frame(index); }
    public void switchToFrame(String name) { driver().switchTo().frame(name); }
    public void switchToDefaultContent()   { driver().switchTo().defaultContent(); }
    public void switchToParentFrame()      { driver().switchTo().parentFrame(); }

    // ------------------------------------------------------------------ windows / tabs

    public String currentWindow() { return driver().getWindowHandle(); }

    /** Switches to the most recently opened window and returns the handle you came from. */
    public String switchToNewWindow() {
        String origin = currentWindow();
        waitFor().until(d -> d.getWindowHandles().size() > 1);
        Set<String> handles = driver().getWindowHandles();
        for (String h : handles) {
            if (!h.equals(origin)) {
                driver().switchTo().window(h);
                break;
            }
        }
        return origin;
    }

    public void switchToWindow(String handle) { driver().switchTo().window(handle); }

    public void closeCurrentWindowAndReturnTo(String handle) {
        driver().close();
        driver().switchTo().window(handle);
    }

    // ------------------------------------------------------------------ alerts

    public Alert waitForAlert() {
        return waitFor().until(ExpectedConditions.alertIsPresent());
    }

    public String acceptAlert() {
        Alert a = waitForAlert();
        String text = a.getText();
        a.accept();
        return text;
    }

    public String dismissAlert() {
        Alert a = waitForAlert();
        String text = a.getText();
        a.dismiss();
        return text;
    }

    public void typeInAlert(String text) {
        Alert a = waitForAlert();
        a.sendKeys(text);
        a.accept();
    }

    // ------------------------------------------------------------------ cookies / storage

    public void addCookie(String name, String value) {
        driver().manage().addCookie(new Cookie(name, value));
    }

    public String getCookie(String name) {
        Cookie c = driver().manage().getCookieNamed(name);
        return c == null ? null : c.getValue();
    }

    public void deleteAllCookies() { driver().manage().deleteAllCookies(); }

    public String getLocalStorageItem(String key) {
        return (String) js().executeScript("return window.localStorage.getItem(arguments[0]);", key);
    }

    public void setLocalStorageItem(String key, String value) {
        js().executeScript("window.localStorage.setItem(arguments[0], arguments[1]);", key, value);
    }

    // ------------------------------------------------------------------ evidence

    public byte[] screenshot() {
        return ((TakesScreenshot) driver()).getScreenshotAs(OutputType.BYTES);
    }

    public byte[] screenshot(By locator) {
        return waitUntilElementIsVisible(locator).getScreenshotAs(OutputType.BYTES);
    }

    // ------------------------------------------------------------------ internals

    /**
     * Re-runs an action when the DOM swaps the element out from under us. A bounded retry -
     * never an unbounded loop, or a genuinely broken locator will hang the suite.
     */
    private <T> T retryOnStale(java.util.function.Supplier<T> action) {
        StaleElementReferenceException last = null;
        for (int attempt = 0; attempt <= STALE_RETRIES; attempt++) {
            try {
                return action.get();
            } catch (StaleElementReferenceException e) {
                last = e;
                sleep(Duration.ofMillis(200));
            }
        }
        throw last;
    }

    /** Deliberately private. Keywords must not sleep; if you need a wait, add a condition. */
    private void sleep(Duration d) {
        try {
            Thread.sleep(d.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting", e);
        }
    }
}