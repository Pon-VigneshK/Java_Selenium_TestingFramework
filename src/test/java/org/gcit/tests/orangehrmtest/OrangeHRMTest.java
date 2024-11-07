package org.gcit.tests.orangehrmtest;

import org.assertj.core.api.Assertions;
import org.gcit.pages.orangehrmpage.HRMLoginPage;
import org.gcit.tests.BaseSetup.BaseTest;
import org.testng.annotations.Test;

import java.util.Map;

public class OrangeHRMTest extends BaseTest {
    private OrangeHRMTest() {
    }

    @Test
    public void loginLogoutTest(Map<String, String> data) {
        String title = new HRMLoginPage()
                .enterUsername(data.get("username")).enterPassword(data.get("password")).clickLoginButton()
                .clickWelcome().clickLogoutButton()
                .getPageTitle();
        Assertions.assertThat(title)
                .isEqualTo("OrangeHRM");
    }
}
