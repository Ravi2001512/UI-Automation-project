package com.ucsc.tutionplatform.tests.userdetails;

import com.ucsc.tutionplatform.config.ConfigReader;
import com.ucsc.tutionplatform.config.Constants;
import com.ucsc.tutionplatform.tests.BaseTest;
import org.testng.annotations.BeforeClass;

/**
 * Base class for all User-Details module tests.
 *
 * <p>Extends {@link BaseTest} and adds a {@code @BeforeClass} that:
 * <ol>
 *   <li>Truncates the tables listed in {@code user.details.truncate.tables}
 *       (comma-separated, optional – defaults to empty).</li>
 *   <li>Bulk-inserts every {@code .csv} file found under
 *       {@code InsertDir/UserDetails} on the test classpath, in alphabetical order.</li>
 * </ol>
 *
 * <p>This ensures each test class in the module starts from a known, reproducible
 * database state without manual data management.
 */
public class UserDetailsBaseTest extends BaseTest {

    private static final String USER_DETAILS_TRUNCATE_TABLES = "user.details.truncate.tables";
    private static final String USER_DETAILS_INSERT_DIR      = "InsertDir/UserDetails";

    protected UserDetailsBaseTest() {
        super(Constants.USER_DETAILS_ASSERTION_PATH);
    }

    @BeforeClass(alwaysRun = true)
    public void dataSetup() {
        truncateTables(ConfigReader.getProperty(USER_DETAILS_TRUNCATE_TABLES, ""));
        insertCsvDataFromResourceDirectory(USER_DETAILS_INSERT_DIR);
    }
}
