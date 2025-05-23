package org.gcit.reports;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.gcit.enums.ConfigProperties;
import org.gcit.utils.PropertyUtils;
import org.gcit.utils.ScreenshotUtils;

/**
 * Utility class providing static methods to log messages and events to the
 * current {@link com.aventstack.extentreports.ExtentTest} instance managed by {@link ExtentManager}.
 * <p>
 * This class acts as a facade for logging test steps with various statuses (PASS, FAIL, SKIP, INFO)
 * to the Extent report. It supports optional inclusion of screenshots based on both a global
 * configuration setting (e.g., {@link ConfigProperties#PASSEDSTEPSCREENSHOT}) and a per-call boolean flag.
 * Messages are formatted with HTML labels for better visual distinction in the report.
 * <p>
 * This class is final and cannot be instantiated.
 *
 * @see ExtentManager
 * @see ExtentReport
 * @see ScreenshotUtils
 * @see ConfigProperties
 * @see com.aventstack.extentreports.ExtentTest
 * @see com.aventstack.extentreports.MediaEntityBuilder
 * @see com.aventstack.extentreports.markuputils.MarkupHelper
 */
public class ExtentLogger {
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ExtentLogger() {
    }

    /**
     * Logs a message with PASS status to the current Extent test.
     * The message is styled with a green label in the report.
     *
     * @param message The descriptive message for the passed step or assertion.
     */
    public static void pass(String message) {
        ExtentManager.getExtentTest().pass(MarkupHelper.createLabel(message, ExtentColor.GREEN));
    }

    /**
     * Logs a message with FAIL status to the current Extent test.
     * The message is styled with a red label in the report.
     *
     * @param message The descriptive message for the failed step or assertion.
     */
    public static void fail(String message) {
        ExtentManager.getExtentTest().fail(MarkupHelper.createLabel(message, ExtentColor.RED));
    }

    /**
     * Logs a message with SKIP status to the current Extent test.
     * The message is styled with a yellow label in the report.
     *
     * @param message The descriptive message for the skipped step or test.
     */
    public static void skip(String message) {
        ExtentManager.getExtentTest().skip(MarkupHelper.createLabel(message, ExtentColor.YELLOW));
    }

    /**
     * Logs an informational message to the current Extent test.
     * The message is styled with a blue label in the report.
     *
     * @param message The informational message to log.
     */
    public static void info(String message) {
        ExtentManager.getExtentTest().info(MarkupHelper.createLabel(message, ExtentColor.BLUE));
    }

    /**
     * Logs a message with PASS status to the current Extent test, optionally including a screenshot.
     * <p>
     * A screenshot is attached if both the global configuration property
     * {@link ConfigProperties#PASSEDSTEPSCREENSHOT} is set to "yes" (case-insensitive)
     * AND the {@code isScreenshotNeeded} parameter is {@code true}.
     * If a screenshot is attached, it's captured using {@link ScreenshotUtils#getBase64Image()}.
     *
     * @param message            The descriptive message for the passed step or assertion.
     * @param isScreenshotNeeded A boolean flag indicating whether a screenshot should be attempted
     *                           for this specific log entry, subject to global configuration.
     */
    public static void pass(String message, Boolean isScreenshotNeeded) {
        if (PropertyUtils.getValue(ConfigProperties.PASSEDSTEPSCREENSHOT).equalsIgnoreCase("yes") && isScreenshotNeeded) {
            ExtentManager.getExtentTest().pass(message, MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Image()).build());
        } else {
            pass(message); // Call the version without screenshot
        }
    }

    /**
     * Logs a message with FAIL status to the current Extent test, optionally including a screenshot.
     * <p>
     * A screenshot is attached if both the global configuration property
     * {@link ConfigProperties#FAILEDSTEPSCREENSHOT} is set to "yes" (case-insensitive)
     * AND the {@code isScreenshotNeeded} parameter is {@code true}.
     * If a screenshot is attached, it's captured using {@link ScreenshotUtils#getBase64Image()}.
     *
     * @param message            The descriptive message for the failed step or assertion.
     * @param isScreenshotNeeded A boolean flag indicating whether a screenshot should be attempted
     *                           for this specific log entry, subject to global configuration.
     */
    public static void fail(String message, Boolean isScreenshotNeeded) {
        if (PropertyUtils.getValue(ConfigProperties.FAILEDSTEPSCREENSHOT).equalsIgnoreCase("yes") && isScreenshotNeeded) {
            ExtentManager.getExtentTest().fail(message, MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Image()).build());
        } else {
            fail(message); // Call the version without screenshot
        }
    }

    /**
     * Logs a message with SKIP status to the current Extent test, optionally including a screenshot.
     * <p>
     * A screenshot is attached if both the global configuration property
     * {@link ConfigProperties#SKIPPEDSTEPSCREENSHOT} is set to "yes" (case-insensitive)
     * AND the {@code isScreenshotNeeded} parameter is {@code true}.
     * If a screenshot is attached, it's captured using {@link ScreenshotUtils#getBase64Image()}.
     *
     * @param message            The descriptive message for the skipped step or test.
     * @param isScreenshotNeeded A boolean flag indicating whether a screenshot should be attempted
     *                           for this specific log entry, subject to global configuration.
     */
    public static void skip(String message, Boolean isScreenshotNeeded) {
        if (PropertyUtils.getValue(ConfigProperties.SKIPPEDSTEPSCREENSHOT).equalsIgnoreCase("yes") && isScreenshotNeeded) {
            ExtentManager.getExtentTest().skip(message, MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Image()).build());
        } else {
            skip(message); // Call the version without screenshot
        }
    }
}
