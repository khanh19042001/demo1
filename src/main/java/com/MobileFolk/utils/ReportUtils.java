package com.MobileFolk.utils;

import com.MobileFolk.constants.FrameworkConst;
import com.MobileFolk.exceptions.FrameworkException;
import com.MobileFolk.exceptions.InvalidPathForExtentReportFileException;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

public final class ReportUtils {

    private ReportUtils() {
    }

    public static String createExtentReportPath() {
        if (FrameworkConst.override_reports.trim().equalsIgnoreCase(FrameworkConst.NO)) {
            /**
             * Report name ->
             *
             * Windows_10_Tue_Oct_05_02_30_46_IST_2021_AutomationReport.html
             * Mac_OS_X_Tue_Feb_22_17_09_05_IST_2022_AutomationReport.html
             */
            return FrameworkConst.EXTENT_REPORT_FOLDER_PATH + "/" + BrowserInfoUtils.getOSInfo() + "_" + getCurrentDate() + "_"
                    + FrameworkConst.EXTENT_REPORT_FILE_NAME;

        } else {
            return FrameworkConst.EXTENT_REPORT_FOLDER_PATH + "/" + FrameworkConst.EXTENT_REPORT_FILE_NAME;
        }
    }

    public static String createJsonExtentObserverPath(){
        if (FrameworkConst.override_reports.trim().equalsIgnoreCase(FrameworkConst.NO)) {
            /**
             * Report name ->
             *
             * Windows_10_Tue_Oct_05_02_30_46_IST_2021_AutomationReport.html
             * Mac_OS_X_Tue_Feb_22_17_09_05_IST_2022_AutomationReport.html
             */
            return FrameworkConst.EXTENT_REPORT_FOLDER_PATH + "/" + BrowserInfoUtils.getOSInfo() + "_" + getCurrentDate() + "_"
                    + FrameworkConst.EXTENT_REPORT_NAME + ".json";

        } else {
            return FrameworkConst.EXTENT_REPORT_FOLDER_PATH + "/" + FrameworkConst.EXTENT_REPORT_NAME + ".json";
        }
    }

    public static void openReports() {
        if (FrameworkConst.open_reports_after_execution.trim().equalsIgnoreCase(FrameworkConst.YES)) {
            try {
                Desktop.getDesktop().browse(new File(FrameworkConst.getExtentReportFilePath()).toURI());
            } catch (FileNotFoundException fileNotFoundException) {
                throw new InvalidPathForExtentReportFileException("Extent Report file you are trying to reach is not found", fileNotFoundException.fillInStackTrace());
            } catch (IOException ioException) {
                throw new FrameworkException("Extent Report file you are trying to reach got IOException while reading the file", ioException.fillInStackTrace());
            }
        }
    }

    private static String getCurrentDate(){
        Date date = new Date();
        return date.toString().replace(":", "_").replace(" ", "_");
    }
}

