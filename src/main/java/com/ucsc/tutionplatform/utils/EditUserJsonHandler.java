package com.ucsc.tutionplatform.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucsc.tutionplatform.models.EditUserTestData;

import java.io.InputStream;

public final class EditUserJsonHandler {

    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper();

    private EditUserJsonHandler() {
    }

    public static EditUserTestData getTestData(
            String resourcePath,
            String testCaseName
    ) {

        try (
                InputStream inputStream =
                        EditUserJsonHandler.class
                                .getClassLoader()
                                .getResourceAsStream(
                                        resourcePath
                                )
        ) {

            if (inputStream == null) {

                throw new IllegalArgumentException(
                        "JSON file not found: "
                                + resourcePath
                );
            }

            JsonNode root =
                    OBJECT_MAPPER.readTree(
                            inputStream
                    );

            for (JsonNode testCase : root) {

                String name =
                        testCase.path("name")
                                .asText();

                if (!testCaseName.equals(name)) {
                    continue;
                }

                JsonNode dataArray =
                        testCase.path("data");

                if (!dataArray.isArray()
                        || dataArray.isEmpty()) {

                    throw new IllegalArgumentException(
                            "No data found for: "
                                    + testCaseName
                    );
                }

                return OBJECT_MAPPER.treeToValue(
                        dataArray.get(0),
                        EditUserTestData.class
                );
            }

            throw new IllegalArgumentException(
                    "Test case not found: "
                            + testCaseName
            );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Unable to load Edit User test data",
                    exception
            );
        }
    }
}