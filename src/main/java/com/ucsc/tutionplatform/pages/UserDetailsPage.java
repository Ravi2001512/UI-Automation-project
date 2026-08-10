package com.ucsc.tutionplatform.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class UserDetailsPage extends BasePage {

    private final WebDriverWait wait;

    // =========================================================================
    // 1. XPATH SCOPES & STRING TEMPLATES
    // =========================================================================

    private static final String FORM = "//form[contains(@class,'detail-form')]";
    private static final String LIST_CARD = "//article[contains(@class,'user-list-card')]";
    private static final String FORM_ACTIONS = FORM + "//div[contains(@class,'form-actions')]";

    private static final String SEARCH_INPUT_XPATH = "//input[@placeholder='Search by any detail: ID, name, username, email, mobile, NIC, level, or status']";
    private static final String USER_ROW_TEMPLATE = LIST_CARD + "//article[contains(@class,'user-row')][.//strong[normalize-space()=%s]]";
    private static final String USER_STATUS_BADGE_TEMPLATE = USER_ROW_TEMPLATE + "//span[contains(@class,'status-badge')]";
    private static final String USER_LEVEL_PILL_TEMPLATE = USER_ROW_TEMPLATE + "//span[contains(@class,'level-pill')]";

    // =========================================================================
    // 2. STATIC LOCATORS
    // =========================================================================

    // --- Create / Edit User Form ---
    private final By userIdInput = By.xpath(labeledInput(FORM, "User ID"));
    private final By userLevelSelect = By.xpath(labeledSelect(FORM, "User Level"));
    private final By userDisplayNameInput = By.xpath(labeledInput(FORM, "Display Name"));
    private final By userNameInput = By.xpath(labeledInput(FORM, "Username"));
    private final By passwordInput = By.xpath(labeledInput(FORM, "Password"));
    private final By personalEmailInput = By.xpath(labeledInput(FORM, "Personal Email"));
    private final By mobileInput = By.xpath(labeledInput(FORM, "Mobile"));
    private final By nicInput = By.xpath(labeledInput(FORM, "NIC"));

    private final By createUserBtn = By.xpath(FORM_ACTIONS + "//button[@type='submit']");
    private final By clearFormBtn = By.xpath(buttonWithText(FORM_ACTIONS, "Clear"));
    private final By formModeKicker = By.cssSelector(".detail-card .panel-kicker");
    private final By formHeading = By.cssSelector(".detail-card .card-head h3");

    // --- User List Card ---
    private final By newUserBtn = By.xpath(buttonWithText(LIST_CARD, "New User"));
    private final By userSearchInput = By.xpath(SEARCH_INPUT_XPATH);
    private final By clearUserSearchBtn = By.xpath(SEARCH_INPUT_XPATH + "/following-sibling::button");
    private final By userList = By.xpath("//div[@class='user-list']");

    // =========================================================================
    // 3. CONSTRUCTOR
    // =========================================================================

    public UserDetailsPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    // =========================================================================
    // 4. PRIVATE DYNAMIC LOCATOR GENERATORS
    // =========================================================================

    private By getUserRowLocator(String displayName) {
        return By.xpath(String.format(USER_ROW_TEMPLATE, xpathLiteral(displayName)));
    }

    private By getUserStatusBadgeLocator(String displayName) {
        return By.xpath(String.format(USER_STATUS_BADGE_TEMPLATE, xpathLiteral(displayName)));
    }

    private By getUserLevelPillLocator(String displayName) {
        return By.xpath(String.format(USER_LEVEL_PILL_TEMPLATE, xpathLiteral(displayName)));
    }

    private By getDeactivateBtnLocator(String displayName) {
        String row = String.format(USER_ROW_TEMPLATE, xpathLiteral(displayName));
        return By.xpath(buttonWithText(row, "Deactivate"));
    }

    private By getActivateBtnLocator(String displayName) {
        String row = String.format(USER_ROW_TEMPLATE, xpathLiteral(displayName));
        return By.xpath(buttonWithText(row, "Activate"));
    }

    // =========================================================================
    // 5. FORM INTERACTIONS
    // =========================================================================

    public void selectUserLevel(String level) {
        Select select = new Select(wait.until(ExpectedConditions.elementToBeClickable(userLevelSelect)));
        try {
            select.selectByVisibleText(level);
        } catch (org.openqa.selenium.NoSuchElementException ignored) {
            select.selectByValue(level.toLowerCase().replace(" ", "_"));
        }
    }

    public void fillUserForm(String id, String dispName, String username, String pass, String email, String mobile, String nic) {
        sendKeys(userIdInput, id);
        sendKeys(userDisplayNameInput, dispName);
        sendKeys(userNameInput, username);
        sendKeys(passwordInput, pass);
        sendKeys(personalEmailInput, email);
        sendKeys(mobileInput, mobile);
        sendKeys(nicInput, nic);
    }

    public void clickCreateUserButton() {
        click(createUserBtn);
    }

    public void clickClearForm() {
        click(clearFormBtn);
    }

    public String getFormModeKickerText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(formModeKicker)).getText();
    }

    public String getFormHeadingText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(formHeading)).getText();
    }

    // =========================================================================
    // 6. HTML5 FORM VALIDATION HELPERS
    // =========================================================================

    public boolean isPersonalEmailValid() {
        WebElement emailElement = wait.until(ExpectedConditions.presenceOfElementLocated(personalEmailInput));
        return (Boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].checkValidity();", emailElement);
    }

    public String getPersonalEmailValidationMessage() {
        WebElement emailElement = wait.until(ExpectedConditions.presenceOfElementLocated(personalEmailInput));
        return (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].validationMessage;", emailElement);
    }

    // =========================================================================
    // 7. USER LIST ACTIONS & VERIFICATIONS
    // =========================================================================

    public void clickNewUser() {
        click(newUserBtn);
    }

    public void searchUser(String searchStr) {
        sendKeys(userSearchInput, searchStr);
    }

    public void clearSearch() {
        click(clearUserSearchBtn);
    }

    public boolean isUserVisible(String displayName) {
        return isDisplayed(getUserRowLocator(displayName));
    }

    public boolean isUserCreatedInList(String displayName) {
        return isUserVisible(displayName);
    }

    public String getUserStatusBadgeText(String displayName) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                getUserStatusBadgeLocator(displayName))).getText();
    }

    public String getUserLevelPillText(String displayName) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                getUserLevelPillLocator(displayName))).getText();
    }

    public void deactivateUser(String displayName) {
        click(getDeactivateBtnLocator(displayName));
    }

    public void activateUser(String displayName) {
        click(getActivateBtnLocator(displayName));
    }
}