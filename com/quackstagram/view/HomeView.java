package com.quackstagram.view;

import com.quackstagram.controller.PictureController;
import com.quackstagram.controller.SessionController;
import com.quackstagram.model.Picture;
import com.quackstagram.util.NavigationController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.time.LocalDateTime;

/**
 * View for displaying the user's home feed.
 */
public class HomeView extends BaseView {
    private static final int IMAGE_WIDTH = WIDTH - 20;
    private static final int IMAGE_HEIGHT = 200;
    private static final Color LIKE_BUTTON_COLOR = new Color(255, 90, 95);

    private final PictureController pictureController;
    private JPanel contentPanel;
    private JScrollPane scrollPane;

    /**
     * Constructor for HomeView
     * 
     * @param sessionController controller for user session management
     * @param navigationController controller for view navigation
     * @param pictureController controller for picture operations
     */
    public HomeView(SessionController sessionController, NavigationController navigationController,
                PictureController pictureController) {
        super(sessionController, navigationController);
        this.pictureController = pictureController;
        
        setTitle("Quackstagram Home");
        initialize();
    }

    @Override
    public void initialize() {
        getContentPane().removeAll();
        
        // Header panel
        JPanel headerPanel = createHeaderPanel("Quackstagram");
        add(headerPanel, BorderLayout.NORTH);
        
        // Content panel with scroll
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);
        
        // Navigation panel
        JPanel navigationPanel = createNavigationPanel();
        add(navigationPanel, BorderLayout.SOUTH);
        
        // Load feed content
        loadFeedContent();
        
        revalidate();
        repaint();
    }

    @Override
    public void refreshView() {
        // Reload feed content
        contentPanel.removeAll();
        loadFeedContent();
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Load feed content from followed users
     */
    private void loadFeedContent() {
        if (!sessionController.isLoggedIn()) {
            displayLoginPrompt();
            return;
        }
        
        List<Picture> feedPictures = pictureController.getHomeFeedPictures(
                sessionController.getCurrentUser().getUsername());
        
        if (feedPictures.isEmpty()) {
            displayEmptyFeedMessage();
            return;
        }
        
        for (Picture picture : feedPictures) {
            contentPanel.add(createPicturePanel(picture));
            
            // Add a spacing panel between posts
            JPanel spacingPanel = new JPanel();
            spacingPanel.setPreferredSize(new Dimension(WIDTH - 10, 10));
            spacingPanel.setBackground(new Color(230, 230, 230));
            contentPanel.add(spacingPanel);
        }
    }
    
    /**
     * Create a panel for a single picture post
     * 
     * @param picture the picture to display
     * @return a panel containing the picture post
     */
    private JPanel createPicturePanel(Picture picture) {
        JPanel picturePanel = new JPanel();
        picturePanel.setLayout(new BoxLayout(picturePanel, BoxLayout.Y_AXIS));
        picturePanel.setBackground(Color.WHITE);
        picturePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Username header
        JLabel usernameLabel = new JLabel(picture.getUsername());
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        picturePanel.add(usernameLabel);
        
        // Image
        ImageIcon imageIcon;
        try {
            ImageIcon originalIcon = new ImageIcon(picture.getImagePath());
            Image scaledImage = originalIcon.getImage().getScaledInstance(
                    IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(scaledImage);
        } catch (Exception e) {
            imageIcon = new ImageIcon(); // Empty icon if image can't be loaded
        }
        
        JLabel imageLabel = new JLabel(imageIcon);
        imageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        imageLabel.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        // Make image clickable to show full-size view
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                displayFullImage(picture);
            }
        });
        
        picturePanel.add(imageLabel);
        
        // Caption
        JLabel captionLabel = new JLabel(picture.getCaption());
        captionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        picturePanel.add(captionLabel);
        
        // Likes
        JLabel likesLabel = new JLabel("Likes: " + picture.getLikesCount());
        likesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        picturePanel.add(likesLabel);
        
        // Timestamp
        JLabel timeLabel = new JLabel(getTimeAgo(picture.getTimestamp()));
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        timeLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        picturePanel.add(timeLabel);
        
        // Like button
        JButton likeButton = new JButton("❤");
        likeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        likeButton.setBackground(LIKE_BUTTON_COLOR);
        likeButton.setOpaque(true);
        likeButton.setBorderPainted(false);
        likeButton.addActionListener(e -> {
            if (sessionController.isLoggedIn()) {
                pictureController.likePicture(
                        sessionController.getCurrentUser().getUsername(),
                        picture.getImageId());
                likesLabel.setText("Likes: " + (picture.getLikesCount() + 1));
            }
        });
        picturePanel.add(likeButton);
        
        return picturePanel;
    }
    
    /**
     * Display a dialog with the full-sized image
     * 
     * @param picture the picture to display
     */
    private void displayFullImage(Picture picture) {
        JDialog dialog = new JDialog(this, picture.getCaption(), true);
        dialog.setLayout(new BorderLayout());
        
        // Image
        ImageIcon imageIcon = new ImageIcon(picture.getImagePath());
        JLabel imageLabel = new JLabel(imageIcon);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        
        // Username and timestamp
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(new JLabel(picture.getUsername()), BorderLayout.WEST);
        headerPanel.add(new JLabel(getTimeAgo(picture.getTimestamp())), BorderLayout.EAST);
        infoPanel.add(headerPanel);
        
        // Caption
        JLabel captionLabel = new JLabel(picture.getCaption());
        infoPanel.add(captionLabel);
        
        // Likes
        JLabel likesLabel = new JLabel("Likes: " + picture.getLikesCount());
        infoPanel.add(likesLabel);
        
        // Like button
        JButton likeButton = new JButton("❤ Like");
        likeButton.addActionListener(e -> {
            if (sessionController.isLoggedIn()) {
                pictureController.likePicture(
                        sessionController.getCurrentUser().getUsername(),
                        picture.getImageId());
                likesLabel.setText("Likes: " + (picture.getLikesCount() + 1));
            }
        });
        infoPanel.add(likeButton);
        
        dialog.add(imageLabel, BorderLayout.CENTER);
        dialog.add(infoPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    /**
     * Format timestamp as "X days/hours/minutes ago"
     * 
     * @param timestamp the timestamp to format
     * @return a formatted string representing time elapsed
     */
    private String getTimeAgo(LocalDateTime timestamp) {
        LocalDateTime now = LocalDateTime.now();
        long daysBetween = ChronoUnit.DAYS.between(timestamp, now);
        
        if (daysBetween > 0) {
            return daysBetween + " day" + (daysBetween > 1 ? "s" : "") + " ago";
        }
        
        long hoursBetween = ChronoUnit.HOURS.between(timestamp, now);
        if (hoursBetween > 0) {
            return hoursBetween + " hour" + (hoursBetween > 1 ? "s" : "") + " ago";
        }
        
        long minutesBetween = ChronoUnit.MINUTES.between(timestamp, now);
        return minutesBetween + " minute" + (minutesBetween > 1 ? "s" : "") + " ago";
    }
    
    /**
     * Display a message when user is not logged in
     */
    private void displayLoginPrompt() {
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));
        
        JLabel messageLabel = new JLabel("Please sign in to see your feed");
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JButton signInButton = new JButton("Sign In");
        signInButton.addActionListener(e -> navigateTo("auth"));
        
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        messagePanel.add(signInButton, BorderLayout.SOUTH);
        
        contentPanel.add(messagePanel);
    }
    
    /**
     * Display a message when feed is empty
     */
    private void displayEmptyFeedMessage() {
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));
        
        // Create a label with HTML formatting for proper text wrapping
        JLabel messageLabel = new JLabel("<html><div style='text-align: center; width: 200px;'>Your feed is empty. Follow users to see their posts!</div></html>");
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14)); // Reduced font size from 16 to 14
        
        JButton exploreButton = new JButton("Explore Users");
        exploreButton.addActionListener(e -> navigateTo("explore"));
        
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        messagePanel.add(exploreButton, BorderLayout.SOUTH);
        
        contentPanel.add(messagePanel);
    }
}