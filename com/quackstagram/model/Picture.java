package com.quackstagram.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a picture on Quackstagram
 */
public class Picture {
    private final String imageId;
    private final String username;
    private final String imagePath;
    private final String caption;
    private int likesCount;
    private final LocalDateTime timestamp;
    private final List<String> comments;

    /**
     * Constructor with all required fields
     * 
     * @param imageId the unique ID of the image
     * @param username the username of the owner
     * @param imagePath the file path to the image
     * @param caption the caption for the image
     * @param timestamp the timestamp when the image was posted
     */
    public Picture(String imageId, String username, String imagePath, String caption, LocalDateTime timestamp) {
        this.imageId = imageId;
        this.username = username;
        this.imagePath = imagePath;
        this.caption = caption;
        this.timestamp = timestamp;
        this.likesCount = 0;
        this.comments = new ArrayList<>();
    }

    /**
     * Adds a comment to the picture
     * 
     * @param comment the comment to add
     */
    public void addComment(String comment) {
        comments.add(comment);
    }

    /**
     * Increments the like count
     */
    public void like() {
        likesCount++;
    }

    /**
     * Gets the image ID
     * 
     * @return the image ID
     */
    public String getImageId() { return imageId; }
    
    /**
     * Gets the username of the owner
     * 
     * @return the username
     */
    public String getUsername() { return username; }
    
    /**
     * Gets the file path to the image
     * 
     * @return the image path
     */
    public String getImagePath() { return imagePath; }
    
    /**
     * Gets the caption
     * 
     * @return the caption
     */
    public String getCaption() { return caption; }
    
    /**
     * Gets the number of likes
     * 
     * @return the likes count
     */
    public int getLikesCount() { return likesCount; }
    
    /**
     * Gets the timestamp when the image was posted
     * 
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() { return timestamp; }
    
    /**
     * Gets the list of comments
     * 
     * @return the comments list
     */
    public List<String> getComments() { return comments; }
}