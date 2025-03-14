// File: com/quackstagram/dao/impl/FilePictureDAO.java
package com.quackstagram.dao.impl;

import com.quackstagram.dao.interfaces.PictureDAO;
import com.quackstagram.model.Picture;
import com.quackstagram.util.FileUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * File-based implementation of PictureDAO
 */
public class FilePictureDAO implements PictureDAO {
    private final String imageDetailsFilePath = "img/image_details.txt";
    private final String uploadedImagesPath = "img/uploaded/";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Picture findById(String imageId) {
        try {
            // Create the file if it doesn't exist
            FileUtil.createFileIfNotExists(imageDetailsFilePath);
            
            // Find line with image ID
            List<String> lines = FileUtil.readMatchingLines(imageDetailsFilePath, 
                    line -> line.contains("ImageID: " + imageId));
            
            if (!lines.isEmpty()) {
                return parsePictureFromLine(lines.get(0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Picture> findByUsername(String username) {
        List<Picture> pictures = new ArrayList<>();
        try {
            // Create the file if it doesn't exist
            FileUtil.createFileIfNotExists(imageDetailsFilePath);
            
            // Find lines with username
            List<String> lines = FileUtil.readMatchingLines(imageDetailsFilePath, 
                    line -> line.contains("Username: " + username));
            
            // Parse each line into a Picture object
            for (String line : lines) {
                pictures.add(parsePictureFromLine(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pictures;
    }

    @Override
    public void save(Picture picture) {
        try {
            // Create the file if it doesn't exist
            FileUtil.createFileIfNotExists(imageDetailsFilePath);
            
            // Format the line to save
            String line = String.format("ImageID: %s, Username: %s, Bio: %s, Timestamp: %s, Likes: %d",
                    picture.getImageId(),
                    picture.getUsername(),
                    picture.getCaption(),
                    picture.getTimestamp().format(formatter),
                    picture.getLikesCount());
            
            // Append to file
            FileUtil.appendLine(imageDetailsFilePath, line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Picture picture) {
        try {
            // Create the file if it doesn't exist
            FileUtil.createFileIfNotExists(imageDetailsFilePath);
            
            // Update the file
            FileUtil.updateLines(imageDetailsFilePath, 
                    line -> line.contains("ImageID: " + picture.getImageId()), 
                    line -> String.format("ImageID: %s, Username: %s, Bio: %s, Timestamp: %s, Likes: %d",
                            picture.getImageId(),
                            picture.getUsername(),
                            picture.getCaption(),
                            picture.getTimestamp().format(formatter),
                            picture.getLikesCount()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String imageId) {
        try {
            // Create the file if it doesn't exist
            FileUtil.createFileIfNotExists(imageDetailsFilePath);
            
            // Read all lines except the one to delete
            List<String> lines = FileUtil.readMatchingLines(imageDetailsFilePath, 
                    line -> !line.contains("ImageID: " + imageId));
            
            // Write back to file
            FileUtil.writeLines(imageDetailsFilePath, lines, false);
            
            // Delete the image file
            FileUtil.deleteFile(uploadedImagesPath + imageId + ".png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Picture> getFollowedUsersPictures(String username) {
        List<Picture> pictures = new ArrayList<>();
        try {
            // Get list of followed users
            List<String> followedUsers = new ArrayList<>();
            
            // Create following file if it doesn't exist
            FileUtil.createFileIfNotExists("data/following.txt");
            
            // Find line with username
            List<String> followingLines = FileUtil.readMatchingLines("data/following.txt", 
                    line -> line.startsWith(username + ":"));
            
            if (!followingLines.isEmpty()) {
                String[] parts = followingLines.get(0).split(":");
                if (parts.length > 1 && !parts[1].trim().isEmpty()) {
                    String[] users = parts[1].trim().split(";");
                    for (String user : users) {
                        followedUsers.add(user.trim());
                    }
                }
            }
            
            // Get pictures for each followed user
            for (String followedUser : followedUsers) {
                pictures.addAll(findByUsername(followedUser));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pictures;
    }

    @Override
    public List<Picture> getAllPictures() {
        List<Picture> pictures = new ArrayList<>();
        try {
            // Create the file if it doesn't exist
            FileUtil.createFileIfNotExists(imageDetailsFilePath);
            
            // Read all lines
            List<String> lines = FileUtil.readAllLines(imageDetailsFilePath);
            
            // Parse each line into a Picture object
            for (String line : lines) {
                pictures.add(parsePictureFromLine(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pictures;
    }

    /**
     * Parse a line from the image details file into a Picture object
     * 
     * @param line the line to parse
     * @return the Picture object
     */
    private Picture parsePictureFromLine(String line) {
        String[] parts = line.split(", ");
        
        String imageId = parts[0].split(": ")[1];
        String username = parts[1].split(": ")[1];
        String caption = parts[2].split(": ")[1];
        LocalDateTime timestamp = LocalDateTime.parse(parts[3].split(": ")[1], formatter);
        int likes = Integer.parseInt(parts[4].split(": ")[1]);
        
        String imagePath = uploadedImagesPath + imageId + ".png";
        
        Picture picture = new Picture(imageId, username, imagePath, caption, timestamp);
        // Set the likes count
        for (int i = 0; i < likes; i++) {
            picture.like();
        }
        
        return picture;
    }
}