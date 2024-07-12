package org.gcit.pages.orangehrmpage;

import org.gcit.enums.WaitStrategy;
import org.gcit.pages.commonaction.BaseAction;
import org.gcit.pages.orangehrmpage.HRMLoginPage;
import org.openqa.selenium.By;

public class HRMHomePage extends BaseAction {

    private final By linkWelcome = By.xpath("//i[contains(@class, 'oxd-userdropdown-icon')]");
    private final By linkLogout = By.xpath("//a[contains(@href, 'auth')]");

    public HRMHomePage clickWelcome() {
        click(linkWelcome, WaitStrategy.CLICKABLE, "linkWelcome");
        return this;
    }

    public HRMLoginPage clickLogoutButton() {
        click(linkLogout, WaitStrategy.CLICKABLE, "linkLogout");
        return new HRMLoginPage();
    }
}
