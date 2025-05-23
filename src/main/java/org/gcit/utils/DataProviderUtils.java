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
 * The class uses the {@link org.testng.annotations.DataProvider @DataProvider} annotation to supply test data to TestNG test methods.
 * The specific test data retrieval strategy is chosen dynamically based on the {@code DATASOURCE}
 * configuration property retrieved via {@link PropertyUtils#getValue(ConfigProperties)}.
 * <p>
 * It supports fetching data from:
 * <ul>
 *   <li>A database (via {@link JsonUtils#generateTestDataJson()} and then {@link JsonUtils#getTestDataDetails()}).</li>
 *   <li>An Excel file (via {@link ExcelUtils#getTestDetails(String)}).</li>
 *   <li>A JSON file (via {@link JsonUtils#getJsonTestDataDetails()}).</li>
 * </ul>
 * The data is filtered to match the specific test method and its invocation count (if applicable, using {@link org.gcit.listeners.MethodInterceptor#getInvocation(String)}).
 *
 * @see org.testng.annotations.DataProvider
 * @see PropertyUtils
 * @see JsonUtils
 * @see ExcelUtils
 * @see org.gcit.listeners.MethodInterceptor
 */
public final class DataProviderUtils {
    /**
     * Logger tag for this class.
     */
    private static final String LOG_TAG = DataProviderUtils.class.getSimpleName();
    /**
     * A static cache for test data loaded from JSON, particularly when the data source is 'db'.
     * When {@code DATASOURCE} is "db", {@link #getJsonData(Method)} populates this list by first calling
     * {@link JsonUtils#generateTestDataJson()} (to create/update a JSON file from DB) and then
     * {@link JsonUtils#getTestDataDetails()} (to read that JSON file).
     * <p>
     * When {@code DATASOURCE} is "json", {@link #getJsonDataFromJson(Method)} populates this by directly calling
     * {@link JsonUtils#getJsonTestDataDetails()}.
     * <p>
     * <strong>Warning:</strong> This is a static field. While methods attempt to populate it if empty,
     * its state can persist across different test class executions if the JVM instance is reused by the test runner.
     * This might lead to stale data if the underlying JSON files change and this cache is not reset.
     * Consider strategies for cache invalidation or making it non-static if this becomes an issue.
     */
    private static List<Map<String, Object>> testDataFromJson = new ArrayList<>();

    /**
     * TestNG DataProvider method named "getTestData".
     * <p>
     * This method dynamically determines the data source based on the {@code DATASOURCE} property
     * from {@link ConfigProperties} (via {@link PropertyUtils#getValue(ConfigProperties)}).
     * It then delegates to one of the private helper methods ({@link #getJsonData(Method)},
     * {@link #getData(Method)}, or {@link #getJsonDataFromJson(Method)}) to fetch and prepare the data.
     * <p>
     * The fetched data is filtered based on the test method name ({@code method.getName()}) and an "execute" flag
     * (expected to be "Yes" in the data source for a test case to be included).
     * It also considers the invocation count for parameterized tests using {@link org.gcit.listeners.MethodInterceptor#getInvocation(String)}.
     *
     * @param method The TestNG {@link java.lang.reflect.Method} for which test data is being provided.
     *               This is automatically supplied by TestNG.
     * @return A 2D array of objects ({@code Object[][]}). Each inner array represents a set of parameters for one
     *         invocation of the test method. Typically, each inner array will contain a single {@code Map<String, Object>}
     *         or {@code Map<String, String>} representing the test data for that invocation.
     *         Returns an empty 2D array ({@code new Object[0][0]}) if no suitable data is found or an error occurs.
     * @throws IllegalArgumentException If the configured {@code DATASOURCE} property value is unknown or not supported.
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
     * Retrieves test data for a given test method when the data source is configured as "db".
     * <p>
     * This method first checks if the static {@link #testDataFromJson} list is empty.
     * If it is, it calls {@link JsonUtils#generateTestDataJson()} to generate a JSON file from the database,
     * and then populates {@link #testDataFromJson} by calling {@link JsonUtils#getTestDataDetails()} to read that file.
     * <p>
     * It then filters this list to find data rows where "testcasename" matches the current {@code method.getName()}
     * and "execute" is "Yes". The number of data entries provided is limited by the invocation count
     * obtained from {@link org.gcit.listeners.MethodInterceptor#getInvocation(String)}.
     *
     * @param method The test method for which data is being fetched.
     * @return A 2D Object array where each row contains one {@code Map<String, Object>} for a test invocation.
     *         Returns an empty 2D array ({@code new Object[0][0]}) if an error occurs or no data is found.
     */
    private static Object[][] getJsonData(Method method) {
        String testName = method.getName();
        try {
            // Populate cache if empty. This implies that for DATASOURCE="db",
            // the JSON file is generated once per test run (or until cache is cleared) if testDataFromJson is static.
            if (testDataFromJson.isEmpty()) {
                log(LogType.INFO, LOG_TAG + ": Test data cache for JSON (from DB) is empty. Generating and loading...");
                generateTestDataJson(); // Generates JSON from DB
                testDataFromJson = getTestDataDetails(); // Reads the generated JSON
                log(LogType.INFO, LOG_TAG + ": Loaded " + testDataFromJson.size() + " records into JSON (from DB) cache.");
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
            log(LogType.ERROR, LOG_TAG + ": Error retrieving JSON test data for test method '" + testName + "'", e);
            // Optionally, rethrow as a custom exception if this failure should halt tests using this data
            // throw new TestDataException("Failed to retrieve JSON test data for " + testName, e);
            return new Object[0][0]; // Return empty data to prevent test failure due to provider error
        }
    }

    /**
     * Retrieves test data for a given test method when the data source is configured as "excel".
     * <p>
     * It reads data from the Excel sheet specified by {@link FrameworkConstants#getDataExcelSheet()}
     * using {@link ExcelUtils#getTestDetails(String)}.
     * It then filters this list to find data rows where "testcasename" matches the current {@code method.getName()}
     * and "execute" is "Yes".
     * <p>
     * Note: This method does not currently use {@link org.gcit.listeners.MethodInterceptor#getInvocation(String)}
     * to limit data by invocation count, unlike {@link #getJsonData(Method)} and {@link #getJsonDataFromJson(Method)}.
     * It will provide all matching rows from Excel.
     *
     * @param method The test method for which data is being fetched.
     * @return A 2D Object array where each row contains one {@code Map<String, String>} for a test invocation.
     *         Returns an empty 2D array if no data is found or if an error occurs during Excel processing.
     */
    private static Object[][] getData(Method method) {
        String testName = method.getName();
        try {
            List<Map<String, String>> allExcelData = ExcelUtils.getTestDetails(FrameworkConstants.getDataExcelSheet());
            List<Map<String, String>> filteredData = new ArrayList<>();
            for (Map<String, String> dataRow : allExcelData) {
                if (String.valueOf(dataRow.get("testcasename")).equalsIgnoreCase(testName) &&
                    String.valueOf(dataRow.get("execute")).equalsIgnoreCase("Yes")) {
                    filteredData.add(dataRow);
                }
            }

            // Unlike JSON methods, this does not currently use invocation count from MethodInterceptor.
            // It provides all matching entries from Excel.
            Object[][] testDataArray = new Object[filteredData.size()][1];
            for (int i = 0; i < filteredData.size(); i++) {
                testDataArray[i][0] = filteredData.get(i);
            }
            log(LogType.INFO, LOG_TAG + ": Retrieved " + filteredData.size() + " data sets for test method '" + testName + "' from Excel.");
            return testDataArray;
        } catch (Exception e) {
            log(LogType.ERROR, LOG_TAG + ": Error retrieving Excel test data for test method '" + testName + "'", e);
            return new Object[0][0]; // Return empty data on error
        }
    }

    /**
     * Retrieves test data for a given test method when the data source is configured as "json".
     * <p>
     * This method populates the static {@link #testDataFromJson} list by calling
     * {@link JsonUtils#getJsonTestDataDetails()} (which reads a manually prepared, environment-specific JSON file).
     * <p>
     * It then filters this list to find data rows where "testcasename" matches the current {@code method.getName()}
     * and "execute" is "Yes". The number of data entries provided is limited by the invocation count
     * obtained from {@link org.gcit.listeners.MethodInterceptor#getInvocation(String)}.
     *
     * @param method The test method for which data is being fetched.
     * @return A 2D Object array where each row contains one {@code Map<String, Object>} for a test invocation.
     *         Returns an empty 2D array ({@code new Object[0][0]}) if an error occurs or no data is found.
     */
    private static Object[][] getJsonDataFromJson(Method method) {
        String testName = method.getName();
        try {
            // This implies that for DATASOURCE="json", the data is read from the source JSON file on each call
            // to this data provider method, as getJsonTestDataDetails() typically reads fresh from file.
            // The result is stored in the static testDataFromJson, so subsequent filters in the same overall
            // test run (if DataProvider is called multiple times by TestNG for different methods but same instance)
            // might use this cached version if not cleared or re-evaluated carefully.
            // However, DataProviderUtils is final with static methods, so instance state is not an issue here,
            // but the static testDataFromJson is.
            log(LogType.INFO, LOG_TAG + ": Loading test data from JSON source for method '" + testName + "'...");
            testDataFromJson = getJsonTestDataDetails(); // Reads from manually prepared JSON
            log(LogType.INFO, LOG_TAG + ": Loaded " + testDataFromJson.size() + " records from JSON source.");

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
            log(LogType.ERROR, LOG_TAG + ": Error retrieving JSON (from direct JSON source) test data for test method '" + testName + "'", e);
            return new Object[0][0]; // Return empty data on error
        }
    }
}