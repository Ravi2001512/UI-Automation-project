package com.ucsc.tutionplatform.tests.login;

import com.ucsc.tutionplatform.config.ConfigReader;
import com.ucsc.tutionplatform.pages.LoginPage;
import com.ucsc.tutionplatform.tests.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

public class LoginPageTest extends BaseTest {

    public LoginPageTest() {
        super("login_module");
    }

    @Test(description = "Verify successful login with valid credentials")
    public void testValidLogin() {
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(10));

        driver().manage().deleteAllCookies();
        driver().get(ConfigReader.getProperty("app.url"));

        LoginPage loginPage = new LoginPage(driver());
        loginPage.login(
                ConfigReader.getProperty("admin.username"),
                ConfigReader.getProperty("admin.password")
        );

        // Verify dashboard elements appear after login
        By dashboardHeader = By.xpath("//*[contains(text(),'Manage User Accounts') or contains(text(),'GroupA')]");
        boolean isDashboardLoaded = wait.until(ExpectedConditions.visibilityOfElementLocated(dashboardHeader)).isDisplayed();

        Assert.assertTrue(isDashboardLoaded, "Dashboard did not load after valid login!");
    }

    @Test(description = "Verify login fails with invalid credentials")
    public void testInvalidLogin() {
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(10));

        driver().manage().deleteAllCookies();
        driver().get(ConfigReader.getProperty("app.url"));

        LoginPage loginPage = new LoginPage(driver());
        loginPage.login("invalid_user", "wrong_password");

        // Verify login button remains visible (user is still on login form)
        By loginButton = By.xpath("//button[@type='submit' or contains(translate(text(),'LOGIN','login'), 'login')]");
        boolean isStillOnLoginForm = wait.until(ExpectedConditions.visibilityOfElementLocated(loginButton)).isDisplayed();

        Assert.assertTrue(isStillOnLoginForm, "User should remain on the login form with invalid credentials!");
    }
}