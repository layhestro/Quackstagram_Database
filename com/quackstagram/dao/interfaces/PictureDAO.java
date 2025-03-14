// File: com/quackstagram/dao/interfaces/PictureDAO.java
package com.quackstagram.dao.interfaces;

import com.quackstagram.model.Picture;
import java.util.List;


/**
 * Data Access Object interface for Picture entities
 */
public interface PictureDAO {
    /**
     * Find a picture by ID
     * 
     * @param imageId the ID of the picture
     * @return the Picture if found, null otherwise
     */
    Picture findById(String imageId);
    
    /**
     * Find all pictures by a user
     * 
     * @param username the username of the user
     * @return a list of pictures from the user
     */
    List<Picture> findByUsername(String username);
    
    /**
     * Save a new picture
     * 
     * @param picture the picture to save
     */
    void save(Picture picture);
    
    /**
     * Update an existing picture
     * 
     * @param picture the picture to update
     */
    void update(Picture picture);
    
    /**
     * Delete a picture by ID
     * 
     * @param imageId the ID of the picture to delete
     */
    void delete(String imageId);
    
    /**
     * Get pictures from users that a user follows
     * 
     * @param username the username of the user
     * @return a list of pictures from followed users
     */
    List<Picture> getFollowedUsersPictures(String username);
    
    /**
     * Get all pictures
     * 
     * @return a list of all pictures
     */
    List<Picture> getAllPictures();
}