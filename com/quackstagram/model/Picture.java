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
     * Add a comment to the picture
     */
    public void addComment(String comment) {
        comments.add(comment);
    }

    /**
     * Increment likes count
     */
    public void like() {
        likesCount++;
    }

    // Getter methods
    public String getImageId() { return imageId; }
    public String getUsername() { return username; }
    public String getImagePath() { return imagePath; }
    public String getCaption() { return caption; }
    public int getLikesCount() { return likesCount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public List<String> getComments() { return comments; }
}

