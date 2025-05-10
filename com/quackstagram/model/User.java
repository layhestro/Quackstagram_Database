package com.quackstagram.model;

import com.quackstagram.util.PasswordUtil;

/**
 * Represents a user on Quackstagram
 */
public class User {
    private final String username;
    private String bio;
    private String passwordHash;
    private String salt;
    private int postsCount;
    private int followersCount;
    private int followingCount;

    /**
     * Constructor with all fields - for loading existing users
     * 
     * @param username the username
     * @param bio the user bio
     * @param passwordHash the hashed password
     * @param salt the salt used for password hashing
     */
    public User(String username, String bio, String passwordHash, String salt) {
        this.username = username;
        this.bio = bio;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.postsCount = 0;
        this.followersCount = 0;
        this.followingCount = 0;
    }
    
    /**
     * Constructor for new users - generates salt and hashes password
     * 
     * @param username the username
     * @param bio the user bio
     * @param plainPassword the plain text password
     */
    public User(String username, String bio, String plainPassword) {
        this.username = username;
        this.bio = bio;
        this.salt = PasswordUtil.generateSalt();
        this.passwordHash = PasswordUtil.hashPassword(plainPassword, this.salt);
        this.postsCount = 0;
        this.followersCount = 0;
        this.followingCount = 0;
    }

    /**
     * Constructor with only username
     * 
     * @param username the username
     */
    public User(String username) {
        this.username = username;
        this.bio = "";
        this.passwordHash = "";
        this.salt = "";
    }

    /**
     * Gets the username
     * 
     * @return the username
     */
    public String getUsername() { return username; }
    
    /**
     * Gets the user bio
     * 
     * @return the bio
     */
    public String getBio() { return bio; }
    
    /**
     * Sets the user bio
     * 
     * @param bio the new bio
     */
    public void setBio(String bio) { this.bio = bio; }
    
    /**
     * Gets the posts count
     * 
     * @return the posts count
     */
    public int getPostsCount() { return postsCount; }
    
    /**
     * Gets the followers count
     * 
     * @return the followers count
     */
    public int getFollowersCount() { return followersCount; }
    
    /**
     * Gets the following count
     * 
     * @return the following count
     */
    public int getFollowingCount() { return followingCount; }
    
    /**
     * Gets the password hash
     * 
     * @return the password hash
     */
    public String getPasswordHash() { return passwordHash; }

    /**
     * Gets the salt
     * 
     * @return the salt
     */
    public String getSalt() { return salt; }
    
    /**
     * Verifies a password against the stored hash
     * 
     * @param password the password to verify
     * @return true if the password matches, false otherwise
     */
    public boolean verifyPassword(String password) {
        return PasswordUtil.verifyPassword(password, passwordHash, salt);
    }

    /**
     * Sets the followers count
     * 
     * @param followersCount the new followers count
     */
    public void setFollowersCount(int followersCount) { this.followersCount = followersCount; }
    
    /**
     * Sets the following count
     * 
     * @param followingCount the new following count
     */
    public void setFollowingCount(int followingCount) { this.followingCount = followingCount; }
    
    /**
     * Sets the post count
     * 
     * @param postCount the new post count
     */
    public void setPostCount(int postCount) { this.postsCount = postCount; }

    /**
     * Converts user to string representation for storage
     * 
     * @return string representation of the user
     */
    @Override
    public String toString() {
        return username + ":" + passwordHash + ":" + salt + ":" + bio;
    }
}