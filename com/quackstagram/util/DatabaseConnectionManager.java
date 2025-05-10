package com.quackstagram.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Manages database connections and configuration
 */
public class DatabaseConnectionManager {
    private static final int MAX_POOL_SIZE = 10;
    private static DatabaseConnectionManager instance;
    private String jdbcUrl;
    private String username;
    private String password;
    private BlockingQueue<Connection> connectionPool;
    
    /**
     * Private constructor - loads configuration and initializes connection pool
     */
    private DatabaseConnectionManager() {
        loadConfiguration();
        initializeConnectionPool();
    }
    
    /**
     * Gets the singleton instance of the connection manager
     * 
     * @return the DatabaseConnectionManager instance
     */
    public static synchronized DatabaseConnectionManager getInstance() {
        if (instance == null) {
            instance = new DatabaseConnectionManager();
        }
        return instance;
    }
    
    /**
     * Loads database configuration from properties file
     */
    private void loadConfiguration() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("config/database.properties")) {
            props.load(fis);
            jdbcUrl = props.getProperty("jdbc.url");
            username = props.getProperty("jdbc.username");
            password = props.getProperty("jdbc.password");
        } catch (IOException e) {
            // Fallback to default configuration
            System.err.println("Warning: Could not load database configuration file. Using defaults.");
            jdbcUrl = "jdbc:mysql://localhost:3306/quackstagram?useSSL=false&serverTimezone=UTC";
            username = "quackstagram_user";
            password = "quackstagram_pass";
        }
    }
    
    /**
     * Initializes the connection pool
     */
    private void initializeConnectionPool() {
        connectionPool = new ArrayBlockingQueue<>(MAX_POOL_SIZE);
        for (int i = 0; i < MAX_POOL_SIZE; i++) {
            try {
                Connection connection = createNewConnection();
                connectionPool.offer(connection);
            } catch (SQLException e) {
                System.err.println("Error initializing connection pool: " + e.getMessage());
            }
        }
    }
    
    /**
     * Creates a new database connection
     * 
     * @return a new Connection object
     * @throws SQLException if connection fails
     */
    private Connection createNewConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }
    
    /**
     * Gets a connection from the pool
     * 
     * @return a database connection
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        Connection connection = connectionPool.poll();
        if (connection == null || connection.isClosed()) {
            connection = createNewConnection();
        }
        return connection;
    }
    
    /**
     * Returns a connection to the pool
     * 
     * @param connection the connection to return
     */
    public void releaseConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed() && !connection.isReadOnly()) {
                    connectionPool.offer(connection);
                }
            } catch (SQLException e) {
                System.err.println("Error returning connection to pool: " + e.getMessage());
            }
        }
    }
    
    /**
     * Closes all connections in the pool
     */
    public void closeAllConnections() {
        connectionPool.forEach(connection -> {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        });
        connectionPool.clear();
    }
}