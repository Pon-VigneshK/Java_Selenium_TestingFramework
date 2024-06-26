package org.gcit.tests.BaseSetup;

import org.gcit.driver.Driver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {

    protected BaseTest() {
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        Driver.initDriver("edge");
    }

    @AfterMethod
    protected void tearDown() {
        Driver.quitDriver();
    }
}