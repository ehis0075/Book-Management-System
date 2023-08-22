package com.bms.bms.util;

import com.google.gson.Gson;
import com.xpresspayments.phedMiddlewareAdminPortal.exception.GeneralException;
import com.xpresspayments.phedMiddlewareAdminPortal.general.enums.ResponseCodeAndMessage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExcelUtil {

    Map<Integer, String> classFields;
    CellStyle amountStyle;
    CellStyle dateStyle;
    Workbook workbook;
    Sheet sheet;
    DataFormat format;

    public ExcelUtil(Class<?> aClass) {
        this.classFields = getClassFields(aClass);
    }

    public ExcelUtil() {

    }


    /**
     * This public method initiate the creation of the Excel Sheet
     */
    public String writeExcel(List<?> objectList, String sheetName, String excelFilePath) {
        createExcelVariables(sheetName);

        int rowCount = 0;

        createHeaderRow(sheet, rowCount);

        for (Object o : objectList) {
            Row row = sheet.createRow(++rowCount);
            JSONObject jsonObject = getObjectAsJson(o);
            writeBook(jsonObject, row, rowCount);
        }

        return createExcelFile(excelFilePath);
    }


    /**
     * Concatenates passed in value plus date & export keyword
     * to generate Excel File name
     */
    public String getFileName(String baseName) {
        baseName = baseName.concat("_Export");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String dateTimeInfo = dateFormat.format(new Date());
        return baseName.concat(String.format("_%s.xlsx", dateTimeInfo));
    }


    /**
     * Generate the excel file to specified filepath
     */
    protected String createExcelFile(String excelFilePath) {
        try (FileOutputStream outputStream = new FileOutputStream(excelFilePath)) {
            workbook.write(outputStream);
            return excelFilePath;
        } catch (Exception e) {
            throw new GeneralException(ResponseCodeAndMessage.AN_ERROR_OCCURRED_96.responseCode,
                    "Error Creating file {} " + e.getMessage());
        }
    }


    /**
     * Generate the necessary excel variables
     */
    protected void createExcelVariables(String table) {
        this.workbook = new XSSFWorkbook();
        this.sheet = workbook.createSheet(table);
        this.format = workbook.createDataFormat();

        this.amountStyle = createAmountStyle(sheet.getWorkbook(), format);
        this.dateStyle = createDateStyle(sheet.getWorkbook());
    }


    /**
     * This Create Each Column in the Excel and populates the Value
     */
    public void writeBook(JSONObject jsonObject, Row row, int rowCount) {

        Cell cell = row.createCell(0);
        cell.setCellValue(rowCount);

        for (Map.Entry<Integer, String> entry : classFields.entrySet()) {
            cell = row.createCell(entry.getKey());
            if (jsonObject.has(entry.getValue())) {

                String entryValue = entry.getValue();
                if (entryValue.equals("amount") || entryValue.equals("transactionFee")
                        || entryValue.equals("settlementImpact") || entryValue.equals("totalTransactions")) {
                    setAmount(getObjectAsString(jsonObject.get(entry.getValue())), cell, amountStyle);
                } else if (entry.getValue().equals("transactionDate")) {
                    setDate(getObjectAsString(jsonObject.get(entry.getValue())), cell, dateStyle);
                } else {
                    cell.setCellValue(getObjectAsString(jsonObject.get(entry.getValue())));
                }

            } else {
                cell.setCellValue("");
            }
        }
    }


    /**
     * Get Object as a String, checkmate null pointer Exception
     */
    private String getObjectAsString(Object o) {
        if (Objects.isNull(o)) {
            return "";
        }
        return o.toString();
    }

    /**
     * Converts the Object to a JSONObject
     */
    public JSONObject getObjectAsJson(Object object) {
        String jsonInString = new Gson().toJson(object);
        return new JSONObject(jsonInString);
    }


    /**
     * Defines the Header Styles and sets up the header
     */
    public void createHeaderRow(Sheet sheet, int rowNumber) {
        CellStyle cellStyle = getHeaderCellStyle(sheet);

        setUpHeaderCells(sheet, cellStyle, rowNumber);
    }

    protected CellStyle getHeaderCellStyle(Sheet sheet) {
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        cellStyle.setFont(font);
//        cellStyle.setWrapText(true);
        return cellStyle;
    }


    /**
     * Sets up the Header of the Excel sheet using the Class fields
     */
    private void setUpHeaderCells(Sheet sheet, CellStyle headerStyle, int rowNumber) {
        Row row = sheet.createRow(rowNumber);

        Cell cellNo = row.createCell(0);
        cellNo.setCellStyle(headerStyle);
        cellNo.setCellValue("No.");

        for (Map.Entry<Integer, String> entry : classFields.entrySet()) {
            Cell cell = row.createCell(entry.getKey());
            cell.setCellStyle(headerStyle);
            cell.setCellValue(formatHeader(entry.getValue()));
        }
    }


    /**
     * format Class field to look readable in Excel header
     * from e.g maskedPan to Masked Pan
     */
    private String formatHeader(String fieldName) {
        String[] split = fieldName.split("(?=\\p{Upper})");

        //check if field is all caps, ignore all caps field
        if (fieldName.length() != split.length) {

            StringBuilder header = new StringBuilder();

            for (int i = 0; i < split.length; i++) {
                if (i == 0) {
                    header.append(capitalize(split[i]));
                } else {
                    header.append(" ").append(split[i]);
                }
            }
            return header.toString();
        } else {
            return fieldName;
        }
    }


    /**
     * Capitalize the first Letter of the passed in String
     */
    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        return for3Line(str.substring(0, 1).toUpperCase() + str.substring(1));
    }


    /**
     * Properly format Company Name
     */
    private static String for3Line(String name) {
        if (name.equals("_3line")) {
            return "3LINE";
        }
        return name;
    }


    /**
     * Get all fields of a class and returns an
     * Hashmap containing the index and the field name
     */
    private Map<Integer, String> getClassFields(Class<?> tClass) {
        Map<Integer, String> fieldsMap = new HashMap<>();
        Field[] allFields = tClass.getDeclaredFields();
        int count = 1;

        for (Field field : allFields) {
            fieldsMap.put(count, field.getName());
            count++;
        }

        return fieldsMap;
    }


    /**
     * Create a style for Date Column/Cell
     */
    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle dateCellStyle = workbook.createCellStyle();
        CreationHelper creationHelper = workbook.getCreationHelper();
        dateCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
        return dateCellStyle;
    }


    /**
     * Create a style for Amount Column/Cell
     */
    CellStyle createAmountStyle(Workbook workbook, DataFormat format) {
        CellStyle amountCellStyle = workbook.createCellStyle();
        amountCellStyle.setAlignment(HorizontalAlignment.RIGHT);
        amountCellStyle.setDataFormat(format.getFormat("#,#0.0"));
        return amountCellStyle;
    }


    /**
     * Format Cell with Date style
     */
    private void setDate(String date, Cell cell, CellStyle style) {
        if (null != date) {
            cell.setCellValue(date);
            cell.setCellStyle(style);
            return;
        }
        cell.setCellValue("");
    }


    /**
     * Format Cell with Amount style
     */
    private void setAmount(String amount, Cell cell, CellStyle style) {
        if (Objects.nonNull(amount) && !amount.isEmpty()) {
            cell.setCellValue(amount);
            cell.setCellStyle(style);
            return;
        }
        cell.setCellValue("");
    }
}