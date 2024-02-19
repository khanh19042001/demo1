package com.MobileFolk.TestScript;

import com.MobileFolk.Common.TestBase;
import com.MobileFolk.Page.LoginPage;
import com.MobileFolk.annotations.Annotation;
import com.MobileFolk.constants.AuthorType;
import com.MobileFolk.constants.CategoryType;
import com.MobileFolk.driver.DriverManager;
import com.MobileFolk.driver.TargetFactory;

//import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ThreadGuard;
import org.testng.annotations.*;

public class LoginModelTestScript extends TestBase {
//    static WebDriver driver;
    public LoginPage loginPage;

    @Parameters({"browser"})
    @BeforeClass(alwaysRun = true)
    public void createDriver(@Optional("chrome") String browser) {
        WebDriverManager.chromedriver().clearDriverCache().setup();
        WebDriver webDriver = ThreadGuard.protect(new TargetFactory().createInstance(browser));
        DriverManager.setDriver(webDriver);
        webDriver.manage().window().maximize();
        loginPage = new LoginPage(DriverManager.getDriver());
        loginPage.goUrl();
        loginPage.goToLogin();

    }

    @Annotation(category = {CategoryType.REGRESSION}, author = {AuthorType.Khanh}, reviewer = {AuthorType.Khanh})
    @Test(description = "Login with empty data")
    public void TC_01()
    {
        loginPage.login("","");
        loginPage.clickLogInBtn();
        loginPage.verifyMessage1();
    }

    @Annotation(category = {CategoryType.REGRESSION}, author = {AuthorType.Khanh}, reviewer = {AuthorType.Khanh})
    @Test(description = "Login with invalid data")
    public void TC_02()
    {
        loginPage.login("abc","1234");
        loginPage.clickLogInBtn();
        loginPage.verifyMessage2("abc");
    }

    @Annotation(category = {CategoryType.REGRESSION}, author = {AuthorType.Khanh}, reviewer = {AuthorType.Khanh})
    @Test(description = "Login with email don't register")
    public void TC_03()
    {
        loginPage.login("123@gmail.com","11111");
        loginPage.clickLogInBtn();
        loginPage.verifyMessage3();
    }

    @Annotation(category = {CategoryType.REGRESSION}, author = {AuthorType.Khanh}, reviewer = {AuthorType.Khanh})
    @Test(description = "Login with email register and password empty")
    public void TC_04()
    {
        loginPage.login("abc@gmail.com","");
        loginPage.clickLogInBtn();
        loginPage.verifyMessage4();
    }
    @Annotation(category = {CategoryType.REGRESSION}, author = {AuthorType.Khanh}, reviewer = {AuthorType.Khanh})
    @Test(description = "Login with email register and password wrong")
    public void TC_05()
    {
        loginPage.login("abc@gmail.com","123");
        loginPage.clickLogInBtn();
        loginPage.verifyMessage4();
    }

    @Annotation(category = {CategoryType.REGRESSION}, author = {AuthorType.Khanh}, reviewer = {AuthorType.Khanh})
    @Test(description = "Login successfully")
    public void TC_06()
    {
        loginPage.login("abc@gmail.com","123456");
        loginPage.clickLogInBtn();
    }

    @AfterMethod
    public void afterMethod()
    {
        loginPage.reloadPage();
    }
}
