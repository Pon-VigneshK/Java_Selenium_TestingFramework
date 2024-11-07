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
 * The MethodInterceptor class implements the IMethodInterceptor interface and is used to dynamically
 * modify or filter test methods based on specific criteria. It integrates with the testing framework
 * to provide control over test method execution, including setting descriptions, priorities, and
 * invocation counts.
 *
 * This class interacts with various data sources (e.g., database, Excel, JSON) to determine which
 * test methods should be executed and with what parameters.
 */
public class MethodInterceptor implements IMethodInterceptor {
    /**
     * A static Map to keep track of the number of times each test method has been invoked.
     * The key is a String representing the method name, and the value is an Integer representing
     * the number of times the method has been invoked.
     *
     * This map is updated dynamically during the interception process of test methods
     * based on specified criteria. It is particularly useful for managing and controlling
     * the execution flow of test cases, ensuring that methods are invoked the correct number of times
     * as determined from various data sources like database, Excel, or JSON.
     */
    private static final Map<String, Integer> invocationCounts = new HashMap<>();
    /**
     * A list that stores maps, where each map contains key-value pairs representing various test case details.
     * The keys in the maps are typically strings (such as "testcasename", "execute", "testdescription", "priority", and "count"),
     * and the corresponding values can be of different types (Object).
     * This list is used to dynamically hold and access test details fetched from different data sources.
     *
     * Example keys in the map:
     * - "testcasename" (String): Name of the test case.
     * - "execute" (String): Indicates whether the test case should be executed ("yes" or "no").
     * - "testdescription" (String): Description of the test case.
     * - "priority" (Integer): Priority level of the test case.
     * - "count" (Integer): Number of times the test case should be invoked.
     */
    List<Map<String, Object>> list = new ArrayList<>();

    /**
     * Retrieves the invocation count for a specified method.
     *
     * @param methodName the name of the method for which the invocation count is required
     * @return the invocation count of the specified method, or 1 if the method is not found in the invocation counts map
     */
    public static int getInvocation(String methodName) {
        return invocationCounts.getOrDefault(methodName, 1);
    }

    /**
     * Intercepts and filters the list of test methods based on specified criteria from a data source.
     *
     * @param methods The list of test methods to be filtered.
     * @param iTestContext The test context containing runtime information about the test execution.
     * @return A filtered list of test methods that meet the criteria specified in the data source.
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