package org.gcit.pages.ObjectRepository;

import org.openqa.selenium.By;

public class LoginPageObject {

    private static final By textbox_Username = By.name("username");
    private static final By textbox_Password = By.xpath("//input[@type='password']");
    private static final By button_Login = By.xpath("//button[@type='submit']");

    public static By getTextboxUsername() {
        return textbox_Username;
    }

    public static By getTextboxPassword() {
        return textbox_Password;
    }

    public static By getButtonLogin() {
        return button_Login;
    }
}
