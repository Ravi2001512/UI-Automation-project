package com.ucsc.tutionplatform.tests.classexaminations;

import com.ucsc.tutionplatform.config.ConfigReader;
import com.ucsc.tutionplatform.config.Constants;
import com.ucsc.tutionplatform.pages.ClassExaminationPage;
import com.ucsc.tutionplatform.tests.BaseTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

public abstract class ExaminationBaseTest extends BaseTest {

    private static final String EXAMINATION_TRUNCATE_TABLES = "examination.truncate.tables";
    private static final String EXAMINATION_INSERT_DIR      = "InsertDir/ClassExaminations";

    protected ClassExaminationPage examinationPage;

    public ExaminationBaseTest() {
        super(Constants.CLASS_EXAMINATION_ASSERTION_PATH);
    }

    @BeforeClass(alwaysRun = true)
    public void dataSetup() {
        truncateTables(ConfigReader.getProperty(EXAMINATION_TRUNCATE_TABLES, ""));
        insertCsvDataFromResourceDirectory(EXAMINATION_INSERT_DIR);
    }

    @BeforeMethod(alwaysRun = true)
    public void setupExaminationContext() {
        try {
            examinationPage = new ClassExaminationPage(driver());
            examinationPage.navigateToClassExaminationTab();
        } catch (Exception e) {
            System.err.println("Session invalid or navigation failed in setupExaminationContext. Re-authenticating... " + e.getMessage());
            navigateAndLogin();
            examinationPage = new ClassExaminationPage(driver());
            examinationPage.navigateToClassExaminationTab();
        }
    }
}