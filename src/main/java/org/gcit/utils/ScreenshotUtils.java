package org.gcit.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import static org.gcit.driver.DriverManager.getDriver;

public final class ScreenshotUtils {
    private ScreenshotUtils() {
    }

    public static String getBase64Image() {
        return ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.BASE64);
    }
}
