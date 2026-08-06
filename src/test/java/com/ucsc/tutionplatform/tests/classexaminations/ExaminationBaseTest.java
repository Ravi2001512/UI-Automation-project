package com.ucsc.tutionplatform.tests.classexaminations;

import com.ucsc.tutionplatform.config.ConfigReader;
import com.ucsc.tutionplatform.config.Constants;
import com.ucsc.tutionplatform.pages.ClassExaminationPage;
import com.ucsc.tutionplatform.tests.BaseTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

/**
 * Base class for all Class-Examination module tests.
 *
 * <p>Extends {@link BaseTest} and adds:
 * <ul>
 *   <li>A {@code @BeforeClass} that truncates and re-seeds the examination tables.</li>
 *   <li>A {@code @BeforeMethod} that navigates to the Class Examination tab so
 *       each test starts from the correct UI state.</li>
 * </ul>
 */
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
        examinationPage = new ClassExaminationPage(driver());
        examinationPage.navigateToClassExaminationTab();
    }
}