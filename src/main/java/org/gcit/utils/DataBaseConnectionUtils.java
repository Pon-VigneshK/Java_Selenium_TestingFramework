package org.gcit.utils;

import org.gcit.constants.FrameworkConstants;
import org.gcit.enums.ConfigProperties;
import org.gcit.enums.DataBaseProperties;
import org.gcit.exceptions.BaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.gcit.enums.LogType.ERROR;
import static org.gcit.enums.LogType.INFO;
import static org.gcit.logger.LogService.log;

/**
 * Utility class for managing database connections.
 *
 * This class handles the creation and management of database connections based on the run mode specified in the configuration.
 * It supports both local MySQL and remote SQLite databases.
 *
 * The appropriate database connection is established during the class initialization based on the configured run mode.
 */
public final class DataBaseConnectionUtils {
    /**
     * A constant that holds the simple name of the class `DataBaseConnectionUtils`.
     * It is typically used for logging purposes to provide a consistent tag that
     * denotes messages originating from this class.
     */
    private static final String LOG_TAG =
            DataBaseConnectionUtils.class.getSimpleName();
    /**
     * A static instance of {@link Connection} used to manage the database connection.
     *
     * This variable provides a central point of access to the database connection
     * within the application, ensuring that a single connection instance is
     * reused, managed, and properly closed when no longer needed.
     *
     * Initialization and management of this connection should be handled through
     * the methods provided within the {@code DataBaseConnectionUtils} class such as
     * {@code getMyConn()} and {@code closeConnection()} to prevent potential resource leaks.
     */
    private static Connection myConn;
    /**
     * A string variable that stores the running mode of the application.
     *
     * The value is fetched from the configuration properties using the key {@code ConfigProperties.RUNMODE}.
     * It determines the operational mode in which the application should be run (e.g., local, remote).
     *
     * This variable is initialized when the class is loaded and remains constant throughout the application lifecycle.
     */
    private static String runmode = "remote";

    static {
        try {
            switch (runmode.toLowerCase()) {
                case "local":
                    try {
                        String hostName = JsonConfigUtils.get(DataBaseProperties.HOSTNAME);
                        String port = JsonConfigUtils.get(DataBaseProperties.PORT);
                        String schema = JsonConfigUtils.get(DataBaseProperties.SCHEMA);
                        String dbusername = JsonConfigUtils.get(DataBaseProperties.DBUSERNAME);
                        String password = JsonConfigUtils.get(DataBaseProperties.DBPASSWORD);
                        String url = "jdbc:mysql://" + hostName + ":" + port + "/" + schema + "?autoReconnect=true&useSSL=false";
                        myConn = DriverManager.getConnection(url, dbusername, password);
                        log(INFO, LOG_TAG + ": Connected to MySql database successfully.");
                    } catch (SQLException e) {
                        log(ERROR, LOG_TAG + ": Error connecting to MySQL database.");
                        System.err.println("Error connecting to MySQL database.");
                        e.printStackTrace();
                        System.exit(0);
                        throw new SQLException("Error connecting to MySQL database." + e);
                    }
                    break;
                case "remote":
                    try {
                        Class.forName("org.sqlite.JDBC");
                        String databasePath = FrameworkConstants.getDatabasePath();
                        String urlRemote = "jdbc:sqlite:" + databasePath;
                        myConn = DriverManager.getConnection(urlRemote);
                        log(INFO, LOG_TAG + ": Connected to SQLite database successfully.");
                    } catch (ClassNotFoundException e) {
                        log(ERROR, LOG_TAG + ": SQLite JDBC driver not found.");
                        System.err.println("SQLite JDBC driver not found.");
                        throw new RuntimeException(e);
                    } catch (SQLException e) {
                        log(ERROR, LOG_TAG + ": Error connecting to SQLite database.");
                        System.err.println("Error connecting to SQLite database.");
                        e.printStackTrace();
                        System.exit(0);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Invalid run mode: " + runmode);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Private constructor to prevent instantiation of the DataBaseConnectionUtils class.
     * This class provides utility methods to manage database connections.
     */
    private DataBaseConnectionUtils() {
    }

    /**
     * Retrieves the active database connection object.
     *
     * @return the current Connection object used to interact with the database.
     */
    public static Connection getMyConn() {
        return myConn;
    }

    /**
     * Closes the current database connection if it is not already closed.
     *
     * This method checks if the database connection `myConn` is not null.
     * If the connection is valid, it attempts to close it while logging the action.
     * Upon successful closure, an informational log message is recorded.
     * If an SQLException occurs during the closure process, an error log message
     * is recorded and a custom `BaseException` is thrown with details about the error.
     *
     * The method ensures proper resource management by closing the database connection
     * and logging the process for audit purposes.
     *
     * @throws BaseException if there is an error closing the database connection
     */
    public static void closeConnection() {
        if (myConn != null) {
            try {
                myConn.close();
                log(INFO, LOG_TAG + ": Database connection closed.");
            } catch (SQLException e) {
                log(ERROR, LOG_TAG + ": Error closing database connection.");
                throw new BaseException("Error closing database connection", e);
            }
        }
    }
}
