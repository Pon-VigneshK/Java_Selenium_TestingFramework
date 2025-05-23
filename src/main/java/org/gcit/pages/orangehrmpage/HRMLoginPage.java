package org.gcit.pages.orangehrmpage;

import org.gcit.enums.WaitStrategy;
import org.gcit.pages.CommonAction.BaseAction;
import org.openqa.selenium.By;

/**
 * Represents the OrangeHRM Login Page and its associated functionalities.
 * <p>
 * This class extends {@link BaseAction} to inherit common web interaction methods
 * and provides specific methods to interact with the login form elements,
 * such as entering username and password, and clicking the login button.
 * It also provides a method to retrieve the title of the login page.
 *
 * @see BaseAction
 * @see HRMHomePage
 */
public final class HRMLoginPage extends BaseAction {
    /**
     * Locator for the username input field on the login page.
     * It targets an element with the {@code name} attribute set to "username".
     */
    private static final By textboxUsername = By.name("username");

    /**
     * Locator for the password input field on the login page.
     * It targets an {@code <input>} element with {@code type="password"}.
     */
    private static final By textboxPassword = By.xpath("//input[@type='password']");

    /**
     * Locator for the login button on the login page.
     * It targets a {@code <button>} element with {@code type="submit"}.
     */
    private static final By buttonLogin = By.xpath("//button[@type='submit']");

    /**
     * Enters the provided username into the username input field.
     * <p>
     * This method uses an explicit wait strategy ({@link WaitStrategy#PRESENCE}) to ensure the
     * username field is present in the DOM before attempting to type into it.
     * The action is logged to ExtentReports.
     *
     * @param username The username string to be entered.
     * @return The current instance of {@code HRMLoginPage}, allowing for fluent method chaining.
     */
    public HRMLoginPage enterUsername(String username) {
        sendKeys(textboxUsername, username, WaitStrategy.PRESENCE, "Username Textbox", true);
        return this;
    }

    /**
     * Enters the provided password into the password input field.
     * <p>
     * This method uses an explicit wait strategy ({@link WaitStrategy#PRESENCE}) to ensure the
     * password field is present in the DOM before attempting to type into it.
     * The action is logged to ExtentReports.
     *
     * @param password The password string to be entered.
     * @return The current instance of {@code HRMLoginPage}, allowing for fluent method chaining.
     */
    public HRMLoginPage enterPassword(String password) {
        sendKeys(textboxPassword, password, WaitStrategy.PRESENCE, "Password Textbox", true);
        return this;
    }

    /**
     * Clicks the login button on the OrangeHRM login page.
     * <p>
     * This action is expected to submit the login form. Upon successful login,
     * it typically navigates the user to the {@link HRMHomePage}.
     * An explicit wait strategy ({@link WaitStrategy#CLICKABLE}) is used to ensure the
     * button is clickable before the action.
     *
     * @return A new instance of {@link HRMHomePage}, representing the page displayed after a successful login.
     */
    public HRMHomePage clickLoginButton() {
        click(buttonLogin, WaitStrategy.CLICKABLE, "Login Button");
        return new HRMHomePage();
    }

    /**
     * Retrieves the title of the current page, which is expected to be the login page title.
     * <p>
     * This method directly calls {@link #getTitle()} inherited from {@link BaseAction}.
     *
     * @return The title of the current web page as a String.
     */
    public String getPageTitle() {
        return getTitle();
    }
}
