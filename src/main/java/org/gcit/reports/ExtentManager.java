package org.gcit.reports;

import com.aventstack.extentreports.ExtentTest;

/**
 * Manages {@link com.aventstack.extentreports.ExtentTest} objects in a thread-safe manner
 * using {@link ThreadLocal}.
 * <p>
 * This utility class is crucial for parallel test execution, ensuring that each test
 * running on a separate thread logs to its own independent {@link ExtentTest} instance.
 * This prevents log messages from different tests from being mixed up in the final Extent report.
 * <p>
 * The methods are package-private, intended for use by other classes within the
 * {@code org.gcit.reports} package (e.g., {@link ExtentReport} to set the test
 * and {@link ExtentLogger} to get the current test for logging).
 * <p>
 * This class is final and cannot be instantiated.
 *
 * @see com.aventstack.extentreports.ExtentTest
 * @see java.lang.ThreadLocal
 * @see ExtentReport
 * @see ExtentLogger
 */
public final class ExtentManager {
    /**
     * The core {@link ThreadLocal} storage for {@link ExtentTest} instances.
     * Each thread that calls {@link #setExtentTest(ExtentTest)} will store its own {@code ExtentTest}
     * object here, which can then be retrieved by {@link #getExtentTest()} within the same thread.
     * This ensures test log isolation during parallel execution.
     */
    private static final ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ExtentManager() {
    }

    /**
     * Retrieves the {@link ExtentTest} instance associated with the current thread.
     * <p>
     * This method is called by logging utilities (like {@link ExtentLogger}) to get the
     * current test object to which logs should be appended.
     *
     * @return The {@link ExtentTest} instance for the current thread, or {@code null} if
     *         {@link #setExtentTest(ExtentTest)} has not been called for this thread yet,
     *         or if {@link #unloadExtentTest()} has been called.
     */
    static ExtentTest getExtentTest() {
        return extentTestThreadLocal.get();
    }

    /**
     * Associates the given {@link ExtentTest} instance with the current thread.
     * <p>
     * This is typically called by {@link ExtentReport#createTest(String)} when a new test
     * is started in the Extent report. Storing the {@code ExtentTest} object in a
     * {@code ThreadLocal} variable ensures that subsequent logging calls from within that
     * test (even if interleaved with other tests in a parallel run) correctly write to
     * this specific test's report entry.
     *
     * @param test The {@link ExtentTest} instance to be associated with the current thread.
     *             Should not be {@code null}.
     */
    static void setExtentTest(ExtentTest test) {
        extentTestThreadLocal.set(test);
    }

    /**
     * Removes the {@link ExtentTest} instance associated with the current thread from
     * the {@link ThreadLocal} storage.
     * <p>
     * This method should be called after a test method has completed its execution and all
     * its logging is done (e.g., in an {@code ITestListener#onTestFinish} or similar hook).
     * It helps prevent potential memory leaks in environments where threads are reused,
     * and ensures that a subsequent test on the same thread (if applicable) starts with a clean slate
     * (i.e., {@link #getExtentTest()} would return {@code null} until a new test is set).
     */
    static void unloadExtentTest() {
        extentTestThreadLocal.remove();
    }
}
