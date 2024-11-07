package org.gcit.reports;

import com.aventstack.extentreports.ExtentTest;

/**
 * ExtentManager is a utility class that manages the ThreadLocal instance of ExtentTest.
 * It provides methods to get, set, and unload an ExtentTest instance for the current thread.
 */
public final class ExtentManager {
    /**
     * A ThreadLocal instance to manage {@code ExtentTest} objects for each thread individually.
     * This ensures that each thread has its own instance of {@code ExtentTest}.
     */
    private static final ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();

    /**
     * Private constructor to prevent instantiation of ExtentManager.
     * This class is intended to be used as a utility class with static methods
     * and should not be instantiated.
     */
    private ExtentManager() {

    }

    /**
     * Returns the ExtentTest instance associated with the current thread.
     *
     * @return the current thread's ExtentTest instance, or null if no instance is associated
     */
    static ExtentTest getExtentTest() {
        return extentTestThreadLocal.get();
    }

    /**
     * Sets the current thread's ExtentTest instance.
     *
     * @param test the ExtentTest instance to be associated with the current thread
     */
    static void setExtentTest(ExtentTest test) {
        extentTestThreadLocal.set(test);
    }

    static void unloadExtentTest() {
        extentTestThreadLocal.remove();
    }
}
