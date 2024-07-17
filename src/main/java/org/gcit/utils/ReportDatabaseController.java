package org.gcit.utils;

import org.gcit.constants.FrameworkConstants;
import org.gcit.enums.ConfigProperties;

import java.sql.*;
import java.time.LocalDateTime;
public class ReportDatabaseController {
    private ReportDatabaseController(){
    }

    public static void storeReportInDatabase(String testCaseName, String status) {
        if (PropertyUtils.getValue(ConfigProperties.RUNMODE).equalsIgnoreCase("local")){
        String insertQuery = "INSERT INTO `result`.`demo` (`environment`, `testCaseName`, `status`, `executionTime`) VALUES (?, ?, ?, ?)";
        try (Connection connection = DataBaseConnectionUtils.getMyConn();
             PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            if (connection == null) {
                throw new SQLException("No database connection available.");
            }
            statement.setString(1, FrameworkConstants.getEnvironment().toUpperCase());
            statement.setString(2, testCaseName);
            statement.setString(3, status);
            statement.setString(4, LocalDateTime.now().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }}
    }
