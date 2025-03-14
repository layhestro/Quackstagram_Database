// File: com/quackstagram/controller/UserController.java
package com.quackstagram.controller;

import com.quackstagram.dao.interfaces.FollowDAO;
import com.quackstagram.dao.interfaces.UserDAO;
import com.quackstagram.model.User;

import java.io.IOException;
import java.util.List;

/**
 * Controller for user-related operations
 */
public class UserController {
    private final UserDAO userDAO;
    private final FollowDAO followDAO;

    /**
     * Constructor for UserController
     * 
     * @param userDAO DAO for user operations
     * @param followDAO DAO for following relationships
     */
    public UserController(UserDAO userDAO, FollowDAO followDAO) {
        this.userDAO = userDAO;
        this.followDAO = followDAO;
    }
    
    /**
     * Get a user by username
     * 
     * @param username the username of the user
     * @return the User if found, null otherwise
     */
    public User getUser(String username) {
        User user = userDAO.findByUsername(username);
        if (user != null) {
            // Update user stats
            updateUserStats(user);
        }
        return user;
    }
    
    /**
     * Register a new user
     * 
     * @param username the username
     * @param password the password
     * @param bio the user bio
     * @return true if registration successful, false otherwise
     */
    public boolean registerUser(String username, String password, String bio) {
        // Check if username already exists
        if (userDAO.findByUsername(username) != null) {
            return false;
        }
        
        // Create and save user
        User user = new User(username, bio, password);
        userDAO.save(user);
        return true;
    }
    
    /**
     * Update user information
     * 
     * @param user the user to update
     */
    public void updateUser(User user) {
        userDAO.update(user);
    }
    
    /**
     * Follow a user
     * 
     * @param follower the username of the follower
     * @param followed the username of the followed user
     */
    public void followUser(String follower, String followed) {
        try {
            // Check if already following
            if (!followDAO.isFollowing(follower, followed)) {
                followDAO.follow(follower, followed);
                
                // Update user stats
                User followerUser = userDAO.findByUsername(follower);
                User followedUser = userDAO.findByUsername(followed);
                
                if (followerUser != null) {
                    followerUser.setFollowingCount(followDAO.getFollowing(follower).size());
                    userDAO.update(followerUser);
                }
                
                if (followedUser != null) {
                    followedUser.setFollowersCount(followDAO.getFollowers(followed).size());
                    userDAO.update(followedUser);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Get followers for a user
     * 
     * @param username the username of the user
     * @return a list of followers usernames
     */
    public List<String> getFollowers(String username) {
        try {
            return followDAO.getFollowers(username);
        } catch (IOException e) {
            e.printStackTrace();
            return List.of(); // Return empty list on error
        }
    }
    
    /**
     * Get users that a user is following
     * 
     * @param username the username of the user
     * @return a list of following usernames
     */
    public List<String> getFollowing(String username) {
        try {
            return followDAO.getFollowing(username);
        } catch (IOException e) {
            e.printStackTrace();
            return List.of(); // Return empty list on error
        }
    }
    
    /**
     * Authenticate a user
     * 
     * @param username the username
     * @param password the password
     * @return the User if authentication successful, null otherwise
     */
    public User authenticate(String username, String password) {
        if (userDAO.verifyCredentials(username, password)) {
            User user = userDAO.findByUsername(username);
            if (user != null) {
                updateUserStats(user);
            }
            return user;
        }
        return null;
    }
    
    /**
     * Update user statistics (post count, followers, following)
     * 
     * @param user the user to update stats for
     */
    private void updateUserStats(User user) {
        try {
            // Update follower and following counts
            int followersCount = followDAO.getFollowers(user.getUsername()).size();
            int followingCount = followDAO.getFollowing(user.getUsername()).size();
            
            user.setFollowersCount(followersCount);
            user.setFollowingCount(followingCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}





