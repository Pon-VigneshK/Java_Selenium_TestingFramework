package org.gcit.reports;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.gcit.enums.ConfigProperties;
import org.gcit.utils.PropertyUtils;
import org.gcit.utils.ScreenshotUtils;

public class ExtentLogger {
    private ExtentLogger() {
    }

    public static void pass(String message) {
        ExtentManager.getExtentTest().pass(MarkupHelper.createLabel(message, ExtentColor.GREEN));
    }

    public static void fail(String message) {
        ExtentManager.getExtentTest().fail(MarkupHelper.createLabel(message, ExtentColor.RED));
    }

    public static void skip(String message) {
        ExtentManager.getExtentTest().skip(MarkupHelper.createLabel(message, ExtentColor.YELLOW));
    }

    public static void info(String message) {
        ExtentManager.getExtentTest().info(MarkupHelper.createLabel(message, ExtentColor.BLUE));
    }

    public static void pass(String message, Boolean isScreenshotNeeded) {
        if (PropertyUtils.getValue(ConfigProperties.PASSEDSTEPSCREENSHOT).equalsIgnoreCase("yes") && isScreenshotNeeded) {
            ExtentManager.getExtentTest().pass(message, MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Image()).build());
        } else {
            pass(message);
        }
    }

    public static void fail(String message, Boolean isScreenshotNeeded) {
        if (PropertyUtils.getValue(ConfigProperties.FAILEDSTEPSCREENSHOT).equalsIgnoreCase("yes") && isScreenshotNeeded) {
            ExtentManager.getExtentTest().fail(message, MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Image()).build());
        } else {
            fail(message);
        }
    }

    public static void skip(String message, Boolean isScreenshotNeeded) {
        if (PropertyUtils.getValue(ConfigProperties.SKIPPEDSTEPSCREENSHOT).equalsIgnoreCase("yes") && isScreenshotNeeded) {
            ExtentManager.getExtentTest().skip(message, MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Image()).build());
        } else {
            skip(message);
        }
    }

}
