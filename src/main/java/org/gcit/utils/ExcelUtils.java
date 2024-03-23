package org.gcit.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.gcit.constants.FrameworkConstants;
import org.gcit.exceptions.BaseException;
import org.gcit.exceptions.ExcelFileNotFoundException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ExcelUtils {
    private ExcelUtils() {
    }

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
        }
        catch (java.io.FileNotFoundException e) {
            throw new ExcelFileNotFoundException("Excel file not found");
        }catch (IOException e) {
            throw new BaseException("Some error occurred while reading excel file");
        }
        return list;
    }

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



