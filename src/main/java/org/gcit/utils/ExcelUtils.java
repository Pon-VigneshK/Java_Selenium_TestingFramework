package org.gcit.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.gcit.constants.FrameworkConstants;
import org.gcit.enums.LogType;
import org.gcit.exceptions.BaseException;
import org.gcit.exceptions.ExcelFileNotFoundException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.gcit.logger.LogService.log;

/**
 * Utility class for operations related to Excel files.
 * Provides methods to extract data from Excel sheets for testing purposes.
 */
public final class ExcelUtils {
    /**
     * Private constructor to prevent instantiation of the ExcelUtils utility class.
     * This class is designed to provide static methods for operations related to Excel files.
     */
    private ExcelUtils() {
    }

    /**
     * Extracts test details from the specified Excel sheet and returns them as a list of maps.
     * Each map represents a row in the sheet, with keys as column headers and values as cell values.
     *
     * @param sheetname The name of the Excel sheet from which to extract the test details.
     * @return A list of maps containing the test details from the specified sheet.
     * @throws ExcelFileNotFoundException If the Excel file is not found.
     * @throws BaseException If an error occurs while reading the Excel sheet.
     */
    public static List<Map<String, String>> getTestDetails(String sheetname) {
        List<Map<String, String>> list = new ArrayList<>();
        try (FileInputStream fs = new FileInputStream(FrameworkConstants.getExcelFilePath())) {
            XSSFWorkbook workbook = new XSSFWorkbook(fs);
            String sheetName = sheetname;
//            String sheetName = "RUNMANAGER";
            XSSFSheet sheet = workbook.getSheet(sheetName);
            int lastrownum = sheet.getLastRowNum();
            int lastcolnum = sheet.getRow(0).getLastCellNum();
            Map<String, String> map;
            for (int i = 1; i <= lastrownum; i++) {
                map = new HashMap<>();
                for (int j = 0; j < lastcolnum; j++) {
                    String key = sheet.getRow(0).getCell(j).getStringCellValue();
                    String value = convertCellValueToString(sheet.getRow(i).getCell(j));
                    map.put(key, value);
                }
                list.add(map);
            }
        } catch (java.io.FileNotFoundException e) {
            log(LogType.ERROR, "Excel file not found: " + e.getMessage());
            throw new ExcelFileNotFoundException("Excel file not found");
        } catch (IOException e) {
            log(LogType.ERROR, "Error retrieving test data from Excel sheet: " + sheetname + ". " + e.getMessage());
            throw new BaseException("Error retrieving test data from Excel sheet", e);
        }
        return list;
    }

    /**
     * Converts the value of a given Excel cell into a string.
     *
     * @param cell the cell whose value needs to be converted to a string
     * @return the string representation of the cell's value
     */
    private static String convertCellValueToString(Cell cell) {
        if (cell == null) {
            return "";
        }
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                double numericValue = cell.getNumericCellValue();
                if (numericValue % 1 == 0) {
                    return String.valueOf((int) numericValue);
                } else {
                    return String.valueOf(numericValue);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}


