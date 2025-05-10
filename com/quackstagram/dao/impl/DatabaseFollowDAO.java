package com.quackstagram.dao.impl;

import com.quackstagram.dao.interfaces.FollowDAO;
import com.quackstagram.exception.DatabaseException;
import com.quackstagram.util.DatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Database implementation of FollowDAO
 */
public class DatabaseFollowDAO implements FollowDAO {
    private final DatabaseConnectionManager connectionManager;
    
    /**
     * Constructor for DatabaseFollowDAO
     */
    public DatabaseFollowDAO() {
        this.connectionManager = DatabaseConnectionManager.getInstance();
    }

    /**
     * Creates a following relationship between users
     * 
     * @param follower the username of the follower
     * @param followed the username of the followed user
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void follow(String follower, String followed) throws IOException {
        Connection connection = null;
        PreparedStatement stmt = null;
        
        try {
            connection = connectionManager.getConnection();
            String query = "INSERT INTO Follows (follower, followed) VALUES (?, ?)";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, follower);
            stmt.setString(2, followed);
            
            stmt.executeUpdate();
            
            // The trigger will handle updating follower/following counts and creating notifications
        } catch (SQLException e) {
            System.err.println("Error creating follow relationship: " + e.getMessage());
            throw new IOException("Database error when following user", e);
        } finally {
            closeResources(connection, stmt, null);
        }
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
        Connection connection = null;
        PreparedStatement stmt = null;
        
        try {
            connection = connectionManager.getConnection();
            
            // Start transaction
            connection.setAutoCommit(false);
            
            // Delete the follow relationship
            String deleteQuery = "DELETE FROM Follows WHERE follower = ? AND followed = ?";
            stmt = connection.prepareStatement(deleteQuery);
            stmt.setString(1, follower);
            stmt.setString(2, followed);
            stmt.executeUpdate();
            stmt.close();
            
            // Update follower count for the followed user
            String updateFollowedQuery = "UPDATE Users SET followersCount = followersCount - 1 " +
                                       "WHERE username = ? AND followersCount > 0";
            stmt = connection.prepareStatement(updateFollowedQuery);
            stmt.setString(1, followed);
            stmt.executeUpdate();
            stmt.close();
            
            // Update following count for the follower user
            String updateFollowerQuery = "UPDATE Users SET followingCount = followingCount - 1 " +
                                       "WHERE username = ? AND followingCount > 0";
            stmt = connection.prepareStatement(updateFollowerQuery);
            stmt.setString(1, follower);
            stmt.executeUpdate();
            
            // Commit transaction
            connection.commit();
            
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
            System.err.println("Error removing follow relationship: " + e.getMessage());
            throw new IOException("Database error when unfollowing user", e);
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
            closeResources(connection, stmt, null);
        }
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
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<String> followers = new ArrayList<>();
        
        try {
            connection = connectionManager.getConnection();
            String query = "SELECT follower FROM Follows WHERE followed = ?";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            
            rs = stmt.executeQuery();
            while (rs.next()) {
                followers.add(rs.getString("follower"));
            }
            
            return followers;
        } catch (SQLException e) {
            System.err.println("Error getting followers: " + e.getMessage());
            throw new IOException("Database error when retrieving followers", e);
        } finally {
            closeResources(connection, stmt, rs);
        }
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
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<String> following = new ArrayList<>();
        
        try {
            connection = connectionManager.getConnection();
            String query = "SELECT followed FROM Follows WHERE follower = ?";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            
            rs = stmt.executeQuery();
            while (rs.next()) {
                following.add(rs.getString("followed"));
            }
            
            return following;
        } catch (SQLException e) {
            System.err.println("Error getting following: " + e.getMessage());
            throw new IOException("Database error when retrieving following", e);
        } finally {
            closeResources(connection, stmt, rs);
        }
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
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            connection = connectionManager.getConnection();
            String query = "SELECT 1 FROM Follows WHERE follower = ? AND followed = ?";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, follower);
            stmt.setString(2, followed);
            
            rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error checking follow relationship: " + e.getMessage());
            throw new IOException("Database error when checking follow status", e);
        } finally {
            closeResources(connection, stmt, rs);
        }
    }
    
    /**
     * Closes database resources
     * 
     * @param connection the database connection
     * @param stmt the prepared statement
     * @param rs the result set
     */
    private void closeResources(Connection connection, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (connection != null) connectionManager.releaseConnection(connection);
        } catch (SQLException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }
}