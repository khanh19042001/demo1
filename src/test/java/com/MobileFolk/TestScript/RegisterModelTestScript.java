package com.MobileFolk.TestScript;

import com.MobileFolk.Common.TestBase;
import com.MobileFolk.Page.RegisterPage;
import com.MobileFolk.annotations.Annotation;
import com.MobileFolk.constants.AuthorType;
import com.MobileFolk.constants.CategoryType;
import com.MobileFolk.driver.DriverManager;
import com.MobileFolk.driver.TargetFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ThreadGuard;
import org.testng.annotations.*;


public class RegisterModelTestScript extends TestBase {

    public RegisterPage register;

    @Parameters({"browser"})
    @BeforeClass(alwaysRun = true)
    public void createDriver(@Optional("chrome") String browser) {
        WebDriver webDriver = ThreadGuard.protect(new TargetFactory().createInstance(browser));
        DriverManager.setDriver(webDriver);
        webDriver.manage().window().maximize();
        register = new RegisterPage(DriverManager.getDriver());
        register.goUrl();
        register.goToRegister();

    }

    @Annotation(category = {CategoryType.REGRESSION}, author = {AuthorType.Khanh}, reviewer = {AuthorType.Khanh})
    @Test(description = "Dang ky voi empty data")
    public void TC_01()
    {
        register.register("Male","","","","","","");
        register.verifyErrorMessage("","","","","");
    }

    @Annotation(category = {CategoryType.REGRESSION}, author = {AuthorType.Khanh}, reviewer = {AuthorType.Khanh})
    @Test(description = "Dang ky voi invalid email")
    public void TC_02()
    {
        register.register("Male","","","123","","","");
        register.verifyErrorMessage("","","123","","");
    }

    @Annotation(category = {CategoryType.REGRESSION}, author = {AuthorType.Khanh}, reviewer = {AuthorType.Khanh})
    @Test(description = "Dang ky voi email da ton tai")
    public void TC_03()
    {
        register.register("Male","Nguyen","Khanh","abc@gmail.com","abc","123456","123456");
        register.clickRegisterBtn();
        register.verifyEmailExists();
    }

    @Annotation(category = {CategoryType.REGRESSION}, author = {AuthorType.Khanh}, reviewer = {AuthorType.Khanh})
    @Test(description = "Dang ky voi password <6 ki tu")
    public void TC_04()
    {
        register.yourPassword("12345","");
    }

    @Annotation(category = {CategoryType.REGRESSION}, author = {AuthorType.Khanh}, reviewer = {AuthorType.Khanh})
    @Test(description = "Dang ky voi confirm password no match with password")
    public void TC_05()
    {
        register.yourPassword("123456","12345");
    }
    @Annotation(category = {CategoryType.REGRESSION}, author = {AuthorType.Khanh}, reviewer = {AuthorType.Khanh})
    @Test(description = "Dang ky voi thong tin hop le")
    public void TC_06() {
        register.selectGender("Male");
        register.inputFirstName("Nguyen");
        register.inputLastName("Khanh");
        register.selectDate("1","Month","1914");
        register.inputEmail("abc123@gmail.com");
        register.inputCompany("abc");
        register.selectOptions();
        register.inputPassword("123");
        register.inputConfirmPassword("123");
    }

}
