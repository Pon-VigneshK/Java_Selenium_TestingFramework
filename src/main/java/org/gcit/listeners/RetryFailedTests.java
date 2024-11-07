package org.gcit.listeners;

import org.gcit.enums.ConfigProperties;
import org.gcit.utils.PropertyUtils;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * The RetryFailedTests class implements the IRetryAnalyzer interface, enabling retry logic
 * for failed tests based on configurable properties.
 *
 * This class retrieves retry configurations from a properties file through PropertyUtils,
 * allowing tests to be retried a specified number of times. The number of retry attempts is
 * determined by the 'RETRYATTEMPTS' property, and the retry functionality is controlled by
 * the 'RETRY' property, which must be set to 'yes' for retries to occur.
 */
public class RetryFailedTests implements IRetryAnalyzer {
    /**
     * The number of retry attempts to be made for failed tests.
     *
     * This value is fetched from the configuration properties,
     * specifically the 'RETRYATTEMPTS' property. The value is parsed
     * as an integer and used to determine how many times a failed test
     * should be retried.
     */
    private final int retries = Integer.parseInt(PropertyUtils.getValue(ConfigProperties.RETRYATTEMPTS));
    /**
     * The current count of retry attempts for a test.
     * This variable is incremented each time a retry is performed and is
     * used to keep track of the number of times the test has been retried.
     */
    private int count = 0;

    /**
     * Determines whether a test should be retried based on the retry configuration.
     *
     * @param result The result of the test execution, encapsulated in an ITestResult object.
     * @return {@code true} if the test should be retried, {@code false} otherwise.
     */
    @Override
    public boolean retry(ITestResult result) {
        boolean state = false;
        if (PropertyUtils.getValue(ConfigProperties.RETRY).equalsIgnoreCase("yes")) {
            state = count < retries;
            count++;
            return state;
        } else {
            return state;
        }
    }
}
