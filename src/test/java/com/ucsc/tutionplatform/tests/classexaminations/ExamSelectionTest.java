package com.ucsc.tutionplatform.tests.classexaminations;

import com.ucsc.tutionplatform.tests.classexaminations.data.ExamTestData;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ExamSelectionTest extends ExaminationBaseTest {

    @Test(
            priority = 1,
            dataProvider = "examSearchTerms",
            dataProviderClass = ExamTestData.class,
            description = "TC_SEC_001: Verify typing into the exam snapshot search updates the input field value"
    )
    public void testSearchInputValueUpdatesOnTyping(String searchTerm) {
        examinationPage.searchExamSnapshot(searchTerm);

        Assert.assertEquals(
                examinationPage.getExamSnapshotSearchValue(),
                searchTerm,
                "Search input value did not match the typed search term!"
        );

        examinationPage.clickExamSnapshotClear();
    }

    @Test(
            priority = 2,
            dataProvider = "examSearchTerms",
            dataProviderClass = ExamTestData.class,
            description = "TC_SEC_002: Verify clicking Clear empties the search input field"
    )
    public void testClearButtonClearsSearchInput(String searchTerm) {
        examinationPage.searchExamSnapshot(searchTerm);

        Assert.assertEquals(
                examinationPage.getExamSnapshotSearchValue(),
                searchTerm,
                "Search input value did not match the typed search term!"
        );

        examinationPage.clickExamSnapshotClear();

        Assert.assertEquals(
                examinationPage.getExamSnapshotSearchValue(),
                "",
                "Search input box was not cleared after clicking Clear button!"
        );
    }

    @Test(
            priority = 3,
            dataProvider = "examSearchTerms",
            dataProviderClass = ExamTestData.class,
            description = "TC_SEC_003: Verify clearing search filter restores all exam snapshot cards"
    )
    public void testClearSearchRestoresAllExamCards(String searchTerm) {
        int initialCount = examinationPage.getVisibleExamCardsCount();

        examinationPage.searchExamSnapshot(searchTerm);
        examinationPage.clickExamSnapshotClear();

        int restoredCount = examinationPage.getVisibleExamCardsCount();
        Assert.assertEquals(
                restoredCount,
                initialCount,
                "Exam snapshot list count was not restored after clearing the search!"
        );
    }

    @Test(
            priority = 4,
            dataProvider = "examSearchTerms",
            dataProviderClass = ExamTestData.class,
            description = "TC_SEC_004: Verify searching filters visible cards and clearing resets count"
    )
    public void testSearchFilteringAndClearReset(String searchTerm) {
        int initialCount = examinationPage.getVisibleExamCardsCount();

        examinationPage.searchExamSnapshot(searchTerm);
        examinationPage.waitForAllVisibleExamCardsToContain(searchTerm);

        boolean cardsFiltered = examinationPage.allVisibleExamCardsContain(searchTerm);
        Assert.assertTrue(
                cardsFiltered,
                "Visible exam cards do not match the applied search term: " + searchTerm
        );

        examinationPage.clickExamSnapshotClear();

        Assert.assertEquals(
                examinationPage.getVisibleExamCardsCount(),
                initialCount,
                "Exam card count was not fully restored after clearing!"
        );
    }
}