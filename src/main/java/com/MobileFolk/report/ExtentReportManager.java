package com.MobileFolk.report;

import com.MobileFolk.constants.AuthorType;
import com.MobileFolk.constants.CategoryType;
import com.MobileFolk.constants.FrameworkConst;
import com.MobileFolk.driver.DriverManager;
import com.MobileFolk.utils.IconUtils;
import com.MobileFolk.utils.ReportUtils;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.JsonFormatter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

import static com.MobileFolk.constants.FrameworkConst.TFS_LINK;

public final class ExtentReportManager {

    private static ExtentReports extentReports;

    public static void initReports() {
        if (Objects.isNull(extentReports)) {
            extentReports = new ExtentReports();

            ExtentSparkReporter spark = new ExtentSparkReporter(ReportUtils.createExtentReportPath());
            JsonFormatter json = new JsonFormatter(ReportUtils.createJsonExtentObserverPath());
            extentReports.attachReporter(spark);
            spark.config().setOfflineMode(true);
            spark.config().setTheme(Theme.STANDARD);
            spark.config().setDocumentTitle(FrameworkConst.REPORT_TITLE + " Test version " + FrameworkConst.TESTING_VERSION);
            spark.config().setReportName(FrameworkConst.REPORT_TITLE + " Test version " + FrameworkConst.TESTING_VERSION);
            spark.config().setEncoding("UTF-8");
            spark.viewConfigurer().viewOrder().as(new ViewName[]{
                    ViewName.DASHBOARD,
                    ViewName.TEST,
                    ViewName.CATEGORY,
                    ViewName.DEVICE,
                    ViewName.EXCEPTION,
                    ViewName.AUTHOR,
                    ViewName.LOG
            }).apply();
            extentReports.setSystemInfo("Framework Name", FrameworkConst.REPORT_TITLE);
            extentReports.setSystemInfo("Author", FrameworkConst.AUTHOR);
            extentReports.setSystemInfo("Testing Version", FrameworkConst.TESTING_VERSION);
        }
    }

    public static void flushReports() {
        if (Objects.nonNull(extentReports)) {
            extentReports.flush();
            updateContent();
        }
        ExtentTestManager.unload();
        ReportUtils.openReports();
    }

    public static void createTest(String testCaseName) {
        ExtentTestManager.setExtentTest(extentReports.createTest(testCaseName, null));
    }

    public static void createTest(String testCaseName, String description, String browser) {
        String testName = IconUtils.getBrowserIcon(browser) + " : " + testCaseName + String.format("<br/> <p style='font-size: 0.75em'>%s</p>", description);
        ExtentTestManager.setExtentTest(extentReports.createTest(testName, null));
    }

    public static void unloadTest() {
        if (Objects.nonNull(ExtentTestManager.getExtentTest()))
            ExtentTestManager.unload();
    }

    public static void removeTest(String testCaseName) {
        unloadTest();
        extentReports.removeTest(testCaseName);
    }

    /**
     * Adds the screenshot.
     *
     * @param message the message
     */
    public static void addScreenShot(String message) {
        addScreenShot(Status.INFO, message);
    }

    /**
     * Adds the screenshot.
     *
     * @param status  the status
     * @param message the message
     */
    public static void addScreenShot(Status status, String message) {
        if (ExtentTestManager.getExtentTest() != null) {
            try {
                if(DriverManager.getDriver()!=null) {
                    String base64Image = "data:image/png;base64," + ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BASE64);
                    ExtentTestManager.getExtentTest().log(status, message, MediaEntityBuilder.createScreenCaptureFromBase64String(base64Image).build());
                } else
                    ExtentTestManager.getExtentTest().log(status, message);
            } catch (Exception exception) {
                ExtentTestManager.getExtentTest().log(status, message);
                ExtentTestManager.getExtentTest().log(status, exception);
            }
        }
    }

    synchronized public static void addAuthors(AuthorType[] authors) {
        if (authors == null) {
            ExtentTestManager.getExtentTest().assignAuthor("AUTHOR");
        } else {
            for (AuthorType author : authors) {
                ExtentTestManager.getExtentTest().assignAuthor(author.toString());
            }
        }

    }

    // public static void addCategories(String[] categories) {
    synchronized public static void addTestingType(CategoryType[] categories) {
        if (categories == null) {
            ExtentTestManager.getExtentTest().assignCategory("REGRESSION");
        } else {
            Arrays.stream(categories).forEach(c -> addCategory(c.toString()));
            // for (String category : categories) {
//            for (CategoryType category : categories) {
//                addCategory(category.toString());
//            }
        }
    }

    public static void addTFSLink(String tfsLink) {
        if (Objects.nonNull(tfsLink) && !tfsLink.isEmpty()) {
            String[] tmp = tfsLink.split(",");
            for (String link : tmp)
                ExtentTestManager.getExtentTest().info(MarkupHelper.createLabel("This TC has been FAILED, see details at <a href=\"" + TFS_LINK + link + "\">TFS Link</a>", ExtentColor.AMBER));
        }
    }

    public static void addNode(String message, String nodeTitle) {
        if (ExtentTestManager.getExtentTest() != null) {
            ExtentTest extentTest = ExtentTestManager.getExtentTest().createNode(nodeTitle);
            extentTest.log(Status.INFO, message);
        }
    }

    synchronized public static void addCategory(String cateName) {
        ExtentTestManager.getExtentTest().assignCategory(cateName);
    }


    synchronized public static void addDevices(String device) {
        ExtentTestManager.getExtentTest().assignDevice(device.trim().toUpperCase());
    }

    public static void logMessage(String message) {
        if (ExtentTestManager.getExtentTest() != null) ExtentTestManager.getExtentTest().log(Status.INFO, message);
    }

    public static void logMessage(Status status, String message) {
        if (ExtentTestManager.getExtentTest() != null) ExtentTestManager.getExtentTest().log(status, message);
    }

    public static void logMessage(Status status, Object message) {
        if (ExtentTestManager.getExtentTest() != null)
            ExtentTestManager.getExtentTest().log(status, (Throwable) message);
    }

    public static void pass(String message) {
        if (ExtentTestManager.getExtentTest() != null) ExtentTestManager.getExtentTest().pass(message);
    }

    public static void pass(Markup message) {
        if (ExtentTestManager.getExtentTest() != null) ExtentTestManager.getExtentTest().pass(message);
    }

    public static void fail(String message) {
        if (ExtentTestManager.getExtentTest() != null) ExtentTestManager.getExtentTest().fail(message);
    }

    public static void fail(Object message) {
        if (ExtentTestManager.getExtentTest() != null) ExtentTestManager.getExtentTest().fail((String) message);
    }

    public static void fail(Markup message) {
        if (ExtentTestManager.getExtentTest() != null) ExtentTestManager.getExtentTest().fail(message);
    }

    public static void skip(String message) {
        if (ExtentTestManager.getExtentTest() != null) ExtentTestManager.getExtentTest().skip(message);
    }

    public static void skip(Markup message) {
        if (ExtentTestManager.getExtentTest() != null) ExtentTestManager.getExtentTest().skip(message);
    }

    public static void info(Markup message) {
        if (ExtentTestManager.getExtentTest() != null) ExtentTestManager.getExtentTest().info(message);
    }

    public static void info(String message) {
        if (ExtentTestManager.getExtentTest() != null) ExtentTestManager.getExtentTest().info(message);
    }

    public static void warning(String message) {
        if (ExtentTestManager.getExtentTest() != null) ExtentTestManager.getExtentTest().log(Status.WARNING, message);
    }

    private static void updateContent() {
        String defaultLogo = "spark/logo.png";
        String newLogo = "./css/Logo_MISA.jpg";

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(FrameworkConst.EXTENT_REPORT_FILE_PATH), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String val;
            while ((val = br.readLine()) != null) {
                if (val.isEmpty())
                    continue;
                if (val.contains(defaultLogo))
                    val = val.replace(defaultLogo, newLogo);
                if (val.contains("class=\"nav-logo\""))
                    val = val.replace("class=\"nav-logo\"", " class=\"nav-logo\" style=\"padding:0 0\"");
                if (val.contains("</html>"))
                    val = val.replace("</html>", "<style>.test-wrapper .test-content .test-content-detail .detail-head .info{height: unset;}</style></html>");
                stringBuilder.append(val).append("\n");
            }
            br.close();

            File f = new File(FrameworkConst.EXTENT_REPORT_FILE_PATH);
            FileUtils.writeStringToFile(f, stringBuilder.toString(), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
