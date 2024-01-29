package com.MobileFolk.JiraConfig;

import com.MobileFolk.driver.DriverManager;
import com.MobileFolk.helpers.CaptureHelpers;
import com.MobileFolk.report.ExtentReportManager;
import com.MobileFolk.report.ExtentTestManager;
import com.MobileFolk.utils.Log;
import com.aventstack.extentreports.Status;
import junit.framework.TestListener;
import org.codehaus.plexus.util.ExceptionUtils;

import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.Objects;

import static com.MobileFolk.constants.FrameworkConst.YES;
import static com.MobileFolk.constants.FrameworkConst.screenshot_failed_steps;

public class TestJiraListener implements ITestListener{

    @Override
    public void onTestFailure(ITestResult result) {
        JiraPolicy jiraPolicy = result.getMethod().getConstructorOrMethod().getMethod().getAnnotation(JiraPolicy.class);
        boolean isTicketReady = jiraPolicy.logTicketReady();
        if (isTicketReady) {
            //raise jira ticket:
            System.out.println("is ticket ready for JIRA: " + isTicketReady);
            JiraServerProvider jiraSP = new JiraServerProvider("https://min1904.atlassian.net/", "minsoah1904@gmail.com", "ATATT3xFfGF0LfsDHdV9eJD36az-oq3vG3gRO-6aB84r1NG7rGcirIJDz8BPVbw-bEgLus6FnBOjnzpBdaZd1BsMyvik4jUgfl7o9X53QT7o1fzem_H96E_qNA_w50WZXkHs4yuiJTpxWOA8q9NjyC1cxwM8KmbgScAwH72y8YiecKXS2usTQow=08BE6222", "EL");
            String issueSummary = result.getMethod().getConstructorOrMethod().getMethod().getName() + " got failed due to some assertion or exception";

            String errorMessage = result.getThrowable().getMessage();


            // Construct the issue description with the error message, stack trace, and screenshot
            String issueDescription = errorMessage + "\n\n";
            issueDescription = issueDescription.concat(ExceptionUtils.getFullStackTrace(result.getThrowable()));

            jiraSP.createJiraTicket("Bug", issueSummary, issueDescription, "Khanh");

        }
    }


}
