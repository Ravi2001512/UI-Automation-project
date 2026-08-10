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
public class SearchUserTest extends UserDetailsBaseTest {

    @Test(description = "TC-SEARCH-001: Verify searching user by Display Name")
     public void verifySearchByDisplayName() {
        UserDetailsPage userDetailsPage = new UserDetailsPage(driver());

        // Setup: Create a dynamic user
        long timestamp = System.currentTimeMillis();
        String userId = "USR-SRC-" + timestamp;
        String displayName = "Search User " + timestamp;
        String username = "search_user_" + timestamp;
        String email = "search" + timestamp + "@example.com";

        userDetailsPage.clickNewUser();
        userDetailsPage.fillUserForm(userId, displayName, username, "SecurePass@123", email, "0771234567", "981234567V");
        userDetailsPage.clickCreateUserButton();

        // Perform Search
        userDetailsPage.searchUser(displayName);

        // Verify
        boolean isFound = userDetailsPage.isUserVisible(displayName);
        Assert.assertTrue(isFound, "Search failed: User '" + displayName + "' was not found in results!");

        // Clean up search filter
        userDetailsPage.clearSearch();
    }

    @Test(description = "TC-SEARCH-002: Verify search is case-insensitive")
    public void verifySearchIsCaseInsensitive() {
        UserDetailsPage userDetailsPage = new UserDetailsPage(driver());

        // Setup: Create a user
        long timestamp = System.currentTimeMillis();
        String userId = "USR-CASE-" + timestamp;
        String displayName = "Case Test " + timestamp;
        String username = "case_user_" + timestamp;
        String email = "casetest" + timestamp + "@example.com";

        userDetailsPage.clickNewUser();
        userDetailsPage.fillUserForm(userId, displayName, username, "SecurePass@123", email, "0771234567", "981234567V");
        userDetailsPage.clickCreateUserButton();

        // Search in ALL LOWERCASE
        userDetailsPage.searchUser(displayName.toLowerCase());

        boolean isFound = userDetailsPage.isUserVisible(displayName);
        Assert.assertTrue(isFound, "Case-insensitive search failed for keyword: " + displayName.toLowerCase());

        userDetailsPage.clearSearch();
    }

    @Test(description = "TC-SEARCH-003: Verify searching user by User ID")
    public void verifySearchByUserId() {
        UserDetailsPage userDetailsPage = new UserDetailsPage(driver());

        long timestamp = System.currentTimeMillis();
        String userId = "USR-ID-" + timestamp;
        String displayName = "ID Search User " + timestamp;

        userDetailsPage.clickNewUser();
        userDetailsPage.fillUserForm(userId, displayName, "id_user_" + timestamp, "SecurePass@123",
                "iduser" + timestamp + "@example.com", "0771234567", "981234567V");
        userDetailsPage.clickCreateUserButton();

        // Search by User ID
        userDetailsPage.searchUser(userId);

        boolean isFound = userDetailsPage.isUserVisible(displayName);
        Assert.assertTrue(isFound, "Search by User ID failed: User with ID '" + userId + "' was not found!");

        userDetailsPage.clearSearch();
    }

    @Test(description = "TC-SEARCH-004: Verify searching user by Email Address")
    @Story("Search Functionality")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies that typing an email address into the search input correctly matches and displays the corresponding user.")
    public void verifySearchByPersonalEmail() {
        UserDetailsPage userDetailsPage = new UserDetailsPage(driver());

        long timestamp = System.currentTimeMillis();
        String email = "unique_email_" + timestamp + "@example.com";
        String displayName = "Email Search User " + timestamp;

        userDetailsPage.clickNewUser();
        userDetailsPage.fillUserForm("USR-EMAIL-" + timestamp, displayName, "email_user_" + timestamp,
                "SecurePass@123", email, "0771234567", "981234567V");
        userDetailsPage.clickCreateUserButton();

        // Search by Email
        userDetailsPage.searchUser(email);

        boolean isFound = userDetailsPage.isUserVisible(displayName);
        Assert.assertTrue(isFound, "Search by Email failed: User with email '" + email + "' was not found!");

        userDetailsPage.clearSearch();
    }

    @Test(description = "TC-SEARCH-005: Verify searching user by Mobile Number")
    public void verifySearchByMobileNumber() {
        UserDetailsPage userDetailsPage = new UserDetailsPage(driver());

        long timestamp = System.currentTimeMillis();
        String mobile = "077" + (timestamp % 10000000); // 10-digit Sri Lankan phone number format
        String displayName = "Mobile Search User " + timestamp;

        userDetailsPage.clickNewUser();
        userDetailsPage.fillUserForm("USR-MOB-" + timestamp, displayName, "mob_user_" + timestamp,
                "SecurePass@123", "mob" + timestamp + "@example.com", mobile, "981234567V");
        userDetailsPage.clickCreateUserButton();

        // Search by Mobile
        userDetailsPage.searchUser(mobile);

        boolean isFound = userDetailsPage.isUserVisible(displayName);
        Assert.assertTrue(isFound, "Search by Mobile Number failed: User with mobile '" + mobile + "' was not found!");

        userDetailsPage.clearSearch();
    }

    @Test(description = "TC-SEARCH-006: Verify search with non-existing term yields no results")
    public void verifySearchByNonExistingTermReturnsNoResult() {
        UserDetailsPage userDetailsPage = new UserDetailsPage(driver());

        String nonExistingKeyword = "NonExistingUser_" + System.currentTimeMillis();

        // Search for a non-existing user
        userDetailsPage.searchUser(nonExistingKeyword);

        // Verify the fake user is not visible
        boolean isFound = userDetailsPage.isUserVisible(nonExistingKeyword);
        Assert.assertFalse(isFound, "Search failed: Non-existing user term '" + nonExistingKeyword + "' unexpectedly returned results!");

        userDetailsPage.clearSearch();
    }

    @Test(description = "TC-SEARCH-007: Verify clear search button restores user list")
    public void verifyClearSearchRestoresOriginalList() {
        UserDetailsPage userDetailsPage = new UserDetailsPage(driver());

        long timestamp = System.currentTimeMillis();
        String displayName = "Clear Search User " + timestamp;

        // Setup user
        userDetailsPage.clickNewUser();
        userDetailsPage.fillUserForm("USR-CLR-" + timestamp, displayName, "clr_user_" + timestamp,
                "SecurePass@123", "clr" + timestamp + "@example.com", "0771234567", "981234567V");
        userDetailsPage.clickCreateUserButton();

        // Search for a different term to filter out our user
        userDetailsPage.searchUser("NonMatchingQueryKey");
        Assert.assertFalse(userDetailsPage.isUserVisible(displayName), "User should be hidden when filtered out!");

        // Clear search
        userDetailsPage.clearSearch();

        // Verify user is visible again
        Assert.assertTrue(userDetailsPage.isUserVisible(displayName),
                "Clearing search failed: Created user '" + displayName + "' did not reappear after clearing search!");
    }
}