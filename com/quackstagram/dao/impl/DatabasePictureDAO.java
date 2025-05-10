package com.quackstagram.dao.impl;

import com.quackstagram.dao.interfaces.PictureDAO;
import com.quackstagram.model.Picture;
import com.quackstagram.util.DatabaseConnectionManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabasePictureDAO implements PictureDAO {
    private final DatabaseConnectionManager connectionManager;
    
    public DatabasePictureDAO() {
        this.connectionManager = DatabaseConnectionManager.getInstance();
    }

    @Override
    public Picture findById(String imageId) {
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Pictures WHERE imageId = ?")) {
            
            stmt.setString(1, imageId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return createPictureFromResultSet(rs);
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Error finding picture: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Picture> findByUsername(String username) {
        List<Picture> pictures = new ArrayList<>();
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM Pictures WHERE username = ? ORDER BY timestamp DESC")) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                pictures.add(createPictureFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding pictures by username: " + e.getMessage());
        }
        
        return pictures;
    }

    @Override
    public void save(Picture picture) {
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO Pictures (imageId, username, imagePath, caption, timestamp) VALUES (?, ?, ?, ?, ?)")) {
            
            stmt.setString(1, picture.getImageId());
            stmt.setString(2, picture.getUsername());
            stmt.setString(3, picture.getImagePath());
            stmt.setString(4, picture.getCaption());
            stmt.setTimestamp(5, Timestamp.valueOf(picture.getTimestamp()));
            
            stmt.executeUpdate();
            
            // Add likes
            addLikesToPicture(conn, picture);
        } catch (SQLException e) {
            System.err.println("Error saving picture: " + e.getMessage());
        }
    }

    @Override
    public void update(Picture picture) {
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE Pictures SET caption = ? WHERE imageId = ?")) {
            
            stmt.setString(1, picture.getCaption());
            stmt.setString(2, picture.getImageId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating picture: " + e.getMessage());
        }
    }

    @Override
    public void delete(String imageId) {
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement deleteNotifs = conn.prepareStatement("DELETE FROM Notifications WHERE imageId = ?");
             PreparedStatement deleteLikes = conn.prepareStatement("DELETE FROM Likes WHERE imageId = ?");
             PreparedStatement deleteComments = conn.prepareStatement("DELETE FROM Comments WHERE imageId = ?");
             PreparedStatement deletePicture = conn.prepareStatement("DELETE FROM Pictures WHERE imageId = ?")) {
            
            conn.setAutoCommit(false);
            
            deleteNotifs.setString(1, imageId);
            deleteNotifs.executeUpdate();
            
            deleteLikes.setString(1, imageId);
            deleteLikes.executeUpdate();
            
            deleteComments.setString(1, imageId);
            deleteComments.executeUpdate();
            
            deletePicture.setString(1, imageId);
            deletePicture.executeUpdate();
            
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Error deleting picture: " + e.getMessage());
        }
    }

    @Override
    public List<Picture> getFollowedUsersPictures(String username) {
        List<Picture> pictures = new ArrayList<>();
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT p.* FROM Pictures p " +
                     "JOIN Follows f ON p.username = f.followed " +
                     "WHERE f.follower = ? " +
                     "ORDER BY p.timestamp DESC")) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Picture picture = createPictureFromResultSet(rs);
                getLikesForPicture(conn, picture);
                pictures.add(picture);
            }
        } catch (SQLException e) {
            System.err.println("Error getting followed users' pictures: " + e.getMessage());
        }
        
        return pictures;
    }

    @Override
    public List<Picture> getAllPictures() {
        List<Picture> pictures = new ArrayList<>();
        
        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Pictures ORDER BY timestamp DESC")) {
            
            while (rs.next()) {
                Picture picture = createPictureFromResultSet(rs);
                getLikesForPicture(conn, picture);
                pictures.add(picture);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all pictures: " + e.getMessage());
        }
        
        return pictures;
    }
    
    private Picture createPictureFromResultSet(ResultSet rs) throws SQLException {
        String imageId = rs.getString("imageId");
        String username = rs.getString("username");
        String imagePath = rs.getString("imagePath");
        String caption = rs.getString("caption");
        LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();
        
        return new Picture(imageId, username, imagePath, caption, timestamp);
    }
    
    private void getLikesForPicture(Connection conn, Picture picture) {
        try (PreparedStatement stmt = conn.prepareStatement(
                     "SELECT COUNT(*) FROM Likes WHERE imageId = ?")) {
            
            stmt.setString(1, picture.getImageId());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int likeCount = rs.getInt(1);
                for (int i = 0; i < likeCount; i++) {
                    picture.like();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting likes for picture: " + e.getMessage());
        }
    }
    
    private void addLikesToPicture(Connection conn, Picture picture) {
        try (PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO Likes (username, imageId) VALUES (?, ?)")) {
            
            // We're just using a dummy username for likes added programmatically
            for (int i = 0; i < picture.getLikesCount(); i++) {
                stmt.setString(1, "system");
                stmt.setString(2, picture.getImageId());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error adding likes to picture: " + e.getMessage());
        }
    }
}