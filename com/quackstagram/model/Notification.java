// File: com/quackstagram/model/Notification.java
package com.quackstagram.model;

import java.time.LocalDateTime;

/**
 * Represents a notification on Quackstagram
 */
public class Notification {
    private final String receiverUsername;
    private final String senderUsername;
    private final String imageId; // Optional, can be null for follow notifications
    private final LocalDateTime timestamp;
    private final NotificationType type;

    /**
     * Constructor with all fields
     */
    public Notification(String receiverUsername, String senderUsername, String imageId, 
                       LocalDateTime timestamp, NotificationType type) {
        this.receiverUsername = receiverUsername;
        this.senderUsername = senderUsername;
        this.imageId = imageId;
        this.timestamp = timestamp;
        this.type = type;
    }

    // Getter methods
    public String getReceiverUsername() { return receiverUsername; }
    public String getSenderUsername() { return senderUsername; }
    public String getImageId() { return imageId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public NotificationType getType() { return type; }

    /**
     * Convert notification to string representation for storage
     */
    @Override
    public String toString() {
        return receiverUsername + ";" + senderUsername + ";" + 
               (imageId != null ? imageId : "") + ";" + 
               timestamp + ";" + type;
    }
}