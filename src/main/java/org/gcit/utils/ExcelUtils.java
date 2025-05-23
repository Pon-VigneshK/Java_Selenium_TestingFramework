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
 * Utility class for performing operations related to Microsoft Excel files,
 * primarily focused on extracting test data from {@code .xlsx} sheets.
 * <p>
 * This class provides static methods to read data from specified sheets within an Excel workbook.
 * It assumes a common structure where the first row of a sheet contains column headers,
 * and subsequent rows contain the actual test data.
 *
 * @see org.gcit.constants.FrameworkConstants#getExcelFilePath()
 */
public final class ExcelUtils {
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ExcelUtils() {
    }

    /**
     * Extracts test data from the specified Excel sheet and returns it as a list of maps.
     * <p>
     * This method reads an Excel file located at the path defined by {@link FrameworkConstants#getExcelFilePath()}.
     * It expects the first row (row 0) of the specified sheet to contain the column headers.
     * Each subsequent row is treated as a data record.
     * <p>
     * Each map in the returned list represents one row of data, where the keys are the
     * column headers (from the first row) and the values are the corresponding cell values
     * from that data row, converted to strings using {@link #convertCellValueToString(Cell)}.
     *
     * @param sheetname The name of the Excel sheet from which to extract the test details.
     *                  Must not be null or empty.
     * @return A {@code List<Map<String, String>>} containing the test data. Each map represents a row,
     *         with column headers as keys and cell values as string values. Returns an empty list if
     *         the sheet has no data rows (beyond the header row).
     * @throws ExcelFileNotFoundException If the Excel file specified by {@link FrameworkConstants#getExcelFilePath()} is not found.
     * @throws BaseException If an I/O error occurs while reading the Excel file or sheet,
     *                       or if the sheet structure is not as expected (e.g., missing header row).
     * @see #convertCellValueToString(Cell)
     * @see FrameworkConstants#getExcelFilePath()
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
     * Converts the value of a given Excel {@link Cell} into its string representation.
     * <p>
     * This method handles various cell types:
     * <ul>
     *   <li>{@link CellType#STRING}: Returns the string value directly.</li>
     *   <li>{@link CellType#NUMERIC}: Converts numeric values. If the number is an integer (e.g., 123.0),
     *       it's converted to its integer string representation (e.g., "123"). Otherwise, it's converted
     *       to its standard double string representation.</li>
     *   <li>{@link CellType#BOOLEAN}: Converts to "true" or "false".</li>
     *   <li>{@link CellType#FORMULA}: Returns the formula string itself, not the evaluated result.</li>
     *   <li>{@code null} cell: Returns an empty string.</li>
     *   <li>Other cell types (e.g., {@link CellType#BLANK}, {@link CellType#ERROR}): Returns an empty string.</li>
     * </ul>
     *
     * @param cell The Excel cell whose value needs to be converted to a string. Can be {@code null}.
     * @return The string representation of the cell's value. Returns an empty string if the cell is {@code null}
     *         or if its type is not explicitly handled (like BLANK or ERROR).
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
                // Check if the numeric value is an integer (e.g., 10.0, 123.0)
                if (numericValue == Math.floor(numericValue) && !Double.isInfinite(numericValue)) {
                    return String.valueOf((long) numericValue); // Use long to handle larger integers before converting to string
                } else {
                    return String.valueOf(numericValue); // For actual floating-point numbers
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                // Note: This returns the formula string itself, e.g., "SUM(A1:B1)".
                // To get the evaluated result of a formula, a FormulaEvaluator would be needed.
                return cell.getCellFormula();
            default:
                // Handles BLANK, ERROR, and any other types by returning an empty string.
                return "";
        }
    }
}


