package org.gcit.listeners;

import org.gcit.constants.FrameworkConstants;
import org.gcit.enums.ConfigProperties;
import org.gcit.utils.JsonUtils;
import org.gcit.utils.PropertyUtils;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements the TestNG {@link org.testng.IMethodInterceptor} interface to dynamically
 * filter and modify the list of test methods that TestNG will execute.
 * <p>
 * This interceptor plays a crucial role in enabling data-driven test execution by:
 * <ol>
 *   <li>Reading a "runner list" (a list of test cases and their execution parameters) from a configured
 *       data source (database, Excel, or JSON, determined by the {@code DATASOURCE} property).</li>
 *   <li>Filtering the initial list of test methods provided by TestNG. Only methods that are
 *       present in the runner list and marked for execution (e.g., "execute" flag is "yes") are retained.</li>
 *   <li>Modifying the retained test methods by setting their description, priority, and invocation count
 *       based on the data from the runner list.</li>
 * </ol>
 * The invocation counts set here are accessible via {@link #getInvocation(String)}, which can be used by
 * data providers (like {@link DataProviderUtils}) to supply the correct number of data sets.
 *
 * @see org.testng.IMethodInterceptor
 * @see JsonUtils
 * @see PropertyUtils
 * @see ConfigProperties#DATASOURCE
 * @see FrameworkConstants
 */
public class MethodInterceptor implements IMethodInterceptor {
    /**
     * A static map that stores the desired invocation count for each test method.
     * The key is the test method name (e.g., "testLoginFeature"), and the value is the number of times
     * TestNG should invoke this method.
     * <p>
     * This map is populated by the {@link #intercept(List, ITestContext)} method based on the "count"
     * attribute found in the runner list (e.g., from Excel or database).
     * It is then used by {@link #getInvocation(String)} to provide these counts, typically to a data provider.
     */
    private static final Map<String, Integer> invocationCounts = new HashMap<>();

    /**
     * An instance field to hold the list of test case details fetched from the configured data source
     * (e.g., Excel, DB via JSON, or direct JSON). Each map in the list represents a test case
     * and contains details like "testcasename", "execute", "testdescription", "priority", and "count".
     * This list is populated at the beginning of the {@link #intercept(List, ITestContext)} method.
     */
    List<Map<String, Object>> list = new ArrayList<>();

    /**
     * Retrieves the configured invocation count for a given test method name.
     * <p>
     * This method queries the static {@link #invocationCounts} map, which is populated by the
     * {@link #intercept(List, ITestContext)} method. The count determines how many times
     * a TestNG test method should be invoked, often corresponding to different sets of test data.
     *
     * @param methodName The name of the test method (e.g., as obtained from {@code ITestResult.getMethod().getMethodName()}).
     * @return The configured invocation count for the specified method. If the method name is not found
     *         in the {@link #invocationCounts} map (e.g., if it wasn't in the runner list or had no count specified),
     *         this method defaults to returning {@code 1}.
     */
    public static int getInvocation(String methodName) {
        return invocationCounts.getOrDefault(methodName, 1); // Default to 1 if no specific count is set
    }

    /**
     * Intercepts the list of test methods that TestNG intends to run and returns a modified list
     * based on external configuration (e.g., from Excel, database, or JSON).
     * <p>
     * This method is automatically called by TestNG before it starts executing the test methods.
     * It performs the following key operations:
     * <ol>
     *   <li>Determines the data source for the "runner list" (list of test cases to run) based on the
     *       {@code DATASOURCE} property (from {@link PropertyUtils}).</li>
     *   <li>Fetches the runner list using appropriate methods from {@link JsonUtils}.
     *       For "db" or "excel" sources, this involves {@link JsonUtils} generating an intermediate
     *       JSON runner list if needed, then parsing it. For "json", it reads directly.</li>
     *   <li>Iterates through the original list of {@code methods} provided by TestNG.</li>
     *   <li>For each method, it searches for a matching entry in the fetched runner list
     *       (based on "testcasename" and "execute" status being "yes").</li>
     *   <li>If a match is found:
     *     <ul>
     *       <li>The test method's description, priority, and invocation count are updated from the runner list data.</li>
     *       <li>The invocation count is stored in the static {@link #invocationCounts} map for later retrieval
     *           (e.g., by a data provider via {@link #getInvocation(String)}).</li>
     *       <li>The method instance is added to the {@code results} list, which will be returned to TestNG.</li>
     *     </ul>
     *   </li>
     * </ol>
     * Only methods that are found in the runner list and marked for execution will be included in the returned list.
     *
     * @param methods      A list of {@link IMethodInstance} objects representing all test methods
     *                     TestNG intends to run.
     * @param iTestContext The {@link ITestContext} for the current test execution. Not directly used in this
     *                     implementation but available for more complex scenarios.
     * @return A {@code List<IMethodInstance>} containing only those test methods that are configured
     *         to run, with their properties (description, priority, invocation count) potentially modified.
     * @throws IllegalArgumentException if the configured {@code DATASOURCE} is unknown.
     */
    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext iTestContext) {
        List<IMethodInstance> results = new ArrayList<>();
        // generate runner list json
        String dataSource = PropertyUtils.getValue(ConfigProperties.DATASOURCE);
        switch (dataSource.toLowerCase()) {
            case "db":
                JsonUtils.generateRunnerListJsonData();
                list = JsonUtils.getTestDetails(FrameworkConstants.getRunmanager());
                break;
            case "excel":
                JsonUtils.generateRunnerListJsonDataFromExcel(FrameworkConstants.getRunmangerExcelSheet());
                list = JsonUtils.getTestDetails(FrameworkConstants.getRunmanager());
                break;
            case "json":
                list = JsonUtils.getTestDetails(FrameworkConstants.getRunmanager());
                break;
            default:
                throw new IllegalArgumentException("Unknown data source: " + dataSource);
        }

        for (IMethodInstance methodInstance : methods) {
            for (Map<String, Object> testData : list) {
                if (methodInstance.getMethod().getMethodName().equalsIgnoreCase(String.valueOf(testData.get("testcasename"))) &&
                        String.valueOf(testData.get("execute")).equalsIgnoreCase("yes")) {
                    methodInstance.getMethod().setDescription(String.valueOf(testData.get("testdescription")));
                    methodInstance.getMethod().setPriority(Integer.parseInt(String.valueOf(testData.get("priority"))));
                    int count = Integer.parseInt(String.valueOf(testData.get("count")));
                    invocationCounts.put(methodInstance.getMethod().getMethodName(), count);
                    results.add(methodInstance);
                    break;
                }
            }
        }
        return results;
    }
}