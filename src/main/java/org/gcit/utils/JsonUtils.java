package org.gcit.utils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.gcit.constants.FrameworkConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static org.gcit.utils.DataBaseConnectionUtils.getMyConn;
@SuppressWarnings({ "unchecked", "rawtypes" })
public final class JsonUtils {
    private JsonUtils(){}
    private static List<Map<String, Object>> finaltestcaselist = new ArrayList();

    private static HashMap<String, Object> queriesList= new HashMap();
    private static FileInputStream fileInputStream;
    private static LinkedHashMap<String , LinkedHashMap<String, ArrayList<HashMap<String, Object>>>> testDataHashMap= new LinkedHashMap();
    private static String envName;
    private static List<Map<String, Object>> finalDatalist= new ArrayList<>();
    private static LinkedHashMap<String, ArrayList<HashMap<String, Object>>> finalMap = new LinkedHashMap();

    //test case runner list map
    private static LinkedHashMap<String, ArrayList<HashMap<String, Object>>> finalTestList= new LinkedHashMap();
    private static LinkedHashMap<String , LinkedHashMap<String, ArrayList<HashMap<String, Object>>>> testRunnerHashMap= new LinkedHashMap();
    public static LinkedHashMap<String , LinkedHashMap<String, ArrayList<HashMap<String, Object>>>> getTestDataHashMap() {
        return testDataHashMap;
    }

    /**
     * Read RunnerList data from Excel and write it to a JSON file.
     *
     * @param sheetName Name of the Excel sheet containing data.
     */
    public static void generateRunnerListJsonDataFromExcel(String sheetName) {
        try{
            List<Map<String, String>> testDataList = ExcelUtils.getTestDetails(sheetName);
            String testCaseListName = "testCaseLists";
            Map<String, Object> finalTestList = new HashMap<>();
            finalTestList.put(testCaseListName, testDataList);
            HashMap<String, Map<String, Object>> testRunnerHashMap = new HashMap<>();
            testRunnerHashMap.put(FrameworkConstants.getRunmanager(), finalTestList);
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(FrameworkConstants.getTestCaseJsonPath()), testRunnerHashMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Get test details from the JSON file based on high-level and nested key names.
     *
     * @param highLevelKeyName The high-level key name in the JSON structure.
     * @return List of test details as maps.
     */
    public static List<Map<String, Object>> getTestDetails(String highLevelKeyName) {
        List<Map<String, Object>> testDetailsList = new ArrayList<>();
        FileInputStream fileInputStream = null;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            fileInputStream = new FileInputStream(FrameworkConstants.getTestCaseJsonPath());
            HashMap<String, HashMap> testCaseMap =  objectMapper.readValue(fileInputStream, HashMap.class);
            HashMap<String, ArrayList<Map<String, Object >>> jsonTestCaseList = (HashMap<String, ArrayList<Map<String, Object>>>) testCaseMap.get(highLevelKeyName);
            for (Map.Entry<String, ArrayList<Map<String,Object>>> testCase : jsonTestCaseList.entrySet()) {
                for (Map<String,Object> cases: testCase.getValue())
                    testDetailsList.add(cases);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(fileInputStream)) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return testDetailsList;
    }
    public static List<Map<String, Object>> getTestDataDetails(){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            fileInputStream= new FileInputStream(FrameworkConstants.getTestDataJsonFilePath());
            HashMap<String, HashMap> jsonTestDataMap = objectMapper.readValue(fileInputStream, HashMap.class);
            LinkedHashMap<String ,Object> jsonTestDataLinkedMap = (LinkedHashMap) jsonTestDataMap.get(FrameworkConstants.getEnvironment());
            try {
                for (Map.Entry<String, Object> finaltestcasemap : jsonTestDataLinkedMap.entrySet()) {
                    finalDatalist.addAll((ArrayList) finaltestcasemap.getValue());
                }
            }
            catch (RuntimeException e){
                e.printStackTrace();
            }
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {

            if(Objects.nonNull(fileInputStream)){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return finalDatalist;
    }
    public static HashMap<String, Object> getQueryDetails(String queryType){

        try {
            String keyname = queryType.toLowerCase()+"queries";
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            fileInputStream= new FileInputStream(FrameworkConstants.getSqlQueryjsonfilepath());
            HashMap<String, Object> jsontestcasemap = objectMapper.readValue(fileInputStream, HashMap.class);
            queriesList = (HashMap<String, Object>) jsontestcasemap.get(keyname);
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {

            if(Objects.nonNull(fileInputStream)){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return queriesList;
    }
    public static void generateTestDataJson() {
        try {

            Statement st =getMyConn().createStatement();
            HashMap<String, Object> queryDetails = getQueryDetails("select");


            for (Map.Entry<String, Object> mapdata : queryDetails.entrySet()) {

                ResultSet resultSet = st.executeQuery((String) mapdata.getValue());
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                int columns = resultSetMetaData.getColumnCount();
                HashMap<String, Object> rowDatas;
                ArrayList<HashMap<String, Object>> testDataList = new ArrayList();

                while (resultSet.next()) {
                    rowDatas = new HashMap(columns);
                    for (int i = 1; i <= columns; ++i) {
                        rowDatas.put(resultSetMetaData.getColumnName(i), resultSet.getObject(i));
                        //envName = PropertyUtils.getValue(ConfigProperties.ENV);
                        envName = resultSet.getString("environment");
                    }
                    testDataList.add(rowDatas);

                }
                System.out.println("env name is:"+ envName);
                finalMap.put(mapdata.getKey(), testDataList);
            }
            testDataHashMap.put(envName,finalMap);
            FrameworkConstants.setEnvironment(envName);

            ObjectMapper mapper = new ObjectMapper();
            FrameworkConstants.setTestDataJsonFilePath(envName);
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FrameworkConstants.getTestDataJsonFilePath()), testDataHashMap);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            if (Objects.nonNull(getMyConn())){
                try {
                    getMyConn().close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void generateRunnerListJsonData() {
        try {
            Statement st =getMyConn().createStatement();
            HashMap<String, Object> queryDetails = getQueryDetails("runnerlist");
            for (Map.Entry<String, Object> mapdata : queryDetails.entrySet()) {
                ResultSet resultSet = st.executeQuery((String) mapdata.getValue());
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                int columns = resultSetMetaData.getColumnCount();
                HashMap<String, Object> rowDatas;
                ArrayList<HashMap<String, Object>> testDataList = new ArrayList();
                while (resultSet.next()) {
                    rowDatas = new HashMap(columns);
                    for (int i = 1; i <= columns; ++i) {
                        rowDatas.put(resultSetMetaData.getColumnName(i), resultSet.getObject(i));
                    }
                    testDataList.add(rowDatas);
                }
                finalTestList.put(mapdata.getKey(), testDataList);
            }
            testRunnerHashMap.put(FrameworkConstants.getRunmanager() ,finalTestList);
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FrameworkConstants.getTestCaseJsonPath()), testRunnerHashMap);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();

        } catch (JsonGenerationException e) {
            e.printStackTrace();

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

