package org.gcit.utils;

import org.gcit.enums.ConfigProperties;
import org.gcit.enums.LogType;
import org.gcit.logger.LogService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
/**
 * Manages the storage of test execution report entries into a database.
 * <p>
 * This utility class provides a static method to record the outcome of test cases.
 * Crucially, it only performs database operations if the application's run mode,
 * determined by {@link PropertyUtils#getValue(ConfigProperties)} with key {@link ConfigProperties#RUNMODE},
 * is configured to "local". This allows for selective database reporting based on the execution context.
 * It interacts with {@link DataBaseConnectionUtils} to obtain database connections and
 * uses {@link PropertyUtils} to fetch environment details for the report.
 *
 * @see PropertyUtils
 * @see DataBaseConnectionUtils
 * @see ConfigProperties#RUNMODE
 * @see ConfigProperties#ENV
 */
public class ReportDatabaseController {
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ReportDatabaseController(){
    }

    /**
     * Stores a test report entry into the database, conditional on the application's run mode.
     * <p>
     * This method inserts a record into the {@code ui_automation_result.hrm_result} table
     * if the {@code RUNMODE} configuration property (obtained via {@link PropertyUtils}) is "local".
     * The record includes:
     * <ul>
     *   <li>The current runtime environment (e.g., "QA", "DEV"), fetched using {@link PropertyUtils#getValue(ConfigProperties)} with key {@link ConfigProperties#ENV}.</li>
     *   <li>The name of the test case.</li>
     *   <li>The status of the test case (e.g., "PASSED", "FAILED").</li>
     *   <li>The execution timestamp (current date and time).</li>
     * </ul>
     * <p>
     * If a {@link SQLException} occurs during database interaction (e.g., connection issue, SQL error),
     * the error is logged using {@link LogService}, but the exception is not re-thrown. This means
     * that a failure to store the report in the database will not halt the test execution flow.
     * If no database connection is available from {@link DataBaseConnectionUtils#getMyConn()},
     * an {@link SQLException} is thrown internally and caught, leading to an error log.
     *
     * @param testCaseName The name of the test case to be recorded.
     * @param status The execution status of the test case (e.g., "PASSED", "FAILED", "SKIPPED").
     */
    public static void storeReportInDatabase(String testCaseName, String status) {
        if (PropertyUtils.getValue(ConfigProperties.RUNMODE).equalsIgnoreCase("local")){
            String insertQuery = "INSERT INTO `ui_automation_result`.`hrm_result` (`environment`, `testCaseName`, `status`, `executionTime`) VALUES (?, ?, ?, ?)";
            // Using try-with-resources for Connection and PreparedStatement
            try (Connection connection = DataBaseConnectionUtils.getMyConn();
                 PreparedStatement statement = (connection != null) ? connection.prepareStatement(insertQuery) : null) {

                if (statement == null) {
                    // This case implies connection was null. getMyConn() might return null and log an error.
                    // If getMyConn() itself throws, it would be caught by the outer catch.
                    LogService.log(LogType.ERROR, "Failed to store report for test case: " + testCaseName + ". Database connection or statement could not be prepared (connection might be null).");
                    return; // Exit if statement couldn't be prepared (likely due to null connection)
                }

                statement.setString(1, PropertyUtils.getValue(ConfigProperties.ENV).toUpperCase());
                statement.setString(2, testCaseName);
                statement.setString(3, status);
                statement.setString(4, LocalDateTime.now().toString());
                statement.executeUpdate();
                LogService.log(LogType.INFO, "Test report entry stored in database for test case: " + testCaseName + " with status: " + status);
            } catch (SQLException e) {
                LogService.log(LogType.ERROR, "Error storing report in database for test case: " + testCaseName + ". SQLState: " + e.getSQLState(), e);
                // Exception is logged but not re-thrown, allowing application to continue.
            } catch (Exception e) {
                // Catch any other unexpected exceptions during the process
                LogService.log(LogType.ERROR, "Unexpected error while storing report in database for test case: " + testCaseName, e);
            }
        } else {
            LogService.log(LogType.INFO, "Skipping database report storage: RUNMODE is not 'local'.");
        }
    }
}
