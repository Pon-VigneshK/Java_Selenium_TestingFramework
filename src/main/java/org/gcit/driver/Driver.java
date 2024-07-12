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

public final class Driver {
    private Driver() {
    }

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
