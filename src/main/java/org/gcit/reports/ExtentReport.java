package org.gcit.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.gcit.constants.FrameworkConstants;
import org.gcit.enums.CategoryType;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class ExtentReport {
    private static ExtentReports extentReports;

    private ExtentReport() {

    }

    public static void initReports(String classname) {
        if (Objects.isNull(extentReports)) {
            extentReports = new ExtentReports();
            FrameworkConstants.setReportClassName(classname);
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(FrameworkConstants.getReportPath());
//                    .viewConfigurer()
//                    .viewOrder().apply();
//                    .as(new ViewName[] { ViewName.DASHBOARD, ViewName.TEST })

            extentReports.attachReporter(sparkReporter);
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setDocumentTitle(classname + "_Automation Report");
            sparkReporter.config().setReportName("Automation Testing Report");
        }
    }

    public static void flushReports() throws IOException {
        if (Objects.nonNull(extentReports)) {
            extentReports.flush();
        }
        ExtentManager.unloadExtentTest();
        Desktop.getDesktop().browse(new File(FrameworkConstants.getReportPath()).toURI());
    }

    public static void createTest(String testcasename) {
        ExtentManager.setExtentTest(extentReports.createTest(testcasename));
    }

    public static void addAuthors(String[] authors) {
        for (String temp : authors) {
            ExtentManager.getExtentTest().assignAuthor(temp);
        }
    }

    public static void addCategories(CategoryType[] categories) {

        for (CategoryType temp : categories) {
            ExtentManager.getExtentTest().assignAuthor(temp.toString());
        }

    }

}
