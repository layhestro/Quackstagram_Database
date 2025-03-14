// File: com/quackstagram/QuackstagramApp.java
package com.quackstagram;

import com.quackstagram.controller.*;
import com.quackstagram.dao.impl.*;
import com.quackstagram.dao.interfaces.*;
import com.quackstagram.model.User;
import com.quackstagram.util.NavigationController;
import com.quackstagram.view.*;

import javax.swing.*;
import java.io.IOException;

/**
 * Main application class for Quackstagram
 */
public class QuackstagramApp {
    
    /**
     * Entry point for the application
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // Use Swing's event dispatch thread for UI operations
        SwingUtilities.invokeLater(() -> {
            try {
                // Set look and feel to system default
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Initialize the application
            initializeApplication();
        });
    }
    
    /**
     * Initialize the application components
     */
    private static void initializeApplication() {
        // Initialize DAOs
        UserDAO userDAO = new FileUserDAO();
        PictureDAO pictureDAO = new FilePictureDAO();
        NotificationDAO notificationDAO = new FileNotificationDAO();
        FollowDAO followDAO = new FileFollowDAO();
        
        // Initialize Controllers
        UserController userController = new UserController(userDAO, followDAO);
        NotificationController notificationController = new NotificationController(notificationDAO);
        PictureController pictureController = new PictureController(pictureDAO, userDAO, notificationController);
        SessionController sessionController = new SessionController();
        
        // Initialize Navigation Controller
        NavigationController navigationController = new NavigationController();
        
        // Initialize Views
        AuthView authView = new AuthView(sessionController, navigationController, userController);
        ProfileView profileView = new ProfileView(sessionController, navigationController, 
                                                 userController, pictureController);
        HomeView homeView = new HomeView(sessionController, navigationController, pictureController);
        NotificationsView notificationsView = new NotificationsView(sessionController, navigationController, 
                                                                   notificationController);
        ExploreView exploreView = new ExploreView(sessionController, navigationController, pictureController);
        ImageUploadView imageUploadView = new ImageUploadView(sessionController, navigationController, 
                                                             pictureController);
        
        // Register Views with Navigation Controller
        navigationController.registerView("auth", authView);
        navigationController.registerView("profile", profileView);
        navigationController.registerView("home", homeView);
        navigationController.registerView("notifications", notificationsView);
        navigationController.registerView("explore", exploreView);
        navigationController.registerView("upload", imageUploadView);
        
        // Check if there's already a user logged in
        try {
            // If a user is found in users.txt, consider them logged in
            String currentUsername = sessionController.loadUserFromFile();
            if (currentUsername != null && !currentUsername.isEmpty()) {
                User currentUser = userDAO.findByUsername(currentUsername);
                if (currentUser != null) {
                    // User is already logged in
                    sessionController.login(currentUser);
                    // Start with Home View
                    navigationController.navigateTo("home");
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // No user logged in, start with Auth View
        navigationController.navigateTo("auth");
    }
}