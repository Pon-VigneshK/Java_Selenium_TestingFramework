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
 * Manages the lifecycle and configuration of {@link com.aventstack.extentreports.ExtentReports}
 * for generating HTML test execution reports.
 * <p>
 * This utility class follows a singleton-like pattern for the {@link ExtentReports} instance,
 * ensuring that all test logging throughout a suite contributes to a single, cohesive report.
 * It provides methods to initialize the report (typically at the start of a suite),
 * create individual test entries, assign metadata like authors and categories, and
 * finalize (flush) the report at the end of a suite.
 * <p>
 * Report configuration includes setting up {@link ExtentSparkReporter} for the main report
 * and a separate reporter for just failed test cases. It also handles opening the main
 * report in a browser after the suite execution is complete.
 * <p>
 * This class is final and cannot be instantiated.
 *
 * @see com.aventstack.extentreports.ExtentReports
 * @see com.aventstack.extentreports.reporter.ExtentSparkReporter
 * @see ExtentManager
 * @see FrameworkConstants
 */
public final class ExtentReport {
    /**
     * The single, static instance of {@link ExtentReports} used throughout the test suite execution.
     * This instance is initialized by {@link #initReports(String)} and written to by
     * methods in this class and {@link ExtentLogger}. It is {@code null} until initialized.
     */
    private static ExtentReports extentReports;

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ExtentReport() {
    }

    /**
     * Initializes the Extent reporting system if it hasn't been already.
     * <p>
     * This method should typically be called once at the beginning of a test suite (e.g., in an
     * {@code ISuiteListener#onStart(ISuite)} implementation). It performs the following actions
     * only if the static {@link #extentReports} instance is {@code null}:
     * <ol>
     *   <li>Creates a new {@link ExtentReports} instance.</li>
     *   <li>Sets the report class name in {@link FrameworkConstants} (used for report path generation).</li>
     *   <li>Configures and attaches an {@link ExtentSparkReporter} for the main HTML report:
     *     <ul>
     *       <li>Report path is obtained from {@link FrameworkConstants#getReportPath()}.</li>
     *       <li>View order is set to Dashboard then Test view.</li>
     *       <li>Theme, encoding, document title, and report name are configured.</li>
     *     </ul>
     *   </li>
     *   <li>Configures and attaches a separate {@link ExtentSparkReporter} specifically for failed test cases.
     *       This reporter filters for FAIL status and creates a separate HTML file (e.g., {@code classname_failed_testcase.html}).</li>
     * </ol>
     *
     * @param classname Typically the name of the test suite or a base class name, used for
     *                  configuring report titles and filenames. This value is passed to
     *                  {@link FrameworkConstants#setReportClassName(String)}.
     * @see FrameworkConstants#setReportClassName(String)
     * @see FrameworkConstants#getReportPath()
     * @see ExtentSparkReporter
     * @see Status#FAIL
     * @see Theme#STANDARD
     */
    public static void initReports(String classname) {
        if (Objects.isNull(extentReports)) {
            extentReports = new ExtentReports();
            FrameworkConstants.setReportClassName(classname); // Used by getReportPath() logic

            // Main reporter for all tests
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(getReportPath())
                    .viewConfigurer()
                    .viewOrder()
                    .as(new ViewName[]{ViewName.DASHBOARD, ViewName.TEST})
                    .apply();
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setEncoding("utf-8");
            sparkReporter.config().setDocumentTitle(classname + "_Automation Report");
            sparkReporter.config().setReportName(classname.toUpperCase() + " Automation Testing Report");

            // Separate reporter for failed tests only
            String failedTestsReportPath = classname.toLowerCase().replace(" ", "_") + "_failed_testcases.html";
            ExtentSparkReporter failedSparkReporter = new ExtentSparkReporter(failedTestsReportPath)
                    .filter()
                    .statusFilter()
                    .as(new Status[]{Status.FAIL})
                    .apply();
            failedSparkReporter.config().setTheme(Theme.STANDARD);
            failedSparkReporter.config().setEncoding("utf-8");
            failedSparkReporter.config().setDocumentTitle(classname.toUpperCase() + " - Failed Test Cases");
            failedSparkReporter.config().setReportName(classname.toUpperCase() + " - Failed Test Cases Report");

            extentReports.attachReporter(sparkReporter, failedSparkReporter);
        }
    }

    /**
     * Finalizes the Extent report by writing all logged information to the configured report file(s).
     * <p>
     * This method should typically be called once at the end of a test suite (e.g., in an
     * {@code ISuiteListener#onFinish(ISuite)} implementation). It performs the following:
     * <ol>
     *   <li>Calls {@link ExtentReports#flush()} on the static {@link #extentReports} instance
     *       to write all data to the disk if the instance is not null.</li>
     *   <li>Calls {@link ExtentManager#unloadExtentTest()} to remove the {@link ExtentTest}
     *       instance associated with the current thread from {@link ThreadLocal} storage.</li>
     *   <li>Attempts to open the main generated HTML report file (path from {@link FrameworkConstants#getReportPath()})
     *       in the system's default web browser using {@link Desktop#browse(java.net.URI)}.</li>
     * </ol>
     *
     * @throws IOException If an I/O error occurs when trying to open the report file in the browser.
     *                     Note that exceptions during {@code extentReports.flush()} are handled by ExtentReports itself.
     */
    public static void flushReports() throws IOException {
        if (Objects.nonNull(extentReports)) {
            extentReports.flush();
        }
        ExtentManager.unloadExtentTest(); // Clean up ThreadLocal ExtentTest instance
        // Attempt to open the report file automatically
        File reportFile = new File(getReportPath());
        if (Desktop.isDesktopSupported() && reportFile.exists()) {
            Desktop.getDesktop().browse(reportFile.toURI());
        }
    }

    /**
     * Creates a new test entry in the Extent report with the specified test case name.
     * <p>
     * This method uses the static {@link #extentReports} instance to create a new {@link com.aventstack.extentreports.ExtentTest}
     * object. The created {@code ExtentTest} object is then associated with the current thread
     * using {@link ExtentManager#setExtentTest(ExtentTest)}. This allows subsequent logging calls
     * from {@link ExtentLogger} within the same thread to correctly append to this test entry.
     *
     * @param testcasename The name of the test case as it should appear in the report.
     *                     HTML tags can be used for formatting if desired (e.g., for colors).
     * @see ExtentManager#setExtentTest(ExtentTest)
     */
    public static void createTest(String testcasename) {
        ExtentManager.setExtentTest(extentReports.createTest(testcasename));
    }

    /**
     * Assigns one or more author names to the current test in the Extent report.
     * <p>
     * This method retrieves the current thread's {@link ExtentTest} instance via
     * {@link ExtentManager#getExtentTest()} and then calls {@code assignAuthor(String...)}
     * for each author name provided.
     *
     * @param authors An array of strings, where each string is an author's name to be
     *                associated with the current test. If the array is empty or null,
     *                no authors will be assigned by this call.
     */
    public static void addAuthors(String[] authors) {
        if (authors != null) {
            for (String author : authors) {
                if (author != null && !author.trim().isEmpty()) {
                    ExtentManager.getExtentTest().assignAuthor(author.trim());
                }
            }
        }
    }

    /**
     * Assigns one or more categories to the current test in the Extent report.
     * <p>
     * This method retrieves the current thread's {@link ExtentTest} instance via
     * {@link ExtentManager#getExtentTest()}.
     * It then iterates through the provided {@code categories} array.
     * <p>
     * <strong>Note:</strong> The current implementation incorrectly calls {@code assignAuthor(temp.toString())}
     * instead of {@code assignCategory(temp.toString())}. This is a bug and will assign categories
     * as authors. This Javadoc reflects the intended behavior, but the implementation needs correction.
     *
     * @param categories An array of {@link CategoryType} enums representing the categories
     *                   to be associated with the current test. If the array is empty or null,
     *                   no categories will be assigned by this call.
     * @see CategoryType
     * @see ExtentManager#getExtentTest()
     * @see com.aventstack.extentreports.ExtentTest#assignCategory(String...)
     */
    public static void addCategories(CategoryType[] categories) {
        if (categories != null) {
            for (CategoryType category : categories) {
                if (category != null) {
                    // BUG: This should call assignCategory, not assignAuthor
                    ExtentManager.getExtentTest().assignCategory(category.toString());
                    // Corrected line would be:
                    // ExtentManager.getExtentTest().assignCategory(category.toString());
                }
            }
        }
    }
}
