package com.MobileFolk.utils;

import com.MobileFolk.constants.FailureHandling;
import com.MobileFolk.constants.FrameworkConst;
import com.MobileFolk.constants.LogType;
import com.MobileFolk.driver.DriverManager;
import com.MobileFolk.helpers.CaptureHelpers;
import com.MobileFolk.helpers.PropertiesHelpers;
import com.MobileFolk.report.ExtentReportManager;
import com.MobileFolk.report.ExtentTestManager;
import io.qameta.allure.Step;
import lombok.SneakyThrows;

import org.apache.logging.log4j.util.Strings;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.*;

import static com.MobileFolk.constants.FrameworkConst.*;


public class WebUI {
    public static WebDriverWait getWaitDriver(long... duration) {
        if (duration.length > 0 && duration[0] != 0)
            return new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(duration[0]), Duration.ofMillis(500));
        return new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(FrameworkConst.WAIT_EXPLICIT), Duration.ofMillis(500));
    }

    public static Actions getActions() {
        return new Actions(DriverManager.getDriver());
    }

    public static JavascriptExecutor getJsExecutor() {
        JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
        return js;
    }


    // region Navigation

    /**
     * Open website with get URL
     *
     * @param URL
     */
    @Step("Go to URL : {URL}")
    public static void goToURL(String URL) {
        //var currentURL = DriverManager.getDriver().getCurrentUrl();
        //if (URL.equalsIgnoreCase(currentURL)) return;

        DriverManager.getDriver().get(URL);

        String msg = FrameworkConst.BOLD_START + FrameworkConst.ICON_Navigate_Right + " Go to URL : " + FrameworkConst.BOLD_END + URL;
        addReportInfo(LogType.INFO, msg, "goToUrl_", null);
    }


    @Step("Refesh URL : {URL}")
    public void refreshPage() {
        String URL = DriverManager.getDriver().getCurrentUrl();
        DriverManager.getDriver().navigate().refresh();


        String msg = FrameworkConst.BOLD_START + FrameworkConst.ICON_Navigate_Right + " Refresh URL : " + FrameworkConst.BOLD_END + URL;
        addReportInfo(LogType.INFO, msg, "refreshPage", null);
    }


    @Step("Back to URL : {URL}")
    public void backPreviousPage() {
        DriverManager.getDriver().navigate().back();

        String URL = DriverManager.getDriver().getCurrentUrl();
        String msg = FrameworkConst.BOLD_START + FrameworkConst.ICON_Navigate_Right + " Back to URL : " + FrameworkConst.BOLD_END + URL;
        addReportInfo(LogType.INFO, msg, "backToPage", null);
    }

    /**
     * Verify the URL Page
     *
     * @param pageUrl
     * @return
     */
    @Step("Verify the Page URL {pageUrl}")
    public static boolean verifyPageUrl(String pageUrl, String msg) {
        sleep(WAIT_IMPLICIT);
        Log.info("Actual URL: " + DriverManager.getDriver().getCurrentUrl());
        return DriverManager.getDriver().getCurrentUrl().contains(pageUrl.trim());
    }

    public static void openNewTab() {
        // Opens a new tab and switches to new tab
        DriverManager.getDriver().switchTo().newWindow(WindowType.TAB);
    }

    public static void openNewWindow() {
        // Opens a new window and switches to new window
        DriverManager.getDriver().switchTo().newWindow(WindowType.WINDOW);
    }

    public String getCurrentWindowHandle() {
        return DriverManager.getDriver().getWindowHandle();
    }

    public void closeAllWindowExceptCurrent() {
        String currentWindow = DriverManager.getDriver().getWindowHandle();
        sleep(WAIT_SLEEP_STEP);
        Set<String> listWindows = DriverManager.getDriver().getWindowHandles();
        for (String window : listWindows) {
            if (!window.equals(currentWindow)) {
                switchToWindowByHandle(window);
                try {
                    DriverManager.getDriver().close();
                } catch (NoSuchWindowException exception) {
                    Log.info("Need research!");
                }
                sleepMilisecond(500);
            }
        }
        switchToWindowByHandle(currentWindow);
    }

    public static void switchToLastWindow() {
        Set<String> windowHandles = DriverManager.getDriver().getWindowHandles();
        DriverManager.getDriver().switchTo().window(DriverManager.getDriver().getWindowHandles().toArray()[windowHandles.size() - 1].toString());
    }

    // Windows
    public static void switchToWindowOrTab(int position) {
        DriverManager.getDriver().switchTo().window(DriverManager.getDriver().getWindowHandles().toArray()[position].toString());
    }

    public static boolean verifyNumberOfWindowsOrTab(int number) {
        return new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(FrameworkConst.WAIT_EXPLICIT)).until(ExpectedConditions.numberOfWindowsToBe(number));
    }

    public static void switchToMainWindow() {
        DriverManager.getDriver().switchTo().window(DriverManager.getDriver().getWindowHandles().toArray()[0].toString());
    }

    public static void switchToWindowByHandle(String windowHandle) {
        DriverManager.getDriver().switchTo().window(windowHandle);
    }
    // endregion

    // region Find Element

    /**
     * Get Website Elements
     */
    public static WebElement getWebElement(Object object) {
        WebElement element;
        if (object instanceof By) {
            overwriteImplicitTimeout(Duration.ofSeconds(5));
            element = DriverManager.getDriver().findElement((By) object);
            overwriteImplicitTimeout(Duration.ofSeconds(0));
        } else element = (WebElement) object;
        return element;
    }

    public static List<WebElement> getListWebElement(By by) {
        overwriteImplicitTimeout(Duration.ofSeconds(5));
        List<WebElement> elements = DriverManager.getDriver().findElements(by);
        overwriteImplicitTimeout(Duration.ofSeconds(0));
        return elements;
    }

    /**
     * Get all child elements into the list
     *
     * @param object
     * @return
     */
    public static List<WebElement> getElementListWithULHTML(Object object) {
        WebElement element = getWebElement(object);
        if (!verifyElementDisplayed(element)) {
            throw new NoSuchElementException(String.format(ELEMENT_NOT_FOUND, element));
        }
        List<WebElement> links = element.findElements(By.tagName("li"));
        for (int i = 0; i < links.size(); i++) {
            System.out.println(links.get(i).getText());
        }
        return links;
    }

    public List<WebElement> getChildWebElemtn(Object parentObj, Object childXPath) {
        return null;
    }

    public static By getByXpathDynamic(String locatorForm, String... keyValue) {
        return By.xpath(getStringXpathDynamic(locatorForm, keyValue));

    }


    /**
     * Receives a wildcard string, replace the wildcard with the value and return to the caller
     *
     * @param xpath Xpath with wildcard string
     * @param value multi value to be replaced in place of wildcard
     *              VD: ObjectUtils.getXpathDynamic("//button[normalize-space()='%s']//div[%d]//span[%d]", "Login", 2, 10);
     * @return dynamic xpath string
     */
    @SneakyThrows
    public static String getStringXpathDynamic(String xpath, Object... value) {
        if (xpath == null || xpath == "") {
            Log.info("getXpathDynamic: Parameter passing error. The 'xpath' parameter is null.");
            throw new Exception("Warning !! The xpath is null.");
        } else {
            if (value.length == 0) return xpath;
            return String.format(xpath, value);
        }
    }

    public static WebElement getFirstElementOrDefault(WebElement scope, By by) {
        var eles = scope.findElements(by);
        if (Objects.nonNull(eles) && eles.size() > 0) return eles.get(0);
        return null;
    }
    // endregion


    // region Base Action

    /**
     * Click the object
     *
     * @param object
     */
    @Step("Click on the element {element}")
    public void clickElement(Object object, String... titles) {

        var element = getWebElement(object);
        waitForElementClickable(element);
        String locator = getLocatorFromWebElement(element);
        String value = getDomPropertyOfElement(element, FrameworkConst.ELEMENT_PROPERTY_TEXTCONTENT);
        if (titles.length > 0) value = titles[0];
        element.click();

        String msg = String.format("Clicked [%s]  <br/> <span style='font-size: 0.75em'>(Element's locator:  %s)</span>", value, locator);
        addReportInfo(LogType.INFO, msg, "clickElement_", locator);
    }

    @Step("Insert text {value} to {title}     (Element's locator: : {element})")
    public WebElement inputTextTo(Object object, String title, String value, boolean... isClear) {
        WebElement element = null;
        try {

            element = getWebElement(object);

            if (isSameValueOfElement(element, value)) return null;

            if (isClear.length == 0 || (isClear.length > 0 && isClear[0])) {
                if ((Objects.nonNull(element.getText()) && !element.getText().isEmpty()) || !getDomPropertyOfElement(element, "value").isEmpty())
                    clearElementText(element);
                else getActions().click(element).perform();
            } else getActions().click(element).perform();
            element.sendKeys(value);

        } finally {
            String locator = getLocatorFromWebElement(element);

            String msg = String.format("Insert text [%s] to [%s] <br/> <span style='font-size: 0.75em'>(Element's locator:  %s)</span>", FrameworkConst.BOLD_START + value + FrameworkConst.BOLD_END, title, locator);
            addReportInfo(LogType.INFO, msg, "setText_" + value, locator);
        }
        return element;
    }

    @Step("Insert text {value} to {title}     (Element's locator: : {element})")
    public WebElement inputTextToWithSelectAllText(Object object, String title, String value) {
        WebElement element = null;
        try {

            element = getWebElement(object);

            if (isSameValueOfElement(element, value)) return null;

            getActions().click(element).perform();
            getActions().click(element).keyDown(element, Keys.CONTROL).sendKeys("A").keyUp(Keys.CONTROL).build().perform();
            element.sendKeys(value);
        } finally {
            String locator = getLocatorFromWebElement(element);

            String msg = String.format("Insert text [%s] to [%s] <br/> <span style='font-size: 0.75em'>(Element's locator:  %s)</span>", FrameworkConst.BOLD_START + value + FrameworkConst.BOLD_END, title, locator);
            addReportInfo(LogType.INFO, msg, "setText_" + value, locator);
        }
        return element;
    }

    @Step("Insert text {value} to {title}     (Element's locator: : {element})")
    public void inputTextByJSTo(WebElement element, String title, String value) {
        getJsExecutor().executeScript("arguments[0].value = '';", element);
        getJsExecutor().executeScript(String.format("arguments[0].innerText = '%s'", value), element);
        String locator = getLocatorFromWebElement(element);
        String msg = String.format("Insert text [%s] to [%s] <br/> <span style='font-size: 0.75em'>(Element's locator:  %s)</span>", FrameworkConst.BOLD_START + value + FrameworkConst.BOLD_END, title, locator);
        addReportInfo(LogType.INFO, msg, "setText_" + value, locator);
    }


    public void clearElementText(Object object) {
        WebElement element = getWebElement(object);
        sleep(WAIT_SLEEP_STEP);
        getActions().click(element).keyDown(element, Keys.CONTROL).sendKeys("A").keyUp(Keys.CONTROL).sendKeys(Keys.BACK_SPACE).build().perform();
        if (!"".equals(element.getText()))
            getActions().keyDown(element, Keys.CONTROL).sendKeys("A").keyUp(Keys.CONTROL).sendKeys(Keys.BACK_SPACE).build().perform();
        sleep(WAIT_SLEEP_STEP);
    }

    public void selectItemFromCheckBox(Object object, boolean isChecked, String... titles) {
        WebElement element = getWebElement(object);
        String msg = null;
        if (isChecked && !element.isSelected()) {
            try {
                getActions().click(element).perform();
            } catch (ElementNotInteractableException | MoveTargetOutOfBoundsException exception) {
                clickElementViaJs(element);
            }
            msg = "Checked";
        } else if (!isChecked && element.isSelected()) {
            try {
                getActions().click(element).perform();
            } catch (ElementNotInteractableException | MoveTargetOutOfBoundsException exception) {
                clickElementViaJs(element);
            }
            msg = "UnChecked";
        }

        String value = getDomPropertyOfElement(element, FrameworkConst.ELEMENT_PROPERTY_TEXTCONTENT);
        if (titles.length > 0) value = titles[0];
        msg = String.format("%s to [%s]  <br/> <span style='font-size: 0.75em'>(Element's locator:  %s)</span>", msg, value, getLocatorFromWebElement(element));
        addReportInfo(LogType.INFO, msg, "clickElement_", getLocatorFromWebElement(element));
    }

    /**
     * Get text of a element
     */
    @Step("Get text element")
    public static String getTextElement(Object object) {


        WebElement webElement = getWebElement(object);
        String textValue = webElement.getText();
        return textValue.trim();
    }


    /**
     * Get the property of Element
     *
     * @param propertyName
     * @return
     */
    public String getDomPropertyOfElement(Object object, String propertyName) {
        var webElement = getWebElement(object);
        try {
            return webElement.getDomProperty(propertyName);
        } catch (Exception e) {
            return Strings.EMPTY;
        }
    }

    /**
     * Get value of element with DOM
     */
    public String getValueOfElement(Object object) {
        var element = getWebElement(object);
        return getDomPropertyOfElement(element, FrameworkConst.ELEMENT_PROPERTY_VALUE);
    }

    /**
     * Get the property of Element
     *
     * @param element
     * @param attName
     * @return
     */
    public String getAttributeOfElement(WebElement element, String attName) {
        return element.getAttribute(attName);
    }

    /**
     * @param element
     */
    public static void scrollToElement(WebElement element) {
        getJsExecutor().executeScript("arguments[0].scrollIntoView(true);", element);
    }

    /**
     * @param by
     */
    public static void scrollToElementWithBy(By by) {


        if (Objects.nonNull(by)) {
            scrollToElement(getWebElement(by));
        }
    }

    public void clickElementViaJs(Object object, String... titles) {
        WebElement element = getWebElement(object);
        getJsExecutor().executeScript("arguments[0].click()", element);

        String locator = getLocatorFromWebElement(element);
        String value = getDomPropertyOfElement(element, FrameworkConst.ELEMENT_PROPERTY_TEXTCONTENT);
        if (titles.length > 0) value = titles[0];

        String msg = String.format("Clicked [%s]  <br/> <span style='font-size: 0.75em'>(Element's locator:  %s)</span>", value, locator);
        addReportInfo(LogType.INFO, msg, "clickElement_", locator);
    }

    /**
     * Upload file using sendKeys
     *
     * @param object
     * @param filePaths List of file Path
     */
    @Step("Upload a file to the system; File path: {filePath}")
    public static void uploadFileSendKeys(Object object, String... filePaths) {
        if (Objects.isNull(filePaths) || filePaths.length == 0) return;

        WebElement webElement = getWebElement(object);
        Arrays.stream(filePaths).forEach(file -> webElement.sendKeys(file));
        addReportInfo(LogType.INFO, "Upload file ..", "Upload File", getLocatorFromWebElement(webElement));
    }

    /**
     * Hover to element with Action
     *
     * @param object
     * @return
     */
    public boolean hoverElement(Object object, boolean... isJavaScripts) {
        try {
            WebElement element = getWebElement(object);
            if (isJavaScripts.length == 0) getActions().moveToElement(element).perform();
            else hoverElementByJS(element);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Hover to element with JavaScript
     *
     * @param object
     * @return
     */
    public boolean hoverElementByJS(Object object) {
        try {
            WebElement element = getWebElement(object);
            String mouseOverScript = "if(document.createEvent){var evObj = document.createEvent('MouseEvents');evObj.initEvent('mouseover', " + " true, false); arguments[0].dispatchEvent(evObj);} else if(document.createEventObject) { arguments[0].fireEvent('onmouseover');}";
            getJsExecutor().executeScript(mouseOverScript, element);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Click the Enter Keyboard
     */
    public static boolean pressENTER() {

        try {
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Press key {keyEvent} from the keyboard")
    public static boolean pressKeyEvent(int keyEvent) {


        try {
            Robot robot = new Robot();
            robot.keyPress(keyEvent);
            robot.keyRelease(keyEvent);
            addReportInfo(LogType.INFO, String.format("Press key %s from the keyboard", keyEvent), null, null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public void sendKeyF9(Object object) {
        WebElement element = getWebElement(object);
        clickElement(element);
        element.sendKeys(Keys.F9);
    }
    // endregion

    // region Assert Condition

    /**
     * Assert Element Objects.
     * Support 3 type of Failure Handling
     */
    public static void assertTrueCondition(Object object, boolean isResult, FailureHandling failureHandling, @Nullable String errMsg) {
        WebElement element = getWebElement(object);
        drawBorderForErrorElement(element, isResult);

        String locator = Strings.EMPTY;
        if (Objects.nonNull(element)) {
            locator = getLocatorFromWebElement(element);
        }

        if (Objects.isNull(errMsg) || errMsg.isEmpty()) {
            errMsg = String.format("Verify TRUE object: " + locator);
        }

        try {
            if (!isResult) {
                Log.info(String.format("%s -> VERIFY : %s", errMsg, isResult));
            }
            switch (failureHandling) {
                case STOP_ON_FAILURE:
                    if (!isResult) {
                        ExtentReportManager.fail(String.format("%s -> VERIFY : %s", errMsg, FAIL));
                    }
                    Assert.assertTrue(isResult);
                    ExtentReportManager.pass(String.format("%s -> VERIFY : %s", errMsg, PASS));
                    break;
                case CONTINUE_ON_FAILURE:
                    if (!isResult) {
                        String softMsg = "SOFT ASSERT: Assert TRUE object: FAILED";

                        Reporter.getCurrentTestResult().setStatus(ITestResult.FAILURE);
                        ExtentReportManager.fail(String.format("%s -> VERIFY : %s", errMsg, FAIL));
                        ExtentReportManager.addScreenShot(softMsg + " " + locator);
                    } else {
                        ExtentReportManager.pass(String.format("%s -> VERIFY : %s", errMsg, PASS));
                    }
                    break;
                default:
                    break;
            }
        } finally {
            if (FrameworkConst.screenshot_all_steps.equals(FrameworkConst.YES)) {
                CaptureHelpers.captureScreenshot(DriverManager.getDriver(), "image_"+ isResult);
            }
            clearBorderForErrorElement(element, isResult);
        }
    }

    /**
     * Assert True Condition with By Object
     */
    private static void assertTrueConditionWithBy(By by, boolean isResult, FailureHandling failureHandling, @Nullable String errMsg) {
        WebElement element = null;
        if (Objects.nonNull(by)) {
            element = getWebElement(by);
        }
        assertTrueCondition(element, isResult, failureHandling, errMsg);
    }

    /**
     * Assert Fail
     */
    public static void assertFalseCondition(Object object, boolean isResult, FailureHandling failureHandling, String errMsg) {
        WebElement element = getWebElement(object);
        drawBorderForErrorElement(element, isResult);

        String locator = Strings.EMPTY;
        if (Objects.nonNull(element)) {
            locator = getLocatorFromWebElement(element);
        }

        String apiLog = "";
        if (Objects.isNull(errMsg) || errMsg.isEmpty()) {
            errMsg = String.format("Verify FALSE object: " + locator);
        } else if (Objects.nonNull(errMsg) && errMsg.contains(SEPARATE_KEY)) {
            String[] errs = errMsg.split(SEPARATE_KEY);
            errMsg = errs[0];
            apiLog = errs[1];
        }
        try {
            if (isResult) {
                Log.info(String.format("%s -> VERIFY : %s", errMsg, !isResult));
                ExtentReportManager.logMessage(errMsg);
            }
            switch (failureHandling) {
                case STOP_ON_FAILURE:
                    if (isResult) {
                        ExtentReportManager.fail(String.format("%s -> VERIFY : %s", errMsg, FAIL));
                        ExtentReportManager.addNode(apiLog, "API Response");
//                        ExtentReportManager.logMessage("API Log: " + apiLog);
                    }
                    Assert.assertFalse(isResult);
                    ExtentReportManager.pass(String.format("%s -> VERIFY : %s", errMsg, PASS));
                    break;
                case CONTINUE_ON_FAILURE:
                    if (isResult) {
                        String softMsg = "SOFT ASSERT: Verify FALSE object: FAILED";

                        Reporter.getCurrentTestResult().setStatus(ITestResult.FAILURE);
                        ExtentReportManager.fail(String.format("%s -> VERIFY : %s", errMsg, FAIL));
                        ExtentReportManager.addScreenShot(softMsg + " " + locator);
                    } else {
                        ExtentReportManager.pass(String.format("%s -> VERIFY : %s", errMsg, FrameworkConst.PASS));
                    }
                    break;
            }
        } finally {
            //  Attached the report info
            if (FrameworkConst.screenshot_all_steps.equals(FrameworkConst.YES)) {
                CaptureHelpers.captureScreenshot(DriverManager.getDriver(), "verifyElement_" + isResult);
            }
            clearBorderForErrorElement(element, isResult);
        }
    }

    /**
     * Assert False condition
     */
    private static void assertFalseConditionWithBy(By by, boolean isResult, FailureHandling failureHandling, String errMsg) {
        WebElement element = null;
        if (Objects.nonNull(by)) {
            element = getWebElement(by);
        }
        assertFalseCondition(element, isResult, failureHandling, errMsg);
    }

    /**
     * Assert Equal
     */
    public static void assertEqualCondition(Object object, Object actual, Object expected, FailureHandling failureHandling, String errMsg) {
        WebElement element = getWebElement(object);
        boolean isResult = Objects.equals(actual, expected);
        drawBorderForErrorElement(element, isResult);

        String locator = Strings.EMPTY;
        if (Objects.nonNull(element)) {
            locator = getLocatorFromWebElement(element);
        }

        if (Objects.isNull(errMsg) || errMsg.isEmpty()) {
            errMsg = String.format("Verify equal object " + locator);
        }

        errMsg = String.format("%s - Actual: %s ; Expected: %s", errMsg, actual.toString(), expected.toString());

        try {
            if (!isResult) {
                Log.info(String.format("%s -> VERIFY : %s", errMsg, isResult));
            }

            switch (failureHandling) {
                case STOP_ON_FAILURE:
                    if (!isResult) {
                        ExtentReportManager.fail(String.format("%s -> VERIFY : %s", errMsg, FAIL));
                    }
                    Assert.assertEquals(actual, expected);
                    ExtentReportManager.pass(String.format("%s -> VERIFY : %s", errMsg, PASS));
                    break;
                case CONTINUE_ON_FAILURE:
                    if (!isResult) {
                        String softMsg = "SOFT ASSERT: Verify the result: FAILED";
                        Reporter.getCurrentTestResult().setStatus(ITestResult.FAILURE);
                        ExtentReportManager.fail(String.format("%s -> VERIFY : %s", errMsg, FAIL));
                        ExtentReportManager.addScreenShot(softMsg + " " + locator);
                    } else {
                        ExtentReportManager.pass(String.format("%s -> VERIFY : %s", errMsg, PASS));
                    }
                    break;
            }
        } finally {
            //  Attached the report info
            if (FrameworkConst.screenshot_all_steps.equals(FrameworkConst.YES)) {
                CaptureHelpers.captureScreenshot(DriverManager.getDriver(), "verifyElement_" + isResult);
            }
            clearBorderForErrorElement(element, isResult);
        }
    }

    /**
     * Draw a border for the error element
     */
    private static void drawBorderForErrorElement(Object object, boolean isResult) {
        WebElement element = getWebElement(object);
        if (FrameworkConst.draw_border_err_element.equalsIgnoreCase(FrameworkConst.YES) && !isResult && Objects.nonNull(element)) {
            // scrollToElement(element);
            try {
                scrollElementToViewCenter(element);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Log.info("Vincent  =: scrollElementToViewCenter COMPLETE");
            JavascriptExecutor js = getJsExecutor();
            js.executeScript("arguments[0].style.border='3px solid red'", element);
        }
    }


    /**
     * Draw a border for the error element
     */
    private static void drawBorderForErrorElement(By by, boolean isResult) {
        WebElement element = null;
        if (Objects.nonNull(by)) {
            element = getWebElement(by);
        }
        drawBorderForErrorElement(element, isResult);
    }


    /**
     * Clear a border for the error element
     */
    private static void clearBorderForErrorElement(Object object, boolean isResult) {
        WebElement element = getWebElement(object);
        if (FrameworkConst.draw_border_err_element.equalsIgnoreCase(FrameworkConst.YES) && !isResult && Objects.nonNull(element)) {
            JavascriptExecutor js = getJsExecutor();
            js.executeScript("arguments[0].style.border='0px solid red'", element);
        }
    }

    /**
     * Clear a border for the error element
     */
    private static void clearBorderForErrorElement(By by, boolean isResult) {
        WebElement element = null;
        if (Objects.nonNull(by)) {
            element = getWebElement(by);
        }
        clearBorderForErrorElement(element, isResult);
    }

    public List<WebElement> waitForAllElementPresent(By by) {
        try {
            return getWaitDriver().until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
        } catch (Throwable error) {
            Log.error("Element not exist. " + by.toString());
        }
        return null;
    }
    // endregion

    public static WebElement waitForElementVisibleWithBy(By by) {
        try {
            WebElement obj = getWaitDriver().until(ExpectedConditions.visibilityOfElementLocated(by));

            if (Objects.nonNull(obj))
                Log.info(String.format("waitForElementVisibleWithBy: Element %s : visible", by.toString()));

            return obj;
        } catch (Exception e) {
            var elementList = DriverManager.getDriver().findElements(by);
            Log.error(String.format("waitForElementVisibleWithBy: Element %s : invisible %s", by.toString(), elementList.isEmpty() ? "" : " Had more element with this XPATH. Please re-check!"));
        }
        return null;
    }


    /**
     * Verify whether the element is visible or not
     */
    public static WebElement waitForElementVisible(Object object) {
        if (object instanceof By) return waitForElementVisibleWithBy((By) object);
        WebElement element = getWebElement(object);
        String locator = getLocatorFromWebElement(element);
        String msg = String.format("waitForElementVisible: Element %s : invisible", locator);
        try {
            element = getWaitDriver().until(ExpectedConditions.visibilityOf(element));
        } catch (Exception e) {
            Log.info(msg);
            element = null;
        }
        if (Objects.nonNull(element)) msg = String.format("waitForElementVisible: Element %s : visible", locator);
        Log.info(msg);
        return element;
    }

    public static WebElement waitForElementClickableBy(By by) {
        WebElement element;
        String msg = String.format("waitForElementClickableBy: Element %s : unclickable", by.toString());
        try {
            element = getWaitDriver().until(ExpectedConditions.elementToBeClickable(by));
        } catch (Exception e) {
            Log.info(msg);
            element = null;
        }
        if (Objects.nonNull(element))
            msg = String.format("waitForElementClickableBy: Element %s : clickable", by.toString());
        Log.info(msg);
        return element;
    }


    public static WebElement waitForElementClickable(Object object) {
        if (object instanceof By) return waitForElementClickableBy((By) object);
        WebElement element = getWebElement(object);
        String locator = getLocatorFromWebElement(element);
        String msg = String.format("waitForElementClickable: Element %s : unclickable", locator);
        try {
            element = getWaitDriver().until(ExpectedConditions.elementToBeClickable(element));
        } catch (Exception e) {
            Log.info(msg);
            element = null;
        }
        if (Objects.nonNull(element)) msg = String.format("waitForElementClickable: Element %s : clickable", locator);
        Log.info(msg);
        return element;
    }

    /**
     * @param by
     * @return
     */
    public WebElement waitForElementPresent(By by) {
        try {
            return getWaitDriver().until(ExpectedConditions.presenceOfElementLocated(by));
        } catch (Throwable error) {
            Log.error("Element not exist. " + by.toString());
        }
        return null;
    }

    /**
     * wait for element invisible
     */
    public static void waitForElementInvisible(Object object, long... waitDuration) {
        if (object instanceof By)
            getWaitDriver(waitDuration).until(ExpectedConditions.invisibilityOfElementLocated((By) object));
        else if (object instanceof WebElement)
            getWaitDriver(waitDuration).until(ExpectedConditions.invisibilityOf((WebElement) object));
    }

    public static boolean verifyElementDisplayed(Object object, int... wait) {
        try {
            WebElement element;
            if (object instanceof By) {
                overwriteImplicitTimeout(Duration.ofSeconds(wait.length > 0 ? wait[0] : WAIT_IMPLICIT));
                element = DriverManager.getDriver().findElement((By) object);
                overwriteImplicitTimeout(Duration.ofSeconds(0));
            } else element = (WebElement) object;
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Step
    public static void waitToVerifyElementVisible(Object object, boolean expDisplay, FailureHandling failureHandling) {
        if (object instanceof By) waitToVerifyElementVisibleWithBy((By) object, expDisplay, failureHandling);
        WebElement element = getWebElement(object);
        String locator = getLocatorFromWebElement(element);
        String msg;

        boolean isResult;
        if (expDisplay) {
            msg = String.format("Verify the object %s : visible", locator);
            isResult = Objects.nonNull(waitForElementVisible(element)) && element.isDisplayed();
        } else {
            msg = String.format("Verify the object %s : invisible", locator);
            isResult = Objects.isNull(waitForElementVisible(element)) || !element.isDisplayed();
        }
        assertTrueCondition(element, isResult, failureHandling, msg);
    }

    /**
     * Verify whether the element is visible or not
     *
     * @param by
     * @return true if visible or otherwise
     */
    private static void waitToVerifyElementVisibleWithBy(By by, boolean expDisplay, FailureHandling failureHandling) {
        String msg;
        WebElement element = null;
        boolean isResult;
        if (expDisplay) {
            msg = String.format("Verify the object %s : visible", by);
            element = waitForElementVisibleWithBy(by);
            isResult = Objects.nonNull(element) && element.isDisplayed();
        } else {
            msg = String.format("Verify the object %s : invisible", by);
            element = waitForElementVisibleWithBy(by);
            isResult = Objects.isNull(element) || !element.isDisplayed();
        }
        assertTrueCondition(element, isResult, failureHandling, msg);
    }

    // endregion

    // region Alert
    public static void alertAccept() {
        DriverManager.getDriver().switchTo().alert().accept();
    }

    public static void alertDismiss() {
        DriverManager.getDriver().switchTo().alert().dismiss();
    }

    public static void alertGetText() {
        DriverManager.getDriver().switchTo().alert().getText();
    }

    public static void alertSetText(String text) {
        DriverManager.getDriver().switchTo().alert().sendKeys(text);
    }

    public static boolean verifyAlertPresent(int timeOut) {
        try {
            getWaitDriver().until(ExpectedConditions.alertIsPresent());
            return true;
        } catch (Throwable error) {
            Assert.fail("Not found Alert.");
            return false;
        }
    }

    // endregion

    // region Utils

    public static void scrollElementToViewCenter(Object elementObj) {
        //Log.info("Vincent  =: scrollElementToViewCenter ");
        WebElement element = getWebElement(elementObj);
        String scrollElementIntoMiddle = "var viewPortHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);" + "var elementTop = arguments[0].getBoundingClientRect().top;" + "window.scrollBy(0, elementTop-(viewPortHeight/2));";
        ((JavascriptExecutor) DriverManager.getDriver()).executeScript(scrollElementIntoMiddle, element);
    }

    public boolean isSameValueOfElement(WebElement element, String expValue) {
        return expValue.equals(element.getText()) || expValue.equals(getValueOfElement(element));
    }

    public static String getLanguageValue(String key) {
        return PropertiesHelpers.getLanguageValue(key);
    }

    /**
     * Force wait
     *
     * @param second
     */
    public static void sleep(double second) {
        try {
            Thread.sleep((long) (second * 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleepMilisecond(double milisecond) {
        try {
            Thread.sleep((long) (milisecond));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void overwriteImplicitTimeout(Duration duration) {
        DriverManager.getDriver().manage().timeouts().implicitlyWait(duration);
    }

    /**
     * Add more information for Report: Including Extent and Allure.
     * You can add more report at this function.
     *
     * @param extMsg
     * @param capText
     */
    public static void addReportInfo(LogType logType, String extMsg, String capText, String locator) {
        // Add for Extent Report
        if (ExtentTestManager.getExtentTest() != null) {
            if (logType.equals(LogType.INFO)) ExtentReportManager.info(extMsg);
            else ExtentReportManager.pass(extMsg);
        }
    }


    /**
     * Generate unique value
     */
    public String generateUniqueValue(String value) {
        return String.format("%s_%s", value, getTimeStamp());
    }


    /**
     * Get the element locator from WebElement
     */
    public static String getLocatorFromWebElement(WebElement element) {
        var list = element.toString().split("->");
        if (list.length > 1)
            return element.toString().split("->")[1].replaceFirst("xpath:(?s)(.*)\\]", "$1" + "").trim();
        else return element.toString();
    }

    public String getTimeStamp() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        return format.format(date);
    }
    // endregion

    // region Haven't Use

    public static boolean verifyElementListExist(List<WebElement> elements) {
        return Objects.nonNull(elements) && elements.size() > 0;
    }

    /**
     * Upload files using EventKey
     *
     * @param object
     * @param filePath
     */
    @Step("Upload a file to the system; File path: {filePath}")
    public static void uploadFileForm(Object object, String filePath) {

        WebElement webElement = getWebElement(object);
        //Click để mở form upload
        getActions().moveToElement(webElement).click().perform();
        sleep(WAIT_IMPLICIT);

        // Khởi tạo Robot class
        Robot rb = null;
        try {
            rb = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

        // Copy File path vào Clipboard
        StringSelection str = new StringSelection(filePath);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(str, null);

        // Nhấn Control+V để dán
        rb.keyPress(KeyEvent.VK_CONTROL);
        rb.keyPress(KeyEvent.VK_V);

        // Xác nhận Control V trên
        rb.keyRelease(KeyEvent.VK_CONTROL);
        rb.keyRelease(KeyEvent.VK_V);
        sleep(WAIT_IMPLICIT);
        // Nhấn Enter
        rb.keyPress(KeyEvent.VK_ENTER);
        rb.keyRelease(KeyEvent.VK_ENTER);
        addReportInfo(LogType.INFO, "Upload file .." + filePath, "Upload File", getLocatorFromWebElement(webElement));
    }

    public boolean verifyPageTitle(String expectedTitle) {

        return getPageTitle().equals(expectedTitle);
    }

    public static String getPageTitle() {

        String title = DriverManager.getDriver().getTitle();
        Log.info("getPageTitle: Page Title: " + title);
        return title;
    }

    public static boolean verifyElementTextEqual(By by, String expectedValue) {

        var obj = waitForElementVisibleWithBy(by);
        return getTextElement(obj).trim().equals(expectedValue.trim());
    }
    // endregion

}
