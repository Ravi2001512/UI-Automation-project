package com.ucsc.tutionplatform.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ucsc.tutionplatform.models.TestData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility for reading test data from JSON assertion files on the classpath.
 *
 * <p>Expected JSON structure:
 * <pre>{@code
 * [
 *   {
 *     "tc_id": "TC-001",
 *     "data": [
 *       { "name": "Alice", "data": { ... } },
 *       { "name": "Bob",   "data": { ... } }
 *     ]
 *   }
 * ]
 * }</pre>
 *
 * <p>Both {@code "tc_id"} and {@code "test_id"} are accepted as the test-case
 * identifier field, for backwards compatibility.
 */
public final class JsonHandler {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String TEST_ID = "test_id";
    private static final String TC_ID   = "tc_id";
    private static final String DATA    = "data";

    private JsonHandler() {
    }

    // =========================================================================
    // Public API
    // =========================================================================

    /**
     * Parses the JSON file at {@code fileName} on the classpath and returns
     * the root {@link JsonNode}.
     *
     * @throws IllegalArgumentException if the file is not found
     * @throws IllegalStateException    if the file cannot be read
     */
    public static JsonNode readJson(String fileName) {
        ClassLoader classLoader = JsonHandler.class.getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("JSON file not found in resources: " + fileName);
            }

            return OBJECT_MAPPER.readTree(inputStream);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read JSON file: " + fileName, exception);
        }
    }

    /**
     * Finds the first element in the root array whose {@code tc_id} / {@code test_id}
     * matches {@code testCaseId}.
     *
     * @throws IllegalArgumentException if no matching entry is found
     */
    public static JsonNode getTestCaseById(String fileName, String testCaseId) {
        JsonNode rootNode = readJson(fileName);

        if (!rootNode.isArray()) {
            throw new IllegalArgumentException(
                    "Expected JSON root to be an array in file: " + fileName);
        }

        for (JsonNode testCaseNode : rootNode) {
            JsonNode testIdNode = resolveTestIdNode(testCaseNode);

            if (testIdNode != null && testCaseId.equals(testIdNode.asText())) {
                return testCaseNode;
            }
        }

        throw new IllegalArgumentException("Test case id not found: " + testCaseId);
    }

    /**
     * Returns the {@code "data"} array node for the given test-case ID.
     *
     * @throws IllegalArgumentException if the {@code "data"} field is absent
     */
    public static JsonNode getTestDataById(String fileName, String testCaseId) {
        JsonNode testCaseNode = getTestCaseById(fileName, testCaseId);
        JsonNode dataNode     = testCaseNode.get(DATA);

        if (dataNode == null) {
            throw new IllegalArgumentException("Data not found for test case id: " + testCaseId);
        }

        return dataNode;
    }

    /**
     * Returns a typed list of {@link TestData} objects for the given test-case ID.
     * Each element in the {@code "data"} array is deserialised via Jackson.
     *
     * @throws IllegalArgumentException if {@code "data"} is not a JSON array
     */
    public static List<TestData> getTestDataListById(String fileName, String testCaseId) {
        JsonNode dataNode = getTestDataById(fileName, testCaseId);

        if (!dataNode.isArray()) {
            throw new IllegalArgumentException(
                    "Expected data to be an array for test case id: " + testCaseId);
        }

        List<TestData> testDataList = new ArrayList<>();

        for (JsonNode testDataNode : dataNode) {
            testDataList.add(mapTestData(testDataNode, testCaseId));
        }

        return testDataList;
    }

    // =========================================================================
    // Private helpers
    // =========================================================================

    @SuppressWarnings("rawtypes")
    private static TestData mapTestData(JsonNode testDataNode, String testCaseId) {
        try {
            return OBJECT_MAPPER.treeToValue(testDataNode, TestData.class);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "Unable to map test data for test case id: " + testCaseId, exception);
        }
    }

    /**
     * Accepts both {@code "tc_id"} and {@code "test_id"} as the identifier field.
     */
    private static JsonNode resolveTestIdNode(JsonNode testCaseNode) {
        JsonNode testIdNode = testCaseNode.get(TC_ID);

        if (testIdNode == null) {
            testIdNode = testCaseNode.get(TEST_ID);
        }

        return testIdNode;
    }

    public static Map<String, String> getTestData(
            String resourcePath,
            String testCaseName
    ) {
        InputStream inputStream = JsonHandler.class
                .getClassLoader()
                .getResourceAsStream(resourcePath);

        if (inputStream == null) {
            throw new IllegalArgumentException(
                    "JSON resource file not found: " + resourcePath
            );
        }

        try (
                InputStreamReader reader = new InputStreamReader(
                        inputStream,
                        StandardCharsets.UTF_8
                )
        ) {
            Gson gson = new Gson();

            Type type = new TypeToken<List<TestCaseData>>() {
            }.getType();

            List<TestCaseData> testCases =
                    gson.fromJson(reader, type);

            if (testCases == null) {
                throw new IllegalArgumentException(
                        "No test data found in: " + resourcePath
                );
            }

            for (TestCaseData testCase : testCases) {
                if (testCaseName.equals(testCase.name)) {

                    if (testCase.data == null ||
                            testCase.data.isEmpty()) {

                        throw new IllegalArgumentException(
                                "No data found for test case: "
                                        + testCaseName
                        );
                    }

                    return testCase.data.get(0);
                }
            }

            throw new IllegalArgumentException(
                    "Test case not found: " + testCaseName
            );

        } catch (Exception exception) {
            throw new RuntimeException(
                    "Failed to read JSON file: " + resourcePath,
                    exception
            );
        }
    }

    private static class TestCaseData {
        private String name;
        private List<Map<String, String>> data;
    }
}
