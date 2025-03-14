// File: com/quackstagram/view/ImageUploadView.java
package com.quackstagram.view;

import com.quackstagram.controller.PictureController;
import com.quackstagram.controller.SessionController;
import com.quackstagram.util.NavigationController;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

/**
 * View for uploading new images.
 */
public class ImageUploadView extends BaseView {
    private final PictureController pictureController;
    private JLabel imagePreviewLabel;
    private JTextArea captionTextArea;
    private File selectedImageFile;

    /**
     * Constructor for ImageUploadView
     * 
     * @param sessionController controller for user session management
     * @param navigationController controller for view navigation
     * @param pictureController controller for picture operations
     */
    public ImageUploadView(SessionController sessionController, NavigationController navigationController,
                          PictureController pictureController) {
        super(sessionController, navigationController);
        this.pictureController = pictureController;
        
        setTitle("Upload Image");
        initialize();
    }

    @Override
    public void initialize() {
        getContentPane().removeAll();
        
        // Header panel
        JPanel headerPanel = createHeaderPanel("Upload Image");
        add(headerPanel, BorderLayout.NORTH);
        
        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Only allow uploads if logged in
        if (sessionController.isLoggedIn()) {
            // Image preview
            imagePreviewLabel = new JLabel("No image selected");
            imagePreviewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            imagePreviewLabel.setPreferredSize(new Dimension(WIDTH - 40, HEIGHT / 3));
            imagePreviewLabel.setMinimumSize(new Dimension(WIDTH - 40, HEIGHT / 3));
            imagePreviewLabel.setHorizontalAlignment(JLabel.CENTER);
            imagePreviewLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            contentPanel.add(imagePreviewLabel);
            
            // Spacer
            contentPanel.add(Box.createVerticalStrut(10));
            
            // Upload button
            JButton uploadButton = new JButton("Select Image");
            uploadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            uploadButton.addActionListener(this::handleImageSelection);
            contentPanel.add(uploadButton);
            
            // Spacer
            contentPanel.add(Box.createVerticalStrut(10));
            
            // Caption text area
            JLabel captionLabel = new JLabel("Caption:");
            captionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(captionLabel);
            
            captionTextArea = new JTextArea(5, 20);
            captionTextArea.setLineWrap(true);
            captionTextArea.setWrapStyleWord(true);
            JScrollPane captionScrollPane = new JScrollPane(captionTextArea);
            captionScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(captionScrollPane);
            
            // Spacer
            contentPanel.add(Box.createVerticalStrut(10));
            
            // Save button
            JButton saveButton = new JButton("Post Image");
            saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            saveButton.addActionListener(this::handleImagePost);
            contentPanel.add(saveButton);
        } else {
            // Login prompt
            displayLoginPrompt(contentPanel);
        }
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        add(scrollPane, BorderLayout.CENTER);
        
        // Navigation panel
        JPanel navigationPanel = createNavigationPanel();
        add(navigationPanel, BorderLayout.SOUTH);
        
        revalidate();
        repaint();
    }

    @Override
    public void refreshView() {
        initialize();
    }
    
    /**
     * Handle image selection button click
     * 
     * @param event the action event
     */
    private void handleImageSelection(ActionEvent event) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select an image file");
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Image files", "png", "jpg", "jpeg", "gif");
        fileChooser.addChoosableFileFilter(filter);
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            
            try {
                // Load and display image preview
                Image image = ImageIO.read(selectedImageFile);
                int previewWidth = imagePreviewLabel.getWidth();
                int previewHeight = imagePreviewLabel.getHeight();
                
                // If the label doesn't have a size yet, use default sizes
                if (previewWidth <= 0) previewWidth = WIDTH - 40;
                if (previewHeight <= 0) previewHeight = HEIGHT / 3;
                
                // Scale image to fit preview area
                double widthRatio = (double) previewWidth / image.getWidth(null);
                double heightRatio = (double) previewHeight / image.getHeight(null);
                double scale = Math.min(widthRatio, heightRatio);
                
                int scaledWidth = (int) (image.getWidth(null) * scale);
                int scaledHeight = (int) (image.getHeight(null) * scale);
                
                Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                imagePreviewLabel.setIcon(new ImageIcon(scaledImage));
                imagePreviewLabel.setText(""); // Clear text when image is displayed
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                        "Error loading image: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                imagePreviewLabel.setIcon(null);
                imagePreviewLabel.setText("Error loading image");
            }
        }
    }
    
    /**
     * Handle image post button click
     * 
     * @param event the action event
     */
    private void handleImagePost(ActionEvent event) {
        if (selectedImageFile == null) {
            JOptionPane.showMessageDialog(this, 
                    "Please select an image to upload", 
                    "No Image Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String caption = captionTextArea.getText();
        if (caption.trim().isEmpty()) {
            caption = "No caption"; // Default caption if empty
        }
        
        boolean success = pictureController.savePicture(
                sessionController.getCurrentUser().getUsername(), 
                selectedImageFile, 
                caption);
        
        if (success) {
            JOptionPane.showMessageDialog(this, 
                    "Image uploaded successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Reset form
            selectedImageFile = null;
            imagePreviewLabel.setIcon(null);
            imagePreviewLabel.setText("No image selected");
            captionTextArea.setText("");
            
            // Navigate to home to see the new post
            navigateTo("home");
        } else {
            JOptionPane.showMessageDialog(this, 
                    "Failed to upload image. Please try again.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Display a login prompt when user is not logged in
     * 
     * @param panel the panel to add the login prompt to
     */
    private void displayLoginPrompt(JPanel panel) {
        panel.removeAll();
        panel.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));
        
        JLabel messageLabel = new JLabel("Please sign in to upload images");
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(messageLabel);
        
        panel.add(Box.createVerticalStrut(20));
        
        JButton signInButton = new JButton("Sign In");
        signInButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signInButton.addActionListener(e -> navigateTo("auth"));
        panel.add(signInButton);
    }
}