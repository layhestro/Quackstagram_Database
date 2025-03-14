// File: com/quackstagram/controller/SessionController.java
package com.quackstagram.controller;

import com.quackstagram.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Controller for managing user sessions
 */
public class SessionController {
    private User currentUser;
    private final String usersFilePath = "data/users.txt";
    
    /**
     * Constructor for SessionController
     */
    public SessionController() {
        // Initialize with no user logged in
        currentUser = null;
    }
    
    /**
     * Log in a user
     * 
     * @param user the user to log in
     */
    public void login(User user) {
        this.currentUser = user;
        try {
            // Write current user to users.txt
            Files.write(Paths.get(usersFilePath), user.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Log out the current user
     */
    public void logout() {
        this.currentUser = null;
        try {
            // Clear users.txt
            Files.write(Paths.get(usersFilePath), new byte[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Get the currently logged in user
     * 
     * @return the current user, or null if no user is logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Check if any user is logged in
     * 
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Load current user from file
     * 
     * @return the username of the current user, or null if no user is logged in
     * @throws IOException if an I/O error occurs
     */
    public String loadUserFromFile() throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(usersFilePath))) {
            String line = reader.readLine();
            if (line != null) {
                return line.split(":")[0].trim();
            }
        }
        return null;
    }
}