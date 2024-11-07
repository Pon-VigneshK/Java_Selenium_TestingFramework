package org.gcit.driver;

import org.gcit.enums.ConfigProperties;
import org.gcit.enums.LogType;
import org.gcit.exceptions.BrowserInvocationException;
import org.gcit.factories.DriverSupplier;
import org.gcit.utils.PropertyUtils;

import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.gcit.enums.LogType.INFO;
import static org.gcit.logger.LogService.log;

/**
 * The Driver class is a utility for initializing and quitting WebDriver instances.
 * It provides static methods to handle WebDriver setup and teardown operations.
 */
public final class Driver {
    /**
     * Private constructor to prevent instantiation of the Driver class.
     * This class is intended to be used as a utility class with static methods only.
     */
    private Driver() {
    }

    /**
     * Initializes the WebDriver instance for the specified browser and navigates to the configured URL.
     *
     * @param browser the name of the browser to initialize (e.g., "chrome", "firefox", "edge")
     * @throws BrowserInvocationException if the WebDriver instance cannot be created
     * @throws RuntimeException if an error occurs while navigating to the URL
     */
    public static void initDriver(String browser) {
        try {
            DriverManager.setDriver(DriverSupplier.getWebDriver(browser));
            log(INFO, "Launched the " + browser + " " + LocalDateTime.now().toString());

        } catch (MalformedURLException e) {
            throw new BrowserInvocationException("Browser invocation failed, please check capabilities");
        }
        try {
            String url = PropertyUtils.getValue(ConfigProperties.URL);
            DriverManager.getDriver().get(url);
            log(INFO, "Launched the URL: " + url);

        } catch (Exception e) {
            log(LogType.ERROR, "Failed to navigate to URL: " + e.getMessage());
            quitDriver();
            throw e;
        }
    }

    /**
     * Closes the WebDriver instance managed by DriverManager, logs the closure,
     * and unloads the WebDriver instance.
     *
     * If there is an exception during the quit operation,
     * logs the error message and still proceeds to unload the WebDriver instance.
     */
    public static void quitDriver() {
        if (Objects.nonNull(DriverManager.getDriver())) {
            try {
                DriverManager.getDriver().quit();
                log(INFO, "Browser Closed " + LocalDateTime.now());
            } catch (Exception e) {
                log(LogType.ERROR, "Failed to close browser: " + e.getMessage());
            } finally {
                DriverManager.unloadDriver();
            }


        }
    }


}
