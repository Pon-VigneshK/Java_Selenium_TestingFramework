package org.gcit.pages.commonaction;

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

public class BaseAction {

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

    protected String getTitle() {
        return getDriver().getTitle();
    }
}
