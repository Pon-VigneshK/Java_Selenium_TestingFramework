package org.gcit.driver;

import org.gcit.enums.ConfigProperties;
import org.gcit.utils.PropertyUtils;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.Objects;

public final class Driver {
    /**
     * 
     */
    private Driver() {
    }
    public static void initDriver(String browser) {
        if (Objects.isNull(DriverManager.getDriver())) {
            if (browser.equalsIgnoreCase("chrome")) {
                DriverManager.setDriver(new ChromeDriver());
                DriverManager.getDriver().get(PropertyUtils.getValue(ConfigProperties.URL));
            } else if (browser.equalsIgnoreCase("firfox")) {
                DriverManager.setDriver(new FirefoxDriver());
                DriverManager.getDriver().get(PropertyUtils.getValue(ConfigProperties.URL));
            } else if (browser.equalsIgnoreCase("edge")) {
                DriverManager.setDriver(new EdgeDriver());
                DriverManager.getDriver().get(PropertyUtils.getValue(ConfigProperties.URL));
            }

        }
    }

    public static void quitDriver() {
        if (Objects.nonNull(DriverManager.getDriver())) {
            DriverManager.getDriver().quit();
            DriverManager.unloadDriver();
        }
    }
}
