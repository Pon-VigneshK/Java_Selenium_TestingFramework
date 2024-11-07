package org.gcit.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import static org.gcit.driver.DriverManager.getDriver;

/**
 * Utility class for capturing and retrieving screenshots in Base64 format.
 * This class provides a method to capture the current state of the WebDriver
 * as a Base64-encoded string, which can be used for embedding screenshots
 * in logs or reports.
 *
 * <pre>
 * Usage example in log methods:
 * - Logs a passed test step with an optional screenshot.
 * - Attaches a screenshot to a failed test step log.
 * - Includes a screenshot when skipping a test step.
 * </pre>
 *
 * This class is final and cannot be instantiated.
 */
public final class ScreenshotUtils {
    /**
     * Private constructor to prevent instantiation of the ScreenshotUtils class.
     * The ScreenshotUtils class provides static utility methods for capturing
     * and retrieving screenshots in Base64 format. As an utility class, it is
     * designed to be used statically and should not be instantiated.
     */
    private ScreenshotUtils() {
    }

    public static String getBase64Image() {
        return ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.BASE64);
    }
}
