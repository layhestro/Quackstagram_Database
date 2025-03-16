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

    /**
     * Finds a picture by its ID
     * 
     * @param imageId the ID of the picture
     * @return the Picture if found, null otherwise
     */
    @Override
    public Picture findById(String imageId) {
        try {
            FileUtil.createFileIfNotExists(imageDetailsFilePath);
            
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

    /**
     * Finds all pictures by a specific user
     * 
     * @param username the username of the user
     * @return a list of pictures from the user
     */
    @Override
    public List<Picture> findByUsername(String username) {
        List<Picture> pictures = new ArrayList<>();
        try {
            FileUtil.createFileIfNotExists(imageDetailsFilePath);
            
            List<String> lines = FileUtil.readMatchingLines(imageDetailsFilePath, 
                    line -> line.contains("Username: " + username));
            
            for (String line : lines) {
                pictures.add(parsePictureFromLine(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pictures;
    }

    /**
     * Saves a new picture
     * 
     * @param picture the picture to save
     */
    @Override
    public void save(Picture picture) {
        try {
            FileUtil.createFileIfNotExists(imageDetailsFilePath);
            
            String line = String.format("ImageID: %s, Username: %s, Bio: %s, Timestamp: %s, Likes: %d",
                    picture.getImageId(),
                    picture.getUsername(),
                    picture.getCaption(),
                    picture.getTimestamp().format(formatter),
                    picture.getLikesCount());
            
            FileUtil.appendLine(imageDetailsFilePath, line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates an existing picture
     * 
     * @param picture the picture to update
     */
    @Override
    public void update(Picture picture) {
        try {
            FileUtil.createFileIfNotExists(imageDetailsFilePath);
            
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

    /**
     * Deletes a picture by ID
     * 
     * @param imageId the ID of the picture to delete
     */
    @Override
    public void delete(String imageId) {
        try {
            FileUtil.createFileIfNotExists(imageDetailsFilePath);
            
            List<String> lines = FileUtil.readMatchingLines(imageDetailsFilePath, 
                    line -> !line.contains("ImageID: " + imageId));
            
            FileUtil.writeLines(imageDetailsFilePath, lines, false);
            
            FileUtil.deleteFile(uploadedImagesPath + imageId + ".png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves pictures from users that a user follows
     * 
     * @param username the username of the user
     * @return a list of pictures from followed users
     */
    @Override
    public List<Picture> getFollowedUsersPictures(String username) {
        List<Picture> pictures = new ArrayList<>();
        try {
            List<String> followedUsers = new ArrayList<>();
            
            FileUtil.createFileIfNotExists("data/following.txt");
            
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
            
            for (String followedUser : followedUsers) {
                pictures.addAll(findByUsername(followedUser));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pictures;
    }

    /**
     * Retrieves all pictures
     * 
     * @return a list of all pictures
     */
    @Override
    public List<Picture> getAllPictures() {
        List<Picture> pictures = new ArrayList<>();
        try {
            FileUtil.createFileIfNotExists(imageDetailsFilePath);
            
            List<String> lines = FileUtil.readAllLines(imageDetailsFilePath);
            
            for (String line : lines) {
                pictures.add(parsePictureFromLine(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pictures;
    }

    /**
     * Parses a line from the image details file into a Picture object
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
        for (int i = 0; i < likes; i++) {
            picture.like();
        }
        
        return picture;
    }
}