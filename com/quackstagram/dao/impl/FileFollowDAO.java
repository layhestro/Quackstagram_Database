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

    /**
     * Creates a following relationship between users
     * 
     * @param follower the username of the follower
     * @param followed the username of the followed user
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void follow(String follower, String followed) throws IOException {
        FileUtil.createFileIfNotExists(followingFilePath);
        
        boolean found = false;
        List<String> updatedLines = new ArrayList<>();
        
        List<String> lines = FileUtil.readAllLines(followingFilePath);
        
        for (String line : lines) {
            String[] parts = line.split(":");
            if (parts[0].trim().equals(follower)) {
                found = true;
                
                List<String> followedUsers = new ArrayList<>();
                if (parts.length > 1 && !parts[1].trim().isEmpty()) {
                    followedUsers.addAll(Arrays.asList(parts[1].trim().split(";")));
                }
                
                if (!followedUsers.contains(followed)) {
                    followedUsers.add(followed);
                }
                
                line = follower + ": " + String.join("; ", followedUsers);
            }
            updatedLines.add(line);
        }
        
        if (!found) {
            updatedLines.add(follower + ": " + followed);
        }
        
        FileUtil.writeLines(followingFilePath, updatedLines, false);
    }

    /**
     * Removes a following relationship between users
     * 
     * @param follower the username of the follower
     * @param followed the username of the followed user
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void unfollow(String follower, String followed) throws IOException {
        FileUtil.createFileIfNotExists(followingFilePath);
        
        List<String> updatedLines = new ArrayList<>();
        
        List<String> lines = FileUtil.readAllLines(followingFilePath);
        
        for (String line : lines) {
            String[] parts = line.split(":");
            if (parts[0].trim().equals(follower)) {
                List<String> followedUsers = new ArrayList<>();
                if (parts.length > 1 && !parts[1].trim().isEmpty()) {
                    followedUsers = Arrays.stream(parts[1].trim().split(";"))
                                          .map(String::trim)
                                          .filter(u -> !u.equals(followed))
                                          .collect(Collectors.toList());
                }
                
                line = follower + ": " + String.join("; ", followedUsers);
            }
            updatedLines.add(line);
        }
        
        FileUtil.writeLines(followingFilePath, updatedLines, false);
    }

    /**
     * Retrieves all followers of a user
     * 
     * @param username the username of the user
     * @return a list of usernames of followers
     * @throws IOException if an I/O error occurs
     */
    @Override
    public List<String> getFollowers(String username) throws IOException {
        FileUtil.createFileIfNotExists(followingFilePath);
        
        List<String> followers = new ArrayList<>();
        
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

    /**
     * Retrieves all users that a user is following
     * 
     * @param username the username of the user
     * @return a list of usernames of followed users
     * @throws IOException if an I/O error occurs
     */
    @Override
    public List<String> getFollowing(String username) throws IOException {
        FileUtil.createFileIfNotExists(followingFilePath);
        
        List<String> following = new ArrayList<>();
        
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

    /**
     * Checks if a user is following another user
     * 
     * @param follower the username of the follower
     * @param followed the username of the followed user
     * @return true if follower is following followed, false otherwise
     * @throws IOException if an I/O error occurs
     */
    @Override
    public boolean isFollowing(String follower, String followed) throws IOException {
        FileUtil.createFileIfNotExists(followingFilePath);
        
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