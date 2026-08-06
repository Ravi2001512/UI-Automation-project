package com.ucsc.tutionplatform.tests;

import com.ucsc.tutionplatform.config.ConfigReader;
import com.ucsc.tutionplatform.core.DriverManager;
import com.ucsc.tutionplatform.database.DatabaseHandler;
import com.ucsc.tutionplatform.models.TestData;
import com.ucsc.tutionplatform.utils.JsonHandler;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Root base class for all E2E tests.
 *
 * <p>Lifecycle:
 * <ul>
 *   <li>{@link #openBrowser()} – {@code @BeforeClass}: starts Chrome and navigates to the app;
 *       also performs the initial admin login so concrete tests start from an authenticated state.</li>
 *   <li>{@link #initSoftAssert()} – {@code @BeforeMethod}: gives each test method its own
 *       {@link SoftAssert} instance (thread-safe via {@link ThreadLocal}).</li>
 *   <li>{@link #assertAll()} – {@code @AfterMethod}: flushes all soft assertions and cleans up.</li>
 *   <li>{@link #quitBrowser()} – {@code @AfterClass}: tears the driver down regardless of outcome.</li>
 * </ul>
 *
 * <p>Database helpers ({@link #truncateTables} / {@link #insertCsvDataFromResourceDirectory})
 * are intended to be called from sub-class {@code @BeforeClass} methods to seed test data
 * before any test in that class runs.
 */
public abstract class BaseTest {

    /** Per-thread {@link SoftAssert}; initialised in {@link #initSoftAssert()}. */
    private final ThreadLocal<SoftAssert> softAssert = new ThreadLocal<>();

    /** Path to the JSON assertion file for the concrete module (e.g. {@code AssertDir/user_details.json}). */
    protected final String assertionPath;

    protected BaseTest(String assertionPath) {
        this.assertionPath = assertionPath;
    }

    // =========================================================================
    // Browser lifecycle
    // =========================================================================

    /**
     * Opens Chrome (headless when {@code -Dheadless=true} is passed to Maven),
     * navigates to {@code app.url}, and logs in as the configured admin user.
     */
    @BeforeClass(alwaysRun = true)
    public void openBrowser() {
        boolean headless = Boolean.parseBoolean(
                System.getProperty("headless", "false"));

        ChromeOptions options = buildChromeOptions(headless);
        DriverManager.setDriver(new ChromeDriver(options));

        if (!headless) {
            driver().manage().window().maximize();
        }

        navigateAndLogin();
    }

    @AfterClass(alwaysRun = true)
    public void quitBrowser() {
        DriverManager.quitDriver();
    }

    // =========================================================================
    // SoftAssert lifecycle
    // =========================================================================

    @BeforeMethod(alwaysRun = true)
    public void initSoftAssert() {
        softAssert.set(new SoftAssert());
    }

    @AfterMethod(alwaysRun = true)
    public void assertAll() {
        try {
            getSoftAssert().assertAll();
        } finally {
            softAssert.remove();
        }
    }

    // =========================================================================
    // Protected – WebDriver accessor
    // =========================================================================

    protected WebDriver driver() {
        return DriverManager.getDriver();
    }

    protected SoftAssert getSoftAssert() {
        SoftAssert currentSoftAssert = softAssert.get();

        if (currentSoftAssert == null) {
            throw new IllegalStateException(
                    "SoftAssert is not initialized for the current test method");
        }

        return currentSoftAssert;
    }

    // =========================================================================
    // Protected – database helpers (to be called from sub-class @BeforeClass)
    // =========================================================================

    /**
     * Truncates one or more comma-separated table names.
     * Blank entries are silently skipped.
     *
     * <p>Example: {@code truncateTables("users,sessions,audit_log")}
     */
    protected void truncateTables(String tableNames) {
        if (tableNames == null || tableNames.isBlank()) {
            return;
        }

        for (String tableName : tableNames.split(",")) {
            String trimmedTableName = tableName.trim();

            if (!trimmedTableName.isEmpty()) {
                DatabaseHandler.truncateTable(trimmedTableName);
            }
        }
    }

    /**
     * Walks {@code resourceDirectoryPath} on the test classpath, sorts all
     * {@code .csv} files alphabetically (so FK dependencies are respected),
     * and bulk-inserts each one into the table whose name matches the filename
     * (without the {@code .csv} extension).
     *
     * <p>After each insert the PostgreSQL sequence is reset so subsequent
     * auto-generated IDs do not collide with the seeded rows.
     *
     * @param resourceDirectoryPath classpath-relative path, e.g. {@code "InsertDir/UserDetails"}
     */
    protected void insertCsvDataFromResourceDirectory(String resourceDirectoryPath) {
        URL resourceUrl = BaseTest.class.getClassLoader().getResource(resourceDirectoryPath);

        if (resourceUrl == null) {
            throw new IllegalArgumentException(
                    "Insert resource directory not found: " + resourceDirectoryPath);
        }

        try (Stream<Path> paths = Files.list(Path.of(resourceUrl.toURI()))) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".csv"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .forEach(path -> insertCsvData(resourceDirectoryPath, path));
        } catch (IOException | URISyntaxException exception) {
            throw new IllegalStateException(
                    "Unable to read insert resource directory: " + resourceDirectoryPath, exception);
        }
    }

    // =========================================================================
    // DataProvider
    // =========================================================================

    /**
     * Generic data provider that loads test data from the module's JSON assertion
     * file.  The test-case ID is read from {@link Test#description()}.
     *
     * <p>Usage on a test method:
     * <pre>{@code
     * @Test(description = "TC-001", dataProvider = "commonDataProvider")
     * public void myTest(TestData testData) { ... }
     * }</pre>
     */
    @DataProvider(name = "commonDataProvider")
    @SuppressWarnings("rawtypes")
    public Object[][] commonDataProvider(Method method) {
        String testCaseId = method.getAnnotation(Test.class).description();
        List<TestData> testDataList = JsonHandler.getTestDataListById(assertionPath, testCaseId);
        Object[][] testDataArray = new Object[testDataList.size()][1];

        for (int index = 0; index < testDataList.size(); index++) {
            testDataArray[index][0] = testDataList.get(index);
        }

        return testDataArray;
    }

    // =========================================================================
    // Private helpers
    // =========================================================================

    private static ChromeOptions buildChromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();

        // Suppress Chrome's password-manager prompts
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        options.setExperimentalOption("prefs", prefs);

        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");

        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");
        }

        return options;
    }

    private void navigateAndLogin() {
        driver().get(ConfigReader.getProperty("app.url"));

        com.ucsc.tutionplatform.pages.LoginPage loginPage =
                new com.ucsc.tutionplatform.pages.LoginPage(driver());

        loginPage.login(
                ConfigReader.getProperty("admin.username"),
                ConfigReader.getProperty("admin.password")
        );
    }

    private void insertCsvData(String resourceDirectoryPath, Path csvPath) {
        String fileName  = csvPath.getFileName().toString();
        String tableName = fileName.substring(0, fileName.length() - ".csv".length());
        String resourcePath = resourceDirectoryPath + "/" + fileName;

        DatabaseHandler.bulkInsertFromCsv(tableName, resourcePath);
        DatabaseHandler.resetAutoGeneratedColumns(tableName);
    }
}