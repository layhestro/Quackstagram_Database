package com.quackstagram.dao.interfaces;

import com.quackstagram.model.Notification;
import java.util.List;

/**
 * Data Access Object interface for Notification entities
 */
public interface NotificationDAO {
    /**
     * Retrieves all notifications for a receiver
     * 
     * @param username the username of the receiver
     * @return a list of notifications for the receiver
     */
    List<Notification> findByReceiver(String username);
    
    /**
     * Saves a new notification
     * 
     * @param notification the notification to save
     */
    void save(Notification notification);
    
    /**
     * Deletes a notification by ID
     * 
     * @param id the ID of the notification to delete
     */
    void delete(String id);
}