package com.MobileFolk.helpers;

import com.MobileFolk.exceptions.InvalidPathForExcelException;
import com.MobileFolk.utils.Log;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


/*
 * Read all data from excel files.
 */
public class ExcelHelpers {

    private FileInputStream fis;
    private FileOutputStream fileOut;
    private Workbook wb;
    private Sheet sh;
    private Cell cell;
    private Row row;
    private String excelFilePath;
    private Map<String, Integer> columnMapper = new HashMap<>();

    public static Map<String, List<String>> readXLSData(String filePath, String... name) {
        String sheetName = name.length > 0 ? name[0] : Strings.EMPTY;
        Map<String, List<String>> res = new HashMap<>();
        try {
            FileInputStream fis = new FileInputStream(filePath);
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet loginDataSheet;
            if (sheetName.equals(Strings.EMPTY))
                loginDataSheet = wb.getSheetAt(0);
            else {
                loginDataSheet = wb.getSheet(sheetName);
            }
            int rowTotal = loginDataSheet.getLastRowNum();
            for (int row = 0; row <= rowTotal; row++) {
                XSSFRow rowRow = loginDataSheet.getRow(row);
                int cellTotal = rowRow.getLastCellNum();
                List<String> rowDataList = new ArrayList<>();
                for (int col = 0; col < cellTotal; col++) {
                    XSSFCell cell = rowRow.getCell(col);
                    String value = cell.getStringCellValue();
                    rowDataList.add(value);
                }
                System.out.println();
                res.put(String.valueOf(row), rowDataList);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    public static Map<String, List<String>> readXLSData(String filePath) {
        Map<String, List<String>> res = new HashMap<>();
        try {
            FileInputStream fis = new FileInputStream(filePath);
            XSSFWorkbook wb = new XSSFWorkbook(fis);

            XSSFSheet loginDataSheet = wb.getSheetAt(0);
            int rowTotal = loginDataSheet.getLastRowNum();
            for (int row = 0; row <= rowTotal; row++) {
                XSSFRow rowRow = loginDataSheet.getRow(row);
                int cellTotal = rowRow.getLastCellNum();
                List<String> rowDataList = new ArrayList<>();
                for (int col = 0; col < cellTotal; col++) {
                    XSSFCell cell = rowRow.getCell(col);
                    String value = cell.getStringCellValue();
                    rowDataList.add(value);
                }
                System.out.println();
                res.put(String.valueOf(row), rowDataList);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return res;
    }


    //    Set Excel file
    public void setExcelFile(String excelPath, String sheetName) {
        try {
            File f = new File(excelPath);
            fileValidation(f, sheetName);

            fis = new FileInputStream(excelPath);
            wb = WorkbookFactory.create(fis);
            sh = wb.getSheet(sheetName);
            if (sh == null) {
                try {
                    Log.info("setExcelFile: Sheet name not found.");
                    throw new InvalidPathForExcelException("Sheet name not found.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            excelFilePath = excelPath;

            //adding all the column header names to the map 'columns'
            sh.getRow(0).forEach(cell -> {
                columnMapper.put(cell.getStringCellValue(), cell.getColumnIndex());
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Object[][] getDataArray(String excelPath, String sheetName, int startCol, int totalCols) {

        Object[][] data = null;
        try {
            File f = new File(excelPath);
            fileValidation(f, sheetName);


            fis = new FileInputStream(excelPath);
            wb = new XSSFWorkbook(fis);
            sh = wb.getSheet(sheetName);

            if (sh == null) {
                try {
                    Log.info("Sheet name not found.");
                    throw new InvalidPathForExcelException("Sheet name not found.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            int noOfRows = sh.getPhysicalNumberOfRows();
            //int noOfCols = row.getLastCellNum();
            int noOfCols = totalCols + 1;

            System.out.println("Số Dòng: " + (noOfRows - 1));
            System.out.println("Số Cột: " + (noOfCols - startCol));

            data = new String[noOfRows - 1][noOfCols - startCol];
            for (int i = 1; i < noOfRows; i++) {
                for (int j = 0; j < noOfCols - startCol; j++) {
                    data[i - 1][j] = getCellData(i, j + startCol);
                    System.out.println(data[i - 1][j]);
                }
            }
        } catch (Exception e) {
            System.out.println("The exception is: " + e.getMessage());
        }
        return data;
    }


    private void fileValidation(File f, String sheetName) {
        if (!f.exists()) {
            try {
                Log.info("File Excel path not found.");
                throw new InvalidPathForExcelException("File Excel path not found.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (Objects.isNull(sheetName) || sheetName.isEmpty() || sheetName.equals("")) {
            try {
                Log.info("The Sheet Name is empty or null.");
                throw new InvalidPathForExcelException("The Sheet Name is empty or null.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getTestCaseName(String sTestCase) throws Exception {
        String value = sTestCase;
        try {
            int posi = value.indexOf("@");
            value = value.substring(0, posi);
            posi = value.lastIndexOf(".");

            value = value.substring(posi + 1);
            return value;

        } catch (Exception e) {
            throw (e);
        }
    }

    public int getRowContains(String sTestCaseName, int colNum) throws Exception {
        int i;
        try {
            int rowCount = getRowUsed();
            for (i = 0; i < rowCount; i++) {
                if (getCellData(i, colNum).equalsIgnoreCase(sTestCaseName)) {
                    break;
                }
            }
            return i;

        } catch (Exception e) {
            throw (e);
        }

    }

    public int getRowUsed() throws Exception {
        try {
            int RowCount = sh.getLastRowNum();
            return RowCount;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw (e);
        }
    }

    // Get cell data
    public String getCellData(int rowNum, int colNum) {
        try {
            cell = sh.getRow(rowNum).getCell(colNum);
            ((XSSFCell) cell).setCellType(CellType.STRING);
            String CellData = null;
            switch (cell.getCellType()) {
                case STRING:
                    CellData = cell.getStringCellValue();
                    break;
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss");
                        Date date = cell.getDateCellValue();
                        String[] tmp = dateFormat.format(date).split("_");
                        if (date.after(new Date(0)) && tmp[1].equals("00:00:00")) {
                            CellData = tmp[0];
                        } else
                            CellData = tmp[1];

                        //CellData = String.valueOf(cell.getDateCellValue());
                    } else {
                        double tmp = cell.getNumericCellValue();
                        if (tmp >= Long.MIN_VALUE && tmp <= Long.MAX_VALUE) {
                            double tmp2 = (long) tmp - tmp;
                            if (tmp2 != 0)
                                CellData = String.valueOf(tmp);
                            else
                                CellData = String.valueOf((long) tmp);
                        }
                        //CellData = String.valueOf((long) cell.getNumericCellValue());
                    }
                    break;
                case BOOLEAN:
                    CellData = Boolean.toString(cell.getBooleanCellValue());
                    break;
                case BLANK:
                    CellData = "";
                    break;
            }
            return CellData;
        } catch (Exception e) {
            return "";
        }
    }

    public String getCellData(int rowNum, String columnName) {
        return getCellData(rowNum, columnMapper.get(columnName));
    }

    public int getRows() {
        return sh.getPhysicalNumberOfRows();
    }

    public int getRowCount() {
        int rowCount = sh.getLastRowNum() + 1;
        return rowCount;
    }

    public int getColumnCount() {
        row = sh.getRow(0);
        int colCount = row.getLastCellNum();
        return colCount;
    }

    // Write data to excel sheet
    public void setCellData(String text, int rowNumber, int colNumber) {
        try {
            row = sh.getRow(rowNumber);
            if (row == null) {
                row = sh.createRow(rowNumber);
            }
            cell = row.getCell(colNumber);

            if (cell == null) {
                cell = row.createCell(colNumber);
            }
            cell.setCellValue(text);

            XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
            if (text == "pass" || text == "passed" || text == "Pass" || text == "Passed") {
                style.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
            } else {
                style.setFillForegroundColor(IndexedColors.RED.getIndex());
            }
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);

            cell.setCellStyle(style);

            fileOut = new FileOutputStream(excelFilePath);
            wb.write(fileOut);
            fileOut.flush();
            fileOut.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void setCellData(String text, int rowNum, String columnName) {
        try {
            row = sh.getRow(rowNum);
            if (row == null) {
                row = sh.createRow(rowNum);
            }
            cell = row.getCell(columnMapper.get(columnName));

            if (cell == null) {
                cell = row.createCell(columnMapper.get(columnName));
            }
            cell.setCellValue(text);

            XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
            if (text == "pass" || text == "passed" || text == "Pass" || text == "Passed") {
                style.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
            } else {
                style.setFillForegroundColor(IndexedColors.RED.getIndex());
            }
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);

            cell.setCellStyle(style);

            fileOut = new FileOutputStream(excelFilePath);
            wb.write(fileOut);
            fileOut.flush();
            fileOut.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public Hashtable<String, Object> verifyReportData(String excelPath, String reportTitle, String expReportPath, boolean isEmptyFile) {
        boolean isResult = false;
        Hashtable<String, Object> result = new Hashtable<>();
        Hashtable<Integer, List<String>> rowDataList = new Hashtable<>();
        List<String> failureMsg = new ArrayList<>();

        try {
            File f = new File(excelPath);
            if (!f.exists()) {
                try {
                    Log.info("File Excel path not found.");
                    throw new InvalidPathForExcelException("File Excel path not found.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            fis = new FileInputStream(excelPath);
            wb = new XSSFWorkbook(fis);
            sh = wb.getSheetAt(0);

            int endRow = getRowCount();
            // Add to handle file empty
            if (endRow==0) {
                isResult = isEmptyFile? true : false;
                failureMsg.add("File is empty");
            } else {
                int columns = getColumnCount();

                boolean isHeader = true;
                for (int rowNums = 0; rowNums < endRow; rowNums++) {
                    List<String> rowData = new ArrayList<>();
                    for (int colNum = 0; colNum < columns; colNum++) {
                        String value = getCellData(rowNums, colNum).trim().toLowerCase();
                        if (isHeader & value.contains(reportTitle.toLowerCase())) {
                            isResult = true;
                        }
                        rowData.add(getCellData(rowNums, colNum).trim());
                    }
                    isHeader = rowNums < 9 ? true : false;
                    Log.info(String.format("Row %s  - Data: %s", rowNums, rowData));
                    rowDataList.put(rowNums, rowData);
                    if (failureMsg.isEmpty()) failureMsg.add(getCellData(0, 0));
                }
            }

            // TODO: 10/21/2022 Verify Data and update result to - Next Phase

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add result
        result.put("status", isResult);
        result.put("message", failureMsg); // Save err msg Structure: Row 1: Wrong value num
        result.put("fileData", rowDataList);
        return result;
    }
}
