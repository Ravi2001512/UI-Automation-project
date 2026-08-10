package com.ucsc.tutionplatform.tests;

import com.ucsc.tutionplatform.config.ConfigReader;
import com.ucsc.tutionplatform.core.DriverManager;
import com.ucsc.tutionplatform.database.DatabaseHandler;
import com.ucsc.tutionplatform.models.TestData;
import com.ucsc.tutionplatform.utils.JsonHandler;
import org.openqa.selenium.WebDriver;
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
import java.util.List;
import java.util.stream.Stream;

public abstract class BaseTest {

    private final ThreadLocal<SoftAssert> softAssert = new ThreadLocal<>();
    protected final String assertionPath;

    protected BaseTest(String assertionPath) {
        this.assertionPath = assertionPath;
    }

    @BeforeClass(alwaysRun = true)
    public void openBrowser() {
        try {
            DriverManager.openBrowser();
            navigateAndLogin();
        } catch (Exception e) {
            System.err.println("Failed during browser startup/login in @BeforeClass. Retrying initialization...");
            DriverManager.quitDriver();
            DriverManager.openBrowser();
            navigateAndLogin();
        }
    }

    @AfterClass(alwaysRun = true)
    public void quitBrowser() {
        try {
            DriverManager.quitDriver();
        } catch (Exception e) {
            System.err.println("Error while quitting driver in @AfterClass: " + e.getMessage());
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void initSoftAssert() {
        softAssert.set(new SoftAssert());
    }

    @AfterMethod(alwaysRun = true)
    public void assertAll() {
        try {
            if (getSoftAssert() != null) {
                getSoftAssert().assertAll();
            }
        } finally {
            softAssert.remove();
        }
    }

    protected WebDriver driver() {
        WebDriver currentDriver = DriverManager.getDriver();
        if (currentDriver == null) {
            DriverManager.openBrowser();
            navigateAndLogin();
            return DriverManager.getDriver();
        }
        return currentDriver;
    }

    protected SoftAssert getSoftAssert() {
        SoftAssert currentSoftAssert = softAssert.get();
        if (currentSoftAssert == null) {
            throw new IllegalStateException("SoftAssert is not initialized for the current test method");
        }
        return currentSoftAssert;
    }

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

    protected void insertCsvDataFromResourceDirectory(String resourceDirectoryPath) {
        URL resourceUrl = BaseTest.class.getClassLoader().getResource(resourceDirectoryPath);

        if (resourceUrl == null) {
            throw new IllegalArgumentException("Insert resource directory not found: " + resourceDirectoryPath);
        }

        try (Stream<Path> paths = Files.list(Path.of(resourceUrl.toURI()))) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".csv"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .forEach(path -> insertCsvData(resourceDirectoryPath, path));
        } catch (IOException | URISyntaxException exception) {
            throw new IllegalStateException("Unable to read insert resource directory: " + resourceDirectoryPath, exception);
        }
    }

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

    protected void navigateAndLogin() {
        WebDriver driver = DriverManager.getDriver();
        driver.get(ConfigReader.getProperty("app.url"));

        com.ucsc.tutionplatform.pages.LoginPage loginPage =
                new com.ucsc.tutionplatform.pages.LoginPage(driver);

        loginPage.login(
                ConfigReader.getProperty("admin.username"),
                ConfigReader.getProperty("admin.password")
        );
    }

    private void insertCsvData(String resourceDirectoryPath, Path csvPath) {
        String fileName = csvPath.getFileName().toString();
        String tableName = fileName.substring(0, fileName.length() - ".csv".length());
        String resourcePath = resourceDirectoryPath + "/" + fileName;

        DatabaseHandler.bulkInsertFromCsv(tableName, resourcePath);
        DatabaseHandler.resetAutoGeneratedColumns(tableName);
    }
}