package com.quackstagram.dao.impl;

import com.quackstagram.dao.interfaces.NotificationDAO;
import com.quackstagram.model.Notification;
import com.quackstagram.model.NotificationType;
import com.quackstagram.util.DatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Database implementation of NotificationDAO
 */
public class DatabaseNotificationDAO implements NotificationDAO {
    private final DatabaseConnectionManager connectionManager;
    
    /**
     * Constructor for DatabaseNotificationDAO
     */
    public DatabaseNotificationDAO() {
        this.connectionManager = DatabaseConnectionManager.getInstance();
    }

    /**
     * Retrieves all notifications for a specific receiver
     * 
     * @param username the username of the receiver
     * @return a list of notifications for the receiver
     */
    @Override
    public List<Notification> findByReceiver(String username) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Notification> notifications = new ArrayList<>();
        
        try {
            connection = connectionManager.getConnection();
            String query = "SELECT * FROM Notifications WHERE receiverUsername = ? " +
                          "ORDER BY timestamp DESC";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            
            rs = stmt.executeQuery();
            while (rs.next()) {
                notifications.add(mapResultSetToNotification(rs));
            }
            
            return notifications;
        } catch (SQLException e) {
            System.err.println("Error finding notifications by receiver: " + e.getMessage());
            return notifications;
        } finally {
            closeResources(connection, stmt, rs);
        }
    }

    /**
     * Saves a new notification
     * 
     * @param notification the notification to save
     */
    @Override
    public void save(Notification notification) {
        Connection connection = null;
        PreparedStatement stmt = null;
        
        try {
            connection = connectionManager.getConnection();
            String query = "INSERT INTO Notifications (receiverUsername, senderUsername, imageId, timestamp, type) " +
                          "VALUES (?, ?, ?, ?, ?)";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, notification.getReceiverUsername());
            stmt.setString(2, notification.getSenderUsername());
            stmt.setString(3, notification.getImageId());
            stmt.setTimestamp(4, Timestamp.valueOf(notification.getTimestamp()));
            stmt.setString(5, notification.getType().toString());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving notification: " + e.getMessage());
        } finally {
            closeResources(connection, stmt, null);
        }
    }

    /**
     * Deletes a notification by ID
     * 
     * @param id the ID of the notification to delete
     */
    @Override
    public void delete(String id) {
        Connection connection = null;
        PreparedStatement stmt = null;
        
        try {
            connection = connectionManager.getConnection();
            String query = "DELETE FROM Notifications WHERE notificationId = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(id));
            
            stmt.executeUpdate();
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error deleting notification: " + e.getMessage());
        } finally {
            closeResources(connection, stmt, null);
        }
    }
    
    /**
     * Maps a ResultSet to a Notification object
     * 
     * @param rs the ResultSet containing notification data
     * @return a Notification object
     * @throws SQLException if a database error occurs
     */
    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        String receiverUsername = rs.getString("receiverUsername");
        String senderUsername = rs.getString("senderUsername");
        String imageId = rs.getString("imageId");
        LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();
        NotificationType type = NotificationType.valueOf(rs.getString("type"));
        
        return new Notification(receiverUsername, senderUsername, imageId, timestamp, type);
    }
    
    /**
     * Closes database resources
     * 
     * @param connection the database connection
     * @param stmt the prepared statement
     * @param rs the result set
     */
    private void closeResources(Connection connection, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (connection != null) connectionManager.releaseConnection(connection);
        } catch (SQLException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }
}