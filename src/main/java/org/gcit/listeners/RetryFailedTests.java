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
 *
 * @see org.testng.IRetryAnalyzer
 * @see PropertyUtils
 * @see ConfigProperties#RETRY
 * @see ConfigProperties#RETRYATTEMPTS
 */
public class RetryFailedTests implements IRetryAnalyzer {
    /**
     * The maximum number of retry attempts allowed for a failed test.
     * This value is read from the configuration file using {@link PropertyUtils#getValue(ConfigProperties)}
     * with the key {@link ConfigProperties#RETRYATTEMPTS}. It is parsed as an integer
     * upon class instantiation.
     */
    private final int retries = Integer.parseInt(PropertyUtils.getValue(ConfigProperties.RETRYATTEMPTS));

    /**
     * Tracks the number of times a specific test method has already been retried.
     * This counter is incremented each time the {@link #retry(ITestResult)} method is called
     * for a failing test and determines that a retry should be attempted.
     * <p>
     * Note: An instance of {@code RetryFailedTests} is typically associated with a single {@code @Test} method
     * by TestNG. Therefore, this {@code count} correctly tracks retries for that specific method.
     * If TestNG were to reuse instances across different methods (which is not its default behavior
     * for retry analyzers specified per test), this count might behave unexpectedly.
     */
    private int count = 0;

    /**
     * Determines whether a failed test method should be retried.
     * <p>
     * This method is invoked by TestNG when a test method fails. It checks two conditions:
     * <ol>
     *   <li>The {@code RETRY} configuration property (obtained via {@link PropertyUtils#getValue(ConfigProperties)})
     *       must be set to "yes" (case-insensitive). If not, the test will not be retried.</li>
     *   <li>The current {@link #count} of retry attempts for this test instance must be less than
     *       the maximum number of {@link #retries} allowed.</li>
     * </ol>
     * If both conditions are met, the {@link #count} is incremented, and the method returns {@code true},
     * signaling TestNG to retry the test. Otherwise, it returns {@code false}, and the test
     * will be marked as failed without further retries from this analyzer.
     *
     * @param result The {@link ITestResult} object representing the outcome of the test method
     *               that has just failed. This parameter is not directly used to determine if a retry
     *               should occur in this implementation, but it's available for more complex retry logic
     *               (e.g., retrying based on specific exception types).
     * @return {@code true} if the test method should be retried; {@code false} otherwise.
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
