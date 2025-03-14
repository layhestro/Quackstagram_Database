// File: com/quackstagram/dao/interfaces/FollowDAO.java
package com.quackstagram.dao.interfaces;

import java.io.IOException;
import java.util.List;
/**
 * Data Access Object interface for following relationships
 */
public interface FollowDAO {
    /**
     * Follow a user
     * 
     * @param follower the username of the follower
     * @param followed the username of the followed user
     * @throws IOException if an I/O error occurs
     */
    void follow(String follower, String followed) throws IOException;
    
    /**
     * Unfollow a user
     * 
     * @param follower the username of the follower
     * @param followed the username of the followed user
     * @throws IOException if an I/O error occurs
     */
    void unfollow(String follower, String followed) throws IOException;
    
    /**
     * Get all followers of a user
     * 
     * @param username the username of the user
     * @return a list of usernames of followers
     * @throws IOException if an I/O error occurs
     */
    List<String> getFollowers(String username) throws IOException;
    
    /**
     * Get all users that a user is following
     * 
     * @param username the username of the user
     * @return a list of usernames of followed users
     * @throws IOException if an I/O error occurs
     */
    List<String> getFollowing(String username) throws IOException;
    
    /**
     * Check if a user is following another
     * 
     * @param follower the username of the follower
     * @param followed the username of the followed user
     * @return true if follower is following followed, false otherwise
     * @throws IOException if an I/O error occurs
     */
    boolean isFollowing(String follower, String followed) throws IOException;
}