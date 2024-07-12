package org.gcit.pages.orangehrmpage;

import org.gcit.enums.WaitStrategy;
import org.gcit.pages.commonaction.BaseAction;
import org.openqa.selenium.By;

public final class HRMLoginPage extends BaseAction {
    private static final By textboxUsername = By.name("username");
    private static final By textboxPassword = By.xpath("//input[@type='password']");
    private static final By buttonLogin = By.xpath("//button[@type='submit']");

    public HRMLoginPage enterUsername(String username) {
        sendKeys(textboxUsername, username, WaitStrategy.PRESENCE, "textboxUsername", true);
        return this;
    }

    public HRMLoginPage enterPassword(String password) {
        sendKeys(textboxPassword, password, WaitStrategy.PRESENCE, "textboxPassword", true);
        return this;
    }

    public HRMHomePage clickLoginButton() {
        click(buttonLogin, WaitStrategy.CLICKABLE, "buttonLogin");
        return new HRMHomePage();
    }

    public String getPageTitle() {
        return getTitle();
    }
}
