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
 * A factory class responsible for creating and configuring {@link org.openqa.selenium.WebDriver}
 * instances for different browsers (Chrome, Firefox, Edge).
 * <p>
 * This class determines whether to create a local WebDriver instance or a
 * {@link org.openqa.selenium.remote.RemoteWebDriver} based on the {@code RUNMODE}
 * configuration property (retrieved via {@link org.gcit.utils.PropertyUtils}).
 * If "remote", it uses the {@code SELENIUMGRIDADDRESS} property for the RemoteWebDriver URL.
 * <p>
 * It applies various browser-specific configurations, including:
 * <ul>
 *   <li>Headless mode (if {@code BROWSERHEADLESSMODE} property is "Yes").</li>
 *   <li>Arguments to start maximized, disable GPU, disable DevShmUsage, etc.</li>
 *   <li>Experimental options (e.g., excluding "disable-popup-blocking" switch for Chrome/Edge).</li>
 *   <li>Specific Firefox profile settings for certificates and page load strategy.</li>
 * </ul>
 * The created WebDriver instance is typically then managed by {@link org.gcit.driver.DriverManager}.
 * This class is final and cannot be instantiated.
 *
 * @see org.openqa.selenium.WebDriver
 * @see org.openqa.selenium.chrome.ChromeDriver
 * @see org.openqa.selenium.firefox.FirefoxDriver
 * @see org.openqa.selenium.edge.EdgeDriver
 * @see org.openqa.selenium.remote.RemoteWebDriver
 * @see org.gcit.utils.PropertyUtils
 * @see org.gcit.enums.ConfigProperties
 * @see org.gcit.driver.DriverManager
 */
public final class DriverSupplier {

    /**
     * Logger tag for this class, using its simple name.
     */
    private static final String LOG_TAG = DriverSupplier.class.getSimpleName();

    /**
     * Private constructor to prevent instantiation of this factory class.
     */
    private DriverSupplier() {
    }

    /**
     * Creates and returns a {@link WebDriver} instance configured for the specified browser
     * and execution mode (local or remote).
     * <p>
     * This method first checks if a driver is already initialized for the current thread via
     * {@link DriverManager#getDriver()}. If so, it throws a {@link RuntimeException} to prevent
     * re-initialization.
     * <p>
     * Based on the {@code browser} parameter (case-insensitive "chrome", "edge", or "firefox")
     * and the {@code RUNMODE} configuration property:
     * <ul>
     *   <li><b>Local Execution:</b> Instantiates a local {@link ChromeDriver}, {@link EdgeDriver}, or {@link FirefoxDriver}
     *       with appropriate {@link ChromeOptions}, {@link EdgeOptions}, or {@link FirefoxOptions}.</li>
     *   <li><b>Remote Execution:</b> Instantiates a {@link RemoteWebDriver} pointing to the URL specified
     *       by the {@code SELENIUMGRIDADDRESS} property, using the browser-specific options.</li>
     * </ul>
     * Common configurations applied (where applicable):
     * <ul>
     *   <li>Headless mode if {@code BROWSERHEADLESSMODE} is "Yes".</li>
     *   <li>Arguments: {@code --start-maximized}, {@code --disable-dev-shm-usage}, {@code --disable-gpu}.</li>
     *   <li>Chrome/Edge: Excludes "disable-popup-blocking" switch.</li>
     *   <li>Firefox: Additional arguments (e.g., {@code --disable-web-security}) and profile settings
     *       (e.g., accepting untrusted certificates).</li>
     * </ul>
     * After successful instantiation, the browser window is set to a default size of 1920x1080.
     *
     * @param browser The name of the browser for which to create a WebDriver instance
     *                (case-insensitive "chrome", "edge", or "firefox").
     * @return A configured {@link WebDriver} instance for the specified browser and run mode.
     * @throws MalformedURLException If {@code RUNMODE} is "remote" and the {@code SELENIUMGRIDADDRESS}
     *                               property provides a malformed URL for the Selenium Grid.
     * @throws RuntimeException If the specified {@code browser} is unsupported, if a driver is already
     *                          initialized for the current thread, or if any other error occurs during
     *                          WebDriver initialization (wrapping the original exception).
     * @see ConfigProperties#RUNMODE
     * @see ConfigProperties#BROWSERHEADLESSMODE
     * @see ConfigProperties#SELENIUMGRIDADDRESS
     * @see DriverManager#getDriver()
     */
    public static WebDriver getWebDriver(String browser) throws MalformedURLException {
        WebDriver driver = null; // Initialize to null
        String runMode = getValue(RUNMODE); // Fetches "local" or "remote"
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
