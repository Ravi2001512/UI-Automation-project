package com.ucsc.tutionplatform.tests.userdetails;

import com.ucsc.tutionplatform.consts.Constants;
import com.ucsc.tutionplatform.tests.BaseTest;
import com.ucsc.tutionplatform.utils.ConfigReader;
import org.testng.annotations.BeforeClass;

public class UserDetailsBaseTest extends BaseTest {

    private static final String USER_DETAILS_TRUNCATE_TABLES = "user.details.truncate.tables";
    private static final String USER_DETAILS_INSERT_DIR = "InsertDir/UserDetails";

    public UserDetailsBaseTest() {
        super(Constants.USER_DETAILS_ASSERTION_PATH);
    }

    @BeforeClass(alwaysRun = true)
    public void dataSetup() {
        truncateTables(ConfigReader.getProperty(USER_DETAILS_TRUNCATE_TABLES, ""));
        insertCsvDataFromResourceDirectory(USER_DETAILS_INSERT_DIR);
    }
}
