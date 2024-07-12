package org.gcit.driver;

import org.openqa.selenium.WebDriver;

import static org.gcit.enums.LogType.INFO;
import static org.gcit.logger.LogService.log;

public final class DriverManager {
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    private DriverManager() {
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    static void setDriver(WebDriver dr) {
        driver.set(dr);
        log(INFO, "WebDriver instance set for thread: " + Thread.currentThread().getId());
    }

    public static void unloadDriver() {
        driver.remove();
        log(INFO, "WebDriver instance unloaded for thread: " + Thread.currentThread().getId());
    }
}
