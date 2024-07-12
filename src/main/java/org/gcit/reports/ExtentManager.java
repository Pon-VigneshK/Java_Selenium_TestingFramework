package org.gcit.reports;

import com.aventstack.extentreports.ExtentTest;

public final class ExtentManager {
    private static final ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();

    private ExtentManager() {

    }

    static ExtentTest getExtentTest() {
        return extentTestThreadLocal.get();
    }

    static void setExtentTest(ExtentTest test) {
        extentTestThreadLocal.set(test);
    }

    static void unloadExtentTest() {
        extentTestThreadLocal.remove();
    }
}
