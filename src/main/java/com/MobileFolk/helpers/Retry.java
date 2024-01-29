package com.MobileFolk.helpers;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;


public class Retry implements IRetryAnalyzer {
    private int count = 0;
    private static int maxTry = 3;
    private static int status = ITestResult.CREATED;

    @Override
    public boolean retry(ITestResult iTestResult) {
        if (count > 0) {
            iTestResult.setTestName(iTestResult.getName() + String.format(" - Retry #000%s", count));
            status = ITestResult.FAILURE;
        }
        if (!iTestResult.isSuccess()) {
            if (count < maxTry) {
                count++;
                iTestResult.setStatus(ITestResult.FAILURE);
                return true;
            } else {
                iTestResult.setStatus(ITestResult.FAILURE);
                status = ITestResult.SUCCESS;
            }
        } else {
            iTestResult.setStatus(ITestResult.SUCCESS);
            status = ITestResult.SUCCESS;
        }
        return false;
    }

    public static int getRetryStatus() {
        return status;
    }
}
