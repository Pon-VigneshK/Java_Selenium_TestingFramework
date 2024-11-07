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

/**
 * Utility class for handling JSON data related to test cases and runner lists.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class JsonUtils {
    /**
     * A list of maps where each map represents a test case with its associated details.
     * Each map contains key-value pairs representing the properties and values of a specific test case.
     * This list is used to store finalized test cases, typically derived from initial data transformation or processing.
     */
    private static List<Map<String, Object>> finaltestcaselist = new ArrayList();
    /**
     * A static HashMap that stores query details.
     * The key is a String representing the type or name of the query.
     * The value is an Object that holds the query data or information.
     */
    private static HashMap<String, Object> queriesList = new HashMap();
    /**
     * A FileInputStream used for reading data from a file.
     * This stream is used in various methods to handle JSON and Excel data operations.
     */
    private static FileInputStream fileInputStream;
    /**
     * A nested LinkedHashMap structure to store test data.
     * The outermost LinkedHashMap uses a String key to map to another LinkedHashMap.
     * The second LinkedHashMap uses a String key to map to an ArrayList of HashMaps.
     * Each HashMap in the ArrayList contains test data with String keys and Object values.
     * This structure is typically used to store and organize test data fetched from an Excel sheet or other data sources.
     */
    private static LinkedHashMap<String,
            LinkedHashMap<String, ArrayList<HashMap<String, Object>>>>
            testDataHashMap = new LinkedHashMap();
    /**
     * The `envName` variable holds the name of the environment to be used during runtime.
     * This variable is set statically and accessed by various methods within the `JsonUtils` class
     * to determine the context for data retrieval and processing.
     */
    private static String envName;
    /**
     * Specifies the runtime environment in which the application is currently operating.
     * This could be set to various environment types such as "development", "testing", "production", etc.
     */
    private static String runEnv;
    /**
     * A list of maps holding the final data processed by the utility functions.
     * Each map represents a data entry where keys are string identifiers and values are their corresponding objects.
     * This list is intended to aggregate processed data for further operations or analysis within the application.
     */
    private static List<Map<String, Object>> finalDatalist = new ArrayList<>();
    /**
     * A LinkedHashMap that holds structured JSON test data.
     * The outer map uses keys as String values to represent different test cases or categories.
     * Each value in the outer map is an ArrayList of HashMaps where each HashMap holds various key-value pairs representing test data details.
     * This structure allows for ordered storage and retrieval of complex nested test data.
     */
    private static LinkedHashMap<String, ArrayList<HashMap<String, Object>>>
            finalMap = new LinkedHashMap();
    /**
     * A map representing the final list of test cases to be executed.
     * Each key is a string representing a category or identifier for a set of test cases.
     * The value is an array list containing maps, where each map holds the details of a specific test case.
     */
    // test case runner list map
    private static LinkedHashMap<String, ArrayList<HashMap<String, Object>>>
            finalTestList = new LinkedHashMap();
    /**
     * A static LinkedHashMap used within the JsonUtils class to manage hierarchical test runner data.
     * The structure is as follows:
     * - The first level key is a String representing a high-level category or identifier.
     * - The second level is another LinkedHashMap where its key is a String corresponding to a subcategory or more detailed identifier.
     * - The second level value is an ArrayList containing HashMaps, each HashMap holding test runner details where keys are Strings and values are Objects.
     */
    private static LinkedHashMap<String,
            LinkedHashMap<String, ArrayList<HashMap<String, Object>>>>
            testRunnerHashMap = new LinkedHashMap();

    /**
     * Private constructor to prevent instantiation of the JsonUtils utility class.
     * This class is designed to provide static methods for JSON related operations.
     */
    private JsonUtils() {
    }

    /**
     * Retrieves the test data stored in a nested LinkedHashMap structure.
     *
     * @return A nested LinkedHashMap containing the test data.
     *         The outermost LinkedHashMap uses strings as keys, where each key corresponds to a high-level test category.
     *         The value associated with each key is another LinkedHashMap, where keys are mid-level test identifiers.
     *         The value associated with each mid-level key is an ArrayList of HashMaps, where each HashMap stores individual test data points.
     */
    public static LinkedHashMap<String,
            LinkedHashMap<String, ArrayList<HashMap<String, Object>>>>
    getTestDataHashMap() {
        return testDataHashMap;
    }

    /**
     * Generates a JSON file containing runner list data extracted from an Excel sheet.
     * The data is read from the specified sheet in the Excel file, converted into a list of maps,
     * and then written to a JSON file with a predefined structure.
     *
     * @param sheetName the name of the Excel sheet from which to extract the runner list data
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
     * Retrieves test details from a JSON file based on the provided high-level key name.
     *
     * @param highLevelKeyName The key name in the JSON file to look for test details.
     * @return A list of test details where each detail is represented as a map.
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

    /**
     * Reads JSON test data from a file and returns a list of maps containing the test data details.
     * The JSON file path and environment are determined using framework constants.
     *
     * The method reads the JSON file, configures the ObjectMapper to ignore unknown properties during deserialization,
     * and processes the retrieved data to construct the final list of test data maps. If the file is not found or
     * there is an error reading the file, appropriate exceptions are thrown.
     *
     * @return A list of maps where each map contains the details of the test data.
     * @throws InvalidFilepathException if the JSON file is not found in the specified path.
     * @throws RuntimeException if there is an error reading the JSON file or processing the data.
     */
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

    /**
     * Retrieves test data details from a specified JSON file.
     * The method deserializes the JSON file into a structure
     * comprising a list of maps, where each map represents a test data item.
     *
     * @return a list of maps, each containing test data details.
     * @throws InvalidFilepathException if the JSON file is not found at the specified path.
     */
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

    /**
     * Retrieves query details from a specified JSON file based on the given query type.
     *
     * @param queryType The type of queries to retrieve from the JSON file.
     * @return A HashMap containing the query details corresponding to the specified query type.
     * @throws InvalidFilepathException If the SQL Query list JSON file is not found in the given path.
     */
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

    /**
     * Generates a JSON file containing test data from a database based on the current environment settings.
     * <p>
     * This method retrieves query details and executes them to fetch data from the database.
     * The data is filtered according to the specified environment and then written to a JSON file.
     * </p>
     * <p>
     * It handles various exceptions related to SQL operations and JSON processing.
     * In case of any connection issues, it ensures the database connection is closed properly.
     * </p>
     * <p>
     * Uses the following methods:
     * {@link #getQueryDetails(String)}
     * </p>
     *
     * @throws SQLConnectionException     if there are issues executing the SQL queries.
     * @throws JsonMappingException       if there is an error mapping the data to JSON format.
     * @throws JsonGenerationException    if there is an error generating the JSON file.
     * @throws FileNotFoundException      if the file path for the JSON file is invalid.
     */
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

    /**
     * Generates a JSON file containing runner list data by querying the database.
     * This method retrieves SQL queries from a predefined JSON file, executes them
     * using a database connection, and transforms the result sets into JSON format.
     *
     * The generated JSON data is saved to a file specified by a framework constant.
     * The method employs comprehensive error handling to address potential
     * SQLException, JsonMappingException, JsonGenerationException, and
     * FileNotFoundException.
     *
     * Exceptions:
     * - SQLConnectionException: Thrown when there is an issue with executing
     *   the database queries.
     * - JsonExceptions: Thrown for JSON mapping or generation errors.
     * - IOException: Thrown for general I/O errors.
     */
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
