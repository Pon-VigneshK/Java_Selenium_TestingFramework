package org.gcit.factories;

import org.gcit.constants.FrameworkConstants;
import org.gcit.enums.WaitStrategy;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.gcit.driver.DriverManager.getDriver;
import static org.gcit.enums.LogType.ERROR;
import static org.gcit.logger.LogService.log;

public final class ExplicitWaitFactory {
    private static final String LOG_TAG = ExplicitWaitFactory.class.getSimpleName();

    private ExplicitWaitFactory() {
    }

    public static WebElement performExplicitWait(WaitStrategy waitStrategy, By by) {
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(FrameworkConstants.getExplicitWait()));
        try {
            switch (waitStrategy) {
                case CLICKABLE:
                    return wait.until(ExpectedConditions.elementToBeClickable(by));
                case PRESENCE:
                    return wait.until(ExpectedConditions.presenceOfElementLocated(by));
                case VISIBLE:
                    return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
                case NONE:
                    return getDriver().findElement(by);
                default:
                    throw new IllegalStateException("Unsupported wait strategy: " + waitStrategy);
            }
        } catch (TimeoutException e) {
            log(ERROR, LOG_TAG + ": Timeout waiting for element with " + waitStrategy + " strategy. Locator: " + by);
            e.printStackTrace();
        }
        return null;
    }
}






