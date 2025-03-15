package com.quackstagram.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility to migrate plaintext passwords to hashed passwords
 */
public class PasswordMigrationUtil {

    private static final String CREDENTIALS_FILE_PATH = "data/credentials.txt";

    /**
     * Migrate all users from plaintext to hashed passwords
     */
    public static void migratePasswords() {
        try {
            // Create the file if it doesn't exist
            FileUtil.createFileIfNotExists(CREDENTIALS_FILE_PATH);
            
            // Read all lines
            List<String> lines = FileUtil.readAllLines(CREDENTIALS_FILE_PATH);
            List<String> updatedLines = new ArrayList<>();
            
            for (String line : lines) {
                String[] parts = line.split(":");
                if (parts.length >= 3 && !line.contains("::")) {
                    // Old format: username:password:bio
                    String username = parts[0];
                    String password = parts[1];
                    String bio = parts[2];
                    
                    // Generate salt and hash password
                    String salt = PasswordUtil.generateSalt();
                    String passwordHash = PasswordUtil.hashPassword(password, salt);
                    
                    // Format with new structure: username:passwordHash:salt:bio
                    updatedLines.add(username + ":" + passwordHash + ":" + salt + ":" + bio);
                } else {
                    // Already in new format or unknown format
                    updatedLines.add(line);
                }
            }
            
            // Write updated content back to file
            FileUtil.writeLines(CREDENTIALS_FILE_PATH, updatedLines, false);
            
            System.out.println("Password migration completed successfully.");
        } catch (IOException e) {
            System.err.println("Error during password migration: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Main method to run the migration tool
     */
    public static void main(String[] args) {
        migratePasswords();
    }
}