// File: com/quackstagram/dao/interfaces/UserDAO.java
package com.quackstagram.dao.interfaces;

import com.quackstagram.model.User;
import java.util.List;

/**
 * Data Access Object interface for User entities
 */
public interface UserDAO {
    /**
     * Find a user by username
     * 
     * @param username the username to search for
     * @return the User if found, null otherwise
     */
    User findByUsername(String username);
    
    /**
     * Save a new user
     * 
     * @param user the user to save
     */
    void save(User user);
    
    /**
     * Update an existing user
     * 
     * @param user the user to update
     */
    void update(User user);
    
    /**
     * Delete a user by username
     * 
     * @param username the username of the user to delete
     */
    void delete(String username);
    
    /**
     * Get all users
     * 
     * @return a list of all users
     */
    List<User> getAllUsers();
    
    /**
     * Verify user credentials
     * 
     * @param username the username
     * @param password the password
     * @return true if credentials are valid, false otherwise
     */
    boolean verifyCredentials(String username, String password);
}







