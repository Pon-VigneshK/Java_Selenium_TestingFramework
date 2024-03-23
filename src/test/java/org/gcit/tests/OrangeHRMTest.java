package org.gcit.tests;

import org.assertj.core.api.Assertions;
import org.gcit.annotations.FrameworkAnnotation;
import org.gcit.enums.CategoryType;
import org.gcit.pages.OrangeHRM.HRMLoginPage;
import org.gcit.tests.BaseSetup.BaseTest;
import org.testng.annotations.Test;

import java.util.Map;

public class OrangeHRMTest extends BaseTest {
    private OrangeHRMTest() {
    }
    @FrameworkAnnotation(category = {CategoryType.FULL_REGRESSION, CategoryType.HIGH_LEVEL_TESTING})
    @Test
    public void loginLogoutTest(Map<String, String> data) {
        String title = new HRMLoginPage()
                .enterUserName(data.get("username")).enterPwd(data.get("password")).clickLoginButton()
                .clickWelcome().clickLogoutButton()
                .getPageTitle();
        Assertions.assertThat(title)
                .isEqualTo("OrangeHRM");
    }
    @FrameworkAnnotation(category = {CategoryType.FULL_REGRESSION, CategoryType.HIGH_LEVEL_TESTING})
    @Test
    public void newTest(Map<String, String> data) {
        String title = new HRMLoginPage()
                .enterUserName(data.get("username")).enterPwd(data.get("password")).clickLoginButton()
                .clickWelcome().clickLogoutButton()
                .getPageTitle();
        Assertions.assertThat(title)
                .isEqualTo("OrangeHRM");
    }
}

