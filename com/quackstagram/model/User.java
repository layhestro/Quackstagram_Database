// File: com/quackstagram/model/User.java
package com.quackstagram.model;

import com.quackstagram.util.PasswordUtil;

/**
 * Represents a user on Quackstagram
 */
public class User {
    private final String username;
    private String bio;
    private String passwordHash; // Changed from password to passwordHash
    private String salt; // Added salt for password hashing
    private int postsCount;
    private int followersCount;
    private int followingCount;

    /**
     * Constructor with all fields - for loading existing users
     */
    public User(String username, String bio, String passwordHash, String salt) {
        this.username = username;
        this.bio = bio;
        this.passwordHash = passwordHash;
        this.salt = salt;
        // Initialize counts to 0
        this.postsCount = 0;
        this.followersCount = 0;
        this.followingCount = 0;
    }
    
    /**
     * Constructor for new users - generates salt and hashes password
     */
    public User(String username, String bio, String plainPassword) {
        this.username = username;
        this.bio = bio;
        this.salt = PasswordUtil.generateSalt();
        this.passwordHash = PasswordUtil.hashPassword(plainPassword, this.salt);
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
        this.passwordHash = "";
        this.salt = "";
    }

    // Getter methods for user details
    public String getUsername() { return username; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public int getPostsCount() { return postsCount; }
    public int getFollowersCount() { return followersCount; }
    public int getFollowingCount() { return followingCount; }
    
    // Password verification
    public boolean verifyPassword(String password) {
        return PasswordUtil.verifyPassword(password, passwordHash, salt);
    }

    // Setter methods for counts
    public void setFollowersCount(int followersCount) { this.followersCount = followersCount; }
    public void setFollowingCount(int followingCount) { this.followingCount = followingCount; }
    public void setPostCount(int postCount) { this.postsCount = postCount; }

    /**
     * Convert user to string representation for storage
     * Modified to include salt
     */
    @Override
    public String toString() {
        return username + ":" + passwordHash + ":" + salt + ":" + bio;
    }
}