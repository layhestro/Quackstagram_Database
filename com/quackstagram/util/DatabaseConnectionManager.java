package com.quackstagram.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.util.Properties;

public class DatabaseConnectionManager {
    private static DatabaseConnectionManager instance;
    private String jdbcUrl;
    private String username;
    private String password;
    
    private DatabaseConnectionManager() {
        loadConfiguration();
    }
    
    public static synchronized DatabaseConnectionManager getInstance() {
        if (instance == null) {
            instance = new DatabaseConnectionManager();
        }
        return instance;
    }
    
    private void loadConfiguration() {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("config/database.properties"));
            jdbcUrl = props.getProperty("jdbc.url");
            username = props.getProperty("jdbc.username");
            password = props.getProperty("jdbc.password");
            
            // Load driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            System.err.println("Error loading configuration: " + e.getMessage());
            // Default values
            jdbcUrl = "jdbc:mysql://localhost:3306/quackstagram?useSSL=false";
            username = "user";
            password = "pass";
        }
    }
    
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }
}