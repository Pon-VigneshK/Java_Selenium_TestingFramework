package org.gcit.pages.OrangeHRM;

import org.gcit.enums.WaitStrategy;
import org.gcit.pages.CommonAction.BaseAction;
import org.openqa.selenium.By;

public class HRMHomePage extends BaseAction {

    private final By link_welcome = By.xpath("//i[contains(@class, 'oxd-userdropdown-icon')]");
    private final By link_logout = By.xpath("//a[contains(@href, 'auth')]");

    public HRMHomePage clickWelcome() {
        click(link_welcome, WaitStrategy.CLICKABLE, "link_welcome");
        return this;
    }

    public HRMLoginPage clickLogoutButton() {
        click(link_logout, WaitStrategy.CLICKABLE, "link_logout");
        return new HRMLoginPage();
    }

}
