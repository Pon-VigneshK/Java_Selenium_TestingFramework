package org.gcit.constants;

import org.gcit.enums.ConfigProperties;
import org.gcit.utils.PropertyUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public final class FrameworkConstants {
    /**
     * FrameworkConstants holds all the constant values used within the framework. If some value needs to be changed
     * or modified often, then it should be stored in the property files.
     */

// Paths for different resources and report

    private static final String USER_DIR = System.getProperty("user.dir");
    private static final String TEST_RESOURCES_PATH = USER_DIR + File.separator + "src" + File.separator + "test" + File.separator + "resources";
    private static final String CONFIG_FILE_PATH = TEST_RESOURCES_PATH + File.separator + "config";
    private static final String CHROME_DRIVER_PATH = TEST_RESOURCES_PATH + File.separator + "executables" + File.separator + "chromedriver.exe";
    // File paths for test data and configuration
    private static final String EXCEL_PATH = TEST_RESOURCES_PATH + File.separator + "testdata" + File.separator + "excel" + File.separator + "testdata.xlsx";
    private static String TESTDATAJSONFILEPATH = TEST_RESOURCES_PATH + File.separator + "testdata" + File.separator + "json";
    private static final String DATABASE_PATH = TEST_RESOURCES_PATH + File.separator + "testdata" + File.separator + "database" + File.separator + "inputData.db";
    private static final String MAIN_RESOURCES_PATH = USER_DIR + File.separator + "src" + File.separator + "main" + File.separator + "resources";
    private static final String TESTCASE_JSON_PATH = MAIN_RESOURCES_PATH + File.separator + "runnerlist" + File.separator + "runmanager.json";
    private static final String DBConfig_JSON_FILEPATH = MAIN_RESOURCES_PATH + File.separator + "Configuration" + File.separator + "DataBaseConfig.json";
    // Other constants
    private static final String SELECTQUERY_JSON_FILEPATH = MAIN_RESOURCES_PATH + File.separator + "Configuration" + File.separator + "SqlQueries.json";
    private static final String EXTENT_REPORT_PATH = USER_DIR + File.separator + "extent-test-output";
    private static final String EXCEL_REPORT_PATH = USER_DIR + File.separator + "excel-test-report";
    private static final int EXPLICIT_WAIT = Integer.parseInt(PropertyUtils.getValue(ConfigProperties.EXPLICITWAITTIMEOUT));
    // Date and time formatting for reports
    private static final Date CURRENT_DATE = new Date();
    private static final String TODAY_DATE_TIME = new SimpleDateFormat("MMM-dd-yyyy_HH_mm_ss_SSS").format(CURRENT_DATE);
    // Sheet names in Excel files
    private static final String DATA_EXCEL_SHEET = "DATA";
    private static final String RUNMANGER_EXCEL_SHEET = "RUNMANAGER";
    private static final String RUNMANAGER = "RunManager";
    private static final String TESTCASE_LIST = "testCaseLists";
    private static boolean flag = false;
    private static String reportClassName = "";
    private static String reportFilePath = "";
    private static String ENVIRONMENT = PropertyUtils.getValue(ConfigProperties.ENV);
    private static String excelReportFilePath = "";

    /**
     * Constants class for storing framework-related paths and values.
     */

    public static String getRunmangerExcelSheet() {
        return RUNMANGER_EXCEL_SHEET;
    }

    public static String getTestdataJsonFilepath() {
        return TESTDATAJSONFILEPATH;
    }

    public static String getDBConfigJSONPath() {
        return DBConfig_JSON_FILEPATH;
    }

    public static String getExcelFilePath() {
        return EXCEL_PATH;
    }

    public static String getTodayDateTime() {
        return TODAY_DATE_TIME;
    }

    public static String getTestCaseJsonPath() {
        return TESTCASE_JSON_PATH;
    }

    public static String getTestcaselist() {
        return TESTCASE_LIST;
    }

    public static String getRunmanager() {
        return RUNMANAGER;
    }

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

    public static String getChromeDriverPath() {
        return CHROME_DRIVER_PATH;
    }

    public static String getSqlQueryjsonfilepath() {
        return SELECTQUERY_JSON_FILEPATH;
    }

    public static String getDatabasePath() {
        return DATABASE_PATH;
    }

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

    public static String getExcelReportPath() {
        if (excelReportFilePath.isEmpty()) {
            excelReportFilePath = createExcelReportPath();
        }
        return excelReportFilePath;
    }

    public static void setReportClassName(String reportClassName) {
        FrameworkConstants.reportClassName = reportClassName;
    }

    public static String getConfigFilePath() {
        return CONFIG_FILE_PATH + File.separator + "config.properties";
    }

    public static String getReportPath() {
        if (reportFilePath.isEmpty()) {
            reportFilePath = createReportPath();
        }
        return reportFilePath;
    }

    public static String getTestDataJsonFilePath() {
        return TESTDATAJSONFILEPATH;
    }

    public static void setTestDataJsonFilePath(String environmentName) {
        if (!flag) {
            if (Objects.isNull(environmentName))
                TESTDATAJSONFILEPATH = TESTDATAJSONFILEPATH + File.separator + environmentName.toUpperCase() + "_TestData.json";
            else
                TESTDATAJSONFILEPATH = TESTDATAJSONFILEPATH + File.separator + getEnvironment().toUpperCase() + "_TestData.json";
            flag = true;
        }

    }

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


    public static String getDataExcelSheet() {
        return DATA_EXCEL_SHEET;
    }

    public static String getEnvironment() {
        return ENVIRONMENT;
    }

    public static void setEnvironment(String environment) {
        FrameworkConstants.ENVIRONMENT = environment;
    }

    public static int getExplicitWait() {
        return EXPLICIT_WAIT;
    }
}