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
 * BaseAction provides a set of reusable methods for common web interactions
 * using the Selenium WebDriver. These methods include clicking on elements,
 * sending keys to input fields, handling dropdown selections, and managing
 * browser window behaviors, among others. It is designed to be extended by
 * page-specific action classes.
 */
public class BaseAction {

    /**
     * Attempts to click on a web element identified by the specified locator and wait strategy.
     * If the click is successful, the action is logged; otherwise, an exception is caught and logged.
     *
     * @param by the locator used to identify the web element.
     * @param waitStrategy the strategy to use for waiting before attempting to click.
     * @param elementName the name of the web element, used for logging purposes.
     */
    protected void click(By by, WaitStrategy waitStrategy, String elementName) {
        try {
            ExplicitWaitFactory.performExplicitWait(waitStrategy, by).click();
            ExtentLogger.pass("Clicked on element '" + elementName + "'", true);
            log(INFO, "Clicked on element '" + elementName + "'");
        } catch (Exception e) {
            ExtentLogger.fail("Unable to click on element '" + elementName + "'", true);
            log(ERROR, "Unable to click on element '" + elementName + "'");
            e.printStackTrace();
        }
    }

    /**
     * Sends the specified value to an HTML element found using the given locator.
     *
     * @param by the locator used to find the web element
     * @param value the value to be sent to the web element
     * @param waitStrategy the strategy to use for waiting (CLICKABLE, PRESENCE, VISIBLE, NONE)
     * @param elementName the name of the element for logging purposes
     * @param log boolean flag indicating whether to log the action
     */
    protected void sendKeys(By by, String value, WaitStrategy waitStrategy, String elementName, boolean log) {
        try {
            ExplicitWaitFactory.performExplicitWait(waitStrategy, by).sendKeys(value);
            if (log) {
                ExtentLogger.pass("Entered value '" + value + "' in element '" + elementName + "'", true);
            }
            log(INFO, "Entered value '" + value + "' in element '" + elementName + "'");
        } catch (Exception e) {
            ExtentLogger.fail("Unable to enter value '" + value + "' in element '" + elementName + "'", true);
            log(ERROR, "Unable to enter value '" + value + "' in element '" + elementName + "'");
            e.printStackTrace();
        }
    }

    /**
     * Selects a value in a dropdown element by its index.
     *
     * @param by the locator used to find the dropdown element
     * @param index the index of the option to be selected
     * @param waitStrategy the strategy to use for waiting for the dropdown element to be interactable
     * @param elementName the name of the dropdown element, used for logging purposes
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
            log(ERROR, "Error selecting index {" + index + "} in dropdown: {" + elementName + "}");
            e.printStackTrace();
        }
    }

    /**
     * Checks the visibility of a web element located by the specified locator and wait strategy.
     * Logs the visibility status and returns a boolean indicating whether the element is visible.
     *
     * @param by the locator used to find the web element
     * @param waitStrategy the strategy to use for waiting (CLICKABLE, PRESENCE, VISIBLE, NONE)
     * @param elementName the name of the element to be used in logging
     * @return true if the element is visible, false otherwise
     */
    protected boolean isVisible(By by, WaitStrategy waitStrategy, String elementName) {
        try {
            WebElement element = ExplicitWaitFactory.performExplicitWait(waitStrategy, by);
            boolean isVisible = element != null && element.isDisplayed();
            if (isVisible) {
                ExtentLogger.pass("Element '" + elementName + "' is visible", true);
                log(INFO, "Element '" + elementName + "' is visible");
            } else {
                ExtentLogger.fail("Element '" + elementName + "' is not visible", true);
                log(ERROR, "Element '" + elementName + "' is not visible");
            }
            return isVisible;
        } catch (Exception e) {
            ExtentLogger.fail("Error checking visibility of element '" + elementName + "'", true);
            log(ERROR, "Error checking visibility of element '" + elementName + "'");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves the text of a web element identified by the specified locator, applies the given wait strategy, and logs the result.
     *
     * @param by the locator used to find the web element
     * @param waitStrategy the strategy to use for waiting (CLICKABLE, PRESENCE, VISIBLE, NONE)
     * @param elementName the name of the element, used for logging purposes
     * @return the text retrieved from the web element, or {@code null} if an exception occurs
     */
    protected String getText(By by, WaitStrategy waitStrategy, String elementName) {
        try {
            String text = ExplicitWaitFactory.performExplicitWait(waitStrategy, by).getText();
            ExtentLogger.pass("Retrieved text '" + text + "' from element '" + elementName + "'", true);
            log(INFO, "Retrieved text '" + text + "' from element '" + elementName + "'");
            return text;
        } catch (Exception e) {
            ExtentLogger.fail("Unable to retrieve text from element '" + elementName + "'", true);
            log(ERROR, "Unable to retrieve text from element '" + elementName + "'");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Maximizes the browser window to occupy the full screen.
     *
     * If the window is successfully maximized, it logs a success message and reports it using ExtentLogger.
     * In case of an exception during the process, logs an error message and reports the failure using ExtentLogger.
     */
    protected void maximizeWindow() {
        try {
            getDriver().manage().window().maximize();
            ExtentLogger.pass("Window maximized successfully", true);
            log(INFO, "Window maximized successfully");
        } catch (Exception e) {
            ExtentLogger.fail("Unable to maximize window", true);
            log(ERROR, "Unable to maximize window");
            e.printStackTrace();
        }
    }

    /**
     * Minimizes the browser window.
     *
     * This method attempts to minimize the browser window using the WebDriver instance.
     * It also logs the result of the action using ExtentLogger and a custom logging system.
     *
     * If the window is successfully minimized, it logs a pass message.
     * If the window cannot be minimized due to an exception, it logs a fail message
     * and prints the stack trace of the exception.
     */
    protected void minimizeWindow() {
        try {
            getDriver().manage().window().minimize();
            ExtentLogger.pass("Window minimized successfully", true);
            log(INFO, "Window minimized successfully");
        } catch (Exception e) {
            ExtentLogger.fail("Unable to minimize window", true);
            log(ERROR, "Unable to minimize window");
            e.printStackTrace();
        }
    }

    /**
     * Sets the browser window to full screen mode.
     *
     * This method attempts to maximize the browser window to full screen using the
     * WebDriver's window management capabilities. Upon successful operation, it logs the success message
     * via ExtentLogger and a custom logging mechanism.
     *
     * If an exception occurs during the process, the method logs the failure message and stack trace.
     */
    protected void setFullScreen() {
        try {
            getDriver().manage().window().fullscreen();
            ExtentLogger.pass("Window set to full screen successfully", true);
            log(INFO, "Window set to full screen successfully");
        } catch (Exception e) {
            ExtentLogger.fail("Unable to set window to full screen", true);
            log(ERROR, "Unable to set window to full screen");
            e.printStackTrace();
        }
    }

    /**
     * Sets the size of the browser window to the specified width and height.
     *
     * @param width the desired width of the window in pixels.
     * @param height the desired height of the window in pixels.
     */
    protected void setSizeWindow(int width, int height) {
        try {
            Dimension size = new Dimension(width, height);
            getDriver().manage().window().setSize(size);
            ExtentLogger.pass("Window size set to width " + width + " and height " + height + " successfully", true);
            log(INFO, "Window size set to width " + width + " and height " + height + " successfully");
        } catch (Exception e) {
            ExtentLogger.fail("Unable to set window size to width " + width + " and height " + height, true);
            log(ERROR, "Unable to set window size to width " + width + " and height " + height);
            e.printStackTrace();
        }
    }

    /**
     * Clears the text in the specified web element located using the given locator and wait strategy.
     * Logs the outcome of the operation using ExtentLogger and a custom logger.
     *
     * @param by the locator used to find the web element
     * @param waitStrategy the strategy to use for waiting until the web element is ready
     * @param elementName the name of the element, used for logging purposes
     */
    protected void clear(By by, WaitStrategy waitStrategy, String elementName) {
        try {
            ExplicitWaitFactory.performExplicitWait(waitStrategy, by).clear();
            ExtentLogger.pass("Cleared text in element '" + elementName + "'", true);
            log(INFO, "Cleared text in element '" + elementName + "'");
        } catch (Exception e) {
            ExtentLogger.fail("Unable to clear text in element '" + elementName + "'", true);
            log(ERROR, "Unable to clear text in element '" + elementName + "'");
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the title of the current page using the WebDriver instance.
     *
     * @return the title of the current page as a String.
     */
    protected String getTitle() {
        return getDriver().getTitle();
    }
}
