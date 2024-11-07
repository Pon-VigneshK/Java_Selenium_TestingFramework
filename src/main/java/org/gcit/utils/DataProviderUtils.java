package org.gcit.utils;

import org.gcit.constants.FrameworkConstants;
import org.gcit.enums.ConfigProperties;
import org.gcit.enums.LogType;
import org.testng.annotations.DataProvider;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.gcit.listeners.MethodInterceptor.getInvocation;
import static org.gcit.logger.LogService.log;
import static org.gcit.utils.JsonUtils.*;

/**
 * Utility class for providing test data from various sources to data-driven tests.
 * The class supports retrieving test data from different sources such as databases,
 * Excel files, and JSON files based on the configured data source.
 *
 * The supported data sources are:
 * - "db": Retrieves test data from a database.
 * - "excel": Retrieves test data from an Excel file.
 * - "json": Retrieves test data from a JSON file.
 *
 * The class uses the `@DataProvider` annotation to supply test data to test methods.
 * The specific test data retrieval method is chosen based on the data source configuration.
 */
public final class DataProviderUtils {
    /**
     * The LOG_TAG is a constant that holds the simple name of the containing class,
     * which is used for logging purposes to identify the context of the log messages
     * generated within the DataProviderUtils class.
     */
    private static final String LOG_TAG = DataProviderUtils.class.getSimpleName();
    /**
     * A list of maps representing test data loaded from a JSON file.
     * Each map contains key-value pairs corresponding to the attributes of a test case.
     *
     * This variable is used by data provider methods to supply test data to test methods.
     * The data is filtered based on test case names and execution indicators before being
     * passed to test methods.
     *
     * The data is generated or retrieved from JSON using methods like `generateTestDataJson()`
     * and `getJsonTestDataDetails()`.
     */
    private static List<Map<String, Object>> testDataFromJson = new ArrayList<>();

    /**
     * Provides a data provider named "getTestData" based on the specified method and the data source type.
     * The data source type is determined by the value of the "DATASOURCE" property.
     * Supported data sources are: "db", "excel", and "json".
     *
     * @param method The method for which test data is to be provided.
     * @return A 2D array of objects containing the test data.
     * @throws IllegalArgumentException If the data source type specified in the properties is unknown.
     */
    @DataProvider(name = "getTestData")
    public static Object[][] getDataProvider(Method method) {
        String dataSource = PropertyUtils.getValue(ConfigProperties.DATASOURCE);
        switch (dataSource.toLowerCase()) {
            case "db":
                return getJsonData(method);
            case "excel":
                return getData(method);
            case "json":
                return getJsonDataFromJson(method);
            default:
                throw new IllegalArgumentException("Unknown data source: " + dataSource);
        }
    }

    /**
     * Retrieves the JSON formatted test data for a specific test method.
     *
     * @param method the test method for which the test data is required
     * @return a two-dimensional array of test data objects specific to the given test method
     */
    private static Object[][] getJsonData(Method method) {
        String testName = method.getName();
        try {
            if (testDataFromJson.isEmpty()) {
                generateTestDataJson();
                testDataFromJson = getTestDataDetails();
            }
            List<Map<String, Object>> filteredTestData = testDataFromJson.stream()
                    .filter(data -> String.valueOf(data.get("testcasename")).equalsIgnoreCase(testName))
                    .filter(data -> String.valueOf(data.get("execute")).equalsIgnoreCase("Yes"))
                    .collect(Collectors.toList());
            int invocationCount = getInvocation(testName);
            int numDataEntries = Math.min(filteredTestData.size(), invocationCount);
            Object[][] testDataArray = new Object[numDataEntries][1];
            for (int i = 0; i < numDataEntries; i++) {
                testDataArray[i][0] = filteredTestData.get(i);
            }
            log(LogType.INFO, LOG_TAG + ": Retrieved test data for test method '" + testName + "'");
            return testDataArray;
        } catch (Exception e) {
            log(LogType.ERROR, LOG_TAG + ": Error retrieving test data for test method '" + testName + "'");
            e.printStackTrace();
            return new Object[0][0];
        }
    }

    /**
     * Retrieves test data for the specified test method from the Excel sheet
     * and returns it as a two-dimensional array of objects.
     *
     * @param method The test method for which the test data is to be fetched.
     * @return A two-dimensional array of test data for the specified test method.
     */
    private static Object[][] getData(Method method) {
        String testcasename = method.getName();
        List<Map<String, String>> list = ExcelUtils.getTestDetails(FrameworkConstants.getDataExcelSheet());
        List<Map<String, String>> iterationlist = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).get("testcasename").equalsIgnoreCase(testcasename) &&
                    list.get(i).get("execute").equalsIgnoreCase("yes")) {
                iterationlist.add(list.get(i));
            }
        }
        Object[][] testDataArray = new Object[iterationlist.size()][1];
        for (int i = 0; i < iterationlist.size(); i++) {
            testDataArray[i][0] = iterationlist.get(i);
        }
        return testDataArray;
    }

    /**
     * Retrieves JSON test data filtered by the test method name and execution flag.
     *
     * @param method the test method for which the JSON test data is required
     * @return a two-dimensional array of JSON test data mapped to the specified test method
     */
    private static Object[][] getJsonDataFromJson(Method method) {
        String testName = method.getName();
        try {
            testDataFromJson = getJsonTestDataDetails();
            List<Map<String, Object>> filteredTestData = testDataFromJson.stream()
                    .filter(data -> String.valueOf(data.get("testcasename")).equalsIgnoreCase(testName))
                    .filter(data -> String.valueOf(data.get("execute")).equalsIgnoreCase("Yes"))
                    .collect(Collectors.toList());
            int invocationCount = getInvocation(testName);
            int numDataEntries = Math.min(filteredTestData.size(), invocationCount);
            Object[][] testDataArray = new Object[numDataEntries][1];
            for (int i = 0; i < numDataEntries; i++) {
                testDataArray[i][0] = filteredTestData.get(i);
            }
            log(LogType.INFO, LOG_TAG + ": Retrieved test data for test method '" + testName + "'");
            return testDataArray;
        } catch (Exception e) {
            log(LogType.ERROR, LOG_TAG + ": Error retrieving test data for test method '" + testName + "'");
            e.printStackTrace();
            return new Object[0][0];
        }
    }

}