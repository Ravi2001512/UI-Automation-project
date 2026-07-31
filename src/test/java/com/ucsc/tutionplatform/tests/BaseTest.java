package com.ucsc.tutionplatform.tests;

import com.ucsc.tutionplatform.core.DriverManager;
import com.ucsc.tutionplatform.models.TestData;
import com.ucsc.tutionplatform.utils.JsonHandler;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.lang.reflect.Method;
import java.util.List;

public class BaseTest {

    private final ThreadLocal<SoftAssert> softAssert = new ThreadLocal<>();
    protected final String assertionPath;

    protected BaseTest(String assertionPath) {
        this.assertionPath = assertionPath;
    }

    @BeforeClass(alwaysRun = true)
    public void openBrowser() {
        DriverManager.setDriver(new ChromeDriver());
    }

    @AfterClass(alwaysRun = true)
    public void quitBrowser() {
        DriverManager.quitDriver();
    }

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

    protected WebDriver driver() {
        return DriverManager.getDriver();
    }

    protected SoftAssert getSoftAssert() {
        SoftAssert currentSoftAssert = softAssert.get();

        if (currentSoftAssert == null) {
            throw new IllegalStateException("SoftAssert is not initialized for the current test method");
        }

        return currentSoftAssert;
    }

    @DataProvider
    public Object[][] commonDataProvider(Method method){
        String testCaseId = method.getAnnotation(Test.class).description();
        List<TestData> testDataList = JsonHandler.getTestDataListById(assertionPath, testCaseId);
        Object[][] testDataArray = new Object[testDataList.size()][1];

        for (int index = 0; index < testDataList.size(); index++) {
            testDataArray[index][0] = testDataList.get(index);
        }

        return testDataArray;
    }
}
