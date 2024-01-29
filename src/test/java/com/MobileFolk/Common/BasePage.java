package com.MobileFolk.Common;

import com.MobileFolk.constants.FailureHandling;
import com.MobileFolk.driver.DriverManager;
import com.MobileFolk.utils.WebUI;
import lombok.Data;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class BasePage extends WebUI {
    protected WebDriver webDriver;

    protected BasePage() {
        webDriver = DriverManager.getDriver();
    }

    /**
     * verify heading when navigation
     *
     * @param tag_name
     * @param text
     */
    protected void checkHeading(String tag_name, String text) {
        By xpath = getByXpathDynamic(BaseConst.DYNAMIC_HEADING_TEXT, tag_name, text);
        WebElement element = waitForElementVisibleWithBy(xpath);
        assert element != null;
        assertEqualCondition(null, element.getText(), text, FailureHandling.STOP_ON_FAILURE, "Check log out page");
    }

    /**
     * input text with element input has use name
     *
     * @param text
     * @param value
     */
    protected void inputTextWithName(String text, String value) {
        By inputXpath = getByXpathDynamic(BaseConst.DYNAMIC_INPUT_NAME_FORM, text);
        WebElement element = waitForElementVisible(inputXpath);
        element.clear();
        inputTextTo(element, text, value, true);
        assertEqualCondition(null, element.getAttribute("value"), value, FailureHandling.STOP_ON_FAILURE, "Check data input ");
    }

    /**
     * select checkbox or radio button
     * @param option
     */
    protected void selectCheckBoxRadio(String option) {
        By xpath = getByXpathDynamic(BaseConst.DYNAMIC_OPTION_TEXT_FORM, option);
        clickElement(xpath, "Click " + option);
    }

    /**
     * click element button
     *
     * @param text
     */
    protected void clickButton(String text) {
        By element = getByXpathDynamic(BaseConst.DYNAMIC_BUTTON_TEXT_FORM, text);
        clickElementViaJs(element, "Click Submit button");
    }

    /**
     * Select date when element has type select/option
     * @param nameDate
     * @param text
     */
    protected void selectDate(String nameDate, String text) {
        Select selectDate = new Select(webDriver.findElement(By.name(nameDate)));
        selectDate.selectByVisibleText(text);
    }

    /**
     * Verify error message
     * @param xpathError
     * @param title
     * @param mess
     */
    protected void verifyErrorMessage(String xpathError, String title, String mess) {
        By xpath;
        if (title == null) {
            xpath = getByXpathDynamic(xpathError, mess);
        } else {
            xpath = getByXpathDynamic(xpathError, title, mess);
        }
        WebElement element = waitForElementVisibleWithBy(xpath);
        if(element !=null)
        {
            assertTrueCondition(null, element.isDisplayed(), FailureHandling.STOP_ON_FAILURE, "Verify error message display");
        }
    }

    /**
     * Verify email valid
     * @param email
     * @return
     */
    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}


