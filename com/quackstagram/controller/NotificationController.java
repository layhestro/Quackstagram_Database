package com.quackstagram.controller;

import com.quackstagram.dao.interfaces.NotificationDAO;
import com.quackstagram.model.Notification;
import com.quackstagram.model.NotificationType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for notification-related operations
 */
public class NotificationController {
    private final NotificationDAO notificationDAO;
    
    /**
     * Constructor for NotificationController
     * 
     * @param notificationDAO DAO for notification operations
     */
    public NotificationController(NotificationDAO notificationDAO) {
        this.notificationDAO = notificationDAO;
    }
    
    /**
     * Retrieves all notifications for a specific user
     * 
     * @param username the username of the user
     * @return a list of notifications for the user
     */
    public List<Notification> getNotifications(String username) {
        return notificationDAO.findByReceiver(username);
    }
    
    /**
     * Creates a notification when a user likes another user's picture
     * 
     * @param sender the username of the sender
     * @param receiver the username of the receiver
     * @param imageId the ID of the picture
     */
    public void createLikeNotification(String sender, String receiver, String imageId) {
        if (sender.equals(receiver)) {
            return;
        }
        
        Notification notification = new Notification(
            receiver,
            sender,
            imageId,
            LocalDateTime.now(),
            NotificationType.LIKE
        );
        
        notificationDAO.save(notification);
    }
    
    /**
     * Creates a notification when a user follows another user
     * 
     * @param follower the username of the follower
     * @param followed the username of the followed user
     */
    public void createFollowNotification(String follower, String followed) {
        if (follower.equals(followed)) {
            return;
        }
        
        Notification notification = new Notification(
            followed,
            follower,
            null,
            LocalDateTime.now(),
            NotificationType.FOLLOW
        );
        
        notificationDAO.save(notification);
    }
}