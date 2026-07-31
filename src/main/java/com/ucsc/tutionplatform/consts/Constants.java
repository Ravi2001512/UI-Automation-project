package com.ucsc.tutionplatform.consts;

public class Constants {

    public static final String ASSERT_DIR = "AssertDir/";
    public static final String CLASS_EXAMINATIONS_ASSERTION_PATH = ASSERT_DIR + "class_examinations.json";
    public static final String USER_DETAILS_ASSERTION_PATH = ASSERT_DIR + "user_details.json";
    public static final String USER_DETAILS_URL = System.getProperty(
            "user.details.url",
            "http://localhost:3000/user-details"
    );

    private Constants() {
    }
}
