package org.gcit.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import static org.gcit.driver.DriverManager.getDriver;

/**
 * Provides utility methods for capturing screenshots during automated test execution.
 * <p>
 * This class primarily offers a way to get a Base64 encoded string representation
 * of the current state of the WebDriver's viewport. This is particularly useful for
 * embedding screenshots directly into HTML reports (e.g., ExtentReports) or for
 * logging purposes.
 * <p>
 * It relies on the {@link org.gcit.driver.DriverManager} to obtain the current WebDriver instance.
 * The WebDriver instance must implement the {@link org.openqa.selenium.TakesScreenshot} interface
 * for screenshot capabilities.
 *
 * @see org.openqa.selenium.TakesScreenshot
 * @see org.openqa.selenium.OutputType#BASE64
 * @see org.gcit.driver.DriverManager
 */
public final class ScreenshotUtils {
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ScreenshotUtils() {
    }

    /**
     * Captures a screenshot of the current browser viewport and returns it as a Base64 encoded string.
     * <p>
     * This method retrieves the current WebDriver instance via {@link org.gcit.driver.DriverManager#getDriver()}.
     * It then casts this driver to {@link org.openqa.selenium.TakesScreenshot} and invokes
     * {@code getScreenshotAs(OutputType.BASE64)} to capture the image.
     * <p>
     * The resulting Base64 string can be directly embedded into HTML reports, for example,
     * in an {@code <img>} tag: {@code <img src="data:image/png;base64,YOUR_BASE64_STRING_HERE" />}.
     *
     * @return A Base64 encoded string representing the captured PNG image of the current viewport.
     * @throws NullPointerException if {@link org.gcit.driver.DriverManager#getDriver()} returns {@code null}.
     * @throws ClassCastException if the WebDriver instance returned by {@link org.gcit.driver.DriverManager#getDriver()}
     *                            does not implement {@link org.openqa.selenium.TakesScreenshot}.
     *                            This is unlikely with standard desktop browser drivers but could occur with
     *                            custom or specialized WebDriver implementations.
     * @throws org.openqa.selenium.WebDriverException if the screenshot capture fails for other driver-specific reasons.
     */
    public static String getBase64Image() {
        return ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.BASE64);
    }
}
