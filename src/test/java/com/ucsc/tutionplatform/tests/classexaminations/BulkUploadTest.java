package com.ucsc.tutionplatform.tests.classexaminations;

import org.testng.annotations.Test;

public class BulkUploadTest extends ExaminationBaseTest {

    @Test(description = "Bulk upload exam marks using XLSX")
    public void verifyBulkUpload() {

        // Select Exam
        examinationPage.selectExamForBulkUpload(
                "PCE|||2026|||6788|||Dekma-Matara"
        );

        // Enter Student IDs
        examinationPage.enterStudentIdCriteria(
                "STU-0049 STU-0050"
        );

        // Upload Excel file
        String filePath = System.getProperty("user.dir")
                + "\\src\\test\\resources\\test-data\\Marks.xlsx";

        examinationPage.uploadExcelFile(filePath);

        // Click Upload XLSX
        examinationPage.clickUploadXlsx();
    }
}