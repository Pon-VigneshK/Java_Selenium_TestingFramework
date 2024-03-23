////package org.gcit.listeners;
////
////import java.io.File;
////import java.io.FileInputStream;
////import java.io.FileNotFoundException;
////import java.io.IOException;
////import java.sql.ResultSet;
////import java.sql.ResultSetMetaData;
////import java.sql.SQLException;
////import java.sql.Statement;
////import java.util.*;
////
////import com.fasterxml.jackson.core.JsonGenerationException;
////import com.fasterxml.jackson.databind.JsonMappingException;
////import com.fasterxml.jackson.databind.ObjectMapper;
////import org.apache.poi.ss.usermodel.*;
////import org.apache.poi.xssf.usermodel.XSSFSheet;
////import org.apache.poi.xssf.usermodel.XSSFWorkbook;
////import org.gcit.constants.FrameworkConstants;
////
////public class ExcelDataReader {
////
////    private static LinkedHashMap<String, LinkedHashMap<String, ArrayList<HashMap<String, Object>>>> testRunnerHashMap =
////            new LinkedHashMap<>();
////    private static String envName;
////    private static LinkedHashMap<String, ArrayList<HashMap<String, Object>>> finalTestList = new LinkedHashMap<>();
////
////    public static void main(String[] args) {
////        // Run the code from the main method
////        generateRunnerListJsonDataFromExcel("DATA");
////    }
////
////    public static void generateRunnerListJsonDataFromExcel(String sheetName) {
////        try (FileInputStream fs = new FileInputStream(FrameworkConstants.getExcelFilePath())) {
////            XSSFWorkbook workbook = new XSSFWorkbook(fs);
////            XSSFSheet sheet = workbook.getSheet(sheetName);
////            int lastrownum = sheet.getLastRowNum();
////            int lastcolnum = sheet.getRow(0).getLastCellNum();
////            HashMap<String, Object> map;
////            ArrayList<HashMap<String, Object>> testDataList = new ArrayList<>();
////            String testCaseListName = "testCaseLists";
////            for (int i = 1; i <= lastrownum; i++) {
////                map = new HashMap<>();
////                for (int j = 0; j < lastcolnum; j++) {
////                    String key = sheet.getRow(0).getCell(j).getStringCellValue();
////                    String value = convertCellValueToString(sheet.getRow(i).getCell(j));
////                    map.put(key, value);
////                }
////                testDataList.add(map);
////                finalTestList.put(testCaseListName, testDataList); // Use a unique key for each test data entry
////            }
////            testRunnerHashMap.put(FrameworkConstants.getRunmanager(), finalTestList);
////            ObjectMapper mapper = new ObjectMapper();
////            mapper.writerWithDefaultPrettyPrinter()
////                    .writeValue(new File(FrameworkConstants.getTestCaseJsonPath()), testRunnerHashMap);
////        } catch (FileNotFoundException e) {
////            throw new RuntimeException(e);
////        } catch (IOException e) {
////            throw new RuntimeException(e);
////        }
////    }
////
////        private static String convertCellValueToString(Cell cell) {
////            if (cell == null) {
////                return "";
////            }
////            CellType cellType = cell.getCellType();
////            switch (cellType) {
////                case STRING:
////                    return cell.getStringCellValue();
////                case NUMERIC:
////                    double numericValue = cell.getNumericCellValue();
////                    if (numericValue % 1 == 0) {
////                        return String.valueOf((int) numericValue);
////                    } else {
////                        return String.valueOf(numericValue);
////                    }
////                case BOOLEAN:
////                    return String.valueOf(cell.getBooleanCellValue());
////                case FORMULA:
////                    return cell.getCellFormula();
////                default:
////                    return "";
////            }
////    }
////
////    public static void generateTestDataJson() {
////        try {
////            HashMap<String, Object> queryDetails = getQueryDetails("select");
////            for (Map.Entry<String, Object> mapdata : queryDetails.entrySet()) {
////
////                ResultSet resultSet = st.executeQuery((String) mapdata.getValue());
////                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
////                int columns = resultSetMetaData.getColumnCount();
////                HashMap<String, Object> rowDatas;
////                ArrayList<HashMap<String, Object>> testDataList = new ArrayList();
////
////                while (resultSet.next()) {
////                    rowDatas = new HashMap(columns);
////                    for (int i = 1; i <= columns; ++i) {
////                        rowDatas.put(resultSetMetaData.getColumnName(i), resultSet.getObject(i));
////                        envName = resultSet.getNString("environment");
////                    }
////                    testDataList.add(rowDatas);
////                }
////                System.out.println("env name is:"+ envName);
////                finalMap.put(mapdata.getKey(), testDataList);
////            }
////            testDataHashMap.put(envName,finalMap);
////            FrameworkConstants.setEnvironment(envName);
////
////            ObjectMapper mapper = new ObjectMapper();
////            FrameWorkConstants.setTestDataJsonFilePath(envName);
////            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FrameWorkConstants.getTestDataJsonFilePath()), testDataHashMap);
////
////        } catch (SQLException e) {
////            throw new SQLConnectionException("Unable to execute the quires, please check the query configurations");
////        } catch (JsonMappingException e) {
////            throw new JsonExceptions("Json mapping exception");
////        } catch (JsonGenerationException e) {
////            throw new JsonExceptions("Json generation exception");
////        } catch (FileNotFoundException e) {
////            throw new JsonExceptions("Unable to write the test data json file, test data json file path/directory missing!");
////        }
////        catch (IOException e){
////            e.printStackTrace();
////        }
////        finally {
////            if (Objects.nonNull(getMyConn())){
////                try {
////                    getMyConn().close();
////                } catch (SQLException e) {
////                    e.printStackTrace();
////                }
////            }
////        }
////    }
////}
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.ResultSetMetaData;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Objects;
//
//import com.fasterxml.jackson.core.JsonGenerationException;
//import com.fasterxml.jackson.databind.JsonMappingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//public class YourClassName {
//    private static Connection getSQLiteConnection() throws SQLException {
//        // Use the appropriate JDBC URL for SQLite, and make sure you have the SQLite JDBC driver in your classpath
//        String url = "jdbc:sqlite:/path/to/your/sqlite/database.db";
//        return DriverManager.getConnection(url);
//    }
//
//    public static void generateTestDataJsonFromDB() {
//        Connection connection = null;
//        try {
//            connection = getSQLiteConnection();
//            Statement st = connection.createStatement();
//            HashMap<String, Object> queryDetails = getQueryDetails("select");
//
//            for (Map.Entry<String, Object> mapdata : queryDetails.entrySet()) {
//                ResultSet resultSet = st.executeQuery((String) mapdata.getValue());
//                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
//                int columns = resultSetMetaData.getColumnCount();
//                HashMap<String, Object> rowDatas;
//                ArrayList<HashMap<String, Object>> testDataList = new ArrayList<>();
//                String envName = null;
//
//                while (resultSet.next()) {
//                    rowDatas = new HashMap<>(columns);
//                    for (int i = 1; i <= columns; ++i) {
//                        rowDatas.put(resultSetMetaData.getColumnName(i), resultSet.getObject(i));
//                        envName = resultSet.getString("environment");
//                        System.out.println("env name is:" + envName);
//                    }
//                    testDataList.add(rowDatas);
//                }
//
//                System.out.println("env name is:" + envName);
//                finalMap.put(mapdata.getKey(), testDataList);
//            }
//
//            testDataHashMap.put(envName, finalMap);
//            FrameworkConstants.setEnvironment(envName);
//
//            ObjectMapper mapper = new ObjectMapper();
//            FrameworkConstants.setTestDataJsonFilePath(envName);
//            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FrameworkConstants.getTestdataJsonFilepath()), testDataHashMap);
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } catch (JsonMappingException | JsonGenerationException | IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (Objects.nonNull(connection)) {
//                try {
//                    connection.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    // Add your existing methods and variables here
//}
