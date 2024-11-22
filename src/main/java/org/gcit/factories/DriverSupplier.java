package org.gcit.factories;

import org.gcit.driver.DriverManager;
import org.gcit.enums.ConfigProperties;
import org.gcit.enums.LogType;
import org.gcit.logger.LogService;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import static org.gcit.enums.ConfigProperties.RUNMODE;
import static org.gcit.utils.PropertyUtils.getValue;

/**
 * Utility class to provide a WebDriver instance based on the specified browser type.
 * It supports Chrome, Firefox, and Edge browsers and can initialize drivers for both
 * local and remote execution environments as defined by the configuration properties.
 *
 * Initialization settings include options for headless mode, window size adjustments,
 * and various browser-specific configurations to ensure optimal test execution.
 */
public final class DriverSupplier {

    /**
     * A constant representing the log tag for the DriverSupplier class.
     * This is used for identifying the source of log messages specific to DriverSupplier.
     */
    private static final String LOG_TAG = DriverSupplier.class.getSimpleName();

    /**
     * Private constructor to prevent instantiation of the DriverSupplier class.
     * This class provides utility methods to supply WebDriver instances based on the provided browser type.
     */
    private DriverSupplier() {
    }

    /**
     * Initializes and returns a WebDriver instance based on the specified browser type.
     * The method configures the WebDriver options for Chrome, Edge, and Firefox
     * to support both local and remote execution modes.
     *
     * @param browser the name of the browser for which the WebDriver is to be initialized.
     *                Supported values are "chrome", "edge", and "firefox".
     * @return the initialized WebDriver instance.
     * @throws MalformedURLException if the URL for the remote WebDriver is malformed.
     * @throws RuntimeException if the browser type is unsupported or if there is an error initializing the WebDriver.
     */
    public static WebDriver getWebDriver(String browser) throws MalformedURLException {
        WebDriver driver = null;
        String runMode = getValue(RUNMODE);
//        String runMode = "local";

        if (Objects.isNull(DriverManager.getDriver())) {
            try {
                switch (browser.toLowerCase()) {
                    case "chrome":
                        final ChromeOptions chromeOptions = new ChromeOptions();
                        if (getValue(ConfigProperties.BROWSERHEADLESSMODE).equalsIgnoreCase("Yes")) {
                            chromeOptions.addArguments("--headless");
                        }
                        chromeOptions.addArguments("--start-maximized");
                        chromeOptions.setExperimentalOption("excludeSwitches", List.of("disable-popup-blocking"));
                        chromeOptions.addArguments("--disable-dev-shm-usage");
                        chromeOptions.addArguments("--disable-gpu");
                        //chromeOptions.addArguments("--window-size","1920,1080");
                        if (runMode.equalsIgnoreCase("remote")) {
                            driver = new RemoteWebDriver(new URL(getValue(ConfigProperties.SELENIUMGRIDADDRESS)), chromeOptions);
                        } else {
                            driver = new ChromeDriver(chromeOptions);
                        }
                        break;

                    case "edge":
                        final EdgeOptions edgeOptions = new EdgeOptions();
                        if (getValue(ConfigProperties.BROWSERHEADLESSMODE).equalsIgnoreCase("Yes")) {
                            edgeOptions.addArguments("--headless");
                        }
                        edgeOptions.addArguments("--start-maximized");
                        edgeOptions.setExperimentalOption("excludeSwitches", List.of("disable-popup-blocking"));
                        edgeOptions.addArguments("--disable-dev-shm-usage");
                        edgeOptions.addArguments("--disable-gpu");
                        //edgeOptions.addArguments("--window-size","1920,1080");
                        if (runMode.equalsIgnoreCase("remote")) {
                            driver = new RemoteWebDriver(new URL(getValue(ConfigProperties.SELENIUMGRIDADDRESS)), edgeOptions);
                        } else {
                            driver = new EdgeDriver(edgeOptions);
                        }
                        break;

                    case "firefox":
                        final FirefoxOptions firefoxOptions = new FirefoxOptions();
                        if (getValue(ConfigProperties.BROWSERHEADLESSMODE).equalsIgnoreCase("Yes")) {
                            firefoxOptions.addArguments("--headless");
                        }
                        final FirefoxProfile profile = new FirefoxProfile();
                        firefoxOptions.addArguments("--start-maximized");
                        firefoxOptions.addArguments("--disable-web-security");
                        firefoxOptions.addArguments("--allow-running-insecure-content");
                        firefoxOptions.addArguments("--no-sandbox");
                        firefoxOptions.addArguments("--disable-dev-shm-usage");
                        firefoxOptions.addArguments("--disable-gpu");
                        // firefoxOptions.addArguments("--window-size","1920,1080");
                        profile.setAcceptUntrustedCertificates(true);
                        profile.setAssumeUntrustedCertificateIssuer(false);
                        profile.setPreference("pageLoadStrategy", "normal");
                        firefoxOptions.setProfile(profile);

                        if (runMode.equalsIgnoreCase("remote")) {
                            driver = new RemoteWebDriver(new URL(getValue(ConfigProperties.SELENIUMGRIDADDRESS)), firefoxOptions);
                        } else {
                            driver = new FirefoxDriver(firefoxOptions);
                        }
                        break;

                    default:
                        throw new RuntimeException("Unsupported browser: " + browser);
                }

                if (driver != null) {
                    driver.manage().window().setSize(new Dimension(1920, 1080));
                    LogService.log(LogType.INFO, LOG_TAG + ": WebDriver initialized for " + browser);
                }

            } catch (Exception e) {
                LogService.log(LogType.ERROR, LOG_TAG + ": Error initializing WebDriver for " + browser + " - " + e.getMessage());
                throw new RuntimeException("Error initializing WebDriver for " + browser, e);
            }

        } else {
            LogService.log(LogType.ERROR, LOG_TAG + ": WebDriver is already initialized.");
            throw new RuntimeException("Driver is already initialized.");
        }

        return driver;
    }
}
