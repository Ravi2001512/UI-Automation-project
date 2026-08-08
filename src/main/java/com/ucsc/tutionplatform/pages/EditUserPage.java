package com.ucsc.tutionplatform.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class EditUserPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final Duration TIMEOUT =
            Duration.ofSeconds(20);

    public EditUserPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(
                driver,
                TIMEOUT
        );
    }

    private final By userSearchInput = By.cssSelector(
            "input[placeholder='Search by any detail: ID, name, username, email, mobile, NIC, level, or status']"
    );

    private final By editUserIdInput = By.xpath(
            "//form[contains(@class,'detail-form')]"
                    + "//input[@placeholder='USR-ADM-002']"
    );

    private final By editDisplayNameInput = By.xpath(
            "//form[contains(@class,'detail-form')]"
                    + "//input[@placeholder='Admin display name']"
    );

    private final By updateUserButton = By.xpath(
            "//form[contains(@class,'detail-form')]"
                    + "//button[@type='submit' "
                    + "and normalize-space()='Update User']"
    );


    private By userResultRow(String userId) {
        return By.xpath(
                "//div[contains(@class,'user-list')]"
                        + "//article[contains(@class,'user-row')]"
                        + "[.//p[contains(normalize-space(.),"
                        + xpathLiteral(userId)
                        + ")]]"
        );
    }

    private By userResultButton(String userId) {
        return By.xpath(
                "//div[contains(@class,'user-list')]"
                        + "//article[contains(@class,'user-row')]"
                        + "[.//p[contains(normalize-space(.),"
                        + xpathLiteral(userId)
                        + ")]]"
                        + "//button[contains(@class,'user-row-main')]"
        );
    }

    private By displayNameInResult(String userId) {
        return By.xpath(
                "//div[contains(@class,'user-list')]"
                        + "//article[contains(@class,'user-row')]"
                        + "[.//p[contains(normalize-space(.),"
                        + xpathLiteral(userId)
                        + ")]]"
                        + "//strong"
        );
    }

    public void searchUser(String userId) {

        WebElement searchInput = wait.until(
                ExpectedConditions.elementToBeClickable(
                        userSearchInput
                )
        );

        searchInput.clear();
        searchInput.sendKeys(userId);

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        userResultRow(userId)
                )
        );
    }


    public boolean isUserDisplayed(String userId) {

        try {

            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            userResultRow(userId)
                    )
            ).isDisplayed();

        } catch (RuntimeException exception) {

            return false;
        }
    }


    public void selectUser(String userId) {

        WebElement resultButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        userResultButton(userId)
                )
        );

        resultButton.click();


        wait.until(driver -> {

            WebElement userIdField =
                    driver.findElement(
                            editUserIdInput
                    );

            return !userIdField.isEnabled();
        });

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        updateUserButton
                )
        );
    }


    public String getEditingUserId() {

        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        editUserIdInput
                )
        ).getAttribute("value");
    }

    public String getCurrentDisplayName() {

        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        editDisplayNameInput
                )
        ).getAttribute("value");
    }


    public void enterNewDisplayName(
            String newDisplayName
    ) {

        WebElement input = wait.until(
                ExpectedConditions.elementToBeClickable(
                        editDisplayNameInput
                )
        );

        input.clear();
        input.sendKeys(newDisplayName);
    }

    public void clickUpdateUser() {

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        updateUserButton
                )
        ).click();
    }

    public String getDisplayNameFromResult(
            String userId
    ) {

        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        displayNameInResult(userId)
                )
        ).getText().trim();
    }

    public void waitForUpdatedDisplayName(
            String userId,
            String expectedDisplayName
    ) {

        wait.until(driver -> {

            WebElement nameElement =
                    driver.findElement(
                            displayNameInResult(userId)
                    );

            return expectedDisplayName.equals(
                    nameElement.getText().trim()
            );
        });
    }

    private static String xpathLiteral(
            String value
    ) {

        if (!value.contains("'")) {
            return "'" + value + "'";
        }

        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }

        String[] parts =
                value.split("'");

        StringBuilder result =
                new StringBuilder("concat(");

        for (int i = 0;
             i < parts.length;
             i++) {

            if (i > 0) {
                result.append(
                        ", \"'\", "
                );
            }

            result.append("'")
                    .append(parts[i])
                    .append("'");
        }

        result.append(")");

        return result.toString();
    }
}