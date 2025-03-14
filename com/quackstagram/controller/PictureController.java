// File: com/quackstagram/controller/PictureController.java
package com.quackstagram.controller;

import com.quackstagram.dao.interfaces.PictureDAO;
import com.quackstagram.dao.interfaces.UserDAO;
import com.quackstagram.model.Picture;
import com.quackstagram.model.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for picture-related operations
 */
public class PictureController {
    private final PictureDAO pictureDAO;
    private final UserDAO userDAO;
    private final NotificationController notificationController;
    private final String uploadedImagesPath = "img/uploaded/";

    /**
     * Constructor for PictureController
     * 
     * @param pictureDAO DAO for picture operations
     * @param userDAO DAO for user operations
     * @param notificationController controller for notification operations
     */
    public PictureController(PictureDAO pictureDAO, UserDAO userDAO, NotificationController notificationController) {
        this.pictureDAO = pictureDAO;
        this.userDAO = userDAO;
        this.notificationController = notificationController;
    }
    
    /**
     * Get pictures for a specific user
     * 
     * @param username the username of the user
     * @return a list of pictures from the user
     */
    public List<Picture> getUserPictures(String username) {
        return pictureDAO.findByUsername(username);
    }
    
    /**
     * Get pictures for the user's home feed (from followed users)
     * 
     * @param username the username of the user
     * @return a list of pictures from followed users
     */
    public List<Picture> getHomeFeedPictures(String username) {
        return pictureDAO.getFollowedUsersPictures(username);
    }
    
    /**
     * Get all pictures for explore view
     * 
     * @return a list of all pictures
     */
    public List<Picture> getAllPictures() {
        return pictureDAO.getAllPictures();
    }
    
    /**
     * Save a new picture
     * 
     * @param username the username of the user posting the picture
     * @param imageFile the image file
     * @param caption the caption for the picture
     * @return true if save successful, false otherwise
     */
    public boolean savePicture(String username, File imageFile, String caption) {
        try {
            // Generate image ID
            String imageId = username + "_" + getNextImageId(username);
            
            // Create picture object
            Picture picture = new Picture(imageId, username, uploadedImagesPath + imageId + ".png", 
                                         caption, LocalDateTime.now());
            
            // Save image file
            Path destPath = Paths.get(uploadedImagesPath, imageId + ".png");
            Files.copy(imageFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
            
            // Save picture metadata
            pictureDAO.save(picture);
            
            // Update user post count
            User user = userDAO.findByUsername(username);
            if (user != null) {
                user.setPostCount(pictureDAO.findByUsername(username).size());
                userDAO.update(user);
            }
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Like a picture
     * 
     * @param username the username of the user liking the picture
     * @param imageId the ID of the picture being liked
     */
    public void likePicture(String username, String imageId) {
        Picture picture = pictureDAO.findById(imageId);
        if (picture != null) {
            // Increment likes
            picture.like();
            pictureDAO.update(picture);
            
            // Create notification
            notificationController.createLikeNotification(username, picture.getUsername(), imageId);
        }
    }
    
    /**
     * Get the next available image ID for a user
     * 
     * @param username the username of the user
     * @return the next available ID
     * @throws IOException if an I/O error occurs
     */
    private int getNextImageId(String username) throws IOException {
        Path storageDir = Paths.get(uploadedImagesPath);
        if (!Files.exists(storageDir)) {
            Files.createDirectories(storageDir);
        }
        
        int maxId = 0;
        try (var stream = Files.newDirectoryStream(storageDir, username + "_*")) {
            for (Path path : stream) {
                String fileName = path.getFileName().toString();
                int idEndIndex = fileName.lastIndexOf('.');
                if (idEndIndex != -1) {
                    String idStr = fileName.substring(username.length() + 1, idEndIndex);
                    try {
                        int id = Integer.parseInt(idStr);
                        if (id > maxId) {
                            maxId = id;
                        }
                    } catch (NumberFormatException ex) {
                        // Ignore filenames that do not have a valid numeric ID
                    }
                }
            }
        }
        return maxId + 1;
    }
}