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

    /**
     * Initializes the database connection based on the configured run mode.
     * This method is called from the static initializer block and when {@link #getMyConn()}
     * detects a closed or null connection.
     *
     * @throws Exception if there's an error during driver loading, configuration fetching,
     *                   or establishing the database connection. Specific exceptions like
     *                   {@link SQLException}, {@link ClassNotFoundException}, or
     *                   {@link IllegalArgumentException} for invalid run mode can occur.
     */
    private static void initializeConnection() throws Exception {
        switch (runmode.toLowerCase()) {
            case "local":
                // System.out.println("Local MYSQL"); // Consider removing or replacing with LogService.log
                connectToMySQL();
                break;
            case "remote":
                connectToSQLite();
                break;
            default:
                throw new IllegalArgumentException("Invalid run mode: " + runmode);
        }
    }

    /**
     * Establishes a connection to a MySQL database using configuration parameters
     * fetched via {@link JsonConfigUtils}.
     * Sets the static {@link #myConn} field upon successful connection.
     *
     * @throws Exception if {@link JsonConfigUtils#get(DataBaseProperties)} fails to retrieve
     *                   configuration or if {@link Class#forName(String)} for the MySQL driver fails.
     * @throws SQLException if {@link DriverManager#getConnection(String, String, String)} fails
     *                      to establish a connection.
     */
    private static void connectToMySQL() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String hostName = JsonConfigUtils.get(DataBaseProperties.HOSTNAME);
        String port = JsonConfigUtils.get(DataBaseProperties.PORT);
        String schema = JsonConfigUtils.get(DataBaseProperties.SCHEMA);
        String dbusername = JsonConfigUtils.get(DataBaseProperties.DBUSERNAME);
        String password = JsonConfigUtils.get(DataBaseProperties.DBPASSWORD);
        log(INFO, LOG_TAG + ": Connecting to MySQL database with details: " + hostName + ":" + port + "/" + schema);

        //String url = "jdbc:mysql://" + hostName + ":" + port + "/" + schema + "?autoReconnect=true&useSSL=false";
        String url = "jdbc:mysql://" + hostName + ":" + port + "/" + schema + "?sslMode=REQUIRED";
        log(INFO, LOG_TAG + ": Connected to MySQL database successfully." + url);
        try {
            myConn = DriverManager.getConnection(url, dbusername, password);
            log(INFO, LOG_TAG + ": Connected to MySQL database successfully.");
        } catch (SQLException e) {
            log(ERROR, LOG_TAG + ": Error connecting to MySQL database.");
            throw new SQLException("Error connecting to MySQL database.", e);
        }
    }

    /**
     * Establishes a connection to an SQLite database using the database path
     * from {@link FrameworkConstants#getDatabasePath()}.
     * Sets the static {@link #myConn} field upon successful connection.
     *
     * @throws SQLException if {@link DriverManager#getConnection(String)} fails to establish a connection.
     * @throws ClassNotFoundException if the SQLite JDBC driver class ({@code org.sqlite.JDBC}) cannot be found.
     */
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

    /**
     * Retrieves the active database connection.
     * <p>
     * If the current connection ({@link #myConn}) is {@code null} or closed, this method attempts
     * to re-initialize it by calling {@link #initializeConnection()}. This ensures that a valid
     * connection is returned if possible.
     * <p>
     * In case of errors during connection retrieval or re-initialization (e.g., {@link SQLException},
     * {@link ClassNotFoundException}, or other exceptions from {@link #initializeConnection()}),
     * an error is logged, and {@code null} is returned. If a more general {@link Exception} occurs
     * during re-initialization, it is wrapped in a {@link RuntimeException}.
     *
     * @return The active {@link Connection} instance, or {@code null} if a connection cannot be
     *         established or retrieved.
     * @see #initializeConnection()
     */
    public static Connection getMyConn() {
        try {
            if (myConn == null || myConn.isClosed()) {
                log(INFO, LOG_TAG + ": Database connection is null or closed. Attempting to re-initialize.");
                initializeConnection();
            }
        } catch (SQLException | ClassNotFoundException e) {
            log(ERROR, LOG_TAG + ": Error obtaining database connection. SQL State: " + (e instanceof SQLException ? ((SQLException)e).getSQLState() : "N/A"), e);
            return null;
        } catch (Exception e) {
            // Catching general Exception from initializeConnection()
            log(ERROR, LOG_TAG + ": A non-SQL/ClassNotFound exception occurred while obtaining database connection.", e);
            throw new RuntimeException("Failed to initialize database connection due to an unexpected error.", e);
        }
        return myConn;
    }

    /**
     * Closes the current active database connection ({@link #myConn}).
     * <p>
     * If the connection is not {@code null} and open, it will be closed.
     * A log message is recorded upon successful closure.
     * If a {@link SQLException} occurs during the close operation, an error is logged,
     * and the exception is wrapped in a {@link BaseException}.
     *
     * @throws BaseException if a {@link SQLException} occurs while trying to close the connection.
     */
    public static void closeConnection() {
        if (myConn != null) {
            try {
                if (!myConn.isClosed()) {
                    myConn.close();
                    log(INFO, LOG_TAG + ": Database connection closed successfully.");
                } else {
                    log(INFO, LOG_TAG + ": Database connection was already closed.");
                }
            } catch (SQLException e) {
                log(ERROR, LOG_TAG + ": Error closing database connection. SQLState: " + e.getSQLState(), e);
                throw new BaseException("Error closing database connection", e);
            }
        } else {
            log(INFO, LOG_TAG + ": Attempted to close a null database connection. No action taken.");
        }
    }
}
