package com.quackstagram.view;

import com.quackstagram.controller.SessionController;
import com.quackstagram.controller.UserController;
import com.quackstagram.model.User;
import com.quackstagram.util.NavigationController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * View for handling user authentication (login and registration).
 */
public class AuthView extends BaseView {
    private final UserController userController;
    
    private JPanel loginPanel;
    private JPanel registerPanel;
    private JTextField txtUsername;
    private JTextField txtPassword;
    private JTextField txtRegUsername;
    private JTextField txtRegPassword;
    private JTextField txtBio;
    private JLabel lblPhoto;
    private File selectedProfilePhoto;
    private final String profilePhotoStoragePath = "img/storage/profile/";
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private boolean initialized = false;
    
    /**
     * Constructor for AuthView
     */
    public AuthView(SessionController sessionController, NavigationController navigationController, UserController userController) {
        super(sessionController, navigationController);
        this.userController = userController;
        
        setTitle("Authentication");
        initialize();
    }

    @Override
    public void initialize() {
        if (initialized) {
            return; // Don't reinitialize if already done
        }
        
        getContentPane().removeAll();
        
        // Create card layout to switch between login and register
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        
        // Create login panel
        loginPanel = createLoginPanel();
        cardPanel.add(loginPanel, "login");
        
        // Create register panel
        registerPanel = createRegisterPanel();
        cardPanel.add(registerPanel, "register");
        
        // Add to frame
        add(cardPanel, BorderLayout.CENTER);
        
        // Start with login panel
        cardLayout.show(cardPanel, "login");
        
        initialized = true;
        revalidate();
        repaint();
    }

    @Override
    public void refreshView() {
        // Only show card, don't recreate panels
        if (cardPanel != null && cardLayout != null) {
            cardLayout.show(cardPanel, "login");
        }
    }
    
    /**
     * Create the login panel
     */
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Header
        JPanel headerPanel = createHeaderPanel("Sign In");
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Fields panel
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Logo (same as before)
        JLabel logoLabel = new JLabel();
        logoLabel.setPreferredSize(new Dimension(80, 80));
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        logoLabel.setIcon(new ImageIcon(new ImageIcon("img/logos/DACS.png").getImage()
                                        .getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.add(logoLabel);
        fieldsPanel.add(logoPanel);
        fieldsPanel.add(Box.createVerticalStrut(20));
        
        // Username field - Create a new dedicated field for login
        txtUsername = new JTextField(20);
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        txtUsername.setBorder(BorderFactory.createTitledBorder("Username"));
        fieldsPanel.add(txtUsername);
        fieldsPanel.add(Box.createVerticalStrut(10));
        
        // Password field - Create a new dedicated field for login
        txtPassword = new JTextField(20);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createTitledBorder("Password"));
        fieldsPanel.add(txtPassword);
        fieldsPanel.add(Box.createVerticalStrut(20));
        
        panel.add(fieldsPanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        // Sign in button
        JButton btnSignIn = new JButton("Sign In");
        btnSignIn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSignIn.setFont(new Font("Arial", Font.BOLD, 14));
        btnSignIn.setBackground(new Color(255, 90, 95));
        btnSignIn.setForeground(Color.BLACK);
        btnSignIn.setOpaque(true);
        btnSignIn.setBorderPainted(false);
        btnSignIn.setMaximumSize(new Dimension(Integer.MAX_VALUE, btnSignIn.getPreferredSize().height));
        btnSignIn.addActionListener(this::onSignInClicked);
        buttonsPanel.add(btnSignIn);
        buttonsPanel.add(Box.createVerticalStrut(10));
        
        // Register link
        JButton btnRegisterNow = new JButton("No Account? Register Now");
        btnRegisterNow.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegisterNow.setFont(new Font("Arial", Font.PLAIN, 12));
        btnRegisterNow.setBorderPainted(false);
        btnRegisterNow.setContentAreaFilled(false);
        btnRegisterNow.setForeground(Color.BLUE);
        btnRegisterNow.addActionListener(e -> {
            cardLayout.show(cardPanel, "register");
        });
        buttonsPanel.add(btnRegisterNow);
        
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Create the register panel with separate fields
     */
    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Header
        JPanel headerPanel = createHeaderPanel("Register");
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Fields panel
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Profile photo
        lblPhoto = new JLabel();
        lblPhoto.setPreferredSize(new Dimension(80, 80));
        lblPhoto.setHorizontalAlignment(JLabel.CENTER);
        lblPhoto.setIcon(new ImageIcon(new ImageIcon("img/logos/DACS.png").getImage()
                                      .getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
        JPanel photoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        photoPanel.add(lblPhoto);
        fieldsPanel.add(photoPanel);
        
        // Upload photo button
        JButton btnUploadPhoto = new JButton("Upload Photo");
        btnUploadPhoto.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnUploadPhoto.addActionListener(e -> handleProfilePictureUpload());
        JPanel photoButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        photoButtonPanel.add(btnUploadPhoto);
        fieldsPanel.add(photoButtonPanel);
        fieldsPanel.add(Box.createVerticalStrut(10));
        
        // Username field - Create separate field for registration
        txtRegUsername = new JTextField(20);
        txtRegUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        txtRegUsername.setBorder(BorderFactory.createTitledBorder("Username"));
        fieldsPanel.add(txtRegUsername);
        fieldsPanel.add(Box.createVerticalStrut(10));
        
        // Password field - Create separate field for registration
        txtRegPassword = new JTextField(20);
        txtRegPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        txtRegPassword.setBorder(BorderFactory.createTitledBorder("Password"));
        fieldsPanel.add(txtRegPassword);
        fieldsPanel.add(Box.createVerticalStrut(10));
        
        // Bio field
        txtBio = new JTextField(20);
        txtBio.setFont(new Font("Arial", Font.PLAIN, 14));
        txtBio.setBorder(BorderFactory.createTitledBorder("Bio"));
        fieldsPanel.add(txtBio);
        
        panel.add(fieldsPanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        // Register button
        JButton btnRegister = new JButton("Register");
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegister.setFont(new Font("Arial", Font.BOLD, 14));
        btnRegister.setBackground(new Color(255, 90, 95));
        btnRegister.setForeground(Color.BLACK);
        btnRegister.setOpaque(true);
        btnRegister.setBorderPainted(false);
        btnRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, btnRegister.getPreferredSize().height));
        btnRegister.addActionListener(this::onRegisterClicked);
        buttonsPanel.add(btnRegister);
        buttonsPanel.add(Box.createVerticalStrut(10));
        
        // Sign in link
        JButton btnSignIn = new JButton("Already have an account? Sign In");
        btnSignIn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSignIn.setFont(new Font("Arial", Font.PLAIN, 12));
        btnSignIn.setBorderPainted(false);
        btnSignIn.setContentAreaFilled(false);
        btnSignIn.setForeground(Color.BLUE);
        btnSignIn.addActionListener(e -> {
            cardLayout.show(cardPanel, "login");
        });
        buttonsPanel.add(btnSignIn);
        
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Handle sign in button click
     * 
     * @param event the action event
     */
    private void onSignInClicked(ActionEvent event) {
        // Get values directly from the text fields
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        
        System.out.println("Login attempt - Username: " + username + ", Password length: " + password.length());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        User user = userController.authenticate(username, password);
        if (user != null) {
            sessionController.login(user);
            navigateTo("home");
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Handle register button click
     * 
     * @param event the action event
     */
    private void onRegisterClicked(ActionEvent event) {
        // Use separate fields for registration
        String username = txtRegUsername.getText();
        String password = txtRegPassword.getText();
        String bio = txtBio.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean registered = userController.registerUser(username, password, bio);
        if (registered) {
            // Save profile photo if selected
            if (selectedProfilePhoto != null) {
                saveProfilePhoto(selectedProfilePhoto, username);
            }
            
            JOptionPane.showMessageDialog(this, "Registration successful! Please sign in.", 
                                         "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Clear registration fields
            txtRegUsername.setText("");
            txtRegPassword.setText("");
            txtBio.setText("");
            selectedProfilePhoto = null;
            lblPhoto.setIcon(new ImageIcon(new ImageIcon("img/logos/DACS.png").getImage()
                                          .getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
            
            // Switch to login panel
            cardLayout.show(cardPanel, "login");
        } else {
            JOptionPane.showMessageDialog(this, "Username already exists. Please choose a different username.", 
                                         "Registration Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Handle profile picture upload
     */
    private void handleProfilePictureUpload() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
        fileChooser.setFileFilter(filter);
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedProfilePhoto = fileChooser.getSelectedFile();
            try {
                Image image = ImageIO.read(selectedProfilePhoto);
                Image scaledImage = image.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                lblPhoto.setIcon(new ImageIcon(scaledImage));
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error loading image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Save profile photo to storage
     * 
     * @param file the profile photo file
     * @param username the username
     */
    private void saveProfilePhoto(File file, String username) {
        try {
            // Ensure directory exists
            Files.createDirectories(Paths.get(profilePhotoStoragePath));
            
            // Copy file to profile photo storage
            Files.copy(file.toPath(), Paths.get(profilePhotoStoragePath, username + ".png"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving profile photo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}