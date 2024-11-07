package org.gcit.constants;

import org.gcit.enums.ConfigProperties;
import org.gcit.utils.PropertyUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * FrameworkConstants holds all the constant values used within the framework.
 * If some value needs to be changed or modified often, then it should be stored in the property files.
 */
public final class FrameworkConstants {

    /**
     * Represents the user's current working directory.
     * This variable is initialized with the value of the system property "user.dir".
     */
    private static final String USER_DIR = System.getProperty("user.dir");
    /**
     * This constant holds the path to the test resources directory.
     * It is dynamically composed using the user's current working directory.
     * The path is typically used for locating files needed during testing, such as test data and configuration files.
     */
    private static final String TEST_RESOURCES_PATH = USER_DIR + File.separator + "src" + File.separator + "test" + File.separator + "resources";
    /**
     * Path to the configuration file used to load application-specific properties.
     * This is constructed dynamically using the `TEST_RESOURCES_PATH` and the appropriate file separator.
     */
    private static final String CONFIG_FILE_PATH = TEST_RESOURCES_PATH + File.separator + "config";
    /**
     * Path to the ChromeDriver executable, constructed using the base test resources path
     * and the file separator appropriate for the operating system.
     *
     * The path points to the "chromedriver.exe" file within the "executables" directory.
     */
    private static final String CHROME_DRIVER_PATH = TEST_RESOURCES_PATH + File.separator + "executables" + File.separator + "chromedriver.exe";
    /**
     * Path to the Excel file containing test data.
     * This file is located within the test resources directory and is used to store and retrieve test data for execution.
     */
    // File paths for test data and configuration
    private static final String EXCEL_PATH = TEST_RESOURCES_PATH + File.separator + "testdata" + File.separator + "excel" + File.separator + "testdata.xlsx";
    /**
     * A constant that holds the file path for the JSON test data files.
     * It is constructed using the base test resources path, along with directory separators
     * and the directory name 'json'.
     */
    private static String TESTDATAJSONFILEPATH = TEST_RESOURCES_PATH + File.separator + "testdata" + File.separator + "json";
    /**
     * The path to the database file used for testing purposes.
     * This constant combines the base path for test resources with directory separators
     * and specific subdirectory and file names to form the complete path to the test database file.
     */
    private static final String DATABASE_PATH = TEST_RESOURCES_PATH + File.separator + "testdata" + File.separator + "database" + File.separator + "inputData.db";
    /**
     * The path to the main resources directory within the project's structure.
     * This path is constructed dynamically based on the user's current directory.
     *
     * Example: "/user/dir/src/main/resources"
     */
    private static final String MAIN_RESOURCES_PATH = USER_DIR + File.separator + "src" + File.separator + "main" + File.separator + "resources";
    /**
     * Path to the JSON file that contains the runner list run manager configuration.
     * This variable is constructed using the main resources path and appends the specific path
     * to the run manager JSON file.
     */
    private static final String TESTCASE_JSON_PATH = MAIN_RESOURCES_PATH + File.separator + "runnerlist" + File.separator + "runmanager.json";
    /**
     * The file path to the database configuration JSON file.
     * This path is constructed using the main resources directory and specific subdirectories.
     * It is used to load database configurations needed by the application.
     */
    private static final String DBConfig_JSON_FILEPATH = MAIN_RESOURCES_PATH + File.separator + "Configuration" + File.separator + "DataBaseConfig.json";
    /**
     * The file path for the SQL queries JSON configuration file.
     * This file contains the SQL query definitions used across the application.
     */
    // Other constants
    private static final String SELECTQUERY_JSON_FILEPATH = MAIN_RESOURCES_PATH + File.separator + "Configuration" + File.separator + "SqlQueries.json";
    /**
     * The file path where the extent report will be stored.
     * It is built using the base user directory and a designated subfolder for extent test outputs.
     */
    private static final String EXTENT_REPORT_PATH = USER_DIR + File.separator + "extent-test-output";
    /**
     * Specifies the path for storing generated Excel test reports.
     * The path is constructed by combining the user's directory
     * with a fixed subdirectory name "excel-test-report".
     */
    private static final String EXCEL_REPORT_PATH = USER_DIR + File.separator + "excel-test-report";
    /**
     * Represents the timeout duration (in seconds) explicitly used for waiting operations
     * within the framework, such as waiting for elements to be visible or interactable.
     * The timeout value is retrieved from the configuration properties via PropertyUtils.
     */
    private static final int EXPLICIT_WAIT = Integer.parseInt(PropertyUtils.getValue(ConfigProperties.EXPLICITWAITTIMEOUT));
    /**
     * Represents the current date and time when the application is running.
     * This value is used for generating timestamps in reports and logs.
     */
    // Date and time formatting for reports
    private static final Date CURRENT_DATE = new Date();
    /**
     * A string representing the current date and time, formatted as "MMM-dd-yyyy_HH_mm_ss_SSS".
     * It is used throughout the framework for timestamping purposes, such as generating unique filenames for reports and logs.
     */
    private static final String TODAY_DATE_TIME = new SimpleDateFormat("MMM-dd-yyyy_HH_mm_ss_SSS").format(CURRENT_DATE);
    /**
     * Specifies the name of the data sheet within an Excel file.
     */
    // Sheet names in Excel files
    private static final String DATA_EXCEL_SHEET = "DATA";
    /**
     * Specifies the name of the Excel sheet used for managing the run configurations.
     */
    private static final String RUNMANGER_EXCEL_SHEET = "RUNMANAGER";
    /**
     * Represents a constant string value "RunManager" used within the framework.
     */
    private static final String RUNMANAGER = "RunManager";
    /**
     * Constant representing the string identifier for the test case list.
     * This is used within the framework to denote or fetch information pertaining to test cases.
     */
    private static final String TESTCASE_LIST = "testCaseLists";
    /**
     * A flag indicating the state within the FrameworkConstants.
     * This flag can be utilized to toggle certain features or conditions
     * within the framework based on its boolean value.
     */
    private static boolean flag = false;
    /**
     * Stores the class name used in the generation of reports.
     * This variable helps in structuring and distinguishing various reports
     * created by using different class names.
     */
    private static String reportClassName = "";
    /**
     * Path to the report file that will be generated.
     * This variable is typically set to store the location of the generated report.
     */
    private static String reportFilePath = "";
    /**
     * Holds the current environment configuration value.
     * This value is retrieved from configuration properties using the key ENV.
     * The environment setting affects various aspects of the application's behavior and
     * can be used to switch between different settings such as development, testing, or production.
     */
    private static String ENVIRONMENT = PropertyUtils.getValue(ConfigProperties.ENV);
    /**
     * The file path where the Excel report will be stored.
     */
    private static String excelReportFilePath = "";

    /**
     * Gets the name of the Run Manager Excel sheet.
     *
     * @return the name of the Run Manager Excel sheet as a String
     */

    public static String getRunmangerExcelSheet() {
        return RUNMANGER_EXCEL_SHEET;
    }

    /**
     * Retrieves the file path of the test data JSON file.
     *
     * @return The file path of the test data JSON file.
     */
    public static String getTestdataJsonFilepath() {
        return TESTDATAJSONFILEPATH;
    }

    /**
     * Retrieves the file path for the database configuration JSON file.
     *
     * @return the file path of the database configuration JSON file.
     */
    public static String getDBConfigJSONPath() {
        return DBConfig_JSON_FILEPATH;
    }

    /**
     * Retrieves the file path of the Excel file used in the framework.
     *
     * @return the file path of the Excel file.
     */
    public static String getExcelFilePath() {
        return EXCEL_PATH;
    }

    /**
     * Returns the current date and time as a string.
     *
     * @return the current date and time in string format
     */
    public static String getTodayDateTime() {
        return TODAY_DATE_TIME;
    }

    /**
     * Retrieves the file path for the test case JSON.
     *
     * @return the file path for the test case JSON as a String.
     */
    public static String getTestCaseJsonPath() {
        return TESTCASE_JSON_PATH;
    }

    /**
     * Retrieves the list of test cases.
     *
     * @return A string representing the list of test cases.
     */
    public static String getTestcaselist() {
        return TESTCASE_LIST;
    }

    /**
     * Retrieves the run manager identifier.
     *
     * @return The run manager identifier as a string.
     */
    public static String getRunmanager() {
        return RUNMANAGER;
    }

    /**
     * Creates the report path based on the configuration settings.
     *
     * This method generates different report paths depending on whether
     * overriding reports is enabled or not. If overriding is disabled,
     * it creates a report path with the current date and time in the filename.
     * Otherwise, it returns a default report path.
     *
     * @return the dynamically created report path as a String
     */
    private static String createReportPath() {
        try {
            if (PropertyUtils.getValue(ConfigProperties.OVERRIDEREPORTS).equalsIgnoreCase("NO")) {
                return EXTENT_REPORT_PATH + File.separator + getTodayDateTime() + File.separator + reportClassName + "_Automation Report" + ".html";
            } else {
                return EXTENT_REPORT_PATH + File.separator + "index.html";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    /**
     * Retrieves the file path for the ChromeDriver executable.
     *
     * @return the ChromeDriver file path as a String.
     */
    public static String getChromeDriverPath() {
        return CHROME_DRIVER_PATH;
    }

    /**
     * Retrieves the file path for the SQL query JSON file.
     *
     * @return The file path where the SQL queries JSON file is located.
     */
    public static String getSqlQueryjsonfilepath() {
        return SELECTQUERY_JSON_FILEPATH;
    }

    /**
     * Returns the file path to the database.
     *
     * @return the path to the database.
     */
    public static String getDatabasePath() {
        return DATABASE_PATH;
    }

    /**
     * Creates the file path for the Excel report.
     *
     * @return the path of the Excel report file as a String.
     *         Returns the current date and time in the file name if report overriding is enabled in the configuration;
     *         otherwise, returns a file path with a generic name.
     * @throws RuntimeException if there is an error in retrieving the configuration properties.
     */
    public static String createExcelReportPath() {
        try {
            if (PropertyUtils.getValue(ConfigProperties.OVERRIDEREPORTS).equalsIgnoreCase("Yes")) {
                String path = EXCEL_REPORT_PATH + File.separator + getTodayDateTime() + "_Automation_Report.xlsx";
                System.out.println(path);
                return path;
            } else {
                return EXCEL_REPORT_PATH + File.separator + "Automation_Report.xlsx";
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the file path for the Excel report. If the path is not yet set, it
     * creates a new path using the configuration settings.
     *
     * @return the file path of the Excel report as a String.
     */
    public static String getExcelReportPath() {
        if (excelReportFilePath.isEmpty()) {
            excelReportFilePath = createExcelReportPath();
        }
        return excelReportFilePath;
    }

    /**
     * Sets the name of the report class.
     *
     * @param reportClassName the fully qualified name of the report class to be set
     */
    public static void setReportClassName(String reportClassName) {
        FrameworkConstants.reportClassName = reportClassName;
    }

    /**
     * Retrieves the file path of the configuration properties file.
     *
     * @return the complete file path of the configuration properties file.
     */
    public static String getConfigFilePath() {
        return CONFIG_FILE_PATH + File.separator + "config.properties";
    }

    /**
     * Returns the path of the report file. If the report file path is not already set,
     * it initializes the path by calling the createReportPath method.
     *
     * @return The path of the report file as a String.
     */
    public static String getReportPath() {
        if (reportFilePath.isEmpty()) {
            reportFilePath = createReportPath();
        }
        return reportFilePath;
    }

    /**
     * Retrieves the file path of the test data JSON file.
     *
     * @return the file path of the test data JSON file.
     */
    public static String getTestDataJsonFilePath() {
        return TESTDATAJSONFILEPATH;
    }

    /**
     * Sets the file path for the test data JSON file based on the specified environment name.
     * If the given environment name is null, the file path will be set using the current environment.
     *
     * @param environmentName the name of the environment for which to set the test data JSON file path
     */
    public static void setTestDataJsonFilePath(String environmentName) {
        if (!flag) {
            if (Objects.isNull(environmentName))
                TESTDATAJSONFILEPATH = TESTDATAJSONFILEPATH + File.separator + environmentName.toUpperCase() + "_TestData.json";
            else
                TESTDATAJSONFILEPATH = TESTDATAJSONFILEPATH + File.separator + getEnvironment().toUpperCase() + "_TestData.json";
            flag = true;
        }

    }

    /**
     * Retrieves the file path for the test data JSON file based on the provided environment name.
     *
     * @param environmentName the name of the environment for which the file path is required.
     * @return the file path for the test data JSON file corresponding to the provided environment name.
     *         If the flag is set, an empty string is returned. If the environment name is null, a default
     *         file path is provided.
     */
    public static String getTestDataJsonFilePath(String environmentName) {
        if (!flag) {
            if (Objects.isNull(environmentName))
                return TESTDATAJSONFILEPATH + File.separator + "QA_TestData.json";  // Assuming a default name if environmentName is null
            else
                return TESTDATAJSONFILEPATH + File.separator + environmentName.toUpperCase() + "_TestData.json";
        } else {
            return "";
        }
    }


    /**
     * Retrieves the name of the data Excel sheet.
     *
     * @return the name of the data Excel sheet
     */
    public static String getDataExcelSheet() {
        return DATA_EXCEL_SHEET;
    }

    /**
     * Returns the current environment setting of the application.
     *
     * @return the current environment setting as a string.
     */
    public static String getEnvironment() {
        return ENVIRONMENT;
    }

    /**
     * Sets the environment for the application.
     *
     * @param environment The environment to be set, such as "development", "testing", or "production".
     */
    public static void setEnvironment(String environment) {
        FrameworkConstants.ENVIRONMENT = environment;
    }

    /**
     * Retrieves the duration for explicit wait used in the framework.
     *
     * @return the explicit wait duration as an integer.
     */
    public static int getExplicitWait() {
        return EXPLICIT_WAIT;
    }
}