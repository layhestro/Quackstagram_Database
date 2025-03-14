// File: com/quackstagram/dao/interfaces/NotificationDAO.java
package com.quackstagram.dao.interfaces;

import com.quackstagram.model.Notification;
import java.util.List;

/**
 * Data Access Object interface for Notification entities
 */
public interface NotificationDAO {
    /**
     * Find all notifications for a receiver
     * 
     * @param username the username of the receiver
     * @return a list of notifications for the receiver
     */
    List<Notification> findByReceiver(String username);
    
    /**
     * Save a new notification
     * 
     * @param notification the notification to save
     */
    void save(Notification notification);
    
    /**
     * Delete a notification by ID
     * 
     * @param id the ID of the notification to delete
     */
    void delete(String id);
}