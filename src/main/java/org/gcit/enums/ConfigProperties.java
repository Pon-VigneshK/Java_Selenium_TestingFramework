package org.gcit.enums;

/**
 * Enum representing different configuration properties used in the application.
 * This enum defines various keys that can be retrieved from the configuration properties file.
 *
 * Each enum constant corresponds to a specific configuration setting such as URL,
 * browser settings, screenshot preferences, retry attempts, and execution environment.
 */
public enum ConfigProperties {
    URL,
    OVERRIDEREPORTS,
    PASSEDSTEPSCREENSHOT,
    FAILEDSTEPSCREENSHOT,
    SKIPPEDSTEPSCREENSHOT,
    RETRY,
    BROWSER,
    RETRYATTEMPTS,
    ENV,
    DATASOURCE,
    EXPLICITWAITTIMEOUT,
    BROWSERHEADLESSMODE,
    SELENIUMGRIDADDRESS,
    RUNMODE;
}
