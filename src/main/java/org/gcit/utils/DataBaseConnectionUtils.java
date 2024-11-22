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
    private static String runmode = PropertyUtils.getValue(ConfigProperties.RUNMODE);

    static {
        try {
            initializeConnection();
        } catch (Exception e) {
            log(ERROR, LOG_TAG + ": Error initializing database connection.");
            throw new RuntimeException(e);
        }
    }

    private DataBaseConnectionUtils() {
    }

    private static void initializeConnection() throws Exception {
        switch (runmode.toLowerCase()) {
            case "local":
                System.out.println("Local MYSQL");
                connectToMySQL();
                break;
            case "remote":
                connectToSQLite();
                break;
            default:
                throw new IllegalArgumentException("Invalid run mode: " + runmode);
        }
    }

    private static void connectToMySQL() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String hostName = JsonConfigUtils.get(DataBaseProperties.HOSTNAME);
        String port = JsonConfigUtils.get(DataBaseProperties.PORT);
        String schema = JsonConfigUtils.get(DataBaseProperties.SCHEMA);
        String dbusername = JsonConfigUtils.get(DataBaseProperties.DBUSERNAME);
        String password = JsonConfigUtils.get(DataBaseProperties.DBPASSWORD);
        log(INFO, LOG_TAG + ": Connected to MySQL database successfully." + hostName + port + schema + dbusername + password);

        //String url = "jdbc:mysql://" + hostName + ":" + port + "/" + schema + "?autoReconnect=true&useSSL=false";
        String url = "jdbc:mysql://" + hostName + ":" + port + "/" + schema;
        log(INFO, LOG_TAG + ": Connected to MySQL database successfully." + url);
        try {
            myConn = DriverManager.getConnection(url, dbusername, password);
            log(INFO, LOG_TAG + ": Connected to MySQL database successfully.");
        } catch (SQLException e) {
            log(ERROR, LOG_TAG + ": Error connecting to MySQL database.");
            throw new SQLException("Error connecting to MySQL database.", e);
        }
    }

    private static void connectToSQLite() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        String databasePath = FrameworkConstants.getDatabasePath();
        String url = "jdbc:sqlite:" + databasePath;

        try {
            myConn = DriverManager.getConnection(url);
            log(INFO, LOG_TAG + ": Connected to SQLite database successfully.");
        } catch (SQLException e) {
            log(ERROR, LOG_TAG + ": Error connecting to SQLite database.");
            throw new SQLException("Error connecting to SQLite database.", e);
        }
    }

    public static Connection getMyConn() {
        try {
            if (myConn == null || myConn.isClosed()) {
                initializeConnection();
            }
        } catch (SQLException | ClassNotFoundException e) {
            log(ERROR, LOG_TAG + ": Error obtaining database connection.");
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return myConn;
    }

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
