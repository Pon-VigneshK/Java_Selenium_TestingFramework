package org.gcit.listeners;

import org.gcit.enums.ConfigProperties;
import org.gcit.utils.PropertyUtils;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryFailedTests implements IRetryAnalyzer {
    private final int retries = Integer.parseInt(PropertyUtils.getValue(ConfigProperties.RETRYATTEMPTS));
    private int count = 0;

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
