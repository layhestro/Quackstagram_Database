package com.quackstagram.dao.impl;

import com.quackstagram.dao.interfaces.UserDAO;
import com.quackstagram.model.User;
import com.quackstagram.util.FileUtil;
import com.quackstagram.util.PasswordUtil;

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
                if (parts.length >= 4) {
                    // New format: username:passwordHash:salt:bio
                    String passwordHash = parts[1];
                    String salt = parts[2];
                    String bio = parts[3];
                    return new User(username, bio, passwordHash, salt);
                } else if (parts.length >= 3) {
                    // Legacy format: username:password:bio
                    String password = parts[1];
                    String bio = parts[2];
                    
                    // Migrate to new format
                    String salt = PasswordUtil.generateSalt();
                    String passwordHash = PasswordUtil.hashPassword(password, salt);
                    User user = new User(username, bio, passwordHash, salt);
                    update(user); // Save with new format
                    return user;
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
                if (parts.length >= 4) {
                    // New format: username:passwordHash:salt:bio
                    String username = parts[0];
                    String passwordHash = parts[1];
                    String salt = parts[2];
                    String bio = parts[3];
                    users.add(new User(username, bio, passwordHash, salt));
                } else if (parts.length >= 3) {
                    // Legacy format: username:password:bio
                    String username = parts[0];
                    String password = parts[1];
                    String bio = parts[2];
                    
                    // Create user with hashed password (will be saved later)
                    users.add(new User(username, bio, password));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean verifyCredentials(String username, String password) {
        try {
            // Log the verification attempt
            System.out.println("Verifying credentials for: " + username);
            
            // Find line with username
            List<String> lines = FileUtil.readMatchingLines(credentialsFilePath, 
                    line -> line.startsWith(username + ":"));
            
            if (!lines.isEmpty()) {
                String line = lines.get(0);
                System.out.println("Found credential line: " + line); // For debugging
                
                String[] parts = line.split(":");
                System.out.println("Parts length: " + parts.length); // To check format
                
                if (parts.length >= 4) {
                    // New format with salt and hash
                    String storedHash = parts[1];
                    String storedSalt = parts[2];
                    boolean result = PasswordUtil.verifyPassword(password, storedHash, storedSalt);
                    System.out.println("New format verification result: " + result);
                    return result;
                } else if (parts.length >= 2) {
                    // Legacy format with plaintext password
                    String storedPassword = parts[1];
                    boolean isValid = storedPassword.equals(password);
                    System.out.println("Legacy format verification result: " + isValid);
                    
                    if (isValid) {
                        // Migrate to new format
                        System.out.println("Migrating to new format");
                        User user = new User(username, parts.length >= 3 ? parts[2] : "", password);
                        update(user);
                    }
                    
                    return isValid;
                }
            } else {
                System.out.println("No credentials found for: " + username);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}