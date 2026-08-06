package com.ucsc.tutionplatform.pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage extends BasePage {

    private final WebDriverWait wait;

    private By usernameInput = By.xpath(
            "//input[@name='username' or @type='text' or @placeholder='Username' or ancestor::div[label[contains(text(),'Username')]]//input]"
    );

    private By passwordInput = By.xpath(
            "//input[@name='password' or @type='password' or @placeholder='Password' or ancestor::div[label[contains(text(),'Password')]]//input]"
    );

    private By loginBtn = By.xpath(
            "//button[@type='submit' or contains(translate(text(),'LOGIN','login'), 'login')]"
    );

    public LoginPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void enterUsername(String username) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput));
        sendKeys(usernameInput, username);
    }

    public void enterPassword(String password) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
        sendKeys(passwordInput, password);
    }

    public void clickLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(loginBtn));
        click(loginBtn);
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }
}