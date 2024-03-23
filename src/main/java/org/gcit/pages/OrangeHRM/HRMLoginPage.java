package org.gcit.pages.OrangeHRM;

import org.gcit.enums.WaitStrategy;
import org.gcit.pages.CommonAction.BaseAction;
import org.gcit.pages.ObjectRepository.LoginPageObject;

public final class HRMLoginPage extends BaseAction {

    public HRMLoginPage enterUserName(String username) {
        sendKeys(LoginPageObject.getTextboxUsername(), username, WaitStrategy.PRESENCE, "textbox_Username");
        return this;
    }

    public HRMLoginPage enterPwd(String password) {
        sendKeys(LoginPageObject.getTextboxPassword(), password, WaitStrategy.PRESENCE, "textbox_Password");
        return this;
    }

    public HRMHomePage clickLoginButton() {
        click(LoginPageObject.getButtonLogin(), WaitStrategy.CLICKABLE, "button_Login");
        return new HRMHomePage();
    }

    public String getPageTitle() {
        return getTitle();
    }
}
