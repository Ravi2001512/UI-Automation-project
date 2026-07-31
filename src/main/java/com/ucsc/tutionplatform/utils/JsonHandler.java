package com.ucsc.tutionplatform.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.ucsc.tutionplatform.models.TestData;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JsonHandler {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String TEST_ID = "test_id";
    private static final String TC_ID = "tc_id";
    private static final String DATA = "data";

    private JsonHandler() {
    }

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

    public static JsonNode getTestCaseById(String fileName, String testCaseId) {
        JsonNode rootNode = readJson(fileName);

        if (!rootNode.isArray()) {
            throw new IllegalArgumentException("Expected JSON root to be an array in file: " + fileName);
        }

        for (JsonNode testCaseNode : rootNode) {
            JsonNode testIdNode = getTestIdNode(testCaseNode);

            if (testIdNode != null && testCaseId.equals(testIdNode.asText())) {
                return testCaseNode;
            }
        }

        throw new IllegalArgumentException("Test case id not found: " + testCaseId);
    }

    public static JsonNode getTestDataById(String fileName, String testCaseId) {
        JsonNode testCaseNode = getTestCaseById(fileName, testCaseId);
        JsonNode dataNode = testCaseNode.get(DATA);

        if (dataNode == null) {
            throw new IllegalArgumentException("Data not found for test case id: " + testCaseId);
        }

        return dataNode;
    }

    public static List<TestData> getTestDataListById(String fileName, String testCaseId) {
        JsonNode dataNode = getTestDataById(fileName, testCaseId);

        if (!dataNode.isArray()) {
            throw new IllegalArgumentException("Expected data to be an array for test case id: " + testCaseId);
        }

        List<TestData> testDataList = new ArrayList<>();

        for (JsonNode testDataNode : dataNode) {
            testDataList.add(mapTestData(testDataNode, testCaseId));
        }

        return testDataList;
    }

    private static TestData mapTestData(JsonNode testDataNode, String testCaseId) {
        try {
            TestData testData = OBJECT_MAPPER.treeToValue(testDataNode, TestData.class);
            testData.setTestCaseId(testCaseId);
            return testData;
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to map test data for test case id: " + testCaseId, exception);
        }
    }

    private static JsonNode getTestIdNode(JsonNode testCaseNode) {
        JsonNode testIdNode = testCaseNode.get(TEST_ID);

        if (testIdNode == null) {
            testIdNode = testCaseNode.get(TC_ID);
        }

        return testIdNode;
    }
}
