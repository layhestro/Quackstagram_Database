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

    /**
     * Finds a user by username
     * 
     * @param username the username to search for
     * @return the User if found, null otherwise
     */
    @Override
    public User findByUsername(String username) {
        try {
            FileUtil.createFileIfNotExists(credentialsFilePath);
            
            List<String> lines = FileUtil.readMatchingLines(credentialsFilePath, 
                    line -> line.startsWith(username + ":"));
            
            if (!lines.isEmpty()) {
                String[] parts = lines.get(0).split(":");
                if (parts.length >= 4) {
                    String passwordHash = parts[1];
                    String salt = parts[2];
                    String bio = parts[3];
                    return new User(username, bio, passwordHash, salt);
                } else if (parts.length >= 3) {
                    String password = parts[1];
                    String bio = parts[2];
                    
                    String salt = PasswordUtil.generateSalt();
                    String passwordHash = PasswordUtil.hashPassword(password, salt);
                    User user = new User(username, bio, passwordHash, salt);
                    update(user);
                    return user;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Saves a new user
     * 
     * @param user the user to save
     */
    @Override
    public void save(User user) {
        try {
            FileUtil.createFileIfNotExists(credentialsFilePath);
            
            FileUtil.appendLine(credentialsFilePath, user.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates an existing user
     * 
     * @param user the user to update
     */
    @Override
    public void update(User user) {
        try {
            FileUtil.createFileIfNotExists(credentialsFilePath);
            
            FileUtil.updateLines(credentialsFilePath, 
                    line -> line.startsWith(user.getUsername() + ":"), 
                    line -> user.toString());
            
            Files.write(Paths.get(usersFilePath), user.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a user by username
     * 
     * @param username the username of the user to delete
     */
    @Override
    public void delete(String username) {
        try {
            FileUtil.createFileIfNotExists(credentialsFilePath);
            
            List<String> lines = FileUtil.readMatchingLines(credentialsFilePath, 
                    line -> !line.startsWith(username + ":"));
            
            FileUtil.writeLines(credentialsFilePath, lines, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all users
     * 
     * @return a list of all users
     */
    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            FileUtil.createFileIfNotExists(credentialsFilePath);
            
            List<String> lines = FileUtil.readAllLines(credentialsFilePath);
            
            for (String line : lines) {
                String[] parts = line.split(":");
                if (parts.length >= 4) {
                    String username = parts[0];
                    String passwordHash = parts[1];
                    String salt = parts[2];
                    String bio = parts[3];
                    users.add(new User(username, bio, passwordHash, salt));
                } else if (parts.length >= 3) {
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

    /**
     * Verifies user credentials
     * 
     * @param username the username
     * @param password the password
     * @return true if credentials are valid, false otherwise
     */
    @Override
    public boolean verifyCredentials(String username, String password) {
        try {
            // Log the verification attempt
            System.out.println("Verifying credentials for: " + username);
            
            // Find line with username
            List<String> lines = FileUtil.readMatchingLines(credentialsFilePath, 
                    line -> line.startsWith(username + ":"));
            
            if (!lines.isEmpty()) {
                String line = lines.get(0);
                System.out.println("Found credential line: " + line); 
                
                String[] parts = line.split(":");
                System.out.println("Parts length: " + parts.length); 
                
                if (parts.length >= 3) {
                    String storedHash = parts[1];
                    String storedSalt = parts[2];
                    boolean result = PasswordUtil.verifyPassword(password, storedHash, storedSalt);
                    System.out.println("Verification result: " + result);
                    return result;
                }
            } 
            else {
                System.out.println("No credentials found for: " + username);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}