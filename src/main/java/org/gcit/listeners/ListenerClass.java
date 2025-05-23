package org.gcit.listeners;

import org.gcit.annotations.FrameworkAnnotation;
import org.gcit.reports.ExtentLogger;
import org.gcit.reports.ExtentReport;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.IOException;
import java.util.Arrays;

import static org.gcit.enums.LogType.*;
import static org.gcit.logger.LogService.log;
import static org.gcit.utils.ReportDatabaseController.storeReportInDatabase;

/**
 * Implements TestNG's {@link org.testng.ITestListener} and {@link org.testng.ISuiteListener}
 * interfaces to manage and report on the lifecycle of a test suite and individual test methods.
 * <p>
 * This class is central to the framework's reporting and logging strategy. Key responsibilities include:
 * <ul>
 *   <li>Initializing {@link ExtentReport} at the start of a test suite using the suite name.</li>
 *   <li>Flushing {@link ExtentReport} at the end of a test suite to ensure all report data is written.</li>
 *   <li>Creating a new test entry in the Extent report when a test method starts.
 *       (Note: The code for adding authors and categories from {@link FrameworkAnnotation} is currently commented out).</li>
 *   <li>Logging the status (pass, fail, skip) of each test method to {@link ExtentLogger},
 *       the console log (via {@link org.gcit.logger.LogService}), and a database
 *       (via {@link org.gcit.utils.ReportDatabaseController#storeReportInDatabase(String, String)}).</li>
 *   <li>For failed tests, it logs the exception message, string representation of the throwable, and stack trace.</li>
 * </ul>
 *
 * @see org.testng.ITestListener
 * @see org.testng.ISuiteListener
 * @see ExtentReport
 * @see ExtentLogger
 * @see FrameworkAnnotation
 * @see org.gcit.utils.ReportDatabaseController
 * @see org.gcit.logger.LogService
 */
public class ListenerClass implements ITestListener, ISuiteListener {
    /**
     * Called by TestNG when a test suite defined in the XML (e.g., {@code <suite name="MySuite">}) starts.
     * <p>
     * This implementation initializes the {@link ExtentReport} system, using the name of the
     * TestNG suite (obtained from {@code suite.getXmlSuite().getName()}) as part of the
     * report configuration or filename.
     *
     * @param suite The {@link ISuite} object representing the test suite that is starting.
     *              Provides access to suite configuration and details.
     */
    @Override
    public void onStart(ISuite suite) {
        ExtentReport.initReports(suite.getXmlSuite().getName());
        log(INFO, "Test Suite started: " + suite.getName());
        log(INFO, "------------------------------------------------------------");
    }

    /**
     * Called by TestNG when a test suite defined in the XML finishes.
     * <p>
     * This implementation ensures that all data written to {@link ExtentReport} is finalized
     * and flushed to the report file(s). It wraps the call to {@link ExtentReport#flushReports()}
     * in a try-catch block; if an {@link IOException} occurs during flushing, it's wrapped
     * in a {@link RuntimeException}.
     *
     * @param suite The {@link ISuite} object representing the test suite that has finished.
     *              Contains results and details of the suite execution.
     * @throws RuntimeException if an {@link IOException} occurs during the report flushing process.
     */
    @Override
    public void onFinish(ISuite suite) {
        try {
            ExtentReport.flushReports();
            log(INFO, "Test Suite finished: " + suite.getName());
            log(INFO, "------------------------------------------------------------");
        } catch (IOException e) {
            log(ERROR, "Error flushing ExtentReports for suite: " + suite.getName(), e);
            throw new RuntimeException("Failed to flush ExtentReports", e);
        }
    }

    /**
     * Called by TestNG before each test method (annotated with {@code @Test}) is executed.
     * <p>
     * This method performs the following actions:
     * <ol>
     *   <li>Retrieves the {@link FrameworkAnnotation} associated with the test method (if present).
     *       (Note: The lines for adding authors and categories from this annotation to the report are currently commented out).</li>
     *   <li>Creates a new test entry in the {@link ExtentReport} using the test method's name.
     *       The method name is formatted with HTML for styling in the report.</li>
     * </ol>
     *
     * @param result The {@link ITestResult} object representing the test method that is about to be started.
     *               It provides access to the method details, annotations, and current status.
     */
    @Override
    public void onTestStart(ITestResult result) {
        // The following lines are commented out in the original code.
        // If re-enabled, ensure FrameworkAnnotation is correctly processed.
        // FrameworkAnnotation annotation = result.getMethod().getConstructorOrMethod()
        //         .getMethod().getAnnotation(FrameworkAnnotation.class);
        ExtentReport.createTest("<span style='color: green;'>TestCase Name:</span> "
                + result.getMethod().getMethodName());
        log(INFO, "Test Started: " + result.getMethod().getMethodName());
        // if (annotation != null) {
        //     ExtentReport.addAuthors(annotation.author());
        //     ExtentReport.addCategories(annotation.category());
        // }
    }

    /**
     * Called by TestNG when a test method passes.
     * <p>
     * This method logs the success information:
     * <ul>
     *   <li>To {@link ExtentLogger} as a "pass" status, including a screenshot.</li>
     *   <li>To the database via {@link #storeReportInDatabase(String, String)} with status "Pass".</li>
     *   <li>To the console log via {@link org.gcit.logger.LogService}.</li>
     * </ul>
     *
     * @param result The {@link ITestResult} object representing the successfully executed test method.
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentLogger.pass(result.getMethod().getMethodName() + " is passed !!", true);
        storeReportInDatabase(result.getMethod().getMethodName(), "Pass");
        log(INFO, result.getMethod().getMethodName() + " is passed !!");
        log(INFO, "------------------------------------------------------------");
    }

    /**
     * Called by TestNG when a test method fails.
     * <p>
     * This method logs the failure information:
     * <ul>
     *   <li>To {@link ExtentLogger}:
     *     <ul>
     *       <li>A "pass" status with the failure message (this seems incorrect, likely should be {@code ExtentLogger.fail}).</li>
     *       <li>The exception message using {@code ExtentLogger.fail(result.getThrowable().getMessage())}.</li>
     *       <li>The string representation of the throwable using {@code ExtentLogger.fail(result.getThrowable().toString())}.</li>
     *       <li>A screenshot is typically captured by {@code ExtentLogger.fail} if configured.</li>
     *     </ul>
     *   </li>
     *   <li>To the database via {@link #storeReportInDatabase(String, String)} with status "Fail".</li>
     *   <li>To the console log via {@link org.gcit.logger.LogService}, including the stack trace at DEBUG level.</li>
     * </ul>
     *
     * @param result The {@link ITestResult} object representing the failed test method.
     *               It contains the exception/throwable that caused the failure.
     */
    @Override
    public void onTestFailure(ITestResult result) {
        // TODO: The first log entry here uses ExtentLogger.pass, which seems incorrect for a failure.
        // It should likely be ExtentLogger.fail(result.getMethod().getMethodName() + " is failed !!", true);
        ExtentLogger.pass(result.getMethod().getMethodName() + " is failed !!", true); // This seems to be a bug, should be .fail
        storeReportInDatabase(result.getMethod().getMethodName(), "Fail");

        if (result.getThrowable() != null) {
            ExtentLogger.fail("Reason for failure: " + result.getThrowable().getMessage(), true); // Assuming true for screenshot
            // ExtentLogger.fail("Throwable: " + result.getThrowable().toString()); // This might be redundant if getMessage covers it well
            log(DEBUG, "Stack trace for " + result.getMethod().getMethodName() + ":\n" + Arrays.toString(result.getThrowable().getStackTrace()));
        } else {
            ExtentLogger.fail("Test failed without a throwable exception.", true);
        }
        log(ERROR, result.getMethod().getMethodName() + " is failed !!");
        log(INFO, "------------------------------------------------------------");
    }


    /**
     * Called by TestNG when a test method is skipped.
     * <p>
     * This method logs the skipped information:
     * <ul>
     *   <li>To {@link ExtentLogger} as a "skip" status, including a screenshot.</li>
     *   <li>To the database via {@link #storeReportInDatabase(String, String)} with status "Skip".</li>
     *   <li>To the console log via {@link org.gcit.logger.LogService}.</li>
     * </ul>
     * If the test was skipped due to an exception (e.g., a dependency failure),
     * the exception message is also logged to ExtentLogger.
     *
     * @param result The {@link ITestResult} object representing the skipped test method.
     *               It may contain a throwable if the skip was due to an exception.
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentLogger.skip(result.getMethod().getMethodName() + " is skipped !!", true);
        if (result.getThrowable() != null) {
            ExtentLogger.skip("Reason for skip: " + result.getThrowable().getMessage());
        }
        storeReportInDatabase(result.getMethod().getMethodName(), "Skip");
        log(INFO, result.getMethod().getMethodName() + " is skipped !!");
        log(INFO, "------------------------------------------------------------");
    }
}
