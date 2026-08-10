package com.ucsc.tutionplatform.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage extends BasePage {

    private final WebDriverWait wait;

    private final By usernameInput = By.xpath(
            "//input[@name='username' or @type='text' or @placeholder='Username' or contains(@id,'username') or ancestor::div[label[contains(text(),'Username')]]//input]"
    );

    private final By passwordInput = By.xpath(
            "//input[@name='password' or @type='password' or @placeholder='Password' or contains(@id,'password') or ancestor::div[label[contains(text(),'Password')]]//input]"
    );

    private final By loginBtn = By.xpath(
            "//button[@type='submit' or contains(translate(text(),'LOGIN','login'), 'login')]"
    );

    public LoginPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void enterUsername(String username) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput));
        element.clear();
        element.sendKeys(username);
    }

    public void enterPassword(String password) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
        element.clear();
        element.sendKeys(password);
    }

    public void clickLogin() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(loginBtn));
        try {
            button.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
        }
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }
}