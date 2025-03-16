package com.quackstagram.view;

import com.quackstagram.controller.PictureController;
import com.quackstagram.controller.SessionController;
import com.quackstagram.model.Picture;
import com.quackstagram.controller.NavigationController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.time.temporal.ChronoUnit;
import java.time.LocalDateTime;

/**
 * View for exploring and discovering content.
 */
public class ExploreView extends BaseView {
    private static final int IMAGE_SIZE = WIDTH / 3 - 4;
    
    private final PictureController pictureController;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel gridPanel;
    private JPanel detailPanel;

    /**
     * Constructor for ExploreView
     * 
     * @param sessionController controller for user session management
     * @param navigationController controller for view navigation
     * @param pictureController controller for picture operations
     */
    public ExploreView(SessionController sessionController, NavigationController navigationController,
                    PictureController pictureController) {
        super(sessionController, navigationController);
        this.pictureController = pictureController;
        
        setTitle("Explore");
        initialize();
    }

    /**
     * Initializes the UI components
     */
    @Override
    public void initialize() {
        getContentPane().removeAll();
        
        JPanel headerPanel = createHeaderPanel("Explore");
        add(headerPanel, BorderLayout.NORTH);
        
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        
        gridPanel = new JPanel(new BorderLayout());
        
        JPanel searchPanel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField("Search users or posts");
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Search users or posts")) {
                    searchField.setText("");
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search users or posts");
                }
            }
        });
        searchPanel.add(searchField, BorderLayout.CENTER);
        gridPanel.add(searchPanel, BorderLayout.NORTH);
        
        contentPanel = new JPanel(new GridLayout(0, 3, 2, 2));
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        gridPanel.add(scrollPane, BorderLayout.CENTER);
        
        detailPanel = new JPanel(new BorderLayout());
        
        cardPanel.add(gridPanel, "grid");
        cardPanel.add(detailPanel, "detail");
        
        add(cardPanel, BorderLayout.CENTER);
        
        JPanel navigationPanel = createNavigationPanel();
        add(navigationPanel, BorderLayout.SOUTH);
        
        loadExploreContent();
        
        revalidate();
        repaint();
    }

    /**
     * Refreshes the view with current data
     */
    @Override
    public void refreshView() {
        contentPanel.removeAll();
        loadExploreContent();
        contentPanel.revalidate();
        contentPanel.repaint();
        
        cardLayout.show(cardPanel, "grid");
    }
    
    /**
     * Loads all pictures for explore view
     */
    private void loadExploreContent() {
        List<Picture> allPictures = pictureController.getAllPictures();
        
        for (Picture picture : allPictures) {
            JPanel imageContainer = createImageThumbnail(picture);
            contentPanel.add(imageContainer);
        }
    }
    
    /**
     * Creates a thumbnail for an image in the grid
     * 
     * @param picture the picture to display
     * @return a panel containing the image thumbnail
     */
    private JPanel createImageThumbnail(Picture picture) {
        JPanel container = new JPanel(new BorderLayout());
        container.setPreferredSize(new Dimension(IMAGE_SIZE, IMAGE_SIZE));
        
        ImageIcon originalIcon = new ImageIcon(picture.getImagePath());
        Image scaledImage = originalIcon.getImage().getScaledInstance(
                IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        
        JLabel imageLabel = new JLabel(scaledIcon);
        imageLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                displayImageDetail(picture);
            }
        });
        
        container.add(imageLabel, BorderLayout.CENTER);
        return container;
    }
    
    /**
     * Displays detailed view of an image
     * 
     * @param picture the picture to display in detail
     */
    private void displayImageDetail(Picture picture) {
        detailPanel.removeAll();
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "grid"));
        
        JButton usernameButton = new JButton(picture.getUsername());
        usernameButton.setBorderPainted(false);
        usernameButton.setContentAreaFilled(false);
        usernameButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        usernameButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        usernameButton.addActionListener(e -> {
            navigateToProfile(picture.getUsername());
        });
        
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(usernameButton, BorderLayout.CENTER);
        
        ImageIcon imageIcon = new ImageIcon(picture.getImagePath());
        JLabel imageLabel = new JLabel(imageIcon);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        
        JLabel captionLabel = new JLabel(picture.getCaption());
        captionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JLabel likesLabel = new JLabel("Likes: " + picture.getLikesCount());
        
        JLabel timeLabel = new JLabel(getTimeAgo(picture.getTimestamp()));
        timeLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        
        infoPanel.add(captionLabel);
        infoPanel.add(likesLabel);
        infoPanel.add(timeLabel);
        
        if (sessionController.isLoggedIn()) {
            JButton likeButton = new JButton("❤ Like");
            likeButton.addActionListener(e -> {
                pictureController.likePicture(
                        sessionController.getCurrentUser().getUsername(),
                        picture.getImageId());
                likesLabel.setText("Likes: " + (picture.getLikesCount() + 1));
            });
            infoPanel.add(likeButton);
        }
        
        detailPanel.add(headerPanel, BorderLayout.NORTH);
        detailPanel.add(imageLabel, BorderLayout.CENTER);
        detailPanel.add(infoPanel, BorderLayout.SOUTH);
        
        detailPanel.revalidate();
        detailPanel.repaint();
        
        cardLayout.show(cardPanel, "detail");
    }
    
    /**
     * Formats timestamp as "X days/hours/minutes ago"
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
}