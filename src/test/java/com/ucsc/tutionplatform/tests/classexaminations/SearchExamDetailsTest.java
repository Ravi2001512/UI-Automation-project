package com.ucsc.tutionplatform.tests.classexaminations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.InputStream;

public class SearchExamDetailsTest extends ExaminationBaseTest {

        // Helper method to load the JSON test cases array
        private JsonNode getTestCasesJson() throws Exception {
                try (InputStream stream = getClass().getClassLoader().getResourceAsStream(assertionPath)) {
                        JsonNode root = new ObjectMapper().readTree(stream);
                        return root.path("testCases").path("TC-EXAM-SEARCH-001");
                }
        }

        // DataProvider for positive search terms
        @DataProvider(name = "positiveSearchTermsFromJson")
        public Object[][] positiveSearchTermsFromJson() throws Exception {
                JsonNode searchCases = getTestCasesJson();
                Object[][] data = new Object[searchCases.size()][2];

                for (int i = 0; i < searchCases.size(); i++) {
                        data[i][0] = searchCases.get(i).path("caseName").asText();
                        data[i][1] = searchCases.get(i).path("searchText").asText();
                }
                return data;
        }

        @Test(description = "Verify initial empty search state")
        public void verifyEmptySearchShowsCompleteListAndDisabledClearButton() {
                int totalCards = examinationPage.getVisibleExamCardCount();

                Assert.assertFalse(examinationPage.getExamSnapshotSearchPlaceholder().isEmpty());
                Assert.assertTrue(examinationPage.isExamSnapshotClearButtonDisabled());
                Assert.assertEquals(examinationPage.getVisibleExamCardCount(), totalCards);
        }

        @Test(description = "Verify single character search filtering")
        public void verifyOneCharacterSearchFiltersResults() throws Exception {
                // Gets first search text from JSON (e.g. "First Term Trial") and takes 1st letter ("F")
                String firstLetter = getTestCasesJson().get(0).path("searchText").asText().substring(0, 1);

                examinationPage.enterExamSnapshotSearch(firstLetter);
                examinationPage.waitForAllVisibleExamCardsToContain(firstLetter);

                Assert.assertTrue(examinationPage.getVisibleExamCardCount() > 0);
                Assert.assertTrue(examinationPage.allVisibleExamCardsContain(firstLetter));
                Assert.assertTrue(examinationPage.isExamSnapshotClearButtonEnabled());
        }

        @Test(description = "Verify unmatched long search returns zero results")
        public void verifyVeryLongSearchTextReturnsNoResults() {
                String longText = "NO_MATCH_SEARCH_TERM_" + System.currentTimeMillis();

                examinationPage.enterExamSnapshotSearch(longText);
                examinationPage.waitForNoVisibleExamCards();

                Assert.assertEquals(examinationPage.getVisibleExamCardCount(), 0);
                Assert.assertTrue(examinationPage.isExamSnapshotClearButtonEnabled());
        }

        @Test(description = "Verify clear button resets the search field and cards")
        public void verifyClearRestoresCompleteList() throws Exception {
                String searchTerm = getTestCasesJson().get(0).path("searchText").asText();
                int totalCards = examinationPage.getVisibleExamCardCount();

                examinationPage.enterExamSnapshotSearch(searchTerm);
                examinationPage.waitForAllVisibleExamCardsToContain(searchTerm);

                examinationPage.clearExamSnapshotSearch();
                examinationPage.waitForAtLeastVisibleExamCardCount(totalCards);

                Assert.assertTrue(examinationPage.getVisibleExamCardCount() >= totalCards);
                Assert.assertTrue(examinationPage.isExamSnapshotClearButtonDisabled());
                Assert.assertEquals(examinationPage.getExamSnapshotSearchValue(), "");
        }

        @Test(dataProvider = "positiveSearchTermsFromJson", description = "Verify data-driven search terms")
        public void verifyPositiveSearchFiltering(String caseName, String searchText) {
                examinationPage.enterExamSnapshotSearch(searchText);
                examinationPage.waitForAllVisibleExamCardsToContain(searchText);

                Assert.assertTrue(examinationPage.getVisibleExamCardCount() > 0);
                Assert.assertTrue(examinationPage.allVisibleExamCardsContain(searchText));
                Assert.assertTrue(examinationPage.isExamSnapshotClearButtonEnabled());
        }
}