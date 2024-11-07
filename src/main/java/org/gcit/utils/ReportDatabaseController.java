package org.gcit.utils;

import org.gcit.constants.FrameworkConstants;
import org.gcit.enums.ConfigProperties;

import java.sql.*;
import java.time.LocalDateTime;
/**
 * Controller class for handling report database operations.
 *
 * This class provides a method to store test report entries into the database.
 * It ensures that the report entries are only stored when the application is
 * running in the designated mode.
 */
public class ReportDatabaseController {
    /**
     * Private constructor to prevent instantiation of the ReportDatabaseController class.
     *
     * This class is designed as a controller with static methods to handle database operations
     * related to storing test report entries.
     */
    private ReportDatabaseController(){
    }

    /**
     * Stores the test report entry into the database.
     *
     * This method inserts a test case entry into the database only when the
     * application is running in local mode. It utilizes the configuration properties
     * to determine the runtime environment and prepares a SQL insert statement
     * to add the details of the test case to the database.
     *
     * @param testCaseName the name of the test case to be stored in the database
     * @param status the status of the test case (e.g., passed, failed)
     */
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
