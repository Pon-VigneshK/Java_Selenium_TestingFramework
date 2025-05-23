package org.gcit.pages.orangehrmpage;

import org.gcit.enums.WaitStrategy;
import org.gcit.pages.CommonAction.BaseAction;
import org.openqa.selenium.By;

/**
 * Represents the OrangeHRM Home Page, which is typically the page displayed after a successful login.
 * <p>
 * This class extends {@link BaseAction} to inherit common web interaction methods and provides
 * specific actions that can be performed on the home page, such as accessing user profile
 * options (e.g., welcome link) and logging out.
 *
 * @see BaseAction
 * @see HRMLoginPage
 */
public class HRMHomePage extends BaseAction {

    /**
     * Locator for the welcome link or user dropdown icon, typically used to access profile and logout options.
     * It targets an {@code <i>} tag containing "oxd-userdropdown-icon" in its class attribute.
     */
    private final By linkWelcome = By.xpath("//i[contains(@class, 'oxd-userdropdown-icon')]");

    /**
     * Locator for the logout link.
     * It targets an {@code <a>} tag whose href attribute contains "auth", which is common for logout/authentication links.
     */
    private final By linkLogout = By.xpath("//a[contains(@href, 'auth')]");

    /**
     * Clicks on the "Welcome" link or user dropdown menu icon on the OrangeHRM home page.
     * <p>
     * This action typically opens a dropdown menu containing options like "Logout", "Support", "Change Password", etc.
     * It uses an explicit wait strategy to ensure the element is clickable before performing the action.
     *
     * @return The current instance of {@code HRMHomePage}, allowing for fluent method chaining.
     */
    public HRMHomePage clickWelcome() {
        click(linkWelcome, WaitStrategy.CLICKABLE, "Welcome Link/User Dropdown Icon");
        return this;
    }

    /**
     * Clicks the "Logout" button, typically found within the user dropdown menu initiated by {@link #clickWelcome()}.
     * <p>
     * After clicking logout, the user is expected to be redirected to the OrangeHRM Login Page.
     * This method uses an explicit wait strategy to ensure the logout link is clickable.
     *
     * @return A new instance of {@link HRMLoginPage}, representing the page displayed after logging out.
     */
    public HRMLoginPage clickLogoutButton() {
        // Assumes the logout button is made visible by a previous action, e.g., clickWelcome()
        click(linkLogout, WaitStrategy.CLICKABLE, "Logout Button");
        return new HRMLoginPage();
    }
}
