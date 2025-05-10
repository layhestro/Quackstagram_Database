package com.quackstagram.dao.impl;

import com.quackstagram.dao.interfaces.PictureDAO;
import com.quackstagram.model.Picture;
import com.quackstagram.util.DatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Database implementation of PictureDAO
 */
public class DatabasePictureDAO implements PictureDAO {
    private final DatabaseConnectionManager connectionManager;
    
    /**
     * Constructor for DatabasePictureDAO
     */
    public DatabasePictureDAO() {
        this.connectionManager = DatabaseConnectionManager.getInstance();
    }

    /**
     * Finds a picture by ID
     * 
     * @param imageId the ID of the picture
     * @return the Picture if found, null otherwise
     */
    @Override
    public Picture findById(String imageId) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            connection = connectionManager.getConnection();
            String query = "SELECT * FROM Pictures WHERE imageId = ?";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, imageId);
            
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToPicture(rs);
            }
            
            return null;
        } catch (SQLException e) {
            System.err.println("Error finding picture by ID: " + e.getMessage());
            return null;
        } finally {
            closeResources(connection, stmt, rs);
        }
    }

    /**
     * Finds all pictures by a user
     * 
     * @param username the username of the user
     * @return a list of pictures from the user
     */
    @Override
    public List<Picture> findByUsername(String username) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Picture> pictures = new ArrayList<>();
        
        try {
            connection = connectionManager.getConnection();
            String query = "SELECT * FROM Pictures WHERE username = ? ORDER BY timestamp DESC";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            
            rs = stmt.executeQuery();
            while (rs.next()) {
                pictures.add(mapResultSetToPicture(rs));
            }
            
            return pictures;
        } catch (SQLException e) {
            System.err.println("Error finding pictures by username: " + e.getMessage());
            return pictures;
        } finally {
            closeResources(connection, stmt, rs);
        }
    }

    /**
     * Saves a new picture
     * 
     * @param picture the picture to save
     */
    @Override
    public void save(Picture picture) {
        Connection connection = null;
        PreparedStatement stmt = null;
        
        try {
            connection = connectionManager.getConnection();
            
            // Start transaction
            connection.setAutoCommit(false);
            
            // Insert the picture
            String insertQuery = "INSERT INTO Pictures (imageId, username, imagePath, caption, likesCount, timestamp) " +
                               "VALUES (?, ?, ?, ?, ?, ?)";
            stmt = connection.prepareStatement(insertQuery);
            stmt.setString(1, picture.getImageId());
            stmt.setString(2, picture.getUsername());
            stmt.setString(3, picture.getImagePath());
            stmt.setString(4, picture.getCaption());
            stmt.setInt(5, picture.getLikesCount());
            stmt.setTimestamp(6, Timestamp.valueOf(picture.getTimestamp()));
            
            stmt.executeUpdate();
            stmt.close();
            
            // Update user post count
            String updateQuery = "UPDATE Users SET postsCount = postsCount + 1 WHERE username = ?";
            stmt = connection.prepareStatement(updateQuery);
            stmt.setString(1, picture.getUsername());
            
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
            System.err.println("Error saving picture: " + e.getMessage());
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
     * Updates an existing picture
     * 
     * @param picture the picture to update
     */
    @Override
    public void update(Picture picture) {
        Connection connection = null;
        PreparedStatement stmt = null;
        
        try {
            connection = connectionManager.getConnection();
            String query = "UPDATE Pictures SET caption = ?, likesCount = ? WHERE imageId = ?";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, picture.getCaption());
            stmt.setInt(2, picture.getLikesCount());
            stmt.setString(3, picture.getImageId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating picture: " + e.getMessage());
        } finally {
            closeResources(connection, stmt, null);
        }
    }

    /**
     * Deletes a picture by ID
     * 
     * @param imageId the ID of the picture to delete
     */
    @Override
    public void delete(String imageId) {
        Connection connection = null;
        PreparedStatement stmt = null;
        
        try {
            connection = connectionManager.getConnection();
            
            // Start transaction
            connection.setAutoCommit(false);
            
            // Get the picture's username
            String getUsernameQuery = "SELECT username FROM Pictures WHERE imageId = ?";
            stmt = connection.prepareStatement(getUsernameQuery);
            stmt.setString(1, imageId);
            ResultSet rs = stmt.executeQuery();
            
            String username = null;
            if (rs.next()) {
                username = rs.getString("username");
            }
            rs.close();
            stmt.close();
            
            if (username != null) {
                // Delete the picture
                String deleteQuery = "DELETE FROM Pictures WHERE imageId = ?";
                stmt = connection.prepareStatement(deleteQuery);
                stmt.setString(1, imageId);
                stmt.executeUpdate();
                stmt.close();
                
                // Update user post count
                String updateQuery = "UPDATE Users SET postsCount = GREATEST(0, postsCount - 1) WHERE username = ?";
                stmt = connection.prepareStatement(updateQuery);
                stmt.setString(1, username);
                stmt.executeUpdate();
                
                // Commit transaction
                connection.commit();
            }
            
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
            System.err.println("Error deleting picture: " + e.getMessage());
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
     * Retrieves pictures from users that a user follows
     * 
     * @param username the username of the user
     * @return a list of pictures from followed users
     */
    @Override
    public List<Picture> getFollowedUsersPictures(String username) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Picture> pictures = new ArrayList<>();
        
        try {
            connection = connectionManager.getConnection();
            String query = "SELECT p.* FROM Pictures p " +
                          "JOIN Follows f ON p.username = f.followed " +
                          "WHERE f.follower = ? " +
                          "ORDER BY p.timestamp DESC";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            
            rs = stmt.executeQuery();
            while (rs.next()) {
                pictures.add(mapResultSetToPicture(rs));
            }
            
            return pictures;
        } catch (SQLException e) {
            System.err.println("Error getting followed users' pictures: " + e.getMessage());
            return pictures;
        } finally {
            closeResources(connection, stmt, rs);
        }
    }

    /**
     * Retrieves all pictures
     * 
     * @return a list of all pictures
     */
    @Override
    public List<Picture> getAllPictures() {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Picture> pictures = new ArrayList<>();
        
        try {
            connection = connectionManager.getConnection();
            String query = "SELECT * FROM Pictures ORDER BY timestamp DESC";
            stmt = connection.prepareStatement(query);
            
            rs = stmt.executeQuery();
            while (rs.next()) {
                pictures.add(mapResultSetToPicture(rs));
            }
            
            return pictures;
        } catch (SQLException e) {
            System.err.println("Error getting all pictures: " + e.getMessage());
            return pictures;
        } finally {
            closeResources(connection, stmt, rs);
        }
    }
    
    /**
     * Maps a ResultSet to a Picture object
     * 
     * @param rs the ResultSet containing picture data
     * @return a Picture object
     * @throws SQLException if a database error occurs
     */
    private Picture mapResultSetToPicture(ResultSet rs) throws SQLException {
        String imageId = rs.getString("imageId");
        String username = rs.getString("username");
        String imagePath = rs.getString("imagePath");
        String caption = rs.getString("caption");
        LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();
        int likesCount = rs.getInt("likesCount");
        
        Picture picture = new Picture(imageId, username, imagePath, caption, timestamp);
        // Set likes count using the like() method
        for (int i = 0; i < likesCount; i++) {
            picture.like();
        }
        
        return picture;
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