package com.MobileFolk.driver;

import com.MobileFolk.constants.FrameworkConst;
import com.MobileFolk.exceptions.HeadlessNotSupportedException;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.config.DriverManagerType;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

import static java.lang.Boolean.TRUE;

public enum BrowserFactory {

    CHROME {
        @Override
        public WebDriver createDriver() {
            //  Using the local driver - Wait for firewall approval from Mr. BDHieu
            //  System.setProperty("webdriver.chrome.driver", FrameworkConst.CHROME_DRIVER);
            WebDriverManager.getInstance(DriverManagerType.CHROME).setup();

            return new ChromeDriver(getOptions());
        }

        @Override
        public ChromeOptions getOptions() {
            ChromeOptions chromeOptions = new ChromeOptions();
//            chromeOptions.addArguments(START_MAXIMIZED);
            chromeOptions.addArguments("--headless");
            chromeOptions.addArguments("--disable-infobars");
            chromeOptions.addArguments("--disable-notifications");
//            chromeOptions.addArguments("--window-size=1780,1500");
            chromeOptions.addArguments("--disable-dev-shm-usage");
            chromeOptions.addArguments("--remote-allow-origins=*");
            chromeOptions.addArguments("--illegal-access=*");
            chromeOptions.addArguments("--no-sandbox");
            chromeOptions.addArguments("--ignore-ssl-errors=yes");
            chromeOptions.addArguments("--ignore-certificate-errors");
            chromeOptions.setAcceptInsecureCerts(true);
            chromeOptions.setHeadless(Boolean.valueOf(FrameworkConst.HEADLESS));
            LoggingPreferences logPrefs = new LoggingPreferences();
            logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
            chromeOptions.setCapability("goog:loggingPrefs", logPrefs);
            chromeOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);
            return chromeOptions;
        }
    }, FIREFOX {
        @Override
        public WebDriver createDriver() {
            WebDriverManager.getInstance(DriverManagerType.FIREFOX).setup();
            return new FirefoxDriver(getOptions());

            // Need to catch the pc don't have the browser
        }

        @Override
        public FirefoxOptions getOptions() {
            FirefoxOptions firefoxOptions = new FirefoxOptions();
            firefoxOptions.addArguments(START_MAXIMIZED);
            firefoxOptions.setAcceptInsecureCerts(true);
            firefoxOptions.setHeadless(Boolean.valueOf(FrameworkConst.HEADLESS));
            return firefoxOptions;
        }
    },

    EDGE {
        @Override
        public WebDriver createDriver() {
            WebDriverManager.getInstance(DriverManagerType.EDGE).setup();

            return new EdgeDriver(getOptions());
        }

        @Override
        public EdgeOptions getOptions() {
            EdgeOptions edgeOptions = new EdgeOptions();
            edgeOptions.addArguments(START_MAXIMIZED);
            edgeOptions.setAcceptInsecureCerts(true);
            edgeOptions.setHeadless(Boolean.valueOf(FrameworkConst.HEADLESS));
            return edgeOptions;
        }
    },

    SAFARI {
        @Override
        public WebDriver createDriver() {
            /**
             * The driver for using safari on Window
             *  WebDriverManager wdm = WebDriverManager.getInstance(DriverManagerType.SAFARI).browserInDocker();
             */
            WebDriverManager wdm = WebDriverManager.getInstance(DriverManagerType.SAFARI).browserInDocker();
//
            /**
             * The default driver for safari when using mac
             WebDriverManager.getInstance(DriverManagerType.SAFARI).setup();
             return new SafariDriver(getOptions());
             */
//           WebDriverManager wdm = WebDriverManager.chromedriver().browserInDocker();
            WebDriver driver = wdm.create();
            return driver;
        }

        @Override
        public SafariOptions getOptions() {
            SafariOptions safariOptions = new SafariOptions();
            safariOptions.setAutomaticInspection(false);

            if (TRUE.equals(Boolean.valueOf(FrameworkConst.HEADLESS)))
                throw new HeadlessNotSupportedException(safariOptions.getBrowserName());

            return safariOptions;
        }
    };

    private static final String START_MAXIMIZED = "--start-maximized";

    public abstract WebDriver createDriver();

    public abstract MutableCapabilities getOptions();
}
