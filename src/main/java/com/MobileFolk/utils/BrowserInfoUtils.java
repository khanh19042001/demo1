package com.MobileFolk.utils;

import com.MobileFolk.constants.FrameworkConst;
import org.testng.ITestResult;

public final class BrowserInfoUtils {

    private BrowserInfoUtils() {
    }

    public static String getBrowserInfo(ITestResult iTestResult) {
        String browser = "";
        if (iTestResult.getTestContext().getCurrentXmlTest().getParameter("browser") == null) {
            browser = FrameworkConst.BROWSER.toUpperCase();
        } else {
            browser = iTestResult.getTestContext().getCurrentXmlTest().getParameter("browser").trim().toUpperCase();
        }
        return browser;
    }

    public static String getOSInfo() {
        return System.getProperty("os.name").replace(" ", " ");
    }

}
