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

public final class DataBaseConnectionUtils {
    private static final String LOG_TAG =
            DataBaseConnectionUtils.class.getSimpleName();
    private static Connection myConn;
    private static String runmode = PropertyUtils.getValue(ConfigProperties.RUNMODE);

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
                throw new BaseException("Error closing database connection", e);
            }
        }
    }
}
