package org.gcit.listeners;

import org.gcit.constants.FrameworkConstants;
import org.gcit.utils.JsonUtils;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodInterceptor implements IMethodInterceptor {
    private static final Map<String, Integer> invocationCounts = new HashMap<>();

    public static int getInvocation(String methodName) {
        return invocationCounts.getOrDefault(methodName, 1);
    }

    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext iTestContext) {
        List<IMethodInstance> results = new ArrayList<>();
        // generate runner list json
        JsonUtils.generateRunnerListJsonData();
        List<Map<String, Object>> list = JsonUtils.getTestDetails(FrameworkConstants.getRunmanager());
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