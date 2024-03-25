package org.gcit.tests.HRM_Test;

import org.gcit.driver.DriverManager;
import org.gcit.tests.BaseSetup.BaseTest;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

public final class LoginPageTest extends BaseTest {

    private LoginPageTest() {
    }

    @Test
    public void test1() {
        DriverManager.getDriver().findElement(By.id("btn-make-appointment")).click();
    }
}

