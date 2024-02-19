package com.MobileFolk.Page;

import com.MobileFolk.Common.BaseConst;
import com.MobileFolk.Common.BasePage;
import com.MobileFolk.constants.FailureHandling;
import com.MobileFolk.constants.FrameworkConst;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

public class LoginPage extends BasePage {
    /**
     * Init a new instance
     *
     * @param driver : The WebDriver to interact with elements
     */
    public LoginPage(WebDriver driver) {
        webDriver = driver;
        AjaxElementLocatorFactory factory = new AjaxElementLocatorFactory(webDriver, FrameworkConst.WAIT_EXPLICIT);
        PageFactory.initElements(factory, this);
    }

    /**
     * Truy cập trang web
     */
    public void goUrl() {
        goToURL(url);
        verifyPageUrl(url, "Verify url");
    }

    /**
     * Chuyển sang tab Login
     */
    public void goToLogin() {
        By xpath = getByXpathDynamic(BaseConst.DYNAMIC_A_TEXT, "Log in");
        clickElement(xpath, "Log in");
        checkHeading("h1", "Welcome, Please Sign In!");
    }

    public void reloadPage()
    {
        refreshPage();
    }

    /**
     * Nhập email
     * @param email
     */
    public void inputEmail(String email) {
        inputTextWithName("Email", email);
    }

    /**
     * Nhập password
     * @param password
     */
    public void inputPassword(String password) {
        inputTextWithName("Password", password);
    }

    /**
     * Click Login button
     */
    public void clickLogInBtn() {
        clickButton("Log in");
    }

    public void login(String email, String password) {
        inputEmail(email);
        inputPassword(password);
    }

    /**
     * verifyMessage1: Kiểm tra xuất hiện error message: Please enter your email
     */
    public void verifyMessage1() {
        verifyErrorMessage(BaseConst.DYNAMIC_LABEL_SPAN_TEXT, "Email:", "Please enter your email");
    }

    /**
     * verifyMessage2: Kiểm tra xuất hiện error message: Wrong email
     * @param email
     */
    public void verifyMessage2(String email) {
        if (!isValidEmail(email)) {
            verifyErrorMessage(BaseConst.DYNAMIC_LABEL_SPAN_TEXT, "Email:", "Wrong email");
        }
    }

    /**
     * verifyMessage3: Kiểm tra xuất hiện error message: Login was unsuccessful. Please correct the errors and try again. No customer account found
     */
    public void verifyMessage3() {
            verifyErrorMessage(BaseConst.DYNAMIC_DIV_TEXT, null, "Login was unsuccessful. Please correct the errors and try again.");
            verifyErrorMessage(BaseConst.DYNAMIC_LI_TEXT,null,"No customer account found");
    }

    /**
     * verifyMessage4: Kiểm tra xuất hiện error message: Login was unsuccessful. Please correct the errors and try again. The credentials provided are incorrect
     */
    public void verifyMessage4() {
        verifyErrorMessage(BaseConst.DYNAMIC_DIV_TEXT, null, "Login was unsuccessful. Please correct the errors and try again.");
        verifyErrorMessage(BaseConst.DYNAMIC_LI_TEXT,null,"The credentials provided are incorrect");
    }
    public void isEnableButton(String text) {
        By xpath = getByXpathDynamic(BaseConst.DYNAMIC_BUTTON_TEXT_FORM, text);
        WebElement element = waitForElementVisibleWithBy((xpath));
        assertEqualCondition(null,element.isEnabled(),false, FailureHandling.STOP_ON_FAILURE,"");
    }

    private String url = "https://demo.nopcommerce.com/";
}
