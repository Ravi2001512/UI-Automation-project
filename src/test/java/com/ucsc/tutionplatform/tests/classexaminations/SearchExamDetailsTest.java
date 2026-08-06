package com.ucsc.tutionplatform.tests.classexaminations;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.StringJoiner;

public class SearchExamDetailsTest extends ExaminationBaseTest {

        private static final String SEARCH_PLACEHOLDER = "Search by exam title, type, A/L year, exam number, location, or grade band";

        @DataProvider(name = "positiveSearchTerms")
        public Object[][] positiveSearchTerms() {
                return new Object[][] {
                                { "complete exam title", "First Term Trial" },
                                { "partial exam title", "Trial" },
                                { "exam type", "PCE" },
                                { "al year", "2026" },
                                { "exam number", "1" },
                                { "location", "Sipta-Tangalle" },
                                { "grade marks", "75" },
                                { "lowercase", "pce" },
                                { "uppercase", "PCE" },
                                { "mixed case", "pCe" }
                };
        }


        @Test(description = "Verify the search box placeholder, initial state, and clear button disabled state")
        public void verifyEmptySearchShowsCompleteListAndDisabledClearButton() {
                int totalExamCards = examinationPage.getVisibleExamCardCount();

                Assert.assertEquals(
                                examinationPage.getExamSnapshotSearchPlaceholder(),
                                SEARCH_PLACEHOLDER,
                                "The search placeholder text is incorrect.");
                Assert.assertTrue(
                                examinationPage.isExamSnapshotClearButtonDisabled(),
                                "Clear button must be disabled before any search text is entered.");
                Assert.assertEquals(
                                examinationPage.getVisibleExamCardCount(),
                                totalExamCards,
                                "All exam cards should be visible when the search input is empty.");
        }

        @Test(description = "Verify search results filter as the user types a single character")
        public void verifyOneCharacterSearchFiltersResults() {
                examinationPage.enterExamSnapshotSearch("2");
                examinationPage.waitForAllVisibleExamCardsToContain("2");

                Assert.assertTrue(
                                examinationPage.getVisibleExamCardCount() > 0,
                                "Expected at least one exam card to match a one-character search.");
                Assert.assertTrue(
                                examinationPage.allVisibleExamCardsContain("2"),
                                "Every displayed exam card must contain the typed search text.");
                Assert.assertTrue(
                                examinationPage.isExamSnapshotClearButtonEnabled(),
                                "Clear button must become enabled after typing search text.");
        }

        @Test(description = "Verify very long search text returns no matching exam cards")
        public void verifyVeryLongSearchTextReturnsNoResults() {
                String veryLongText = buildVeryLongText();

                examinationPage.enterExamSnapshotSearch(veryLongText);
                examinationPage.waitForNoVisibleExamCards();

                Assert.assertEquals(
                                examinationPage.getVisibleExamCardCount(),
                                0,
                                "Very long unmatched text should return zero exam cards.");
                Assert.assertTrue(
                                examinationPage.isExamSnapshotClearButtonEnabled(),
                                "Clear button must remain enabled when the search input has text.");
        }

        @Test(description = "Verify clicking Clear empties the field, disables the button, and restores all exam cards")
        public void verifyClearRestoresCompleteList() {
                int totalExamCards = examinationPage.getVisibleExamCardCount();

                examinationPage.enterExamSnapshotSearch("2026");
                examinationPage.waitForAllVisibleExamCardsToContain("2026");
                Assert.assertTrue(
                                examinationPage.isExamSnapshotClearButtonEnabled(),
                                "Clear button must be enabled after entering search text.");

                examinationPage.clearExamSnapshotSearch();
                examinationPage.waitForAtLeastVisibleExamCardCount(totalExamCards);

                Assert.assertTrue(
                                examinationPage.getVisibleExamCardCount() >= totalExamCards,
                                "Clicking Clear should restore the full exam list.");
                Assert.assertTrue(
                                examinationPage.isExamSnapshotClearButtonDisabled(),
                                "Clear button must be disabled again after the search box is cleared.");
                Assert.assertEquals(
                                examinationPage.getExamSnapshotSearchValue(),
                                "",
                                "Clicking Clear should empty the search textbox.");
        }

        @Test(dataProvider = "positiveSearchTerms", description = "Verify search filters exam cards for valid search values")
        public void verifyPositiveSearchFiltering(String caseName, String searchText) {
                examinationPage.enterExamSnapshotSearch(searchText);
                examinationPage.waitForAllVisibleExamCardsToContain(searchText);

                Assert.assertTrue(
                                examinationPage.getVisibleExamCardCount() > 0,
                                "Expected matching exam cards for " + caseName + " search.");
                Assert.assertTrue(
                                examinationPage.allVisibleExamCardsContain(searchText),
                                "Every displayed exam card must contain the search text for " + caseName + ".");
                Assert.assertTrue(
                                examinationPage.isExamSnapshotClearButtonEnabled(),
                                "Clear button must be enabled after typing a valid search value.");
        }


        private String buildVeryLongText() {
                StringJoiner joiner = new StringJoiner("");
                for (int index = 0; index < 40; index++) {
                        joiner.add("VeryLongSearchTerm");
                }
                return joiner.toString();
        }
}
