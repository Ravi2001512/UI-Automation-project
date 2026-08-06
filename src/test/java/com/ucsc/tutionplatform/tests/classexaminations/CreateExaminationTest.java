package com.ucsc.tutionplatform.tests.classexaminations;

import org.testng.annotations.Test;

public class CreateExaminationTest extends ExaminationBaseTest {

    @Test
    public void testCreateExamination() {
        long timestamp = System.currentTimeMillis();
        String alYear = String.valueOf(2025 + (timestamp % 5));
        String examNumber = String.valueOf(timestamp % 1000);
        String examTitle = "Term Trial " + timestamp;

        examinationPage.selectExamType("PCE");
        examinationPage.enterAlYear(alYear);
        examinationPage.enterExamNumber(examNumber);
        examinationPage.selectExamLocation("Dekma-Matara");
        examinationPage.enterExamTitle(examTitle);


        examinationPage.enterGradeDetails(1, "A", "100", "75");

        examinationPage.clickAddGrade();
        examinationPage.enterGradeDetails(2, "B", "74", "65");

        examinationPage.clickAddGrade();
        examinationPage.enterGradeDetails(3, "C", "64", "50");

        examinationPage.clickAddGrade();
        examinationPage.enterGradeDetails(4, "S", "49", "35");

        examinationPage.clickAddGrade();
        examinationPage.enterGradeDetails(5, "W", "34", "0");

        examinationPage.clickCreateExam();
    }

    @Test
    public void testCreateExaminationWithPassFailGrades() {
        long timestamp = System.currentTimeMillis();
        String examTitle = "Revision Pass-Fail Test " + timestamp;

        // Using RCE type as seen in the HTML snippet
        examinationPage.selectExamType("RCE");
        examinationPage.enterAlYear("2026");
        examinationPage.enterExamNumber(String.valueOf(timestamp % 500));
        examinationPage.selectExamLocation("Dekma-Matara"); // Adjust location as needed
        examinationPage.enterExamTitle(examTitle);

        // Testing form functionality with only two custom grade bands
        examinationPage.enterGradeDetails(1, "Pass", "100", "50");

        examinationPage.clickAddGrade();
        examinationPage.enterGradeDetails(2, "Fail", "49", "0");

        examinationPage.clickCreateExam();
    }

    @Test
    public void testCreateExaminationWithSingleGradeBand() {
        long timestamp = System.currentTimeMillis();
        String examTitle = "Participation Assessment " + timestamp;

        // Using ACE type
        examinationPage.selectExamType("ACE");
        examinationPage.enterAlYear("2027");
        examinationPage.enterExamNumber(String.valueOf(timestamp % 100));
        examinationPage.selectExamLocation("Sipta-Tangalle");
        examinationPage.enterExamTitle(examTitle);

        // Testing the edge case of an exam having only 1 grade boundary (e.g. 0 to 100)
        examinationPage.enterGradeDetails(1, "Completed", "100", "0");

        examinationPage.clickCreateExam();
    }

    @Test
    public void testCreateExaminationWithAlternativeGradingScale() {
        long timestamp = System.currentTimeMillis();
        String examTitle = "TCE Model Paper " + timestamp;

        // Using TCE type
        examinationPage.selectExamType("TCE");
        examinationPage.enterAlYear("2024");
        examinationPage.enterExamNumber(String.valueOf(timestamp % 999));
        examinationPage.selectExamLocation("Sipta-Tangalle");
        examinationPage.enterExamTitle(examTitle);

        // Testing alternative 4-tier grading logic
        examinationPage.enterGradeDetails(1, "Distinction", "100", "75");

        examinationPage.clickAddGrade();
        examinationPage.enterGradeDetails(2, "Merit", "74", "65");

        examinationPage.clickAddGrade();
        examinationPage.enterGradeDetails(3, "Credit", "64", "45");

        examinationPage.clickAddGrade();
        examinationPage.enterGradeDetails(4, "Fail", "44", "0");

        examinationPage.clickCreateExam();
    }
}