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
 * Manages the lifecycle of a {@link org.openqa.selenium.WebDriver} instance for test execution.
 * <p>
 * This final utility class provides static methods to initialize ({@link #initDriver(String)})
 * and terminate ({@link #quitDriver()}) WebDriver sessions. It acts as a high-level orchestrator,
 * delegating driver instantiation to {@link org.gcit.factories.DriverSupplier} and
 * thread-safe WebDriver storage to {@link DriverManager}.
 * <p>
 * The behavior of {@code initDriver}, particularly whether it creates a local or remote WebDriver,
 * is influenced by configuration properties such as {@code RUNMODE} (e.g., "local", "remote")
 * and {@code SELENIUMGRIDADDRESS} (if remote).
 *
 * @see DriverManager
 * @see org.gcit.factories.DriverSupplier
 * @see PropertyUtils
 * @see ConfigProperties
 */
public final class Driver {
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Driver() {
    }

    /**
     * Initializes a WebDriver session for the specified browser and navigates to the application URL.
     * <p>
     * The method performs the following steps:
     * <ol>
     *   <li>Obtains a WebDriver instance for the given {@code browser} type (e.g., "chrome", "firefox")
     *       using {@link DriverSupplier#getWebDriver(String)}. This may create a local or remote WebDriver
     *       based on framework configuration (e.g., {@code RUNMODE} property).</li>
     *   <li>Stores the created WebDriver instance in a thread-safe manner using {@link DriverManager#setDriver(org.openqa.selenium.WebDriver)}.</li>
     *   <li>Retrieves the application URL from configuration using {@link PropertyUtils#getValue(ConfigProperties)}
     *       (key: {@link ConfigProperties#URL}).</li>
     *   <li>Navigates the browser to this URL using {@code driver.get(url)}.</li>
     * </ol>
     * Appropriate logging is performed at each step.
     *
     * @param browser The name of the browser to initialize (e.g., "chrome", "firefox").
     *                The actual browser instantiation logic, including handling local vs. remote
     *                and specific browser options, is managed by {@link DriverSupplier}.
     * @throws BrowserInvocationException If a {@link MalformedURLException} occurs during WebDriver setup
     *                                    (typically related to remote WebDriver URL configuration),
     *                                    indicating an issue with browser invocation capabilities.
     * @throws RuntimeException If any other exception occurs during URL navigation (e.g., invalid URL,
     *                          page load timeout if not handled by WebDriver itself), this method
     *                          will attempt to quit the driver and then re-throw the exception.
     *                          Also wraps underlying exceptions from {@link DriverSupplier} if they are not
     *                          {@link MalformedURLException}.
     * @see DriverSupplier#getWebDriver(String)
     * @see DriverManager#setDriver(org.openqa.selenium.WebDriver)
     * @see DriverManager#getDriver()
     * @see PropertyUtils#getValue(ConfigProperties)
     * @see ConfigProperties#URL
     */
    public static void initDriver(String browser) {
        try {
            DriverManager.setDriver(DriverSupplier.getWebDriver(browser)); // Delegates to DriverSupplier
            log(INFO, "Launched the " + browser + " browser. Timestamp: " + LocalDateTime.now().toString());

        } catch (MalformedURLException e) { // Specific exception from RemoteWebDriver setup
            log(LogType.ERROR, "Malformed URL encountered during WebDriver setup for " + browser + ". Check SELENIUMGRIDADDRESS.", e);
            throw new BrowserInvocationException("Browser invocation failed due to MalformedURLException. Please check remote WebDriver URL capabilities and configuration.", e);
        } catch (Exception e) { // Catch other potential exceptions from DriverSupplier or DriverManager.setDriver
            log(LogType.ERROR, "Failed to initialize WebDriver for " + browser + ".", e);
            // Ensure driver is cleaned up if partially initialized or if setDriver failed
            if (DriverManager.getDriver() != null) {
                quitDriver();
            }
            throw new BrowserInvocationException("Browser invocation failed for " + browser + ". Reason: " + e.getMessage(), e);
        }

        try {
            String url = PropertyUtils.getValue(ConfigProperties.URL);
            DriverManager.getDriver().get(url);
            log(INFO, "Navigated to URL: " + url);

        } catch (Exception e) { // Catch exceptions during URL navigation
            log(LogType.ERROR, "Failed to navigate to URL. Details: " + e.getMessage(), e);
            quitDriver(); // Attempt to clean up the driver if URL navigation fails
            throw new RuntimeException("Failed to navigate to the configured URL. Original error: " + e.getMessage(), e);
        }
    }

    /**
     * Quits the currently active WebDriver instance for the current thread and cleans up associated resources.
     * <p>
     * This method performs the following actions:
     * <ol>
     *   <li>Retrieves the WebDriver instance for the current thread from {@link DriverManager#getDriver()}.</li>
     *   <li>If a driver instance exists:
     *     <ul>
     *       <li>Attempts to quit the browser session using {@code driver.quit()}.</li>
     *       <li>Logs the closure event or any error encountered during the quit operation.</li>
     *       <li>Regardless of success or failure in quitting, it calls {@link DriverManager#unloadDriver()}
     *           to remove the WebDriver instance from the current thread's storage. This helps prevent
     *           stale driver references.</li>
     *     </ul>
     *   </li>
     *   <li>If no driver instance exists for the current thread, it logs this information.</li>
     * </ol>
     * Exceptions during the {@code driver.quit()} operation are caught and logged, but do not prevent
     * the subsequent call to {@link DriverManager#unloadDriver()}.
     *
     * @see DriverManager#getDriver()
     * @see DriverManager#unloadDriver()
     * @see org.openqa.selenium.WebDriver#quit()
     */
    public static void quitDriver() {
        if (Objects.nonNull(DriverManager.getDriver())) {
            try {
                DriverManager.getDriver().quit();
                log(INFO, "Browser closed successfully. Timestamp: " + LocalDateTime.now());
            } catch (Exception e) {
                // Log the exception but proceed to unload the driver to prevent resource leaks
                log(LogType.ERROR, "Failed to close browser cleanly. Error: " + e.getMessage(), e);
            } finally {
                // Always unload the driver from ThreadLocal storage
                DriverManager.unloadDriver();
                log(INFO, "WebDriver instance unloaded from ThreadLocal storage.");
            }
        } else {
            log(INFO, "Attempted to quit driver, but no WebDriver instance was found for the current thread.");
        }
    }
}
