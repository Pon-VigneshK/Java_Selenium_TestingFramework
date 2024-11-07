package org.gcit.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import org.gcit.constants.FrameworkConstants;
import org.gcit.enums.CategoryType;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static org.gcit.constants.FrameworkConstants.getReportPath;

/**
 * ExtentReport is a utility class that manages the creation, configuration,
 * and lifecycle of ExtentReports used for generating automated test reports.
 * This class provides methods to initialize and flush reports, as well as
 * to create test cases and assign authors and categories to these test cases.
 * It is implemented as a singleton to ensure only one instance of ExtentReports
 * exists during the execution.
 */
public final class ExtentReport {
    /**
     * Singleton instance of ExtentReports used for generating comprehensive test
     * reports in the ExtentReport utility class. This instance ensures that a
     * single, consistent report is maintained throughout the execution of tests.
     */
    private static ExtentReports extentReports;

    /**
     * The ExtentReport constructor is private to prevent instantiation from outside the class.
     * This ensures that the ExtentReport class follows the singleton pattern,
     * allowing only a single instance of the ExtentReports to be created and managed.
     */
    private ExtentReport() {

    }

    /**
     * Initializes and configures the ExtentReports instance if it is not already initialized.
     * This method sets up the report class name, attaches both the main and failed test case
     * reporters, and configures their properties such as theme, encoding, document title, and report name.
     *
     * @param classname the class name used as the basis for configuring report properties and filenames
     */
    public static void initReports(String classname) {
        if (Objects.isNull(extentReports)) {
            extentReports = new ExtentReports();
            FrameworkConstants.setReportClassName(classname);
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(getReportPath()).viewConfigurer()
                    .viewOrder().as(new ViewName[]{ViewName.DASHBOARD, ViewName.TEST}).apply();
            ExtentSparkReporter failedSparkReporter = new ExtentSparkReporter(classname.toLowerCase() + "_failed_testcase.html").filter().statusFilter().as(new Status[]{Status.FAIL}).apply();
            failedSparkReporter.config().setDocumentTitle(classname.toUpperCase() + "Automation Failed Test Case");
            extentReports.attachReporter(sparkReporter, failedSparkReporter);
            sparkReporter.config().setTheme(Theme.STANDARD);
            failedSparkReporter.config().setTheme(Theme.STANDARD);
            failedSparkReporter.config().setEncoding("utf-8");
            sparkReporter.config().setEncoding("utf-8");
            sparkReporter.config().setDocumentTitle(classname + "_Automation Report");
            sparkReporter.config().setReportName(classname.toUpperCase() + " Automation Testing Report");
        }
    }

    /**
     * Flushes and finalizes the ExtentReports instance if it exists, ensuring that all
     * log entries and test information are written to the report file. Then, it unloads
     * the ExtentTest instance associated with the current thread and opens the generated
     * report using the system's default web browser.
     *
     * @throws IOException if an I/O error occurs when trying to open the report file in the browser
     */
    public static void flushReports() throws IOException {
        if (Objects.nonNull(extentReports)) {
            extentReports.flush();
        }
        ExtentManager.unloadExtentTest();
        Desktop.getDesktop().browse(new File(getReportPath()).toURI());
    }

    /**
     * Creates a test case in the extent report with the given test case name.
     *
     * @param testcasename the name of the test case to be created in the extent report
     */
    public static void createTest(String testcasename) {
        ExtentManager.setExtentTest(extentReports.createTest(testcasename));
    }

    /**
     * Assigns the specified authors to the current ExtentTest instance.
     *
     * @param authors An array of author names to be assigned to the current ExtentTest instance.
     */
    public static void addAuthors(String[] authors) {
        for (String temp : authors) {
            ExtentManager.getExtentTest().assignAuthor(temp);
        }
    }

    /**
     * Assigns categories to the current thread's ExtentTest instance.
     *
     * @param categories an array of CategoryType values to be assigned as categories
     *                   to the current thread's ExtentTest instance
     */
    public static void addCategories(CategoryType[] categories) {

        for (CategoryType temp : categories) {
            ExtentManager.getExtentTest().assignAuthor(temp.toString());
        }

    }

}
