package com.ucsc.tutionplatform.tests.userdetails;

import com.ucsc.tutionplatform.pages.UserDetailsPage;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

@Feature("User Details Management")
public class CreateUserTest extends UserDetailsBaseTest {

    @Test(description = "TC-001: Verify successful user creation with mandatory fields")
    public void verifyThatUserCanBeCreatedByProvidingMandatoryFieldsOnly() {
        UserDetailsPage userDetailsPage = new UserDetailsPage(driver());

        long timestamp = System.currentTimeMillis();
        String userId = "USR-" + timestamp;
        String displayName = "Test User " + timestamp;
        String username = "user_" + timestamp;
        String email = "user" + timestamp + "@example.com";

        userDetailsPage.clickNewUser();
        userDetailsPage.fillUserForm(userId, displayName, username, "SecurePass@123", email, "0771234567", "981234567V");
        userDetailsPage.clickCreateUserButton();

        boolean isUserVisible = userDetailsPage.isUserCreatedInList(displayName);
        Assert.assertTrue(isUserVisible, "Newly created user '" + displayName + "' was not found in the user list!");
    }

    @Test(description = "TC-055: Verify duplicate User ID prevention")
    public void verifyThatDuplicateUserIdIsRejected() {
        UserDetailsPage userDetailsPage = new UserDetailsPage(driver());

        long timestamp = System.currentTimeMillis();
        String existingUserId = "USR-DUP-" + timestamp;
        String firstDisplayName = "Original User " + timestamp;
        String firstUsername = "user_" + timestamp;
        String firstEmail = "user" + timestamp + "@example.com";

        // Setup: Create initial user
        userDetailsPage.clickNewUser();
        userDetailsPage.fillUserForm(existingUserId, firstDisplayName, firstUsername, "SecurePass@123", firstEmail, "0771234567", "981234567V");
        userDetailsPage.clickCreateUserButton();

        Assert.assertTrue(userDetailsPage.isUserCreatedInList(firstDisplayName), "Setup failed: original user was not created!");

        // Attempt duplicate creation
        String duplicateDisplayName = "Duplicate User " + timestamp;
        String duplicateUsername = "dup_user_" + timestamp;
        String duplicateEmail = "dup" + timestamp + "@example.com";

        userDetailsPage.clickNewUser();
        userDetailsPage.fillUserForm(existingUserId, duplicateDisplayName, duplicateUsername, "SecurePass@123", duplicateEmail, "0779876543", "981234568V");
        userDetailsPage.clickCreateUserButton();

        boolean isDuplicateVisible = userDetailsPage.isUserCreatedInList(duplicateDisplayName);
        Assert.assertFalse(isDuplicateVisible, "Bug: duplicate User ID '" + existingUserId + "' was accepted!");
    }

//    @Test(description = "TC-003: Verify rejection of invalid email format")
//    public void verifyThatInvalidEmailFormatIsRejected() {
//        UserDetailsPage userDetailsPage = new UserDetailsPage(driver());
//
//        long timestamp = System.currentTimeMillis();
//        String userId = "USR-" + timestamp;
//        String displayName = "Invalid Email User " + timestamp;
//        String username = "user_" + timestamp;
//        String invalidEmail = "nimal.example.com";
//
//        userDetailsPage.clickNewUser();
//        userDetailsPage.fillUserForm(userId, displayName, username, "SecurePass@123", invalidEmail, "0771234567", "981234567V");
//
//        // Verify HTML5 validation state
//        boolean isEmailValid = userDetailsPage.isPersonalEmailValid();
//        Assert.assertFalse(isEmailValid, "Bug: Personal Email field marked '" + invalidEmail + "' as valid!");
//
//        userDetailsPage.clickCreateUserButton();
//
//        boolean isUserVisible = userDetailsPage.isUserCreatedInList(displayName);
//        Assert.assertFalse(isUserVisible, "Bug: user was created despite an invalid email format!");
//    }

    @Test(description = "TC-004: Verify rejection of invalid mobile number format")
    public void verifyThatInvalidMobileNumberFormatIsRejected() {
        UserDetailsPage userDetailsPage = new UserDetailsPage(driver());

        long timestamp = System.currentTimeMillis();
        String userId = "USR-" + timestamp;
        String displayName = "Invalid Mobile User " + timestamp;
        String username = "user_" + timestamp;
        String invalidMobile = "12345";

        userDetailsPage.clickNewUser();
        userDetailsPage.fillUserForm(userId, displayName, username, "SecurePass@123", "user" + timestamp + "@example.com", invalidMobile, "981234567V");
        userDetailsPage.clickCreateUserButton();

        boolean isUserVisible = userDetailsPage.isUserCreatedInList(displayName);
        Assert.assertFalse(isUserVisible, "Bug: user was created despite an invalid mobile number!");
    }
}