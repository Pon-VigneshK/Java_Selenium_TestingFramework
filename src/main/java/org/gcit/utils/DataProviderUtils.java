package org.gcit.utils;

import org.gcit.constants.FrameworkConstants;
import org.gcit.enums.LogType;
import org.testng.annotations.DataProvider;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.gcit.listeners.MethodInterceptor.getInvocation;
import static org.gcit.logger.LogService.log;
import static org.gcit.utils.JsonUtils.generateTestDataJson;
import static org.gcit.utils.JsonUtils.getTestDataDetails;

public final class DataProviderUtils {
    private static final String LOG_TAG = DataProviderUtils.class.getSimpleName();
    private static List<Map<String, Object>> testDataFromJson = new ArrayList<>();

    @DataProvider(name = "getJsonTestData")
    public static Object[][] getJsonData(Method method) {
        String testName = method.getName();
        try {
            if (testDataFromJson.isEmpty()) {
                generateTestDataJson();
                testDataFromJson = getTestDataDetails();
            }
            // Filter test data based on test case name (testName) and "execute" ="Yes"
            List<Map<String, Object>> filteredTestData =
                    testDataFromJson.stream()
                            .filter(data
                                    -> String.valueOf(data.get("testcasename"))
                                    .equalsIgnoreCase(testName))
                            .filter(data
                                    -> String.valueOf(data.get("execute"))
                                    .equalsIgnoreCase("Yes"))
                            .collect(Collectors.toList());
            int invocationCount = getInvocation(testName);
            int numDataEntries = Math.min(filteredTestData.size(), invocationCount);
            Object[][] testDataArray = new Object[numDataEntries][1];
            for (int i = 0; i < numDataEntries; i++) {
                testDataArray[i][0] = filteredTestData.get(i);
            }
            log(LogType.INFO,
                    LOG_TAG + ": Retrieved test data for test method '" + testName + "'");
            return testDataArray;
        } catch (Exception e) {
            log(LogType.ERROR,
                    LOG_TAG + ": Error retrieving test data for test method '" + testName
                            + "'");
            e.printStackTrace();
            return new Object[0][0];
        }
    }

    @DataProvider(name = "getData")
    public static Object[] getData(Method method) {
        String testcasename = method.getName();
        List<Map<String, String>> list =
                ExcelUtils.getTestDetails(FrameworkConstants.getDataExcelSheet());
        List<Map<String, String>> iterationlist = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).get("testcasename").equalsIgnoreCase(testcasename)) {
                if (list.get(i).get("execute").equalsIgnoreCase("yes")) {
                    iterationlist.add(list.get(i));
                }
            }
        }
        return iterationlist.toArray();
    }
}