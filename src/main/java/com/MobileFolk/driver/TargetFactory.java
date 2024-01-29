package com.MobileFolk.driver;

import com.MobileFolk.constants.FrameworkConst;
import com.MobileFolk.constants.Target;
import com.MobileFolk.exceptions.TargetNotValidException;
import com.MobileFolk.utils.Log;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

public class TargetFactory {

    public WebDriver createInstance() {
        Target target = Target.valueOf(FrameworkConst.TARGET.toUpperCase());
        WebDriver webdriver;

        switch (target) {
            case LOCAL:
                //Create new driver from Enum setup in BrowserFactory class
                webdriver = BrowserFactory.valueOf(FrameworkConst.BROWSER.toUpperCase()).createDriver();
                break;
            case REMOTE:
                //Create new driver on Cloud (Selenium Grid, Docker) from method below
                webdriver = createRemoteInstance(BrowserFactory.valueOf(FrameworkConst.BROWSER.toUpperCase()).getOptions());
                break;
            default:
                throw new TargetNotValidException(target.toString());
        }
        return webdriver;
    }

    public WebDriver createInstance(String browser) {
        Target target = Target.valueOf(FrameworkConst.TARGET.toUpperCase());
        WebDriver webdriver;

        switch (target) {
            case LOCAL:
                //Create new driver from Enum setup in BrowserFactory class
                webdriver = BrowserFactory.valueOf(browser.toUpperCase()).createDriver();
                webdriver.manage().window().setPosition(new Point(-10,0));
                break;
            case REMOTE:
                //Create new driver on Cloud (Selenium Grid, Docker) from method below
                webdriver = createRemoteInstance(BrowserFactory.valueOf(browser.toUpperCase()).getOptions());
                webdriver.manage().window().setPosition(new Point(-10,0));
                break;
            default:
                throw new TargetNotValidException(target.toString());
        }
        return webdriver;
    }

    private RemoteWebDriver createRemoteInstance(MutableCapabilities capability) {
        RemoteWebDriver remoteWebDriver = null;
        try {
            String gridURL = String.format("http://%s:%s/wd/hub", FrameworkConst.REMOTE_URL, FrameworkConst.REMOTE_PORT);

            remoteWebDriver = new RemoteWebDriver(new URL(gridURL), capability);
            remoteWebDriver.setFileDetector(new LocalFileDetector());
        } catch (java.net.MalformedURLException e) {
            Log.error("Grid URL is invalid or Grid Port is not available");
            Log.error(String.format("Browser: %s", capability.getBrowserName()), e);
        } catch (IllegalArgumentException e) {
            Log.error(String.format("Browser %s is not valid or recognized", capability.getBrowserName()), e);
        }

        return remoteWebDriver;
    }
}