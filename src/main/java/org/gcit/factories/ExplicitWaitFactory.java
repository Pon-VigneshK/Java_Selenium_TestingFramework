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
 * Factory class for creating and applying explicit wait conditions for WebElements
 * in Selenium WebDriver.
 * <p>
 * This utility class centralizes the logic for waiting for elements based on different
 * {@link WaitStrategy} conditions (e.g., clickable, present, visible). It uses
 * {@link WebDriverWait} and {@link ExpectedConditions} from Selenium to achieve this.
 * The default timeout for these waits is sourced from
 * {@link FrameworkConstants#getExplicitWait()}.
 * <p>
 * This class is final and cannot be instantiated. All methods are static.
 *
 * @see WaitStrategy
 * @see WebDriverWait
 * @see ExpectedConditions
 * @see FrameworkConstants#getExplicitWait()
 * @see org.gcit.driver.DriverManager#getDriver()
 */
public final class ExplicitWaitFactory {
    /**
     * Logger tag for this class, using its simple name.
     */
    private static final String LOG_TAG = ExplicitWaitFactory.class.getSimpleName();

    /**
     * Private constructor to prevent instantiation of this utility factory class.
     */
    private ExplicitWaitFactory() {
    }

    /**
     * Applies an explicit wait for a {@link WebElement} based on the specified {@link WaitStrategy}
     * and locator ({@link By}).
     * <p>
     * This method uses a {@link WebDriverWait} instance with a timeout obtained from
     * {@link FrameworkConstants#getExplicitWait()}. The behavior depends on the {@code waitStrategy}:
     * <ul>
     *   <li>{@link WaitStrategy#CLICKABLE}: Waits for the element to be clickable using
     *       {@link ExpectedConditions#elementToBeClickable(By)}.</li>
     *   <li>{@link WaitStrategy#PRESENCE}: Waits for the element to be present in the DOM using
     *       {@link ExpectedConditions#presenceOfElementLocated(By)}.</li>
     *   <li>{@link WaitStrategy#VISIBLE}: Waits for the element to be visible on the page using
     *       {@link ExpectedConditions#visibilityOfElementLocated(By)}.</li>
     *   <li>{@link WaitStrategy#NONE}: No explicit wait is applied. The method attempts to find the
     *       element immediately using {@code getDriver().findElement(by)}. This can lead to
     *       {@link org.openqa.selenium.NoSuchElementException} if the element is not readily available.</li>
     * </ul>
     * If a {@link TimeoutException} occurs during the wait (for strategies other than NONE),
     * an error is logged, the stack trace is printed (current behavior), and {@code null} is returned.
     * For unsupported {@code waitStrategy} values, an {@link IllegalStateException} is thrown.
     *
     * @param waitStrategy The {@link WaitStrategy} enum value determining the condition to wait for.
     * @param by           The Selenium {@link By} locator used to find the web element.
     * @return The {@link WebElement} if found and the condition is met within the timeout.
     *         Returns {@code null} if a {@link TimeoutException} occurs for timed wait strategies.
     *         For {@link WaitStrategy#NONE}, it returns the element or throws if not found immediately.
     * @throws IllegalStateException if an unsupported {@code waitStrategy} is provided.
     * @throws org.openqa.selenium.NoSuchElementException if {@code WaitStrategy.NONE} is used and the element
     *                                                    is not immediately found.
     */
    public static WebElement performExplicitWait(WaitStrategy waitStrategy, By by) {
        // The explicit wait timeout is fetched from FrameworkConstants each time.
        // Consider if this value could be cached if performance becomes a concern, though it's likely negligible.
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(FrameworkConstants.getExplicitWait()));
        try {
            switch (waitStrategy) {
                case CLICKABLE:
                    log(LogType.INFO, LOG_TAG + ": Waiting for element to be clickable. Locator: " + by);
                    return wait.until(ExpectedConditions.elementToBeClickable(by));
                case PRESENCE:
                    log(LogType.INFO, LOG_TAG + ": Waiting for element presence. Locator: " + by);
                    return wait.until(ExpectedConditions.presenceOfElementLocated(by));
                case VISIBLE:
                    log(LogType.INFO, LOG_TAG + ": Waiting for element visibility. Locator: " + by);
                    return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
                case NONE:
                    log(LogType.INFO, LOG_TAG + ": No wait strategy. Attempting to find element immediately. Locator: " + by);
                    return getDriver().findElement(by); // No explicit wait, find immediately
                default:
                    // This case should ideally not be reached if WaitStrategy enum is exhaustive and used correctly.
                    String errorMessage = "Unsupported wait strategy: " + waitStrategy;
                    log(ERROR, LOG_TAG + ": " + errorMessage);
                    throw new IllegalStateException(errorMessage);
            }
        } catch (TimeoutException e) {
            log(ERROR, LOG_TAG + ": Timeout (" + FrameworkConstants.getExplicitWait() + "s) waiting for element with "
                                + waitStrategy + " strategy. Locator: " + by, e); // Include exception in log
            // e.printStackTrace(); // Current behavior. Consider removing in favor of logging only, or rethrowing a custom exception.
        } catch (Exception e) {
            // Catch other potential exceptions (e.g., NoSuchElementException from NONE strategy if not handled by caller)
            log(ERROR, LOG_TAG + ": Exception performing wait (" + waitStrategy + ") for locator: " + by, e);
            // Depending on desired behavior, might rethrow or return null.
            // If strategy is NONE and element not found, findElement throws NoSuchElementException.
            // If other strategies cause different Selenium exceptions, they'd be caught here too.
            if (e instanceof org.openqa.selenium.NoSuchElementException && waitStrategy == WaitStrategy.NONE) {
                throw e; // Re-throw NoSuchElementException for NONE strategy as it's an expected outcome
            }
        }
        return null; // Return null if a TimeoutException occurred or other caught non-NoSuchElementException for NONE.
    }
}






