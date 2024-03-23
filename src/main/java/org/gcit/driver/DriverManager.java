package org.gcit.driver;

import org.openqa.selenium.WebDriver;

public final class DriverManager {
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    private DriverManager() {
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void setDriver(WebDriver dr) {
        driver.set(dr);
    }

    public static void unloadDriver() {
        driver.remove();
    }
}
