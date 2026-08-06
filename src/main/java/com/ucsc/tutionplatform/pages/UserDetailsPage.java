package com.ucsc.tutionplatform.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public class UserDetailsPage extends BasePage {

    // CONSTRUCTOR

    public UserDetailsPage(WebDriver driver) {
        super(driver);
    }

    // Two cards on this screen, both scoped so nothing leaks across them.
    private static final String FORM = "//form[contains(@class,'detail-form')]";
    private static final String LIST_CARD = "//article[contains(@class,'user-list-card')]";
    private static final String FORM_ACTIONS = FORM + "//div[contains(@class,'form-actions')]";

    // ---------- Create / Edit user form ----------
    private By userIdInput = By.xpath(labeledInput(FORM, "User ID"));
    private By userLevelSelect = By.xpath(labeledSelect(FORM, "User Level"));
    private By userDisplayNameInput = By.xpath(labeledInput(FORM, "Display Name"));
    private By userNameInput = By.xpath(labeledInput(FORM, "Username"));
    private By passwordInput = By.xpath(labeledInput(FORM, "Password")); // label carries a trailing space
    private By personalEmailInput = By.xpath(labeledInput(FORM, "Personal Email"));
    private By mobileInput = By.xpath(labeledInput(FORM, "Mobile"));
    private By nicInput = By.xpath(labeledInput(FORM, "NIC")); // was pointing at Mobile

    private By createUserBtn = By.xpath(FORM_ACTIONS + "//button[@type='submit']");
    private By clearFormBtn = By.xpath(buttonWithText(FORM_ACTIONS, "Clear"));
    private By formModeKicker = By.cssSelector(".detail-card .panel-kicker"); // "Create Mode"
    private By formHeading = By.cssSelector(".detail-card .card-head h3");

    // ---------- User list ----------
    private By newUserBtn = By.xpath(buttonWithText(LIST_CARD, "New User"));

    // ---------- Dynamic XPath for Row ----------
    private String rowXpath(String displayName) {
        return LIST_CARD + "//article[contains(@class,'user-row')]"
                + "[.//strong[normalize-space()=" + xpathLiteral(displayName) + "]]";
    }

    public By userRow(String displayName) {
        return By.xpath(rowXpath(displayName));
    }

    public By userStatusBadge(String displayName) {
        return By.xpath(rowXpath(displayName) + "//span[contains(@class,'status-badge')]");
    }

    public By userLevelPill(String displayName) {
        return By.xpath(rowXpath(displayName) + "//span[contains(@class,'level-pill')]");
    }

    public By deactivateBtn(String displayName) {
        return By.xpath(buttonWithText(rowXpath(displayName), "Deactivate"));
    }

    public By activateBtn(String displayName) {
        return By.xpath(buttonWithText(rowXpath(displayName), "Activate"));
    }

    private By userSearchInput = By.xpath(
            "//input[@placeholder='Search by any detail: ID, name, username, email, mobile, NIC, level, or status']");
    private By clearUserSearchBtn = By.xpath(
            "//input[@placeholder='Search by any detail: ID, name, username, email, mobile, NIC, level, or status']/following-sibling::button");
    private By userList = By.xpath("//div[@class='user-list']");
    private By individualUserCardBtn = By
            .xpath("//div[@class='user-list']/article[3]//button[contains(@class, 'user-row-main')]");
    private By DisplayNameInUserCard = By.xpath("//button[contains(@class, 'user-row-main')]/descendant::strong");

    // private By user

    public void searchUser(String searchStr) {
        sendKeys(userSearchInput, searchStr);
    }

    public void clearSearch() {
        click(clearUserSearchBtn);
    }

    public boolean isUserVisible(String displayName) {
        return isDisplayed(userRow(displayName));
    }

    public boolean isUserCreatedInList(String displayName) {
        return isUserVisible(displayName);
    }

    public void clickNewUser() {
        click(newUserBtn);
    }

    public void selectUserLevel(String level) {
        Select select = new Select(wait.until(ExpectedConditions.elementToBeClickable(userLevelSelect)));
        try {
            select.selectByVisibleText(level);
        } catch (org.openqa.selenium.NoSuchElementException ignored) {
            select.selectByValue(level.toLowerCase().replace(" ", "_"));
        }
    }

    public void fillUserForm(String id, String dispName, String username, String pass, String email, String mobile,
                             String nic) {
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
}