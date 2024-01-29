package com.MobileFolk.Common;



import com.MobileFolk.driver.DriverManager;
import com.MobileFolk.utils.Log;

import org.testng.annotations.*;


@Listeners({TestListener.class})
public class TestBase {

    @Parameters({"browser"})
    @BeforeSuite
    public void beforeSuite(@Optional("chrome") String browser) {
        Log.info("TestBase: beforeSuite");

    }

    @Parameters({"browser"})
    @BeforeClass(alwaysRun = true)
    public void createDriver(@Optional("chrome") String browser) {

    }

    @AfterClass(alwaysRun = true)
    public void closeDriver() {
        Log.info("TestBase: Close Driver ");
        DriverManager.quit();

    }


}
