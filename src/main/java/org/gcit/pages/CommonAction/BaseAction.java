package org.gcit.pages.CommonAction;

import org.gcit.enums.WaitStrategy;
import org.gcit.factories.ExplicitWaitFactory;
import org.gcit.reports.ExtentLogger;
import org.openqa.selenium.By;

import static org.gcit.driver.DriverManager.getDriver;

public class BaseAction {
    protected void click(By by, WaitStrategy waitStrategy, String elementname) {
        ExplicitWaitFactory.performExplicitWait(waitStrategy, by).click();
        try {
            ExtentLogger.pass(elementname + " is clicked", true);
        } catch (Exception e) {
            ExtentLogger.fail("Unable to click on " + elementname, true);
            e.printStackTrace();
        }
    }

    protected void sendKeys(By by, String value, WaitStrategy waitStrategy, String elementname) {
        ExplicitWaitFactory.performExplicitWait(waitStrategy, by).sendKeys(value);
        try {

            ExtentLogger.pass("Given value " + value + " is entered successfully in " + elementname + " field", true);
        } catch (Exception e) {
            ExtentLogger.fail("Unable to insert/pass the given text in  " + elementname, true);
            e.printStackTrace();
        }
    }

    protected String getTitle() {
        return getDriver().getTitle();
    }

}
