// File: com/quackstagram/dao/impl/FileUserDAO.java
package com.quackstagram.dao.impl;

import com.quackstagram.dao.interfaces.UserDAO;
import com.quackstagram.model.User;
import com.quackstagram.util.FileUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * File-based implementation of UserDAO
 */
public class FileUserDAO implements UserDAO {
    private final String credentialsFilePath = "data/credentials.txt";
    private final String usersFilePath = "data/users.txt";

    @Override
    public User findByUsername(String username) {
        try {
            // Create the file if it doesn't exist
            FileUtil.createFileIfNotExists(credentialsFilePath);
            
            List<String> lines = FileUtil.readMatchingLines(credentialsFilePath, 
                    line -> line.startsWith(username + ":"));
            
            if (!lines.isEmpty()) {
                String[] parts = lines.get(0).split(":");
                if (parts.length >= 3) {
                    String password = parts[1];
                    String bio = parts.length > 2 ? parts[2] : "";
                    return new User(username, bio, password);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void save(User user) {
        try {
            // Create the file if it doesn't exist
            FileUtil.createFileIfNotExists(credentialsFilePath);
            
            // Append user to credentials file
            FileUtil.appendLine(credentialsFilePath, user.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(User user) {
        try {
            // Create the file if it doesn't exist
            FileUtil.createFileIfNotExists(credentialsFilePath);
            
            // Update credentials file
            FileUtil.updateLines(credentialsFilePath, 
                    line -> line.startsWith(user.getUsername() + ":"), 
                    line -> user.toString());
            
            // Update current user file
            Files.write(Paths.get(usersFilePath), user.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String username) {
        try {
            // Create the file if it doesn't exist
            FileUtil.createFileIfNotExists(credentialsFilePath);
            
            // Read all lines except the one to delete
            List<String> lines = FileUtil.readMatchingLines(credentialsFilePath, 
                    line -> !line.startsWith(username + ":"));
            
            // Write back to file
            FileUtil.writeLines(credentialsFilePath, lines, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            // Create the file if it doesn't exist
            FileUtil.createFileIfNotExists(credentialsFilePath);
            
            // Read all lines
            List<String> lines = FileUtil.readAllLines(credentialsFilePath);
            
            // Parse each line into a User object
            for (String line : lines) {
                String[] parts = line.split(":");
                if (parts.length >= 3) {
                    String username = parts[0];
                    String password = parts[1];
                    String bio = parts[2];
                    users.add(new User(username, bio, password));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public boolean verifyCredentials(String username, String password) {
        try {
            // Create the file if it doesn't exist
            FileUtil.createFileIfNotExists(credentialsFilePath);
            
            // Find line with username
            List<String> lines = FileUtil.readMatchingLines(credentialsFilePath, 
                    line -> line.startsWith(username + ":"));
            
            if (!lines.isEmpty()) {
                String[] parts = lines.get(0).split(":");
                if (parts.length >= 2) {
                    return parts[1].equals(password);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}





