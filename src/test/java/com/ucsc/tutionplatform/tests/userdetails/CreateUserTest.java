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
}