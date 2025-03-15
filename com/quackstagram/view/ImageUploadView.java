package com.quackstagram.view;

import com.quackstagram.controller.PictureController;
import com.quackstagram.controller.SessionController;
import com.quackstagram.util.ImageFilterUtil;
import com.quackstagram.util.NavigationController;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
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
    private BufferedImage originalImage;
    private BufferedImage filteredImage;
    private JComboBox<String> filterComboBox;
    private String currentFilter = "None";

    /**
     * Constructor for ImageUploadView
     * 
     * @param sessionController controller for user session management
     * @param navigationController controller for view navigation
     * @param pictureController controller for picture operations
     */
    public ImageUploadView(SessionController sessionController, NavigationController navigationController, PictureController pictureController) {
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
            // Create a panel for the image preview with fixed size
            JPanel previewPanel = new JPanel(new BorderLayout());
            previewPanel.setPreferredSize(new Dimension(WIDTH - 40, HEIGHT / 3));
            previewPanel.setMaximumSize(new Dimension(WIDTH - 40, HEIGHT / 3));
            previewPanel.setMinimumSize(new Dimension(WIDTH - 40, HEIGHT / 3));
            previewPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            previewPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            
            // Image preview
            imagePreviewLabel = new JLabel("No image selected", JLabel.CENTER);
            imagePreviewLabel.setHorizontalAlignment(JLabel.CENTER);
            imagePreviewLabel.setVerticalAlignment(JLabel.CENTER);
            previewPanel.add(imagePreviewLabel, BorderLayout.CENTER);
            
            contentPanel.add(previewPanel);
            
            // Spacer
            contentPanel.add(Box.createVerticalStrut(10));
            
            // Upload and filter section
            JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            controlPanel.setMaximumSize(new Dimension(WIDTH - 20, 40));
            controlPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JButton uploadButton = new JButton("Select Image");
            uploadButton.addActionListener(this::handleImageSelection);
            controlPanel.add(uploadButton);
            
            // Filter dropdown
            JLabel filterLabel = new JLabel("Filter:");
            controlPanel.add(filterLabel);
            
            filterComboBox = new JComboBox<>(ImageFilterUtil.getFilterNames());
            filterComboBox.setSelectedItem("None");
            filterComboBox.addActionListener(e -> {
                currentFilter = (String) filterComboBox.getSelectedItem();
                applyCurrentFilter();
            });
            controlPanel.add(filterComboBox);
            
            contentPanel.add(controlPanel);
            
            // Spacer
            contentPanel.add(Box.createVerticalStrut(10));
            
            // Caption panel
            JPanel captionPanel = new JPanel();
            captionPanel.setLayout(new BoxLayout(captionPanel, BoxLayout.Y_AXIS));
            captionPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            captionPanel.setMaximumSize(new Dimension(WIDTH - 20, 150));
            captionPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            
            JLabel captionLabel = new JLabel("Caption:");
            captionLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 0));
            captionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            captionPanel.add(captionLabel);
            
            captionTextArea = new JTextArea(5, 20);
            captionTextArea.setLineWrap(true);
            captionTextArea.setWrapStyleWord(true);
            JScrollPane captionScrollPane = new JScrollPane(captionTextArea);
            captionScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
            captionScrollPane.setPreferredSize(new Dimension(WIDTH - 40, 100));
            captionScrollPane.setMaximumSize(new Dimension(WIDTH - 40, 100));
            captionPanel.add(captionScrollPane);
            
            contentPanel.add(captionPanel);
            
            // Spacer
            contentPanel.add(Box.createVerticalStrut(10));
            
            // Save button
            JPanel saveButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            saveButtonPanel.setMaximumSize(new Dimension(WIDTH - 20, 40));
            saveButtonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JButton saveButton = new JButton("Post Image");
            saveButton.addActionListener(this::handleImagePost);
            saveButtonPanel.add(saveButton);
            
            contentPanel.add(saveButtonPanel);
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
     * Apply the currently selected filter to the image
     */
    private void applyCurrentFilter() {
        if (originalImage == null) {
            return;
        }
        
        // Apply the selected filter
        filteredImage = ImageFilterUtil.applyFilter(originalImage, currentFilter);
        
        // Update preview
        updatePreview(filteredImage);
    }

        /**
     * Update the preview label with the provided image
     */
    private void updatePreview(BufferedImage image) {
        // Define the maximum preview dimensions
        int previewWidth = WIDTH - 60;  // Accounting for padding and borders
        int previewHeight = HEIGHT / 3 - 20;
        
        // Calculate scaled dimensions while preserving aspect ratio
        double widthRatio = (double) previewWidth / image.getWidth();
        double heightRatio = (double) previewHeight / image.getHeight();
        double scale = Math.min(widthRatio, heightRatio);
        
        int scaledWidth = (int) (image.getWidth() * scale);
        int scaledHeight = (int) (image.getHeight() * scale);
        
        Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        imagePreviewLabel.setIcon(new ImageIcon(scaledImage));
        imagePreviewLabel.setText(""); // Clear text when image is displayed
    }

    /**
     * Handle image selection button click
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
                // Load the original image
                originalImage = ImageIO.read(selectedImageFile);
                
                // Reset filter to "None"
                filterComboBox.setSelectedItem("None");
                currentFilter = "None";
                
                // Apply current filter (which is "None" at this point)
                applyCurrentFilter();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                        "Error loading image: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                imagePreviewLabel.setIcon(null);
                imagePreviewLabel.setText("Error loading image");
                originalImage = null;
                filteredImage = null;
            }
        }
    }
    
/**
     * Handle image post button click
     */
    private void handleImagePost(ActionEvent event) {
        if (originalImage == null) {
            JOptionPane.showMessageDialog(this, 
                    "Please select an image to upload", 
                    "No Image Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String caption = captionTextArea.getText();
        if (caption.trim().isEmpty()) {
            caption = "No caption"; // Default caption if empty
        }
        
        // Save the filtered image to a temporary file
        try {
            File tempFile = File.createTempFile("filtered_", ".png");
            ImageIO.write(filteredImage, "png", tempFile);
            
            boolean success = pictureController.savePicture(
                    sessionController.getCurrentUser().getUsername(), 
                    tempFile, 
                    caption);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                        "Image uploaded successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Reset form
                selectedImageFile = null;
                originalImage = null;
                filteredImage = null;
                imagePreviewLabel.setIcon(null);
                imagePreviewLabel.setText("No image selected");
                captionTextArea.setText("");
                filterComboBox.setSelectedItem("None");
                
                // Navigate to home to see the new post
                navigateTo("home");
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Failed to upload image. Please try again.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            
            // Clean up temp file
            tempFile.delete();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                    "Error processing image: " + e.getMessage(), 
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