package com.quackstagram.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user on Quackstagram
 */
public class User {
    private final String username;
    private String bio;
    private String password;
    private int postsCount;
    private int followersCount;
    private int followingCount;

    /**
     * Constructor with all fields
     */
    public User(String username, String bio, String password) {
        this.username = username;
        this.bio = bio;
        this.password = password;
        // Initialize counts to 0
        this.postsCount = 0;
        this.followersCount = 0;
        this.followingCount = 0;
    }

    /**
     * Constructor with only username
     */
    public User(String username) {
        this.username = username;
        this.bio = "";
        this.password = "";
    }

    // Getter methods for user details
    public String getUsername() { return username; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public int getPostsCount() { return postsCount; }
    public int getFollowersCount() { return followersCount; }
    public int getFollowingCount() { return followingCount; }

    // Setter methods for counts
    public void setFollowersCount(int followersCount) { this.followersCount = followersCount; }
    public void setFollowingCount(int followingCount) { this.followingCount = followingCount; }
    public void setPostCount(int postCount) { this.postsCount = postCount; }

    /**
     * Convert user to string representation for storage
     */
    @Override
    public String toString() {
        return username + ":" + password + ":" + bio;
    }
}