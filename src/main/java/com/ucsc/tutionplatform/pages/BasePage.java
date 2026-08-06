package com.ucsc.tutionplatform.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;

    // Common Page Layout Locators
    protected By adminShell = By.cssSelector("main.shell.admin");
    protected By loggedInUserLevel = By.cssSelector(".admin-header .panel-kicker");   // "admin_user"
    protected By loggedInUserName = By.cssSelector(".admin-header h2");               // "GroupA"
    protected By portalCredit = By.cssSelector(".portal-credit");

    // Profile menu
    protected By profileMenuButton = By.cssSelector(".admin-profile-menu .admin-avatar-button");
    protected By profileMenuItem =
            By.xpath("//div[contains(@class,'admin-profile-dropdown')]/button[normalize-space()='Profile']");
    protected By logoutMenuItem =
            By.xpath("//div[contains(@class,'admin-profile-dropdown')]/button[normalize-space()='Logout']");

    // Stats strip
    protected By activeSectionTab = By.cssSelector(".section-switcher .switcher.active");

    // =========================================================
    // CONSTRUCTORS
    // =========================================================

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Default constructor for static/helper usages if needed
    public BasePage() {
        this.driver = null;
        this.wait = null;
    }

    // =========================================================
    // WEBELEMENT INTERACTION HELPERS
    // =========================================================

    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void sendKeys(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(text);
    }

    protected void selectDropdown(By locator, String visibleText) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        new Select(element).selectByVisibleText(visibleText);
    }

    protected String getText(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText().trim();
    }

    protected boolean isDisplayed(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // =========================================================
    // DYNAMIC XPATH GENERATORS & LOCATORS
    // =========================================================

    /** Stat value by its caption: "System Users", "Active Accounts", "Students", "Exams". */
    public By statValue(String caption) {
        return By.xpath("//div[contains(@class,'stats')]/article[contains(@class,'stat')]"
                + "[./span[normalize-space()=" + xpathLiteral(caption) + "]]/strong");
    }

    /** Section switcher tab, e.g. "User Details", "Class Examination". */
    public By sectionTab(String name) {
        return By.xpath("//div[contains(@class,'section-switcher')]"
                + "/button[normalize-space()=" + xpathLiteral(name) + "]");
    }

    protected static String labeledInput(String scope, String label) {
        return scope + "//label[normalize-space(text())=" + xpathLiteral(label) + "]//input";
    }

    protected static String labeledSelect(String scope, String label) {
        return scope + "//label[normalize-space(text())=" + xpathLiteral(label) + "]//select";
    }

    /** Button matched on its visible text — never on class="ghost"/"mini-btn", which are styling. */
    protected static String buttonWithText(String scope, String text) {
        return scope + "//button[normalize-space()=" + xpathLiteral(text) + "]";
    }

    /** Partial-text match, for buttons whose label contains punctuation ("Calculate Grade & Z-scores"). */
    protected static String buttonContainingText(String scope, String fragment) {
        return scope + "//button[contains(normalize-space(), " + xpathLiteral(fragment) + ")]";
    }

    /** Search box inside a `.search-input-row`, scoped to the card that owns it. */
    protected static String searchInput(String scope) {
        return scope + "//div[contains(@class,'search-input-row')]/input";
    }

    protected static String searchClearButton(String scope) {
        return scope + "//div[contains(@class,'search-input-row')]/button[normalize-space()='Clear']";
    }

    /** Escapes single and double quotes safely inside XPath string expressions. */
    protected static String xpathLiteral(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }
        return "concat('" + value.replace("'", "', \"'\", '") + "')";
    }
}