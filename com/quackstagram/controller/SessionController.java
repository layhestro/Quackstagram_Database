package com.quackstagram.controller;

import com.quackstagram.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for managing user sessions
 */
public class SessionController {
    private User currentUser;
    private final String usersFilePath = "data/users.txt";
    private final Map<String, Object> temporaryData = new HashMap<>();
    
    /**
     * Constructor for SessionController
     */
    public SessionController() {
        currentUser = null;
    }
    
    /**
     * Logs in a user and saves their data
     * 
     * @param user the user to log in
     */
    public void login(User user) {
        this.currentUser = user;
        try {
            Files.write(Paths.get(usersFilePath), user.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Logs out the current user and clears saved data
     */
    public void logout() {
        this.currentUser = null;
        try {
            Files.write(Paths.get(usersFilePath), new byte[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Retrieves the currently logged in user
     * 
     * @return the current user, or null if no user is logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Checks if any user is currently logged in
     * 
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Loads current user from file
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

    /**
     * Stores a value to be passed between views
     * 
     * @param key the key for the data
     * @param value the value to store
     */
    public void setTemporaryData(String key, Object value) {
        temporaryData.put(key, value);
    }

    /**
     * Retrieves a value passed between views
     * 
     * @param key the key for the data
     * @return the stored value, or null if not found
     */
    public Object getTemporaryData(String key) {
        return temporaryData.get(key);
    }

    /**
     * Removes a temporary value after it's been used
     * 
     * @param key the key for the data to remove
     */
    public void removeTemporaryData(String key) {
        temporaryData.remove(key);
    }
}