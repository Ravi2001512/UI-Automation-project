package com.ucsc.tutionplatform.tests.userdetails;

import com.ucsc.tutionplatform.pages.NewUserDetailsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CreateUserNewTest extends com.ucsc.tutionplatform.tests.userdetails.UserDetailsBaseTest {

    @Test(description = "Create user skipping default dropdown selection")
    public void testUserCreationFlow() {
        NewUserDetailsPage page = new NewUserDetailsPage(driver());

        long timestamp = System.currentTimeMillis();
        String id = "USR-" + (timestamp % 10000);
        String name = "Test User " + (timestamp % 10000);
        String username = "user_" + (timestamp % 10000);
        String email = "user" + (timestamp % 10000) + "@test.com";

        page.clickNewUserButton();

        // Fills all fields and submits without touching the dropdown
        page.fillFormAndSubmit(
                id,
                name,
                username,
                "Pass@12345",
                email,
                "0771234567",
                "981234567V"
        );

        Assert.assertTrue(page.isUserPresentInList(name), "User creation failed for: " + name);
    }
}