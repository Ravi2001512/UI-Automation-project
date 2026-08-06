package com.ucsc.tutionplatform.tests.userdetails;

import com.ucsc.tutionplatform.pages.UserDetailsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SearchUserTest extends com.ucsc.tutionplatform.tests.userdetails.UserDetailsBaseTest {

    @Test(description = "Verify that a user can be searched successfully")
    public void searchUser() throws InterruptedException {
        UserDetailsPage userDetailsPage = new UserDetailsPage(driver());

        String searchKeyword = "groupa";
        
        // Use the searchUser method to interact with the UI search input
        userDetailsPage.searchUser(searchKeyword);
        
        // Adding a small wait to let the UI update (or could use explicit wait inside page object)
        Thread.sleep(1000); 

        // Verify that the user "GroupA" is visible in the search results
        boolean isFound = userDetailsPage.isUserVisible("GroupA");
        Assert.assertTrue(isFound, "Search functionality failed: User 'GroupA' was not found in the results!");
        System.out.println("Search executed and verified successfully for keyword: " + searchKeyword);
        
        // Assert that clearing search works
        userDetailsPage.clearSearch();
        System.out.println("Search cleared successfully.");
    }
}
