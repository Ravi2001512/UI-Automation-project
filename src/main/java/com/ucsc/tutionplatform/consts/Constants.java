package com.ucsc.tutionplatform.consts;

import com.ucsc.tutionplatform.utils.ConfigReader;

public class Constants {

    public static final String ASSERT_DIR = "AssertDir/";
    public static final String CLASS_EXAMINATIONS_ASSERTION_PATH = ASSERT_DIR + "class_examinations.json";
    public static final String USER_DETAILS_ASSERTION_PATH = ASSERT_DIR + "user_details.json";
    public static final String USER_DETAILS_URL = ConfigReader.getProperty(
            "user.default.url",
            "http://localhost:3000/user-details"
    );

    private Constants() {
    }
}
