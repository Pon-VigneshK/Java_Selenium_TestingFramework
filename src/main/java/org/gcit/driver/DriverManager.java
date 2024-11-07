package org.gcit.driver;

import org.openqa.selenium.WebDriver;

import static org.gcit.enums.LogType.INFO;
import static org.gcit.logger.LogService.log;

/**
 * The DriverManager class provides a thread-safe mechanism for handling WebDriver instances.
 * It uses a ThreadLocal variable to ensure that each thread has its own instance of WebDriver.
 * This class includes methods to get, set, and unload the WebDriver instance for the current thread.
 */
public final class DriverManager {
    /**
     * ThreadLocal variable to ensure each thread has its own instance of WebDriver.
     * This helps in providing thread-safe WebDriver management within the DriverManager class.
     */
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    /**
     * Private constructor to prevent instantiation of the DriverManager class.
     * The DriverManager class is designed to provide a thread-safe mechanism
     * for handling WebDriver instances. It uses a ThreadLocal variable to
     * ensure each thread has its own instance of WebDriver.
     */
    private DriverManager() {
    }

    /**
     * Retrieves the current WebDriver instance for the calling thread.
     *
     * @return the WebDriver instance associated with the current thread, or {@code null} if no WebDriver is set.
     */
    public static WebDriver getDriver() {
        return driver.get();
    }

    /**
     * Sets the WebDriver instance for the current thread.
     *
     * @param dr the WebDriver instance to be set for the current thread
     */
    static void setDriver(WebDriver dr) {
        driver.set(dr);
        log(INFO, "WebDriver instance set for thread: " + Thread.currentThread().getId());
    }

    /**
     * Unloads the WebDriver instance for the current thread.
     *
     * Removes the WebDriver instance assigned to the current thread from the ThreadLocal storage,
     * ensuring that it is cleaned up and no longer associated with the thread. Logs an informational message
     * indicating that the WebDriver instance has been unloaded for the current thread.
     */
    public static void unloadDriver() {
        driver.remove();
        log(INFO, "WebDriver instance unloaded for thread: " + Thread.currentThread().getId());
    }
}
