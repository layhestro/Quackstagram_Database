package com.quackstagram.dao.impl;

import com.quackstagram.dao.interfaces.UserDAO;
import com.quackstagram.model.User;
import com.quackstagram.util.DatabaseConnectionManager;
import com.quackstagram.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUserDAO implements UserDAO {
    private final DatabaseConnectionManager connectionManager;
    
    public DatabaseUserDAO() {
        this.connectionManager = DatabaseConnectionManager.getInstance();
    }

    @Override
    public User findByUsername(String username) {
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Users WHERE username = ?")) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String bio = rs.getString("bio");
                String passwordHash = rs.getString("passwordHash");
                String salt = rs.getString("salt");
                
                User user = new User(username, bio, passwordHash, salt);
                
                // Count posts
                try (PreparedStatement countStmt = conn.prepareStatement(
                        "SELECT COUNT(*) FROM Pictures WHERE username = ?")) {
                    countStmt.setString(1, username);
                    ResultSet countRs = countStmt.executeQuery();
                    if (countRs.next()) {
                        user.setPostCount(countRs.getInt(1));
                    }
                }
                
                // Count followers
                try (PreparedStatement countStmt = conn.prepareStatement(
                        "SELECT COUNT(*) FROM Follows WHERE followed = ?")) {
                    countStmt.setString(1, username);
                    ResultSet countRs = countStmt.executeQuery();
                    if (countRs.next()) {
                        user.setFollowersCount(countRs.getInt(1));
                    }
                }
                
                // Count following
                try (PreparedStatement countStmt = conn.prepareStatement(
                        "SELECT COUNT(*) FROM Follows WHERE follower = ?")) {
                    countStmt.setString(1, username);
                    ResultSet countRs = countStmt.executeQuery();
                    if (countRs.next()) {
                        user.setFollowingCount(countRs.getInt(1));
                    }
                }
                
                return user;
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Error finding user: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void save(User user) {
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO Users (username, bio, passwordHash, salt) VALUES (?, ?, ?, ?)")) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getBio());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getSalt());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
        }
    }

    @Override
    public void update(User user) {
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE Users SET bio = ?, passwordHash = ?, salt = ? WHERE username = ?")) {
            
            stmt.setString(1, user.getBio());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getSalt());
            stmt.setString(4, user.getUsername());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
        }
    }

    @Override
    public void delete(String username) {
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Users WHERE username = ?")) {
            
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        
        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Users")) {
            
            while (rs.next()) {
                String username = rs.getString("username");
                String bio = rs.getString("bio");
                String passwordHash = rs.getString("passwordHash");
                String salt = rs.getString("salt");
                
                users.add(new User(username, bio, passwordHash, salt));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
        }
        
        return users;
    }

    @Override
    public boolean verifyCredentials(String username, String password) {
        User user = findByUsername(username);
        if (user != null) {
            return PasswordUtil.verifyPassword(password, user.getPasswordHash(), user.getSalt());
        }
        return false;
    }
}