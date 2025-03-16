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

    /**
     * Retrieves all notifications for a specific receiver
     * 
     * @param username the username of the receiver
     * @return a list of notifications for the receiver
     */
    @Override
    public List<Notification> findByReceiver(String username) {
        List<Notification> notifications = new ArrayList<>();
        try {
            FileUtil.createFileIfNotExists(notificationsFilePath);
            
            List<String> lines = FileUtil.readMatchingLines(notificationsFilePath, 
                    line -> {
                        String[] parts = line.split(";");
                        return parts.length >= 1 && parts[0].trim().equals(username);
                    });
            
            for (String line : lines) {
                notifications.add(parseNotificationFromLine(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    /**
     * Saves a new notification to file
     * 
     * @param notification the notification to save
     */
    @Override
    public void save(Notification notification) {
        try {
            FileUtil.createFileIfNotExists(notificationsFilePath);
            
            String line = String.format("%s; %s; %s; %s; %s",
                    notification.getReceiverUsername(),
                    notification.getSenderUsername(),
                    notification.getImageId() != null ? notification.getImageId() : "",
                    notification.getTimestamp().format(formatter),
                    notification.getType().name());
            
            FileUtil.appendLine(notificationsFilePath, line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a notification by ID
     * 
     * @param id the ID of the notification to delete
     */
    @Override
    public void delete(String id) {
        // Not implemented in this version
    }

    /**
     * Parses a line from the notifications file into a Notification object
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
        
        NotificationType type = NotificationType.LIKE;
        if (parts.length >= 5) {
            try {
                type = NotificationType.valueOf(parts[4].trim());
            } catch (IllegalArgumentException e) {
                // Default to LIKE if type is invalid
            }
        } else if (imageId == null) {
            type = NotificationType.FOLLOW;
        }
        
        return new Notification(receiver, sender, imageId, timestamp, type);
    }
}