package org.gcit.enums;

/**
 * Enum representing different configuration properties used in the application.
 * This enum defines various keys that can be retrieved from the configuration properties file.
 *
 * Each enum constant corresponds to a specific configuration setting such as URL,
 * browser settings, screenshot preferences, retry attempts, and execution environment.
 */
public enum ConfigProperties {
    /**
     * The base URL of the application under test.
     */
    URL,
    /**
     * Configuration to determine if existing test reports should be overridden (e.g., "yes" or "no").
     * If "no", new reports are typically timestamped.
     */
    OVERRIDEREPORTS,
    /**
     * Configuration to enable or disable screenshots for passed test steps (e.g., "yes" or "no").
     */
    PASSEDSTEPSCREENSHOT,
    /**
     * Configuration to enable or disable screenshots for failed test steps (e.g., "yes" or "no").
     */
    FAILEDSTEPSCREENSHOT,
    /**
     * Configuration to enable or disable screenshots for skipped test steps (e.g., "yes" or "no").
     */
    SKIPPEDSTEPSCREENSHOT,
    /**
     * General flag or specific configuration related to retrying tests.
     * Consider clarifying its specific use if {@link #RETRYATTEMPTS} is also used.
     */
    RETRY,
    /**
     * Specifies the browser to be used for test execution (e.g., "chrome", "firefox").
     */
    BROWSER,
    /**
     * The number of times a failed test case should be retried.
     */
    RETRYATTEMPTS,
    /**
     * Specifies the current execution environment (e.g., "QA", "DEV", "PROD").
     * This can influence which test data or configurations are used.
     */
    ENV,
    /**
     * Defines the source from which test data should be loaded (e.g., "db", "excel", "json").
     */
    DATASOURCE,
    /**
     * The default timeout duration (in seconds) for explicit waits used in web automation.
     */
    EXPLICITWAITTIMEOUT,
    /**
     * Configuration to run the browser in headless mode (e.g., "true" or "false").
     */
    BROWSERHEADLESSMODE,
    /**
     * The URL of the Selenium Grid hub, used for remote test execution.
     */
    SELENIUMGRIDADDRESS,
    /**
     * Defines the mode of test execution, typically "local" or "remote" (for Selenium Grid).
     */
    RUNMODE;
}
