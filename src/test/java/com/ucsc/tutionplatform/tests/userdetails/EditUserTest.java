package com.ucsc.tutionplatform.tests.userdetails;

import com.ucsc.tutionplatform.models.EditUserTestData;
import com.ucsc.tutionplatform.pages.EditUserPage;
import com.ucsc.tutionplatform.utils.EditUserJsonHandler;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EditUserTest
        extends UserDetailsBaseTest {

    @Test(description = "TC-EDIT-001")
    public void verifyUserDisplayNameCanBeUpdated() {

        EditUserTestData testData =
                EditUserJsonHandler.getTestData(
                        "AssertDir/edit_user.json",
                        "TC-EDIT-001"
                );

        EditUserPage editUserPage =
                new EditUserPage(driver());

        // 1. Search existing user.
        editUserPage.searchUser(
                testData.getUserId()
        );

        Assert.assertTrue(
                editUserPage.isUserDisplayed(
                        testData.getUserId()
                ),
                "User was not found: "
                        + testData.getUserId()
        );

        // 2. Select result.
        editUserPage.selectUser(
                testData.getUserId()
        );

        // 3. Verify correct User ID loaded.
        Assert.assertEquals(
                editUserPage.getEditingUserId(),
                testData.getUserId(),
                "Incorrect user loaded in Edit Mode."
        );

        // 4. Enter updated display name.
        editUserPage.enterNewDisplayName(
                testData.getUpdatedDisplayName()
        );

        // 5. Update.
        editUserPage.clickUpdateUser();

        // 6. Search again.
        editUserPage.searchUser(
                testData.getUserId()
        );

        // 7. Wait for UI update.
        editUserPage.waitForUpdatedDisplayName(
                testData.getUserId(),
                testData.getUpdatedDisplayName()
        );

        // 8. Verify.
        Assert.assertEquals(
                editUserPage.getDisplayNameFromResult(
                        testData.getUserId()
                ),
                testData.getUpdatedDisplayName(),
                "Display Name was not updated correctly."
        );
    }
}