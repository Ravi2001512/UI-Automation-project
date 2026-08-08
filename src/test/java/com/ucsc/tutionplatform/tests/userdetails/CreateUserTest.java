package com.ucsc.tutionplatform.tests.userdetails;

import com.ucsc.tutionplatform.pages.UserDetailsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CreateUserTest extends UserDetailsBaseTest {

    @Test(description = "TC-001") // Removed dataProvider
    public void verifyThatUserCanBeCreatedByProvidingMandatoryFieldsOnly() {
        UserDetailsPage userDetailsPage = new UserDetailsPage(driver());

        // Generate dynamic test data
        long timestamp = System.currentTimeMillis();
        String userId = "USR-" + timestamp;
        String displayName = "Test User " + timestamp;
        String username = "user_" + timestamp;
        String email = "user" + timestamp + "@example.com";

        // Step 1: Open fresh creation form
        userDetailsPage.clickNewUser();

        // Step 2: Fill out mandatory user creation details
        userDetailsPage.fillUserForm(userId, displayName, username, "SecurePass@123", email, "0771234567",
                "981234567V");

        // Step 3: Click "Create User" button
        userDetailsPage.clickCreateUserButton();

        // Step 4: Verify the user appears in the accounts list on the left
        boolean isUserVisible = userDetailsPage.isUserCreatedInList(displayName);
        Assert.assertTrue(isUserVisible, "Newly created user '" + displayName + "' was not found in the user list!");
    }

    @Test(description = "TC-055")
    public void verifyThatDuplicateUserIdIsRejected() {
        UserDetailsPage userDetailsPage = new UserDetailsPage(driver());

        // Step 1: Create an initial user first, so we have a known existing User ID to duplicate
        long timestamp = System.currentTimeMillis();
        String existingUserId = "USR-DUP-" + timestamp;
        String firstDisplayName = "Original User " + timestamp;
        String firstUsername = "user_" + timestamp;
        String firstEmail = "user" + timestamp + "@example.com";

        userDetailsPage.clickNewUser();
        userDetailsPage.fillUserForm(existingUserId, firstDisplayName, firstUsername,
                "SecurePass@123", firstEmail, "0771234567", "981234567V");
        userDetailsPage.clickCreateUserButton();

        // Sanity check: confirm the first user was actually created before attempting the duplicate
        Assert.assertTrue(userDetailsPage.isUserCreatedInList(firstDisplayName),
                "Setup failed: original user '" + firstDisplayName + "' was not created!");

        // Step 2: Attempt to create a second user with the SAME User ID but different other details
        String duplicateDisplayName = "Duplicate User " + timestamp;
        String duplicateUsername = "dup_user_" + timestamp;
        String duplicateEmail = "dup" + timestamp + "@example.com";

        userDetailsPage.clickNewUser();
        userDetailsPage.fillUserForm(existingUserId, duplicateDisplayName, duplicateUsername,
                "SecurePass@123", duplicateEmail, "0779876543", "981234568V");
        userDetailsPage.clickCreateUserButton();

        // Step 3: Verify the duplicate was rejected — should NOT appear in the user list
        boolean isDuplicateVisible = userDetailsPage.isUserCreatedInList(duplicateDisplayName);
        Assert.assertFalse(isDuplicateVisible,
                "Bug: duplicate User ID '" + existingUserId + "' was accepted and account was created!");
    }

    @Test(description = "TC-003")
    public void verifyThatInvalidEmailFormatIsRejected() {
        UserDetailsPage userDetailsPage = new UserDetailsPage(driver());

        long timestamp = System.currentTimeMillis();
        String userId = "USR-" + timestamp;
        String displayName = "Invalid Email User " + timestamp;
        String username = "user_" + timestamp;
        String invalidEmail = "nimal.example.com"; // missing '@' - invalid format

        // Step 1: Open fresh creation form
        userDetailsPage.clickNewUser();

        // Step 2: Fill form with an invalid email
        userDetailsPage.fillUserForm(userId, displayName, username, "SecurePass@123",
                invalidEmail, "0771234567", "981234567V");

        // Step 3: Attempt to submit
        userDetailsPage.clickCreateUserButton();

        // Step 4: Verify the user was NOT created (invalid email should block creation)
        boolean isUserVisible = userDetailsPage.isUserCreatedInList(displayName);
        Assert.assertFalse(isUserVisible,
                "Bug: user was created despite an invalid email format '" + invalidEmail + "'!");
    }

    @Test(description = "TC-004")
    public void verifyThatInvalidMobileNumberFormatIsRejected() {
        UserDetailsPage userDetailsPage = new UserDetailsPage(driver());

        long timestamp = System.currentTimeMillis();
        String userId = "USR-" + timestamp;
        String displayName = "Invalid Mobile User " + timestamp;
        String username = "user_" + timestamp;
        String invalidMobile = "12345"; // fewer than 10 digits - invalid Sri Lankan mobile format

        // Step 1: Open fresh creation form
        userDetailsPage.clickNewUser();

        // Step 2: Fill form with an invalid mobile number
        userDetailsPage.fillUserForm(userId, displayName, username, "SecurePass@123",
                "user" + timestamp + "@example.com", invalidMobile, "981234567V");

        // Step 3: Attempt to submit
        userDetailsPage.clickCreateUserButton();

        // Step 4: Verify the user was NOT created (invalid mobile should block creation)
        boolean isUserVisible = userDetailsPage.isUserCreatedInList(displayName);
        Assert.assertFalse(isUserVisible,
                "Bug: user was created despite an invalid mobile number '" + invalidMobile + "'!");
    }
}