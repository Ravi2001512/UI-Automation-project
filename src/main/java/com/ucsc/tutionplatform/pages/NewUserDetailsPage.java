package com.ucsc.tutionplatform.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class NewUserDetailsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators targeted by unique placeholder attributes visible in the form UI
    private final By newUserButton = By.xpath("//button[contains(.,'New User')]");

    private final By userIdInput = By.xpath("//input[@placeholder='USR-ADM-002'] | //form//input[1]");
    private final By displayNameInput = By.xpath("//input[@placeholder='Admin display name']");
    private final By usernameInput = By.xpath("//input[@placeholder='portalusername']");
    private final By passwordInput = By.xpath("//input[@placeholder='Password']");
    private final By emailInput = By.xpath("//input[@placeholder='email@example.com']");
    private final By mobileInput = By.xpath("//input[@placeholder='0771234567']");
    private final By nicInput = By.xpath("//input[@placeholder='NIC number']");

    private final By createUserButton = By.xpath("//button[normalize-space()='Create User']");

    public NewUserDetailsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickNewUserButton() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(newUserButton)).click();
        } catch (Exception ignored) {
            // Screen is already in Create Mode
        }
    }

    private void type(By locator, String value) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(value);
    }

    public void fillFormAndSubmit(String id, String dispName, String username, String pass, String email, String mobile, String nic) {
        type(userIdInput, id);
        type(displayNameInput, dispName);
        type(usernameInput, username);
        type(passwordInput, pass);
        type(emailInput, email);
        type(mobileInput, mobile);
        type(nicInput, nic);

        // Click Submit
        wait.until(ExpectedConditions.elementToBeClickable(createUserButton)).click();
    }

    public boolean isUserPresentInList(String displayName) {
        By userRecord = By.xpath("//*[contains(text(),'" + displayName + "')]");
        try {
            return wait.until(ExpectedConditions.presenceOfElementLocated(userRecord)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}