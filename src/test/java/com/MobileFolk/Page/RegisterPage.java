package com.MobileFolk.Page;

import com.MobileFolk.Common.BaseConst;
import com.MobileFolk.Common.BasePage;
import com.MobileFolk.constants.FrameworkConst;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterPage extends BasePage {
    /**
     * Init a new instance
     *
     * @param driver : The WebDriver to interact with elements
     */
    public RegisterPage(WebDriver driver) {
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
     * Chuyển sang tab Register bằng cách chọn element Register
     */
    public void goToRegister() {
        By xpath = getByXpathDynamic(BaseConst.DYNAMIC_A_TEXT, "Register");
        clickElement(xpath, "Navigate Register");
        checkHeading("h1", "Register");
    }

    /**
     * Chọn giới tính
     * @param gender
     */
    public void selectGender(String gender) {
        selectCheckBoxRadio(gender);
    }

    /**
     * Nhập firstName
     * @param firstName
     */
    public void inputFirstName(String firstName) {
        inputTextWithName(fName, firstName);
    }

    /**
     * Nhập lastName
     * @param lastName
     */
    public void inputLastName(String lastName) {
        inputTextWithName(lName, lastName);
    }

    /**
     * Chọn ngày
     * @param day
     */
    public void selectDay(String day) {
        selectDate("DateOfBirthDay", day);
    }

    /**
     * Chọn tháng
     * @param month
     */
    public void selectMonth(String month) {
        selectDate("DateOfBirthMonth", month);
    }

    /**
     * Chọn năm
     * @param year
     */
    public void selectYear(String year) {
        selectDate("DateOfBirthYear", year);

    }

    /**
     * Chọn date gồm: ngày, tháng, năm
     * @param day
     * @param month
     * @param year
     */
    public void selectDate(String day, String month, String year) {
        selectDay(day);
        selectMonth(month);
        selectYear(year);
    }

    /**
     * Nhập email
     * @param email
     */
    public void inputEmail(String email) {
        inputTextWithName(emailN, email);
    }

    /**
     * Nhập công ty
     * @param company
     */
    public void inputCompany(String company) {
        inputTextWithName(companyN, company);
    }

    /**
     * Chọn checkbox
     */
    public void selectOptions() {
        By xpath = getByXpathDynamic(BaseConst.DYNAMIC_LABEL_TEXT_FORM, "Newsletter:");
        clickElement(xpath, "Select options");
    }

    /**
     * Nhập password
     * @param password
     */
    public void inputPassword(String password) {
        inputTextWithName(pass, password);
    }

    /**
     * Nhập confirmPassword
     * @param confirmPassword
     */
    public void inputConfirmPassword(String confirmPassword) {
        inputTextWithName(confirmPass, confirmPassword);
    }


    /**
     * Chọn Register button
     */
    public void clickRegisterBtn() {
        clickButton("Register");
    }

    public void register(String gender, String fName, String lName, String email, String company, String pass, String confirmPass) {
        selectGender(gender);
        inputFirstName(fName);
        inputLastName(lName);
        inputEmail(email);
        inputCompany(company);
        inputPassword(pass);
        inputConfirmPassword(confirmPass);
    }


    /**
     * Verify các error message
     * @param fName
     * @param lName
     * @param email
     * @param pass
     * @param confirmPass
     */
    public void verifyErrorMessage(String fName, String lName, String email, String pass, String confirmPass) {

        if (fName != null || lName != null || email != null || pass != null || confirmPass != null) {
            clickRegisterBtn();
            verifyErrorMessage(BaseConst.DYNAMIC_LABEL_SPAN_TEXT, "First name:", "First name is required.");
            verifyErrorMessage(BaseConst.DYNAMIC_LABEL_SPAN_TEXT, "Last name:", "Last name is required.");
            verifyErrorMessage(BaseConst.DYNAMIC_LABEL_SPAN_TEXT, "Email:", "Email is required.");
            verifyErrorMessage(BaseConst.DYNAMIC_LABEL_SPAN_TEXT, "Password:", "Password is required.");
            verifyErrorMessage(BaseConst.DYNAMIC_LABEL_SPAN_TEXT, "Confirm password:", "Password is required.");

            if (!isValidEmail(email)) {
                verifyErrorMessage(BaseConst.DYNAMIC_LABEL_SPAN_TEXT, "Email:", "Wrong email");
            }
        }
    }

    /**
     * Kiểm tra email tồn tại
     */
    public void verifyEmailExists() {
        verifyErrorMessage(BaseConst.DYNAMIC_LI_TEXT, null, "The specified email already exists");
    }

    /**
     * Kiểm tra password với confirmPassword
     * @param password
     * @param confirmPassword
     */
    public void yourPassword(String password, String confirmPassword) {
        inputPassword(password);
        inputConfirmPassword(confirmPassword);
        clickRegisterBtn();
        if (password.length() < 6) {
            verifyErrorMessage(BaseConst.DYNAMIC_P_TEXT, null, "Password must meet the following rules:");
            verifyErrorMessage(BaseConst.DYNAMIC_LI_TEXT, null, "must have at least 6 characters");
        }
        if (!password.equals(confirmPassword)) {
            verifyErrorMessage(BaseConst.DYNAMIC_LABEL_SPAN_TEXT, "Confirm password:", "The password and confirmation password do not match.");
        }
    }




    private String url = "https://demo.nopcommerce.com/";
    String fName = "FirstName";
    String lName = "LastName";
    String emailN = "Email";
    String companyN = "Company";
    String pass = "Password";
    String confirmPass = "ConfirmPassword";

}
