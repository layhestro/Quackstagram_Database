// File: com/quackstagram/dao/impl/FileNotificationDAO.java
package com.quackstagram.dao.impl;

import com.quackstagram.dao.interfaces.NotificationDAO;
import com.quackstagram.model.Notification;
import com.quackstagram.model.NotificationType;
import com.quackstagram.util.FileUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * File-based implementation of NotificationDAO
 */
public class FileNotificationDAO implements NotificationDAO {
    private final String notificationsFilePath = "data/notifications.txt";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<Notification> findByReceiver(String username) {
        List<Notification> notifications = new ArrayList<>();
        try {
            // Create the file if it doesn't exist
            FileUtil.createFileIfNotExists(notificationsFilePath);
            
            // Find lines with receiver username
            List<String> lines = FileUtil.readMatchingLines(notificationsFilePath, 
                    line -> {
                        String[] parts = line.split(";");
                        return parts.length >= 1 && parts[0].trim().equals(username);
                    });
            
            // Parse each line into a Notification object
            for (String line : lines) {
                notifications.add(parseNotificationFromLine(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    @Override
    public void save(Notification notification) {
        try {
            // Create the file if it doesn't exist
            FileUtil.createFileIfNotExists(notificationsFilePath);
            
            // Format the line to save
            String line = String.format("%s; %s; %s; %s; %s",
                    notification.getReceiverUsername(),
                    notification.getSenderUsername(),
                    notification.getImageId() != null ? notification.getImageId() : "",
                    notification.getTimestamp().format(formatter),
                    notification.getType().name());
            
            // Append to file
            FileUtil.appendLine(notificationsFilePath, line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String id) {
        // In a real application, we would need a unique identifier for notifications
        // For this simple implementation, we'll just note that this would remove a specific notification
    }

    /**
     * Parse a line from the notifications file into a Notification object
     * 
     * @param line the line to parse
     * @return the Notification object
     */
    private Notification parseNotificationFromLine(String line) {
        String[] parts = line.split(";");
        
        String receiver = parts[0].trim();
        String sender = parts[1].trim();
        String imageId = parts[2].trim();
        if (imageId.isEmpty()) {
            imageId = null;
        }
        
        LocalDateTime timestamp = LocalDateTime.parse(parts[3].trim(), formatter);
        
        NotificationType type = NotificationType.LIKE; // Default
        if (parts.length >= 5) {
            try {
                type = NotificationType.valueOf(parts[4].trim());
            } catch (IllegalArgumentException e) {
                // Ignore invalid types
            }
        } else if (imageId == null) {
            type = NotificationType.FOLLOW; // Assume FOLLOW if no image ID
        }
        
        return new Notification(receiver, sender, imageId, timestamp, type);
    }
}