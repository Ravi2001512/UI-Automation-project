package com.ucsc.tutionplatform.config;

public final class Constants {

    /** Classpath-relative prefix for assertion JSON files (forward-slash required). */
    public static final String ASSERT_DIR = "AssertDir/";

    public static final String CLASS_EXAMINATION_ASSERTION_PATH = ASSERT_DIR + "class_examinations.json";
    public static final String USER_DETAILS_ASSERTION_PATH      = ASSERT_DIR + "user_details.json";

    private Constants() {
    }
}
