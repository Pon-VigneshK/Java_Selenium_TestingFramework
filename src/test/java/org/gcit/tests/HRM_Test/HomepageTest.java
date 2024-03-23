package org.gcit.tests.HRM_Test;

import org.gcit.driver.DriverManager;
import org.gcit.tests.BaseSetup.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class HomepageTest extends BaseTest {
    private HomepageTest() {
    }

    @Test
    public void test3() {
        DriverManager.getDriver().findElement(By.name("q")).sendKeys("Testing mini bytes - Youtube", Keys.ENTER);
        String title = DriverManager.getDriver().getTitle();
        assertThat(title)
                .as("Object is actuall null").isNotNull()
                .as("it does not contains exceted text").containsIgnoringCase("google search")
                .matches("\\w.*")
                .hasSizeBetween(15, 100);
        List<WebElement> elements = DriverManager.getDriver().findElements(By.xpath("//h3/span"));
        assertThat(elements)
                .hasSize(10)
                .extracting(WebElement::getText)
                .contains("Testing mini bytes - Youtube");
    }
}
