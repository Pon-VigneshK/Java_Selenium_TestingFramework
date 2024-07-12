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

public final class ExtentReport {
    private static ExtentReports extentReports;

    private ExtentReport() {

    }

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

    public static void flushReports() throws IOException {
        if (Objects.nonNull(extentReports)) {
            extentReports.flush();
        }
        ExtentManager.unloadExtentTest();
        Desktop.getDesktop().browse(new File(getReportPath()).toURI());
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
