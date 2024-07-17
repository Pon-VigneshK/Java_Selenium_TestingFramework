package org.gcit.utils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gcit.constants.FrameworkConstants;
import org.gcit.exceptions.InvalidFilepathException;
import org.gcit.exceptions.JsonExceptions;
import org.gcit.exceptions.SQLConnectionException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static org.gcit.utils.DataBaseConnectionUtils.closeConnection;
import static org.gcit.utils.DataBaseConnectionUtils.getMyConn;

@SuppressWarnings({"unchecked", "rawtypes"})
public final class JsonUtils {
    private static List<Map<String, Object>> finaltestcaselist = new ArrayList();
    private static HashMap<String, Object> queriesList = new HashMap();
    private static FileInputStream fileInputStream;
    private static LinkedHashMap<String,
            LinkedHashMap<String, ArrayList<HashMap<String, Object>>>>
            testDataHashMap = new LinkedHashMap();
    private static String envName;
    private static String runEnv;
    private static List<Map<String, Object>> finalDatalist = new ArrayList<>();
    private static LinkedHashMap<String, ArrayList<HashMap<String, Object>>>
            finalMap = new LinkedHashMap();
    // test case runner list map
    private static LinkedHashMap<String, ArrayList<HashMap<String, Object>>>
            finalTestList = new LinkedHashMap();
    private static LinkedHashMap<String,
            LinkedHashMap<String, ArrayList<HashMap<String, Object>>>>
            testRunnerHashMap = new LinkedHashMap();

    private JsonUtils() {
    }

    public static LinkedHashMap<String,
            LinkedHashMap<String, ArrayList<HashMap<String, Object>>>>
    getTestDataHashMap() {
        return testDataHashMap;
    }

    /**
     * Read RunnerList data from Excel and write it to a JSON file.
     *
     * @param sheetName Name of the Excel sheet containing data.
     */
    public static void generateRunnerListJsonDataFromExcel(String sheetName) {
        try {
            List<Map<String, String>> testDataList =
                    ExcelUtils.getTestDetails(sheetName);
            String testCaseListName = "testCaseLists";
            Map<String, Object> finalTestList = new HashMap<>();
            finalTestList.put(testCaseListName, testDataList);
            HashMap<String, Map<String, Object>> testRunnerHashMap = new HashMap<>();
            testRunnerHashMap.put(FrameworkConstants.getRunmanager(), finalTestList);
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(
                    new File(FrameworkConstants.getTestCaseJsonPath()),
                    testRunnerHashMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get test details from the JSON file based on high-level and nested key
     * names.
     *
     * @param highLevelKeyName The high-level key name in the JSON structure.
     * @return List of test details as maps.
     */
    public static List<Map<String, Object>> getTestDetails(
            String highLevelKeyName) {
        List<Map<String, Object>> testDetailsList = new ArrayList<>();
        FileInputStream fileInputStream = null;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            fileInputStream =
                    new FileInputStream(FrameworkConstants.getTestCaseJsonPath());
            HashMap<String, HashMap> testCaseMap =
                    objectMapper.readValue(fileInputStream, HashMap.class);
            HashMap<String, ArrayList<Map<String, Object>>> jsonTestCaseList =
                    (HashMap<String, ArrayList<Map<String, Object>>>) testCaseMap.get(
                            highLevelKeyName);
            for (Map.Entry<String, ArrayList<Map<String, Object>>> testCase :
                    jsonTestCaseList.entrySet()) {
                for (Map<String, Object> cases : testCase.getValue())
                    testDetailsList.add(cases);
            }
        } catch (FileNotFoundException e) {
            throw new InvalidFilepathException("Test case run list JSON file not found in the given path. Please check your path:" + FrameworkConstants.getTestCaseJsonPath());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(fileInputStream)) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return testDetailsList;
    }

    public static List<Map<String, Object>> getJsonTestDataDetails() {
        List<Map<String, Object>> finalDataList = new ArrayList<>();
        FileInputStream fileInputStream = null;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            fileInputStream = new FileInputStream(FrameworkConstants.getTestDataJsonFilePath(FrameworkConstants.getEnvironment()));
            Map<String, Map<String, Object>> jsonTestDataMap = objectMapper.readValue(fileInputStream, Map.class);

            Map<String, Object> jsonTestDataLinkedMap = jsonTestDataMap.get(FrameworkConstants.getEnvironment());

            if (jsonTestDataLinkedMap != null) {
                for (Map.Entry<String, Object> entry : jsonTestDataLinkedMap.entrySet()) {
                    if (entry.getValue() instanceof List) {
                        finalDataList.addAll((List<Map<String, Object>>) entry.getValue());
                    } else {
                        // Handle unexpected data structure if needed
                    }
                }
            } else {
                throw new RuntimeException("No data found for environment: " + FrameworkConstants.getEnvironment());
            }

        } catch (FileNotFoundException e) {
            throw new InvalidFilepathException("Test data JSON file not found in the given path. Please check your path: " + FrameworkConstants.getTestDataJsonFilePath(), e);
        } catch (IOException e) {
            throw new RuntimeException("Error reading JSON file: " + e.getMessage(), e);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace(); // Handle or log this exception properly
                }
            }
        }

        return finalDataList;
    }

    public static List<Map<String, Object>> getTestDataDetails() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            fileInputStream =
                    new FileInputStream(FrameworkConstants.getTestDataJsonFilePath());
            HashMap<String, HashMap> jsonTestDataMap =
                    objectMapper.readValue(fileInputStream, HashMap.class);
            LinkedHashMap<String, Object> jsonTestDataLinkedMap =
                    (LinkedHashMap) jsonTestDataMap.get(
                            FrameworkConstants.getEnvironment());
            try {
                for (Map.Entry<String, Object> finaltestcasemap :
                        jsonTestDataLinkedMap.entrySet()) {
                    finalDatalist.addAll((ArrayList) finaltestcasemap.getValue());
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            throw new InvalidFilepathException("Test data JSON file not found in the given path. Please check your path: " + FrameworkConstants.getTestDataJsonFilePath());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(fileInputStream)) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return finalDatalist;
    }

    public static HashMap<String, Object> getQueryDetails(String queryType) {
        try {
            String keyname = queryType.toLowerCase() + "queries";
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            fileInputStream =
                    new FileInputStream(FrameworkConstants.getSqlQueryjsonfilepath());
            HashMap<String, Object> jsontestcasemap =
                    objectMapper.readValue(fileInputStream, HashMap.class);
            queriesList = (HashMap<String, Object>) jsontestcasemap.get(keyname);
        } catch (FileNotFoundException e) {
            throw new InvalidFilepathException("SQL Query list json file not found in the given path, please check your path:" + FrameworkConstants.getSqlQueryjsonfilepath());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(fileInputStream)) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return queriesList;
    }

    public static void generateTestDataJson() {
        try {
            Statement st = getMyConn().createStatement();
            HashMap<String, Object> queryDetails = getQueryDetails("select");
            runEnv = FrameworkConstants.getEnvironment().trim().toLowerCase();
            for (Map.Entry<String, Object> mapdata : queryDetails.entrySet()) {
                ResultSet resultSet = st.executeQuery((String) mapdata.getValue());
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                int columns = resultSetMetaData.getColumnCount();
                ArrayList<HashMap<String, Object>> testDataList = new ArrayList<>();
                while (resultSet.next()) {
                    HashMap<String, Object> rowDatas = new HashMap<>(columns);
                    envName = null;
                    for (int i = 1; i <= columns; ++i) {
                        String columnName = resultSetMetaData.getColumnName(i);
                        Object columnValue = resultSet.getObject(i);
                        rowDatas.put(columnName, columnValue);
                        if (columnName.equalsIgnoreCase("environment")) {
                            envName = resultSet.getString("environment").trim().toLowerCase();
                        }
                    }
                    if (runEnv.equals(envName)) {
                        testDataList.add(rowDatas);
                    }
                }
                if (!testDataList.isEmpty()) {
                    finalMap.put(mapdata.getKey(), testDataList);
                }
            }
            if (!finalMap.isEmpty()) {
                testDataHashMap.put(runEnv, finalMap);
                FrameworkConstants.setEnvironment(runEnv);
                ObjectMapper mapper = new ObjectMapper();
                FrameworkConstants.setTestDataJsonFilePath(runEnv);
                mapper.writerWithDefaultPrettyPrinter().writeValue(
                        new File(FrameworkConstants.getTestDataJsonFilePath()),
                        testDataHashMap);
            } else {
                System.out.println("No matching data found for runEnv: " + runEnv);
            }
        } catch (SQLException e) {
            throw new SQLConnectionException("Unable to execute the query runner list, please check the query configurations.", e);
        } catch (JsonMappingException e) {
            throw new JsonExceptions("Json mapping exception occurred", e);
        } catch (JsonGenerationException e) {
            throw new JsonExceptions("Json generation exception occurred", e);
        } catch (FileNotFoundException e) {
            throw new JsonExceptions("Unable to write the test runner list to JSON file. Test runner list JSON file path/directory is missing!", e);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(getMyConn())) {
                closeConnection();
            }
        }
    }

    public static void generateRunnerListJsonData() {
        try {
            Statement st = getMyConn().createStatement();
            HashMap<String, Object> queryDetails = getQueryDetails("runnerlist");
            for (Map.Entry<String, Object> mapdata : queryDetails.entrySet()) {
                ResultSet resultSet = st.executeQuery((String) mapdata.getValue());
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                int columns = resultSetMetaData.getColumnCount();
                HashMap<String, Object> rowDatas;
                ArrayList<HashMap<String, Object>> testDataList = new ArrayList();
                while (resultSet.next()) {
                    rowDatas = new HashMap(columns);
                    for (int i = 1; i <= columns; ++i) {
                        rowDatas.put(
                                resultSetMetaData.getColumnName(i), resultSet.getObject(i));
                    }
                    testDataList.add(rowDatas);
                }
                finalTestList.put(mapdata.getKey(), testDataList);
            }
            testRunnerHashMap.put(FrameworkConstants.getRunmanager(), finalTestList);
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(
                    new File(FrameworkConstants.getTestCaseJsonPath()),
                    testRunnerHashMap);
        } catch (SQLException e) {
            throw new SQLConnectionException("Unable to execute the query runner list, please check the query configurations.", e);
        } catch (JsonMappingException e) {
            throw new JsonExceptions("Json mapping exception occurred", e);
        } catch (JsonGenerationException e) {
            throw new JsonExceptions("Json generation exception occurred", e);
        } catch (FileNotFoundException e) {
            throw new JsonExceptions("Unable to write the test runner list to JSON file. Test runner list JSON file path/directory is missing!", e);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
