package com.quackstagram.dao.impl;

import com.quackstagram.dao.interfaces.UserDAO;
import com.quackstagram.exception.DatabaseException;
import com.quackstagram.model.User;
import com.quackstagram.util.DatabaseConnectionManager;
import com.quackstagram.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Database implementation of UserDAO
 */
public class DatabaseUserDAO implements UserDAO {
    private final DatabaseConnectionManager connectionManager;
    
    /**
     * Constructor for DatabaseUserDAO
     */
    public DatabaseUserDAO() {
        this.connectionManager = DatabaseConnectionManager.getInstance();
    }

    /**
     * Finds a user by username
     * 
     * @param username the username to search for
     * @return the User if found, null otherwise
     */
    @Override
    public User findByUsername(String username) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            connection = connectionManager.getConnection();
            String query = "SELECT * FROM Users WHERE username = ?";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            
            return null;
        } catch (SQLException e) {
            System.err.println("Error finding user by username: " + e.getMessage());
            return null;
        } finally {
            closeResources(connection, stmt, rs);
        }
    }

    /**
     * Saves a new user
     * 
     * @param user the user to save
     */
    @Override
    public void save(User user) {
        Connection connection = null;
        PreparedStatement stmt = null;
        
        try {
            connection = connectionManager.getConnection();
            String query = "INSERT INTO Users (username, bio, passwordHash, salt, profileImagePath) " +
                          "VALUES (?, ?, ?, ?, 'img/logos/DACS.png')";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getBio());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getSalt());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
        } finally {
            closeResources(connection, stmt, null);
        }
    }

    /**
     * Updates an existing user
     * 
     * @param user the user to update
     */
    @Override
    public void update(User user) {
        Connection connection = null;
        PreparedStatement stmt = null;
        
        try {
            connection = connectionManager.getConnection();
            String query = "UPDATE Users SET bio = ?, passwordHash = ?, salt = ?, " +
                          "postsCount = ?, followersCount = ?, followingCount = ? " +
                          "WHERE username = ?";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, user.getBio());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getSalt());
            stmt.setInt(4, user.getPostsCount());
            stmt.setInt(5, user.getFollowersCount());
            stmt.setInt(6, user.getFollowingCount());
            stmt.setString(7, user.getUsername());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
        } finally {
            closeResources(connection, stmt, null);
        }
    }

    /**
     * Deletes a user by username
     * 
     * @param username the username of the user to delete
     */
    @Override
    public void delete(String username) {
        Connection connection = null;
        PreparedStatement stmt = null;
        
        try {
            connection = connectionManager.getConnection();
            String query = "DELETE FROM Users WHERE username = ?";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
        } finally {
            closeResources(connection, stmt, null);
        }
    }

    /**
     * Retrieves all users
     * 
     * @return a list of all users
     */
    @Override
    public List<User> getAllUsers() {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<User> users = new ArrayList<>();
        
        try {
            connection = connectionManager.getConnection();
            String query = "SELECT * FROM Users";
            stmt = connection.prepareStatement(query);
            
            rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            
            return users;
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
            return users;
        } finally {
            closeResources(connection, stmt, rs);
        }
    }

    /**
     * Verifies user credentials
     * 
     * @param username the username
     * @param password the password
     * @return true if credentials are valid, false otherwise
     */
    @Override
    public boolean verifyCredentials(String username, String password) {
        User user = findByUsername(username);
        if (user != null) {
            return PasswordUtil.verifyPassword(password, user.getPasswordHash(), user.getSalt());
        }
        return false;
    }
    
    /**
     * Maps a database result to a User object
     * 
     * @param rs the ResultSet containing user data
     * @return a User object
     * @throws SQLException if a database error occurs
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        String username = rs.getString("username");
        String bio = rs.getString("bio");
        String passwordHash = rs.getString("passwordHash");
        String salt = rs.getString("salt");
        
        User user = new User(username, bio, passwordHash, salt);
        user.setPostCount(rs.getInt("postsCount"));
        user.setFollowersCount(rs.getInt("followersCount"));
        user.setFollowingCount(rs.getInt("followingCount"));
        
        return user;
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