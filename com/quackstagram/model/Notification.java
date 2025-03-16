package com.quackstagram.model;

import java.time.LocalDateTime;

/**
 * Represents a notification on Quackstagram
 */
public class Notification {
    private final String receiverUsername;
    private final String senderUsername;
    private final String imageId;
    private final LocalDateTime timestamp;
    private final NotificationType type;

    /**
     * Constructor with all fields
     * 
     * @param receiverUsername the username of the receiver
     * @param senderUsername the username of the sender
     * @param imageId the ID of the image (null for follow notifications)
     * @param timestamp the timestamp of the notification
     * @param type the type of notification
     */
    public Notification(String receiverUsername, String senderUsername, String imageId, 
                       LocalDateTime timestamp, NotificationType type) {
        this.receiverUsername = receiverUsername;
        this.senderUsername = senderUsername;
        this.imageId = imageId;
        this.timestamp = timestamp;
        this.type = type;
    }

    /**
     * Gets the username of the receiver
     * 
     * @return the receiver username
     */
    public String getReceiverUsername() { return receiverUsername; }
    
    /**
     * Gets the username of the sender
     * 
     * @return the sender username
     */
    public String getSenderUsername() { return senderUsername; }
    
    /**
     * Gets the ID of the image
     * 
     * @return the image ID
     */
    public String getImageId() { return imageId; }
    
    /**
     * Gets the timestamp of the notification
     * 
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() { return timestamp; }
    
    /**
     * Gets the type of notification
     * 
     * @return the notification type
     */
    public NotificationType getType() { return type; }

    /**
     * Converts notification to string representation for storage
     * 
     * @return string representation of the notification
     */
    @Override
    public String toString() {
        return receiverUsername + ";" + senderUsername + ";" + 
               (imageId != null ? imageId : "") + ";" + 
               timestamp + ";" + type;
    }
}