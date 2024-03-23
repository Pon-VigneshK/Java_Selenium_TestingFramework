package org.gcit.pages.Amazon;

import org.gcit.enums.WaitStrategy;
import org.gcit.pages.CommonAction.BaseAction;
import org.openqa.selenium.By;

public class AmazonLaptopPage extends BaseAction {
    private final String linkSubMenu = "//a[text()='%replaceable%']";

    public void clickOnSubMenuItem(String menu) {
        String xpath = linkSubMenu.replace("%replaceable%", menu);
        click(By.xpath(xpath), WaitStrategy.CLICKABLE, "menu");
    }
}
