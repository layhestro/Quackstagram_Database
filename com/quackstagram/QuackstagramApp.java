package com.quackstagram;

import com.quackstagram.controller.*;
import com.quackstagram.dao.impl.*;
import com.quackstagram.dao.interfaces.*;
import com.quackstagram.view.*;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } 
            catch (Exception e) {
                e.printStackTrace();
            }
            initializeApplication();
        });
    }
    
    /**
     * Initializes the application components
     */
    public static void initializeApplication() {
        UserDAO userDAO = new DatabaseUserDAO();
        PictureDAO pictureDAO = new DatabasePictureDAO();
        NotificationDAO notificationDAO = new DatabaseNotificationDAO();
        FollowDAO followDAO = new DatabaseFollowDAO();
        
        UserController userController = new UserController(userDAO, followDAO);
        NotificationController notificationController = new NotificationController(notificationDAO);
        PictureController pictureController = new PictureController(pictureDAO, userDAO, notificationController);
        SessionController sessionController = new SessionController();
        
        NavigationController navigationController = new NavigationController();
        
        // Register views as before
        AuthView authView = new AuthView(sessionController, navigationController, userController);
        ProfileView profileView = new ProfileView(sessionController, navigationController, userController, pictureController);
        HomeView homeView = new HomeView(sessionController, navigationController, pictureController);
        NotificationsView notificationsView = new NotificationsView(sessionController, navigationController, notificationController);
        ExploreView exploreView = new ExploreView(sessionController, navigationController, pictureController);
        ImageUploadView imageUploadView = new ImageUploadView(sessionController, navigationController, pictureController);
        
        navigationController.registerView("auth", authView);
        navigationController.registerView("profile", profileView);
        navigationController.registerView("home", homeView);
        navigationController.registerView("notifications", notificationsView);
        navigationController.registerView("explore", exploreView);
        navigationController.registerView("upload", imageUploadView);
        
        navigationController.navigateTo("auth");
    }
}