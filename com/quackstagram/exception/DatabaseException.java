package com.quackstagram.exception;

/**
 * Custom exception for database operations
 */
public class DatabaseException extends Exception {
    
    /**
     * Constructor with message
     * 
     * @param message the error message
     */
    public DatabaseException(String message) {
        super(message);
    }
    
    /**
     * Constructor with message and cause
     * 
     * @param message the error message
     * @param cause the cause of the exception
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Returns a formatted error message with query details
     * 
     * @param query the SQL query that failed
     * @param cause the underlying exception
     * @return a formatted DatabaseException
     */
    public static DatabaseException fromQuery(String query, Throwable cause) {
        return new DatabaseException(
                "Database operation failed. Query: " + query + ". Error: " + cause.getMessage(), 
                cause);
    }
}