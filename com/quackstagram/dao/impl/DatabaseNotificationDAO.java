package com.quackstagram.dao.impl;

import com.quackstagram.dao.interfaces.NotificationDAO;
import com.quackstagram.model.Notification;
import com.quackstagram.model.NotificationType;
import com.quackstagram.util.DatabaseConnectionManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseNotificationDAO implements NotificationDAO {
    private final DatabaseConnectionManager connectionManager;
    
    public DatabaseNotificationDAO() {
        this.connectionManager = DatabaseConnectionManager.getInstance();
    }

    @Override
    public List<Notification> findByReceiver(String username) {
        List<Notification> notifications = new ArrayList<>();
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM Notifications WHERE receiverUsername = ? ORDER BY timestamp DESC")) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String receiverUsername = rs.getString("receiverUsername");
                String senderUsername = rs.getString("senderUsername");
                String imageId = rs.getString("imageId");
                LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();
                NotificationType type = NotificationType.valueOf(rs.getString("type"));
                
                notifications.add(new Notification(receiverUsername, senderUsername, imageId, timestamp, type));
            }
        } catch (SQLException e) {
            System.err.println("Error finding notifications: " + e.getMessage());
        }
        
        return notifications;
    }

    @Override
    public void save(Notification notification) {
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO Notifications (receiverUsername, senderUsername, imageId, timestamp, type) " +
                     "VALUES (?, ?, ?, ?, ?)")) {
            
            stmt.setString(1, notification.getReceiverUsername());
            stmt.setString(2, notification.getSenderUsername());
            stmt.setString(3, notification.getImageId());
            stmt.setTimestamp(4, Timestamp.valueOf(notification.getTimestamp()));
            stmt.setString(5, notification.getType().toString());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving notification: " + e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM Notifications WHERE notificationId = ?")) {
            
            stmt.setInt(1, Integer.parseInt(id));
            stmt.executeUpdate();
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error deleting notification: " + e.getMessage());
        }
    }
}