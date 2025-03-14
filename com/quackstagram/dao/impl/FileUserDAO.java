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
            FileUtil.createFileIfNotExists(credentialsFilePath);
            FileUtil.appendLine(credentialsFilePath, user.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            FileUtil.createFileIfNotExists(credentialsFilePath);
            List<String> lines = FileUtil.readAllLines(credentialsFilePath);
            
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
            FileUtil.createFileIfNotExists(credentialsFilePath);
            
            List<String> lines = FileUtil.readMatchingLines(credentialsFilePath, 
                    line -> line.startsWith(username + ":"));
            
            if (!lines.isEmpty()) {
                String[] parts = lines.get(0).split(":", 3);
                if (parts.length >= 2) {
                    return parts[1].equals(password.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}





