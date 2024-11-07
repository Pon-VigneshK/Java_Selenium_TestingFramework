package org.gcit.reports;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.gcit.enums.ConfigProperties;
import org.gcit.utils.PropertyUtils;
import org.gcit.utils.ScreenshotUtils;

/**
 * ExtentLogger is a utility class to aid in logging test results to an ExtentReports instance.
 * It provides static methods to log different statuses (pass, fail, skip, info) along with
 * optional screenshots based on configuration settings.
 */
public class ExtentLogger {
    /**
     * Private constructor to prevent instantiation of the ExtentLogger class.
     * ExtentLogger is a utility class that provides methods for logging test results
     * to an ExtentReports instance with support for various logging levels and
     * optional screenshots.
     */
    private ExtentLogger() {
    }

    /**
     * Logs a passed test result with the given message to the ExtentReports instance.
     *
     * @param message the message to log with the passed status
     */
    public static void pass(String message) {
        ExtentManager.getExtentTest().pass(MarkupHelper.createLabel(message, ExtentColor.GREEN));
    }

    /**
     * Logs the given message as a failed test result in the ExtentReports.
     *
     * @param message The message that describes the failure and will be logged in the report.
     */
    public static void fail(String message) {
        ExtentManager.getExtentTest().fail(MarkupHelper.createLabel(message, ExtentColor.RED));
    }

    /**
     * Logs a skip event with the provided message in the ExtentReports instance.
     * This method marks the test as skipped and highlights the message with a yellow label.
     *
     * @param message the message that describes the reason for skipping the test
     */
    public static void skip(String message) {
        ExtentManager.getExtentTest().skip(MarkupHelper.createLabel(message, ExtentColor.YELLOW));
    }

    /**
     * Logs an informational message to the ExtentReports instance.
     *
     * @param message The information message to be logged.
     */
    public static void info(String message) {
        ExtentManager.getExtentTest().info(MarkupHelper.createLabel(message, ExtentColor.BLUE));
    }

    /**
     * Logs a passed test step message to the ExtentReport with an optional screenshot.
     *
     * @param message the message to log for the passed step
     * @param isScreenshotNeeded a boolean flag indicating whether a screenshot is needed
     */
    public static void pass(String message, Boolean isScreenshotNeeded) {
        if (PropertyUtils.getValue(ConfigProperties.PASSEDSTEPSCREENSHOT).equalsIgnoreCase("yes") && isScreenshotNeeded) {
            ExtentManager.getExtentTest().pass(message, MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Image()).build());
        } else {
            pass(message);
        }
    }

    /**
     * Logs a failed status to the ExtentTest instance with an optional screenshot based on configuration.
     *
     * @param message A description of the failure to be logged.
     * @param isScreenshotNeeded Indicates whether a screenshot is required if the failure is logged.
     */
    public static void fail(String message, Boolean isScreenshotNeeded) {
        if (PropertyUtils.getValue(ConfigProperties.FAILEDSTEPSCREENSHOT).equalsIgnoreCase("yes") && isScreenshotNeeded) {
            ExtentManager.getExtentTest().fail(message, MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Image()).build());
        } else {
            fail(message);
        }
    }

    /**
     * Logs a skip event with the provided message in the ExtentReports instance.
     * If screenshots for skipped steps are enabled in the configuration and requested,
     * a screenshot will be attached to the skip log entry.
     *
     * @param message the message that describes the reason for skipping the test
     * @param isScreenshotNeeded a boolean flag indicating whether a screenshot is needed
     */
    public static void skip(String message, Boolean isScreenshotNeeded) {
        if (PropertyUtils.getValue(ConfigProperties.SKIPPEDSTEPSCREENSHOT).equalsIgnoreCase("yes") && isScreenshotNeeded) {
            ExtentManager.getExtentTest().skip(message, MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Image()).build());
        } else {
            skip(message);
        }
    }

}
