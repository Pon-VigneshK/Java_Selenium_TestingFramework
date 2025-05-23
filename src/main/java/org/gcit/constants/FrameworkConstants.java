package org.gcit.constants;

import org.gcit.enums.ConfigProperties;
import org.gcit.utils.PropertyUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Provides access to framework-level constants, primarily file paths for resources, configurations,
 * executables, and reports, as well as fixed names for sheets or keys used in data management.
 * <p>
 * This class has been refactored to promote stateless path generation where possible,
 * deriving paths dynamically or relying on {@link PropertyUtils} for configuration-dependent values
 * rather than storing mutable static state.
 * <p>
 * Constants that are truly fixed (e.g., a specific sheet name like "DATA") are defined as
 * {@code public static final String}. Methods are provided for paths that might involve
 * dynamic elements (like timestamps for reports) or are derived from base paths.
 *
 * @see org.gcit.utils.PropertyUtils
 */
public final class FrameworkConstants {

    /**
     * Represents the user's current working directory.
     * Initialized with the value of the system property {@code "user.dir"}.
     */
    private static final String USER_DIR = System.getProperty("user.dir");

    /**
     * Base path to the {@code src/test/resources} directory.
     * Dynamically composed using the user's current working directory.
     * Used as a root for accessing test-specific resources like data files and configurations.
     */
    private static final String TEST_RESOURCES_PATH = USER_DIR + File.separator + "src" + File.separator + "test" + File.separator + "resources";

    /**
     * Path to the configuration directory within test resources: {@code src/test/resources/config}.
     */
    private static final String CONFIG_FILE_PATH = TEST_RESOURCES_PATH + File.separator + "config";

    /**
     * Absolute path to the ChromeDriver executable: {@code src/test/resources/executables/chromedriver.exe}.
     * This path is fixed and assumes the ChromeDriver is present at this location.
     */
    public static final String CHROME_DRIVER_PATH = TEST_RESOURCES_PATH + File.separator + "executables" + File.separator + "chromedriver.exe";

    /**
     * Absolute path to the main Excel test data file: {@code src/test/resources/testdata/excel/testdata.xlsx}.
     * This file is expected to contain various sheets for test data management.
     */
    public static final String EXCEL_PATH = TEST_RESOURCES_PATH + File.separator + "testdata" + File.separator + "excel" + File.separator + "testdata.xlsx";

    /**
     * Base path for JSON test data files: {@code src/test/resources/testdata/json}.
     * Environment-specific JSON files are typically located under this directory.
     * @see #getTestDataJsonFilePath(String)
     * @see #getTestDataJsonFilePath()
     */
    private static final String TESTDATA_JSON_BASE_PATH = TEST_RESOURCES_PATH + File.separator + "testdata" + File.separator + "json";

    /**
     * Absolute path to the SQLite database file used for input data: {@code src/test/resources/testdata/database/inputData.db}.
     */
    public static final String DATABASE_PATH = TEST_RESOURCES_PATH + File.separator + "testdata" + File.separator + "database" + File.separator + "inputData.db";

    /**
     * Base path to the {@code src/main/resources} directory.
     * Dynamically composed using the user's current working directory.
     */
    private static final String MAIN_RESOURCES_PATH = USER_DIR + File.separator + "src" + File.separator + "main" + File.separator + "resources";

    /**
     * Absolute path to the {@code runmanager.json} file: {@code src/main/resources/runnerlist/runmanager.json}.
     * This JSON file is used for configuring test execution, often defining which test cases to run.
     */
    public static final String TESTCASE_JSON_PATH = MAIN_RESOURCES_PATH + File.separator + "runnerlist" + File.separator + "runmanager.json";

    /**
     * Absolute path to the database configuration JSON file: {@code src/main/resources/Configuration/DataBaseConfig.json}.
     * This file contains parameters for connecting to databases.
     */
    public static final String DBConfig_JSON_FILEPATH = MAIN_RESOURCES_PATH + File.separator + "Configuration" + File.separator + "DataBaseConfig.json";

    /**
     * Absolute path to the SQL queries JSON file: {@code src/main/resources/Configuration/SqlQueries.json}.
     * This file stores SQL query strings used by the framework.
     */
    public static final String SELECTQUERY_JSON_FILEPATH = MAIN_RESOURCES_PATH + File.separator + "Configuration" + File.separator + "SqlQueries.json";

    /**
     * Base directory path for storing Extent HTML reports: {@code extent-test-output} in the user's directory.
     * Specific report files will be created within this directory.
     */
    private static final String EXTENT_REPORT_PATH = USER_DIR + File.separator + "extent-test-output";

    /**
     * Base directory path for storing Excel test reports: {@code excel-test-report} in the user's directory.
     */
    private static final String EXCEL_REPORT_PATH = USER_DIR + File.separator + "excel-test-report";

    /**
     * Represents the current date and time when the {@link FrameworkConstants} class is loaded.
     * This is used for generating consistent timestamps within a single test execution session for reports and logs.
     * Note: This value is fixed at class loading time.
     */
    private static final Date CURRENT_DATE = new Date();

    /**
     * A string representing the date and time when the {@link FrameworkConstants} class was loaded,
     * formatted as "MMM-dd-yyyy_HH_mm_ss_SSS".
     * Used for timestamping files and directories to ensure uniqueness, e.g., for non-overwriting report generation.
     * Note: This value is fixed at class loading time.
     */
    private static final String TODAY_DATE_TIME = new SimpleDateFormat("MMM-dd-yyyy_HH_mm_ss_SSS").format(CURRENT_DATE);

    /**
     * Constant for the name of the Excel sheet containing detailed test data: "DATA".
     */
    public static final String DATA_EXCEL_SHEET = "DATA";

    /**
     * Constant for the name of the Excel sheet used for managing test execution suites or run configurations: "RUNMANAGER".
     */
    public static final String RUNMANGER_EXCEL_SHEET = "RUNMANAGER";

    /**
     * Constant string "RunManager", often used as a key or identifier related to test execution management components.
     */
    public static final String RUNMANAGER = "RunManager";

    /**
     * Constant string "testCaseLists", typically used as a key in JSON files or maps to identify a list of test cases.
     */
    public static final String TESTCASE_LIST = "testCaseLists";

    /**
     * Stores the class name to be used in the title or filename of generated reports.
     * This is typically set by the test listener or runner to identify the specific test class being executed.
     * This field is mutable and shared across the test execution.
     */
    private static String reportClassName = "";
    /**
     * Path to the report file that will be generated.
     * This variable is typically set to store the location of the generated report.
     * Its value is cached after the first call to {@link #getReportPath()}.
     */
    private static String reportFilePath = "";
    /**
     * The file path where the Excel report will be stored.
     * Its value is cached after the first call to {@link #getExcelReportPath()}.
     */
    private static String excelReportFilePath = "";

    /**
     * Gets the name of the "Run Manager" Excel sheet.
     * This sheet typically controls which test cases or test suites are executed.
     *
     * @return The name of the Run Manager Excel sheet ({@value #RUNMANGER_EXCEL_SHEET}).
     */
    public static String getRunmangerExcelSheet() {
        return RUNMANGER_EXCEL_SHEET;
    }

    /**
     * Retrieves the absolute file path for the JSON test data file corresponding to the currently configured environment.
     * The environment is determined by {@link PropertyUtils#getValue(ConfigProperties)} with the {@code ENV} key.
     * The filename is constructed as {@code <ENV>_TestData.json} (e.g., QA_TestData.json) and is located under
     * the base JSON test data path.
     *
     * @return The absolute path to the environment-specific JSON test data file.
     * @see #TESTDATA_JSON_BASE_PATH
     * @see PropertyUtils#getValue(ConfigProperties)
     * @see ConfigProperties#ENV
     */
    public static String getTestDataJsonFilePath() {
        return TESTDATA_JSON_BASE_PATH + File.separator + PropertyUtils.getValue(ConfigProperties.ENV).toUpperCase() + "_TestData.json";
    }

    /**
     * Retrieves the absolute path to the database configuration JSON file ({@code DataBaseConfig.json}).
     *
     * @return The path to the database configuration JSON file.
     * @see #DBConfig_JSON_FILEPATH
     */
    public static String getDBConfigJSONPath() {
        return DBConfig_JSON_FILEPATH;
    }

    /**
     * Retrieves the absolute path to the main Excel test data file ({@code testdata.xlsx}).
     *
     * @return The path to the Excel test data file.
     * @see #EXCEL_PATH
     */
    public static String getExcelFilePath() {
        return EXCEL_PATH;
    }

    /**
     * Returns a formatted string representing the date and time when the {@link FrameworkConstants} class was loaded.
     * The format is "MMM-dd-yyyy_HH_mm_ss_SSS". This timestamp is fixed at class loading and can be used for
     * creating unique filenames or directories for a test run.
     *
     * @return The formatted date and time string captured at class loading.
     * @see #TODAY_DATE_TIME
     */
    public static String getTodayDateTime() {
        return TODAY_DATE_TIME;
    }

    /**
     * Retrieves the absolute path to the {@code runmanager.json} file, which is used for test execution management.
     *
     * @return The path to the {@code runmanager.json} file.
     * @see #TESTCASE_JSON_PATH
     */
    public static String getTestCaseJsonPath() {
        return TESTCASE_JSON_PATH;
    }

    /**
     * Retrieves the constant string "testCaseLists".
     * This string is typically used as a key in JSON files or maps to identify a list of test cases.
     *
     * @return The string "testCaseLists".
     * @see #TESTCASE_LIST
     */
    public static String getTestcaselist() {
        return TESTCASE_LIST;
    }

    /**
     * Retrieves the constant string "RunManager".
     * This string is often used as a key or identifier for components related to test execution management.
     *
     * @return The string "RunManager".
     * @see #RUNMANAGER
     */
    public static String getRunmanager() {
        return RUNMANAGER;
    }

    /**
     * Creates the full path for the Extent HTML report file.
     * The path and filename can vary based on the {@code OVERRIDEREPORTS} configuration property:
     * <ul>
     *     <li>If "NO" ({@link ConfigProperties#OVERRIDEREPORTS}), a timestamped directory is created
     *         (e.g., {@code extent-test-output/MMM-dd-yyyy_HH_mm_ss_SSS/reportClassName_Automation Report.html}).</li>
     *     <li>Otherwise (e.g., "YES"), a fixed filename {@code index.html} is used within the base extent report path
     *         (e.g., {@code extent-test-output/index.html}).</li>
     * </ul>
     * The {@code reportClassName} component is set via {@link #setReportClassName(String)}.
     *
     * @return The dynamically created path for the Extent HTML report file.
     * @throws RuntimeException if there is an error accessing configuration properties (e.g., {@code OVERRIDEREPORTS}).
     * @see PropertyUtils#getValue(ConfigProperties)
     * @see ConfigProperties#OVERRIDEREPORTS
     * @see #getTodayDateTime()
     * @see #reportClassName
     */
    private static String createReportPath() {
        try {
            if (PropertyUtils.getValue(ConfigProperties.OVERRIDEREPORTS).equalsIgnoreCase("NO")) {
                return EXTENT_REPORT_PATH + File.separator + getTodayDateTime() + File.separator + reportClassName + "_Automation Report" + ".html";
            } else {
                return EXTENT_REPORT_PATH + File.separator + "index.html";
            }
        } catch (Exception e) {
            // Consider logging this exception with LogService if available and appropriate
            // For now, rethrowing as a runtime exception to ensure visibility of the error
            throw new RuntimeException("Failed to create report path due to configuration error", e);
        }
    }

    /**
     * Retrieves the absolute path to the ChromeDriver executable.
     *
     * @return The ChromeDriver file path as a String.
     * @see #CHROME_DRIVER_PATH
     */
    public static String getChromeDriverPath() {
        return CHROME_DRIVER_PATH;
    }

    /**
     * Retrieves the absolute path to the SQL queries JSON configuration file ({@code SqlQueries.json}).
     *
     * @return The path where the SQL queries JSON file is located.
     * @see #SELECTQUERY_JSON_FILEPATH
     */
    public static String getSqlQueryjsonfilepath() {
        return SELECTQUERY_JSON_FILEPATH;
    }

    /**
     * Returns the absolute path to the SQLite database file ({@code inputData.db}).
     *
     * @return The path to the database file.
     * @see #DATABASE_PATH
     */
    public static String getDatabasePath() {
        return DATABASE_PATH;
    }

    /**
     * Creates the full path for the Excel report file.
     * The path and filename can vary based on the {@code OVERRIDEREPORTS} configuration property:
     * <ul>
     *     <li>If "YES" ({@link ConfigProperties#OVERRIDEREPORTS}), a timestamped filename is generated
     *         (e.g., {@code excel-test-report/MMM-dd-yyyy_HH_mm_ss_SSS_Automation_Report.xlsx}).</li>
     *     <li>Otherwise (e.g., "NO"), a fixed filename {@code Automation_Report.xlsx} is used within the base Excel report path.</li>
     * </ul>
     *
     * @return The dynamically created path for the Excel report file.
     * @throws RuntimeException if there is an error accessing configuration properties (e.g., {@code OVERRIDEREPORTS}).
     * @see PropertyUtils#getValue(ConfigProperties)
     * @see ConfigProperties#OVERRIDEREPORTS
     * @see #getTodayDateTime()
     */
    public static String createExcelReportPath() {
        try {
            // Ensure OVERRIDEREPORTS is fetched directly
            if (PropertyUtils.getValue(ConfigProperties.OVERRIDEREPORTS).equalsIgnoreCase("Yes")) {
                return EXCEL_REPORT_PATH + File.separator + getTodayDateTime() + "_Automation_Report.xlsx";
            } else {
                return EXCEL_REPORT_PATH + File.separator + "Automation_Report.xlsx";
            }
        } catch (Exception e) {
            // Consider logging this exception with LogService
            throw new RuntimeException("Failed to create Excel report path due to configuration error", e);
        }
    }

    /**
     * Retrieves the file path for the Excel report.
     * If the path has not been generated for the current session, it calls {@link #createExcelReportPath()}
     * to generate it based on configuration. Subsequent calls in the same session return the cached path.
     *
     * @return The file path of the Excel report as a String.
     * @see #createExcelReportPath()
     */
    public static String getExcelReportPath() {
        if (excelReportFilePath.isEmpty()) {
            excelReportFilePath = createExcelReportPath();
        }
        return excelReportFilePath;
    }

    /**
     * Sets the class name to be used for naming Extent HTML reports when {@code OVERRIDEREPORTS} is "NO".
     * This method allows different test classes to generate uniquely named report files within a timestamped directory.
     *
     * @param reportClassName The simple name or fully qualified name of the test class. This name will be
     *                        incorporated into the report's filename.
     */
    public static void setReportClassName(String reportClassName) {
        FrameworkConstants.reportClassName = reportClassName;
    }

    /**
     * Retrieves the absolute path to the main configuration properties file ({@code config.properties}).
     * This file is expected to be located under the path defined by {@link #CONFIG_FILE_PATH}.
     *
     * @return The complete file path of the {@code config.properties} file.
     */
    public static String getConfigFilePath() {
        return CONFIG_FILE_PATH + File.separator + "config.properties";
    }

    /**
     * Retrieves the full path for the Extent HTML report file.
     * If the path has not been generated for the current session, it calls {@link #createReportPath()}
     * to generate it based on configuration and the currently set {@code reportClassName}.
     * Subsequent calls in the same session return the cached path.
     *
     * @return The path of the Extent HTML report file.
     * @see #createReportPath()
     * @see #setReportClassName(String)
     */
    public static String getReportPath() {
        if (reportFilePath.isEmpty()) {
            reportFilePath = createReportPath();
        }
        return reportFilePath;
    }

    /**
     * Retrieves the absolute file path for a JSON test data file for a specific environment.
     * The filename is constructed as {@code <environmentName>_TestData.json} (e.g., QA_TestData.json)
     * and is located under the base JSON test data path ({@link #TESTDATA_JSON_BASE_PATH}).
     * If {@code environmentName} is null, "QA" is used as a default.
     *
     * @param environmentName The name of the environment (e.g., "QA", "DEV", "UAT"). Case will be normalized to uppercase.
     *                        If null, "QA" is used.
     * @return The absolute path to the specified environment's JSON test data file.
     * @see #TESTDATA_JSON_BASE_PATH
     */
    public static String getTestDataJsonFilePath(String environmentName) {
        if (Objects.isNull(environmentName)) {
            environmentName = "QA"; // Default to QA if null
        }
        return TESTDATA_JSON_BASE_PATH + File.separator + environmentName.toUpperCase() + "_TestData.json";
    }

    /**
     * Gets the name of the Excel sheet that contains the primary test data.
     *
     * @return The name of the data Excel sheet ({@value #DATA_EXCEL_SHEET}).
     * @see #DATA_EXCEL_SHEET
     */
    public static String getDataExcelSheet() {
        return DATA_EXCEL_SHEET;
    }

    // Note: getEnvironment() and getExplicitWait() methods were removed as these values
    // are now intended to be fetched directly via PropertyUtils where needed,
    // promoting statelessness in FrameworkConstants.
}