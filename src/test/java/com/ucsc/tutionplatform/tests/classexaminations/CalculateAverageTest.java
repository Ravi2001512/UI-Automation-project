package com.ucsc.tutionplatform.tests.classexaminations;

import org.testng.Assert;
import org.testng.annotations.Test;

public class CalculateAverageTest extends ExaminationBaseTest {

    @Test(priority = 1, description = "Verify 'Select Visible' button initial UI state")
    public void testStudentSelectVisibleButtonInitialState() {
        Assert.assertTrue(
                examinationPage.isStudentSelectVisibleBtnDisplayed(),
                "'Select Visible' button in Student Selection card is not visible!"
        );
        Assert.assertFalse(
                examinationPage.isStudentSelectVisibleBtnEnabled(),
                "'Select Visible' button should be disabled prior to running a search query!"
        );
    }

    @Test(priority = 2, description = "Verify UI kicker labels and card headings")
    public void testVisibleTextAndLabels() {
        Assert.assertTrue(
                examinationPage.isExamAveragesPanelDisplayed(),
                "'Exam Averages' panel section is not rendered!"
        );

        Assert.assertTrue(
                examinationPage.getStudentSelectionCardTitle().equalsIgnoreCase("Student Selection"),
                "Card kicker title text mismatch! Expected 'Student Selection' (case-insensitive)."
        );

        Assert.assertEquals(
                examinationPage.getCalculateAveragesBtnText(),
                "Calculate Averages",
                "Action button text mismatch!"
        );
    }

    @Test(priority = 3, description = "Verify calculated average marks for selected students and exams")
    public void testCalculateAverageMark() {
        examinationPage.selectAllVisibleStudents();
        examinationPage.selectAllVisibleExams();
        examinationPage.clickCalculateAverages();

        Assert.assertTrue(
                examinationPage.isAverageResultsDisplayed(),
                "Average marks list was not calculated or displayed on screen!"
        );
    }
}