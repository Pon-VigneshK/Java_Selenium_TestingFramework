package org.gcit.utils;

import org.gcit.constants.FrameworkConstants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DataBaseConnectionUtils {
    private static Connection myConn;
    static {
        try {
            Class.forName("org.sqlite.JDBC");
            String databasePath =FrameworkConstants.getDatabasePath();
            String url = "jdbc:sqlite:" + databasePath;
            myConn = DriverManager.getConnection(url);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("SQLite JDBC driver not found.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error connecting to SQLite database.");
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
            } catch (SQLException e) {
                throw new RuntimeException("Error closing database connection", e);
            }
        }
    }
}