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

public class ListenerClass implements ITestListener, ISuiteListener {
    @Override
    public void onStart(ISuite suite) {
        ExtentReport.initReports(suite.getXmlSuite().getName());

    }
    @Override
    public void onFinish(ISuite suite) {
        try {
            ExtentReport.flushReports();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        FrameworkAnnotation annotation = result.getMethod().getConstructorOrMethod()
                .getMethod().getAnnotation(FrameworkAnnotation.class);
        ExtentReport.createTest("<span style='color: green;'>TestCase Name:</span>"
                + result.getMethod().getMethodName());
//        ExtentReport.addAuthors(annotation.author());
//        ExtentReport.addCategories(annotation.category());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentLogger.pass(result.getMethod().getMethodName() + " is passed !!", true);
        log(INFO, result.getMethod().getMethodName() + " is passed !!");
        log(INFO, "------------------------------------------------------------");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentLogger.pass(result.getMethod().getMethodName() + " is failed !!", true);
        ExtentLogger.fail(result.getThrowable().getMessage());
        ExtentLogger.fail(result.getThrowable().toString());
        log(DEBUG, Arrays.toString(result.getThrowable().getStackTrace()));
        log(ERROR, result.getMethod().getMethodName() + " is failed !!");
        log(INFO, "------------------------------------------------------------");
    }


    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentLogger.skip(result.getMethod().getMethodName() + " is skipped !!", true);
        log(INFO, result.getMethod().getMethodName() + " is skipped !!");
        log(INFO, "------------------------------------------------------------");
    }
}
