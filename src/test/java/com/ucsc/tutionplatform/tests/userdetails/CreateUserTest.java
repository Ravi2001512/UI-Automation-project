package com.ucsc.tutionplatform.tests.userdetails;

import com.ucsc.tutionplatform.consts.Constants;
import com.ucsc.tutionplatform.models.TestData;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CreateUserTest extends UserDetailsBaseTest{

    @BeforeClass(alwaysRun = true)
    public void navigateToUserDetailsPage() {
        driver().get(Constants.USER_DETAILS_URL);
    }

    @Test(description = "TC-001", dataProvider = "commonDataProvider")
    public void verifyThatUserCanBeCreatedByProvidingMandatoryFieldsOnly(TestData testData){
        System.out.println(testData.getName());
        getSoftAssert().assertNotNull(testData.getName(), "Name should be available in test data");
    }
}
