package org.gcit.tests.basesetup;

import org.gcit.driver.Driver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import static org.gcit.enums.ConfigProperties.BROWSER;
import static org.gcit.utils.PropertyUtils.getValue;

public class BaseTest {
    protected BaseTest() {
    }

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        String configBrowser = getValue(BROWSER);
        Driver.initDriver(configBrowser);
    }

    @AfterMethod(alwaysRun = true)
    protected void tearDown() {
        Driver.quitDriver();
    }
}