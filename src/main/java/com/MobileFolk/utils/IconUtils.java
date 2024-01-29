package com.MobileFolk.utils;

import com.MobileFolk.constants.Browser;
import com.MobileFolk.constants.FrameworkConst;

public final class IconUtils {

    private IconUtils() {
    }

    public static String getBrowserIcon(String browser) {
        if (browser.contains(Browser.CHROME.toString())) {
            return FrameworkConst.ICON_BROWSER_CHROME;
        } else if (browser.contains(Browser.EDGE.toString())) {
            return FrameworkConst.ICON_BROWSER_EDGE;
        } else if (browser.contains(Browser.FIREFOX.toString())) {
            return FrameworkConst.ICON_BROWSER_FIREFOX;
        } else if (browser.contains(Browser.SAFARI.toString())) {
            return FrameworkConst.ICON_BROWSER_SAFARI;
        } else {
            return browser;
        }
    }

    public static String getOSIcon() {

        String operationSystem = BrowserInfoUtils.getOSInfo().toLowerCase();
        if (operationSystem.contains("win")) {
            return FrameworkConst.ICON_OS_WINDOWS;
        } else if (operationSystem.contains("nix") || operationSystem.contains("nux") || operationSystem.contains("aix")) {
            return FrameworkConst.ICON_OS_LINUX;
        } else if (operationSystem.contains("mac")) {
            return FrameworkConst.ICON_OS_MAC;
        }
        return operationSystem;
    }
}
