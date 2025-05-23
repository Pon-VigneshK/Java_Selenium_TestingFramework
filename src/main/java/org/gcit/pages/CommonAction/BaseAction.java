package org.gcit.pages.CommonAction;

import org.gcit.enums.WaitStrategy;
import org.gcit.factories.ExplicitWaitFactory;
import org.gcit.reports.ExtentLogger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import static org.gcit.driver.DriverManager.getDriver;
import static org.gcit.enums.LogType.ERROR;
import static org.gcit.enums.LogType.INFO;
import static org.gcit.logger.LogService.log;

/**
 * Provides a base set of reusable actions commonly performed on web elements during UI automation.
 * <p>
 * This class encapsulates Selenium WebDriver interactions such as clicking, sending keys,
 * selecting from dropdowns, and checking element states (visibility, text). It integrates
 * with {@link ExplicitWaitFactory} to ensure elements are interactable before actions are
 * performed, and with {@link ExtentLogger} and {@link org.gcit.logger.LogService} for comprehensive
 * logging of actions and their outcomes.
 * <p>
 * Page object classes within the framework should extend this class to inherit these common
 * action methods, promoting code reuse and consistent interaction patterns.
 * All action methods are {@code protected} to be accessible by subclasses.
 * <p>
 * Note: Current error handling within these methods typically logs the exception and
 * prints the stack trace, but does not re-throw the exception (unless it's a fatal error like
 * {@link OutOfMemoryError} or {@link StackOverflowError}). For methods that are expected to return a value
 * (e.g., {@link #getText(By, WaitStrategy, String)}, {@link #isVisible(By, WaitStrategy, String)}),
 * they return {@code null} or {@code false} respectively in case of an exception.
 *
 * @see ExplicitWaitFactory
 * @see ExtentLogger
 * @see org.gcit.logger.LogService
 * @see org.openqa.selenium.WebDriver
 * @see org.openqa.selenium.WebElement
 */
public class BaseAction {

    /**
     * Performs a click action on a web element identified by the given locator,
     * after applying the specified wait strategy.
     * <p>
     * Logs the action to both ExtentReports (with a screenshot on success/failure if configured)
     * and the console log. If any exception occurs during the wait or click,
     * it is caught, logged, and its stack trace is printed; the method does not re-throw the exception.
     *
     * @param by           The {@link By} locator strategy to find the web element.
     * @param waitStrategy The {@link WaitStrategy} to apply before attempting to click the element
     *                     (e.g., {@link WaitStrategy#CLICKABLE}, {@link WaitStrategy#VISIBLE}).
     * @param elementName  A descriptive name for the element being clicked, used for logging purposes.
     */
    protected void click(By by, WaitStrategy waitStrategy, String elementName) {
        try {
            ExplicitWaitFactory.performExplicitWait(waitStrategy, by).click();
            ExtentLogger.pass("Clicked on element '" + elementName + "'", true);
            log(INFO, "Clicked on element '" + elementName + "'");
        } catch (Exception e) {
            ExtentLogger.fail("Unable to click on element '" + elementName + "'", true);
            log(ERROR, "Unable to click on element '" + elementName + "'. Exception: " + e.getMessage());
            e.printStackTrace(); // Current behavior: prints stack trace.
        }
    }

    /**
     * Enters the specified text value into a web element (typically an input field)
     * identified by the given locator, after applying the specified wait strategy.
     * <p>
     * Logs the action to ExtentReports (optionally with a screenshot) and the console log.
     * If any exception occurs, it is caught, logged, and its stack trace is printed;
     * the method does not re-throw the exception.
     *
     * @param by           The {@link By} locator strategy to find the web element.
     * @param value        The text string to be entered into the element.
     * @param waitStrategy The {@link WaitStrategy} to apply before attempting to send keys
     *                     (e.g., {@link WaitStrategy#VISIBLE}, {@link WaitStrategy#PRESENCE}).
     * @param elementName  A descriptive name for the element, used for logging.
     * @param logToExtent  A boolean flag indicating whether this action should be logged as a separate
     *                     "pass" step in ExtentReports. Console logging occurs regardless.
     */
    protected void sendKeys(By by, String value, WaitStrategy waitStrategy, String elementName, boolean logToExtent) {
        try {
            ExplicitWaitFactory.performExplicitWait(waitStrategy, by).sendKeys(value);
            if (logToExtent) {
                ExtentLogger.pass("Entered value '" + value + "' in element '" + elementName + "'", true);
            }
            log(INFO, "Entered value '" + value + "' in element '" + elementName + "'");
        } catch (Exception e) {
            ExtentLogger.fail("Unable to enter value '" + value + "' in element '" + elementName + "'", true);
            log(ERROR, "Unable to enter value '" + value + "' in element '" + elementName + "'. Exception: " + e.getMessage());
            e.printStackTrace(); // Current behavior: prints stack trace.
        }
    }

    /**
     * Selects an option from a dropdown ({@code <select>}) web element by its zero-based index.
     * <p>
     * The method waits for the dropdown element to be interactable based on the {@code waitStrategy}.
     * Logs the selection to ExtentReports and the console log. If any exception occurs
     * (e.g., element not found, not a select, index out of bounds), it is caught, logged,
     * and its stack trace is printed; the method does not re-throw the exception.
     *
     * @param by           The {@link By} locator strategy to find the dropdown element.
     * @param index        The zero-based index of the option to select.
     * @param waitStrategy The {@link WaitStrategy} to apply before interacting with the dropdown.
     * @param elementName  A descriptive name for the dropdown element, used for logging.
     */
    protected void selectedValueInDropdownByIndex(By by, int index, WaitStrategy waitStrategy, String elementName) {
        try {
            WebElement dropdownElement = ExplicitWaitFactory.performExplicitWait(waitStrategy, by);
            Select dropdown = new Select(dropdownElement);
            dropdown.selectByIndex(index);
            ExtentLogger.pass("Selected index {" + index + "} in dropdown: {" + elementName + "}", true);
            log(INFO, "Selected index {" + index + "} in dropdown: {" + elementName + "}");
        } catch (Exception e) {
            ExtentLogger.fail("Error selecting index {" + index + "} in dropdown: {" + elementName + "}", true);
            log(ERROR, "Error selecting index {" + index + "} in dropdown: {" + elementName + "}. Exception: " + e.getMessage());
            e.printStackTrace(); // Current behavior: prints stack trace.
        }
    }

    /**
     * Checks if a web element, identified by the given locator, is visible on the page
     * after applying the specified wait strategy.
     * <p>
     * Logs the visibility status to ExtentReports and the console log.
     * If any exception occurs during the process (e.g., element not found even after wait),
     * it's logged, its stack trace is printed, and the method returns {@code false}.
     *
     * @param by           The {@link By} locator strategy to find the web element.
     * @param waitStrategy The {@link WaitStrategy} to apply (typically {@link WaitStrategy#VISIBLE} or {@link WaitStrategy#PRESENCE}).
     * @param elementName  A descriptive name for the element, used for logging.
     * @return {@code true} if the element is found and displayed (i.e., {@link WebElement#isDisplayed()} is true);
     *         {@code false} otherwise, or if an exception occurs.
     */
    protected boolean isVisible(By by, WaitStrategy waitStrategy, String elementName) {
        try {
            WebElement element = ExplicitWaitFactory.performExplicitWait(waitStrategy, by);
            boolean isVisible = element != null && element.isDisplayed();
            if (isVisible) {
                ExtentLogger.pass("Element '" + elementName + "' is visible", false); // Typically, isVisible checks are info, not steps needing screenshots
                log(INFO, "Element '" + elementName + "' is visible");
            } else {
                // Log as info or warning rather than fail, as isVisible is a check, not an action that failed.
                // The calling test would determine if non-visibility is a failure.
                ExtentLogger.info("Element '" + elementName + "' is not visible");
                log(INFO, "Element '" + elementName + "' is not visible");
            }
            return isVisible;
        } catch (Exception e) {
            // Log as error because the check itself failed, not necessarily that the element is definitively not visible.
            ExtentLogger.fail("Error checking visibility of element '" + elementName + "'. It might not be present or an error occurred.", false);
            log(ERROR, "Error checking visibility of element '" + elementName + "'. Exception: " + e.getMessage());
            e.printStackTrace(); // Current behavior: prints stack trace.
            return false;
        }
    }

    /**
     * Retrieves the visible text content of a web element identified by the given locator,
     * after applying the specified wait strategy.
     * <p>
     * Logs the retrieved text to ExtentReports and the console log. If any exception occurs
     * (e.g., element not found, element has no text), it is caught, logged,
     * its stack trace is printed, and the method returns {@code null}.
     *
     * @param by           The {@link By} locator strategy to find the web element.
     * @param waitStrategy The {@link WaitStrategy} to apply (e.g., {@link WaitStrategy#VISIBLE}).
     * @param elementName  A descriptive name for the element, used for logging.
     * @return The text content of the element as a String, or {@code null} if the element
     *         is not found, has no text, or an exception occurs.
     */
    protected String getText(By by, WaitStrategy waitStrategy, String elementName) {
        try {
            String text = ExplicitWaitFactory.performExplicitWait(waitStrategy, by).getText();
            ExtentLogger.pass("Retrieved text '" + text + "' from element '" + elementName + "'", false); // Screenshot usually not needed for getText
            log(INFO, "Retrieved text '" + text + "' from element '" + elementName + "'");
            return text;
        } catch (Exception e) {
            ExtentLogger.fail("Unable to retrieve text from element '" + elementName + "'", true); // Screenshot might be useful here
            log(ERROR, "Unable to retrieve text from element '" + elementName + "'. Exception: " + e.getMessage());
            e.printStackTrace(); // Current behavior: prints stack trace.
            return null;
        }
    }

    /**
     * Maximizes the current browser window.
     * <p>
     * Logs the action to ExtentReports and the console log. If any exception occurs,
     * it is caught, logged, and its stack trace is printed.
     */
    protected void maximizeWindow() {
        try {
            getDriver().manage().window().maximize();
            ExtentLogger.pass("Browser window maximized successfully", false);
            log(INFO, "Browser window maximized successfully");
        } catch (Exception e) {
            ExtentLogger.fail("Unable to maximize browser window", false);
            log(ERROR, "Unable to maximize browser window. Exception: " + e.getMessage());
            e.printStackTrace(); // Current behavior: prints stack trace.
        }
    }

    /**
     * Minimizes the current browser window.
     * <p>
     * Logs the action to ExtentReports and the console log. If any exception occurs
     * (e.g., operation not supported by the driver), it is caught, logged, and its stack trace is printed.
     * Note: WebDriver's ability to minimize windows can be driver and OS-dependent.
     */
    protected void minimizeWindow() {
        try {
            getDriver().manage().window().minimize();
            ExtentLogger.pass("Browser window minimized successfully", false);
            log(INFO, "Browser window minimized successfully");
        } catch (Exception e) {
            ExtentLogger.fail("Unable to minimize browser window", false);
            log(ERROR, "Unable to minimize browser window. Exception: " + e.getMessage());
            e.printStackTrace(); // Current behavior: prints stack trace.
        }
    }

    /**
     * Sets the current browser window to full-screen mode.
     * <p>
     * Logs the action to ExtentReports and the console log. If any exception occurs,
     * it is caught, logged, and its stack trace is printed.
     */
    protected void setFullScreen() {
        try {
            getDriver().manage().window().fullscreen();
            ExtentLogger.pass("Browser window set to full screen successfully", false);
            log(INFO, "Browser window set to full screen successfully");
        } catch (Exception e) {
            ExtentLogger.fail("Unable to set browser window to full screen", false);
            log(ERROR, "Unable to set browser window to full screen. Exception: " + e.getMessage());
            e.printStackTrace(); // Current behavior: prints stack trace.
        }
    }

    /**
     * Sets the size of the current browser window to the specified width and height.
     * <p>
     * Logs the action to ExtentReports and the console log. If any exception occurs,
     * it is caught, logged, and its stack trace is printed.
     *
     * @param width  The desired width of the window in pixels.
     * @param height The desired height of the window in pixels.
     */
    protected void setSizeWindow(int width, int height) {
        try {
            Dimension size = new Dimension(width, height);
            getDriver().manage().window().setSize(size);
            ExtentLogger.pass("Window size set to width " + width + " and height " + height + " successfully", false);
            log(INFO, "Window size set to width " + width + " and height " + height + " successfully");
        } catch (Exception e) {
            ExtentLogger.fail("Unable to set window size to width " + width + " and height " + height, false);
            log(ERROR, "Unable to set window size to width " + width + " and height " + height + ". Exception: " + e.getMessage());
            e.printStackTrace(); // Current behavior: prints stack trace.
        }
    }

    /**
     * Clears the content of an input web element (e.g., text field, text area)
     * identified by the given locator, after applying the specified wait strategy.
     * <p>
     * Logs the action to ExtentReports and the console log. If any exception occurs,
     * it is caught, logged, and its stack trace is printed.
     *
     * @param by           The {@link By} locator strategy to find the web element.
     * @param waitStrategy The {@link WaitStrategy} to apply before attempting to clear the element.
     * @param elementName  A descriptive name for the element, used for logging.
     */
    protected void clear(By by, WaitStrategy waitStrategy, String elementName) {
        try {
            ExplicitWaitFactory.performExplicitWait(waitStrategy, by).clear();
            ExtentLogger.pass("Cleared text in element '" + elementName + "'", true);
            log(INFO, "Cleared text in element '" + elementName + "'");
        } catch (Exception e) {
            ExtentLogger.fail("Unable to clear text in element '" + elementName + "'", true);
            log(ERROR, "Unable to clear text in element '" + elementName + "'. Exception: " + e.getMessage());
            e.printStackTrace(); // Current behavior: prints stack trace.
        }
    }

    /**
     * Retrieves the title of the current browser window/tab.
     *
     * @return The title of the current page as a String.
     *         Returns {@code null} if the driver instance is not available, though this
     *         is unlikely if driver initialization was successful.
     */
    protected String getTitle() {
        // Consider adding null check for getDriver() or rely on caller to ensure driver is active.
        return getDriver().getTitle();
    }
}
