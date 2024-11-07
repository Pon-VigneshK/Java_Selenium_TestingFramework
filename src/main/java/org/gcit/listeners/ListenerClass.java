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
 * ListenerClass is an implementation of ITestListener and ISuiteListener interfaces.
 * It primarily manages logging and reporting for the test suite and individual test cases
 * by utilizing the ExtentReport and ExtentLogger utilities.
 */
public class ListenerClass implements ITestListener, ISuiteListener {
    /**
     * Initializes the ExtentReport for the test suite.
     *
     * @param suite The ISuite object representing the test suite being started.
     */
    @Override
    public void onStart(ISuite suite) {
        ExtentReport.initReports(suite.getXmlSuite().getName());

    }

    /**
     * This method is called when the suite finishes its execution. It is responsible
     * for flushing the ExtentReports, ensuring all logs and test information are finalized
     * and written to the report.
     *
     * @param suite the suite that has finished executing, containing details of the run
     *              configuration and results.
     */
    @Override
    public void onFinish(ISuite suite) {
        try {
            ExtentReport.flushReports();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Invoked each time before a test case starts. This method fetches the {@link FrameworkAnnotation} details
     * such as authors and categories from the test method and initializes the reporting mechanism for the test case.
     *
     * @param result   The {@link ITestResult} object that contains information about the test case
     *                 that is about to start.
     */
    @Override
    public void onTestStart(ITestResult result) {
        FrameworkAnnotation annotation = result.getMethod().getConstructorOrMethod()
                .getMethod().getAnnotation(FrameworkAnnotation.class);
        ExtentReport.createTest("<span style='color: green;'>TestCase Name:</span>"
                + result.getMethod().getMethodName());
//        ExtentReport.addAuthors(annotation.author());
//        ExtentReport.addCategories(annotation.category());
    }

    /**
     * This method gets invoked when a test method is successfully executed.
     * It logs the test success details using ExtentLogger and stores the result in the database.
     *
     * @param result the ITestResult object containing information about the executed test.
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentLogger.pass(result.getMethod().getMethodName() + " is passed !!", true);
        storeReportInDatabase(result.getMethod().getMethodName(), "Pass");
        log(INFO, result.getMethod().getMethodName() + " is passed !!");
        log(INFO, "------------------------------------------------------------");
    }

    /**
     * Handles actions to be taken when a test case fails.
     *
     * @param result The result of the test case which contains data related to the test case that failed.
     */
    @Override
    public void onTestFailure(ITestResult result) {
        ExtentLogger.pass(result.getMethod().getMethodName() + " is failed !!", true);
        storeReportInDatabase(result.getMethod().getMethodName(), "Fail");
        ExtentLogger.fail(result.getThrowable().getMessage());
        ExtentLogger.fail(result.getThrowable().toString());
        log(DEBUG, Arrays.toString(result.getThrowable().getStackTrace()));
        log(ERROR, result.getMethod().getMethodName() + " is failed !!");
        log(INFO, "------------------------------------------------------------");
    }


    /**
     * This method is invoked when a test is skipped. It logs the skipped test case message and updates the report
     * both in ExtentLogger and the database. Additionally, it logs the skipped status in the console.
     *
     * @param result The result of the skipped test containing details such as the method name and throwable.
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentLogger.skip(result.getMethod().getMethodName() + " is skipped !!", true);
        storeReportInDatabase(result.getMethod().getMethodName(), "Skip");
        log(INFO, result.getMethod().getMethodName() + " is skipped !!");
        log(INFO, "------------------------------------------------------------");
    }
}
