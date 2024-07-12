package org.gcit.utils;

import org.gcit.constants.FrameworkConstants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.gcit.enums.LogType.ERROR;
import static org.gcit.enums.LogType.INFO;
import static org.gcit.logger.LogService.log;

public final class DataBaseConnectionUtils {
    private static final String LOG_TAG =
            DataBaseConnectionUtils.class.getSimpleName();
    private static Connection myConn;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            String databasePath = FrameworkConstants.getDatabasePath();
            String url = "jdbc:sqlite:" + databasePath;
            myConn = DriverManager.getConnection(url);
            log(INFO, LOG_TAG + ": Connected to SQLite database successfully.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            log(ERROR, LOG_TAG + ": SQLite JDBC driver not found.");
            System.err.println("SQLite JDBC driver not found.");
        } catch (SQLException e) {
            log(ERROR, LOG_TAG + ": Error connecting to SQLite database.");
            System.err.println("Error connecting to SQLite database.");
            e.printStackTrace();
        }
    }

    private DataBaseConnectionUtils() {
    }

    public static Connection getMyConn() {
        return myConn;
    }

    public static void closeConnection() {
        if (myConn != null) {
            try {
                myConn.close();
                log(INFO, LOG_TAG + ": Database connection closed.");
            } catch (SQLException e) {
                log(ERROR, LOG_TAG + ": Error closing database connection.");
                throw new RuntimeException("Error closing database connection", e);
            }
        }
    }
}