package com.quackstagram.util;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionTest {
    public static void main(String[] args) {
        DatabaseConnectionManager connectionManager = DatabaseConnectionManager.getInstance();
        Connection connection = null;
        
        try {
            connection = connectionManager.getConnection();
            if (connection != null && !connection.isClosed()) {
                System.out.println("Connection successful!");
            } else {
                System.out.println("Connection failed.");
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connectionManager.releaseConnection(connection);
            }
        }
    }
}