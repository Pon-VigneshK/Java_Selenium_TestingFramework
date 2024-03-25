package org.gcit.listeners;

import org.gcit.constants.FrameworkConstants;
import org.gcit.utils.JsonUtils;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MethodInterceptor implements IMethodInterceptor {
    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext iTestContext) {
        List<IMethodInstance> results = new ArrayList<>();
        // generate runner list json
        JsonUtils.generateRunnerListJsonData();
        List<Map<String, Object>> list = JsonUtils.getTestDetails(FrameworkConstants.getRunmanager());
        for (int i = 0; i < methods.size(); i++) {
            for (int j = 0; j < list.size(); j++) {
                if (methods.get(i).getMethod().getMethodName().equalsIgnoreCase(String.valueOf(list.get(j).get("testcasename"))) &&
                        String.valueOf(list.get(j).get("execute")).equalsIgnoreCase("yes")) {
                    methods.get(i).getMethod().setDescription(String.valueOf(list.get(j).get("testdescription")));
                    methods.get(i).getMethod().setPriority(Integer.parseInt(String.valueOf(list.get(j).get("priority"))));
                    results.add(methods.get(i));
                }
            }
        }
        return results;
    }


    // ----> For Excel Data <----//

//    public List<IMethodInstance> intercept(List<IMethodInstance> method, ITestContext iTestContext) {
//        List<IMethodInstance> results = new ArrayList<>();
//        JsonUtils.generateRunnerListJsonDataFromExcel(FrameworkConstants.getRunmangerExcelSheet());
//        List<Map<String, Object>> list = JsonUtils.getTestDetails(FrameworkConstants.getRunmanager(), FrameworkConstants.getTestcaselist());
//        for (int i = 0; i < method.size(); i++) {
//            for (int j = 0; j < list.size(); j++) {
//                if (method.get(i).getMethod().getMethodName().equalsIgnoreCase(String.valueOf(list.get(j).get("testcasename"))) &&
//                        String.valueOf(list.get(j).get("execute")).equalsIgnoreCase("yes")) {
//                    method.get(i).getMethod().setDescription(String.valueOf(list.get(j).get("testdescription")));
//                    method.get(i).getMethod().setPriority(Integer.parseInt(String.valueOf(list.get(j).get("priority"))));
//                    results.add(method.get(i));
//                }
//            }
//        }
//        return results;
//    }
}
