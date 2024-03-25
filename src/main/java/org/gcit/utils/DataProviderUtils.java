package org.gcit.utils;

import org.gcit.constants.FrameworkConstants;
import org.testng.annotations.DataProvider;

import java.lang.reflect.Method;
import java.util.*;

import static org.gcit.utils.JsonUtils.generateTestDataJson;
import static org.gcit.utils.JsonUtils.getTestDataDetails;

public final class DataProviderUtils {
    private static List<Map<String, Object>> testDataFromJson = new ArrayList();
    // ----> For Excel Data <----//
    @DataProvider(name = "getExcelData")
    public static Object[] getData(Method method) {
        String testcasename = method.getName();
        List<Map<String, String>> list = ExcelUtils.getTestDetails(FrameworkConstants.getDataExcelSheet());
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

    @DataProvider(name = "getJsonTestData")
    public static Object[] getJsonData(Method method) {
        String testName = method.getName();
        List<Map<String, Object>> iterationList = new ArrayList();
        if (testDataFromJson.isEmpty()) {
            generateTestDataJson();
            testDataFromJson = getTestDataDetails();
        }
        for (int i = 0; i < testDataFromJson.size(); i++) {
            if (String.valueOf(testDataFromJson.get(i).get("testcasename")).equalsIgnoreCase(testName) &&
                    String.valueOf(testDataFromJson.get(i).get("execute")).equalsIgnoreCase("yes")) {
                iterationList.add(testDataFromJson.get(i));
            }
        }
        Set<Map<String, Object>> set = new HashSet(iterationList);
        iterationList.clear();
        iterationList.addAll(set);
        return iterationList.toArray();
    }
}
