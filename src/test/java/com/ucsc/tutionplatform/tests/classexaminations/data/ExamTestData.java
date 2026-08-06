package com.ucsc.tutionplatform.tests.classexaminations.data;

import org.testng.annotations.DataProvider;

public class ExamTestData {

    @DataProvider(name = "examSearchTerms")
    public static Object[][] getExamSearchTerms() {
        return new Object[][] {
                { "2030" },
                { "PCE" }
        };
    }
}