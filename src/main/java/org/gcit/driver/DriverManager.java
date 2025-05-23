package org.gcit.driver;

import org.openqa.selenium.WebDriver;

import static org.gcit.enums.LogType.INFO;
import static org.gcit.logger.LogService.log;

/**
 * Manages WebDriver instances in a thread-safe manner using {@link ThreadLocal}.
 * <p>
 * This utility class ensures that each thread executing tests has its own isolated
 * WebDriver instance. This is crucial for parallel test execution, preventing interference
 * between tests running concurrently. It provides static methods to get, set, and remove
 * the WebDriver instance associated with the current thread.
 * <p>
 * This class is final and cannot be instantiated.
 *
 * @see org.openqa.selenium.WebDriver
 * @see java.lang.ThreadLocal
 */
public final class DriverManager {
    /**
     * The core {@link ThreadLocal} storage for {@link WebDriver} instances.
     * Each thread that accesses this variable through the static methods of this class
     * will have its own independent copy of a WebDriver instance.
     * This prevents conflicts and shared state issues during parallel test execution.
     */
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private DriverManager() {
    }

    /**
     * Retrieves the {@link WebDriver} instance associated with the current thread.
     * <p>
     * If {@link #setDriver(WebDriver)} has been called previously in the current thread,
     * this method returns the WebDriver instance specific to this thread.
     * If no WebDriver has been set for the current thread (e.g., before {@code initDriver}
     * or after {@code quitDriver}), this method will return {@code null}.
     *
     * @return The {@link WebDriver} instance for the current thread, or {@code null} if one is not set.
     */
    public static WebDriver getDriver() {
        return driver.get();
    }

    /**
     * Associates the given {@link WebDriver} instance with the current thread.
     * <p>
     * This method is typically called by the {@link Driver#initDriver(String)} method after a new
     * WebDriver session has been successfully created. The WebDriver instance is stored in
     * the {@link ThreadLocal} variable, making it accessible via {@link #getDriver()} only
     * within the context of the current thread.
     * <p>
     * A log message is recorded indicating that the WebDriver instance has been set for the current thread.
     * This method has package-private access, intended for use by classes within the {@code org.gcit.driver} package.
     *
     * @param dr The {@link WebDriver} instance to be associated with the current thread.
     *           Should not be {@code null}.
     */
    static void setDriver(WebDriver dr) {
        driver.set(dr);
        log(INFO, "WebDriver instance (" + dr.getClass().getSimpleName() + ") set for thread: " + Thread.currentThread().getId());
    }

    /**
     * Removes the {@link WebDriver} instance associated with the current thread from the
     * {@link ThreadLocal} storage.
     * <p>
     * This method should be called when a WebDriver session is terminated (e.g., in {@link Driver#quitDriver()})
     * to ensure proper cleanup and to prevent memory leaks or stale driver references.
     * After calling this method, subsequent calls to {@link #getDriver()} from the same thread
     * will return {@code null} until {@link #setDriver(WebDriver)} is called again.
     * <p>
     * A log message is recorded indicating that the WebDriver instance has been unloaded for the current thread.
     */
    public static void unloadDriver() {
        WebDriver currentDriver = driver.get();
        if (currentDriver != null) {
            driver.remove();
            log(INFO, "WebDriver instance (" + currentDriver.getClass().getSimpleName() + ") unloaded for thread: " + Thread.currentThread().getId());
        } else {
            log(INFO, "Attempted to unload WebDriver, but no instance was found for thread: " + Thread.currentThread().getId());
        }
    }
}
