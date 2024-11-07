package org.gcit.tests.BaseSetup;

import org.gcit.driver.Driver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import static org.gcit.enums.ConfigProperties.BROWSER;
import static org.gcit.utils.PropertyUtils.getValue;

/**
 * BaseTest is a base class for setting up and tearing down browser tests.
 * This class provides common test preparation and cleanup operations
 * using the TestNG framework.
 */
public class BaseTest {
    /**
     * Initializes a new instance of the BaseTest class.
     *
     * This protected constructor ensures that the BaseTest class can only
     * be instantiated by subclasses, providing a common setup for browser tests
     * including web driver initialization and teardown.
     */
    protected BaseTest() {
    }

    /**
     * Sets up the browser environment before each test method.
     *
     * This method initializes the WebDriver instance based on the browser configuration
     * specified in the properties file. It fetches the browser type from the configuration
     * and uses the Driver utility class to initialize the WebDriver.
     *
     * This setup operation runs before every test method, ensuring that the WebDriver
     * is properly configured and ready for the tests to execute.
     */
    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        String configBrowser = getValue(BROWSER);
        Driver.initDriver(configBrowser);
    }

    /**
     * Tears down the WebDriver instance after each test method execution.
     *
     * This method is annotated with @AfterMethod, ensuring it runs after
     * each test method, regardless of the test method outcome. It calls
     * the Driver.quitDriver() method to close and unload the WebDriver instance.
     */
    @AfterMethod(alwaysRun = true)
    protected void tearDown() {
        Driver.quitDriver();
    }
}