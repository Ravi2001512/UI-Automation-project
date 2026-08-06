package com.ucsc.tutionplatform.tests.classexaminations;

import com.ucsc.tutionplatform.pages.IndividualMarkUploadPage;
import com.ucsc.tutionplatform.utils.JsonHandler;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Map;

public class SingleUploadTest extends ExaminationBaseTest {

    @Test(
            description = "TC-095 - Save valid mark for selected student and exam"
    )
    public void saveValidMarkForSelectedStudentAndExam() {

        Map<String, String> testData =
                JsonHandler.getTestData(
                        "AssertDir/single_upload.json",
                        "TC-095"
                );

        String examValue = testData.get("examValue");
        String studentId = testData.get("studentId");
        String mark = testData.get("mark");

        IndividualMarkUploadPage page =
                new IndividualMarkUploadPage(driver());

        page.saveIndividualMark(
                examValue,
                studentId,
                mark
        );

        Assert.assertTrue(
                page.isSavedMarkDisplayedForStudent(studentId),
                "Student was not displayed in Saved Marks."
        );

        BigDecimal expectedMark =
                new BigDecimal(mark);

        BigDecimal actualMark =
                page.waitForSavedMarkUpdate(
                        studentId,
                        mark
                );

        Assert.assertEquals(
                actualMark.compareTo(expectedMark),
                0,
                "Saved mark is different from the entered mark. "
                        + "Expected mark: " + expectedMark
                        + ", Actual mark: " + actualMark
        );
    }

    @Test(
            description = "TC-096 - Verify mark greater than 100 cannot be saved"
    )
    public void shouldRejectMarkGreaterThanOneHundred() {

        Map<String, String> testData =
                JsonHandler.getTestData(
                        "AssertDir/single_upload.json",
                        "TC-096"
                );

        String examValue = testData.get("examValue");
        String studentId = testData.get("studentId");
        String invalidMark = testData.get("mark");

        IndividualMarkUploadPage page =
                new IndividualMarkUploadPage(driver());

        page.waitUntilDisplayed();
        page.selectExamByValue(examValue);
        page.searchStudent(studentId);
        page.selectStudent(studentId);
        page.enterMark(invalidMark);

        Assert.assertFalse(
                page.isMarkInputValid(),
                "The mark input should be invalid for a value greater than 100."
        );

        String validationMessage =
                page.getMarkValidationMessage();

        Assert.assertFalse(
                validationMessage.isBlank(),
                "A validation message should appear for a mark greater than 100."
        );

        System.out.println(
                "Validation message: " + validationMessage
        );

        page.clickSaveMark();

        Assert.assertFalse(
                page.isSpecificMarkDisplayed(
                        studentId,
                        invalidMark
                ),
                "Invalid mark 101 should not be saved."
        );
    }

    @Test(
            description = "TC-097 - Verify negative mark cannot be saved"
    )
    public void shouldRejectNegativeMark() {

        Map<String, String> testData =
                JsonHandler.getTestData(
                        "AssertDir/single_upload.json",
                        "TC-097"
                );

        String examValue = testData.get("examValue");
        String studentId = testData.get("studentId");
        String invalidMark = testData.get("mark");

        IndividualMarkUploadPage page =
                new IndividualMarkUploadPage(driver());

        page.waitUntilDisplayed();
        page.selectExamByValue(examValue);
        page.searchStudent(studentId);
        page.selectStudent(studentId);
        page.enterMark(invalidMark);

        Assert.assertFalse(
                page.isMarkInputValid(),
                "Mark input should be invalid when the value is below 0."
        );

        String validationMessage =
                page.getMarkValidationMessage();

        Assert.assertFalse(
                validationMessage.isBlank(),
                "A browser validation message should appear for a negative mark."
        );

        Assert.assertTrue(
                validationMessage.toLowerCase()
                        .contains("greater than or equal to 0"),
                "Unexpected validation message: " + validationMessage
        );

        System.out.println(
                "Validation message: " + validationMessage
        );

        page.clickSaveMark();

        Assert.assertFalse(
                page.isSpecificMarkDisplayed(
                        studentId,
                        invalidMark
                ),
                "Negative mark " + invalidMark + " should not be saved."
        );
    }
}