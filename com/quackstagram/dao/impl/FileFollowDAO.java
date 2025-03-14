// File: com/quackstagram/dao/impl/FileFollowDAO.java
package com.quackstagram.dao.impl;

import com.quackstagram.dao.interfaces.FollowDAO;
import com.quackstagram.util.FileUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * File-based implementation of FollowDAO
 */
public class FileFollowDAO implements FollowDAO {
    private final String followingFilePath = "data/following.txt";

    @Override
    public void follow(String follower, String followed) throws IOException {
        // Create the file if it doesn't exist
        FileUtil.createFileIfNotExists(followingFilePath);
        
        boolean found = false;
        List<String> updatedLines = new ArrayList<>();
        
        // Read the current following.txt file
        List<String> lines = FileUtil.readAllLines(followingFilePath);
        
        for (String line : lines) {
            String[] parts = line.split(":");
            if (parts[0].trim().equals(follower)) {
                found = true;
                
                // Check if the user is already being followed
                List<String> followedUsers = new ArrayList<>();
                if (parts.length > 1 && !parts[1].trim().isEmpty()) {
                    followedUsers.addAll(Arrays.asList(parts[1].trim().split(";")));
                }
                
                // Add the new followed user if not already in the list
                if (!followedUsers.contains(followed)) {
                    followedUsers.add(followed);
                }
                
                // Reconstruct the line
                line = follower + ": " + String.join("; ", followedUsers);
            }
            updatedLines.add(line);
        }
        
        // If the follower wasn't found, add a new entry
        if (!found) {
            updatedLines.add(follower + ": " + followed);
        }
        
        // Write the updated content back to the file
        FileUtil.writeLines(followingFilePath, updatedLines, false);
    }

    @Override
    public void unfollow(String follower, String followed) throws IOException {
        // Create the file if it doesn't exist
        FileUtil.createFileIfNotExists(followingFilePath);
        
        List<String> updatedLines = new ArrayList<>();
        
        // Read the current following.txt file
        List<String> lines = FileUtil.readAllLines(followingFilePath);
        
        for (String line : lines) {
            String[] parts = line.split(":");
            if (parts[0].trim().equals(follower)) {
                // Remove the unfollowed user
                List<String> followedUsers = new ArrayList<>();
                if (parts.length > 1 && !parts[1].trim().isEmpty()) {
                    followedUsers = Arrays.stream(parts[1].trim().split(";"))
                                          .map(String::trim)
                                          .filter(u -> !u.equals(followed))
                                          .collect(Collectors.toList());
                }
                
                // Reconstruct the line
                line = follower + ": " + String.join("; ", followedUsers);
            }
            updatedLines.add(line);
        }
        
        // Write the updated content back to the file
        FileUtil.writeLines(followingFilePath, updatedLines, false);
    }

    @Override
    public List<String> getFollowers(String username) throws IOException {
        // Create the file if it doesn't exist
        FileUtil.createFileIfNotExists(followingFilePath);
        
        List<String> followers = new ArrayList<>();
        
        // Read the current following.txt file
        List<String> lines = FileUtil.readAllLines(followingFilePath);
        
        for (String line : lines) {
            String[] parts = line.split(":");
            if (parts.length > 1) {
                String follower = parts[0].trim();
                List<String> followedUsers = Arrays.stream(parts[1].trim().split(";"))
                                                  .map(String::trim)
                                                  .collect(Collectors.toList());
                
                if (followedUsers.contains(username)) {
                    followers.add(follower);
                }
            }
        }
        
        return followers;
    }

    @Override
    public List<String> getFollowing(String username) throws IOException {
        // Create the file if it doesn't exist
        FileUtil.createFileIfNotExists(followingFilePath);
        
        List<String> following = new ArrayList<>();
        
        // Read the current following.txt file
        List<String> lines = FileUtil.readAllLines(followingFilePath);
        
        for (String line : lines) {
            String[] parts = line.split(":");
            if (parts[0].trim().equals(username) && parts.length > 1 && !parts[1].trim().isEmpty()) {
                following = Arrays.stream(parts[1].trim().split(";"))
                                 .map(String::trim)
                                 .collect(Collectors.toList());
                break;
            }
        }
        
        return following;
    }

    @Override
    public boolean isFollowing(String follower, String followed) throws IOException {
        // Create the file if it doesn't exist
        FileUtil.createFileIfNotExists(followingFilePath);
        
        // Read the current following.txt file
        List<String> lines = FileUtil.readAllLines(followingFilePath);
        
        for (String line : lines) {
            String[] parts = line.split(":");
            if (parts[0].trim().equals(follower) && parts.length > 1) {
                List<String> followedUsers = Arrays.stream(parts[1].trim().split(";"))
                                                  .map(String::trim)
                                                  .collect(Collectors.toList());
                return followedUsers.contains(followed);
            }
        }
        
        return false;
    }
}