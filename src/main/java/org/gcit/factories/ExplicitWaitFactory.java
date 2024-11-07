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

/**
 * The ExplicitWaitFactory class provides a static method to perform explicit waits on web elements using different wait strategies.
 * This class is designed to facilitate the synchronization of web elements in a Selenium automated test environment.
 * It employs the WebDriverWait class to wait for specific conditions to be met before interacting with web elements.
 *
 * The supported wait strategies include:
 *
 * - CLICKABLE: Waits until the element is clickable.
 * - PRESENCE: Waits until the element is present in the DOM.
 * - VISIBLE: Waits until the element is visible on the page.
 * - NONE: Does not wait and attempts to find the element immediately.
 */
public final class ExplicitWaitFactory {
    /**
     * The LOG_TAG variable is used for logging purposes within the ExplicitWaitFactory class.
     * It holds the simple name of the class, which helps in identifying log entries
     * specific to this class.
     */
    private static final String LOG_TAG = ExplicitWaitFactory.class.getSimpleName();

    /**
     * Private constructor to prevent instantiation.
     *
     * The ExplicitWaitFactory class provides a singleton-like structure with static methods
     * to perform explicit waits on web elements. This ensures that the class cannot be
     * instantiated from outside, promoting the use of its static utility methods.
     */
    private ExplicitWaitFactory() {
    }

    /**
     * Performs an explicit wait on a web element based on the specified wait strategy and locator.
     * Utilizes WebDriverWait to wait for the given conditions to be met before returning the web element.
     *
     * @param waitStrategy the strategy to use for waiting (CLICKABLE, PRESENCE, VISIBLE, NONE)
     * @param by the locator used to find the web element
     * @return the WebElement found using the specified wait strategy and locator, or {@code null} if the wait times out
     */
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






