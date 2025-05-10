package com.quackstagram.dao.impl;

import com.quackstagram.dao.interfaces.FollowDAO;
import com.quackstagram.util.DatabaseConnectionManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseFollowDAO implements FollowDAO {
    private final DatabaseConnectionManager connectionManager;
    
    public DatabaseFollowDAO() {
        this.connectionManager = DatabaseConnectionManager.getInstance();
    }

    @Override
    public void follow(String follower, String followed) throws IOException {
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO Follows (follower, followed) VALUES (?, ?)")) {
            
            stmt.setString(1, follower);
            stmt.setString(2, followed);
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IOException("Database error when following user: " + e.getMessage());
        }
    }

    @Override
    public void unfollow(String follower, String followed) throws IOException {
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM Follows WHERE follower = ? AND followed = ?")) {
            
            stmt.setString(1, follower);
            stmt.setString(2, followed);
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IOException("Database error when unfollowing user: " + e.getMessage());
        }
    }

    @Override
    public List<String> getFollowers(String username) throws IOException {
        List<String> followers = new ArrayList<>();
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT follower FROM Follows WHERE followed = ?")) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                followers.add(rs.getString("follower"));
            }
        } catch (SQLException e) {
            throw new IOException("Database error when retrieving followers: " + e.getMessage());
        }
        
        return followers;
    }

    @Override
    public List<String> getFollowing(String username) throws IOException {
        List<String> following = new ArrayList<>();
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT followed FROM Follows WHERE follower = ?")) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                following.add(rs.getString("followed"));
            }
        } catch (SQLException e) {
            throw new IOException("Database error when retrieving following: " + e.getMessage());
        }
        
        return following;
    }

    @Override
    public boolean isFollowing(String follower, String followed) throws IOException {
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT 1 FROM Follows WHERE follower = ? AND followed = ?")) {
            
            stmt.setString(1, follower);
            stmt.setString(2, followed);
            
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new IOException("Database error when checking follow status: " + e.getMessage());
        }
    }
}