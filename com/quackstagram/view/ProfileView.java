package com.quackstagram.view;

import com.quackstagram.controller.PictureController;
import com.quackstagram.controller.SessionController;
import com.quackstagram.controller.UserController;
import com.quackstagram.model.Picture;
import com.quackstagram.model.User;
import com.quackstagram.controller.NavigationController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * View for displaying a user's profile.
 */
public class ProfileView extends BaseView {
    private static final int PROFILE_IMAGE_SIZE = 80;
    private static final int GRID_IMAGE_SIZE = WIDTH / 3;
    
    private final UserController userController;
    private final PictureController pictureController;
    
    private User displayedUser;
    private JPanel contentPanel;
    private JPanel headerPanel;
    
    /**
     * Constructor for ProfileView
     * 
     * @param sessionController controller for user session management
     * @param navigationController controller for view navigation
     * @param userController controller for user operations
     * @param pictureController controller for picture operations
     */
    public ProfileView(SessionController sessionController, NavigationController navigationController,UserController userController, PictureController pictureController) {
        super(sessionController, navigationController);
        this.userController = userController;
        this.pictureController = pictureController;
        
        this.contentPanel = new JPanel();
        this.headerPanel = new JPanel();
        
        setTitle("Profile");
        initialize();
    }

    /**
     * Initializes the UI components
     */
    @Override
    public void initialize() {
        if (sessionController.isLoggedIn()) {
            displayProfile(sessionController.getCurrentUser().getUsername());
        }
    }

    /**
     * Refreshes the view with current data
     */
    @Override
    public void refreshView() {
        if (sessionController.getTemporaryData("viewOwnProfile") != null) {
            sessionController.removeTemporaryData("viewOwnProfile");
            if (sessionController.isLoggedIn()) {
                displayProfile(sessionController.getCurrentUser().getUsername());
                return;
            }
        }
        
        Object profileUsername = sessionController.getTemporaryData("profileUsername");
        if (profileUsername != null && profileUsername instanceof String) {
            sessionController.removeTemporaryData("profileUsername");
            
            displayProfile((String) profileUsername);
        } else if (displayedUser != null) {
            displayProfile(displayedUser.getUsername());
        } else if (sessionController.isLoggedIn()) {
            displayProfile(sessionController.getCurrentUser().getUsername());
        }
    }
    
    /**
     * Displays a user's profile
     * 
     * @param username the username of the user to display
     */
    public void displayProfile(String username) {
        User user = userController.getUser(username);
        if (user == null) {
            return;
        }
        
        this.displayedUser = user;
        
        getContentPane().removeAll();
        
        headerPanel = createProfileHeaderPanel(user);
        
        contentPanel = new JPanel(new GridLayout(0, 3, 5, 5));
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        loadUserPictures(user.getUsername());
        
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(createNavigationPanel(), BorderLayout.SOUTH);
        
        revalidate();
        repaint();
    }
    
    /**
     * Creates the profile header panel with user info
     * 
     * @param user the user to display
     * @return a JPanel containing the user profile header
     */
    private JPanel createProfileHeaderPanel(User user) {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.WHITE);
        
        JPanel topHeaderPanel = new JPanel(new BorderLayout(10, 0));
        topHeaderPanel.setBackground(new Color(249, 249, 249));
        
        ImageIcon profileIcon = new ImageIcon(new ImageIcon("img/storage/profile/" + user.getUsername() + ".png")
                .getImage().getScaledInstance(PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE, Image.SCALE_SMOOTH));
        JLabel profileImage = new JLabel(profileIcon);
        profileImage.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topHeaderPanel.add(profileImage, BorderLayout.WEST);
        
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        statsPanel.setBackground(new Color(249, 249, 249));
        statsPanel.add(createStatLabel(Integer.toString(user.getPostsCount()), "Posts"));
        statsPanel.add(createStatLabel(Integer.toString(user.getFollowersCount()), "Followers"));
        statsPanel.add(createStatLabel(Integer.toString(user.getFollowingCount()), "Following"));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0));
        
        JButton followButton;
        boolean isCurrentUser = sessionController.isLoggedIn() && 
                               sessionController.getCurrentUser().getUsername().equals(user.getUsername());
        
        if (isCurrentUser) {
            followButton = new JButton("Edit Profile");
        } else if (sessionController.isLoggedIn()) {
            boolean isFollowing = userController.getFollowing(sessionController.getCurrentUser().getUsername())
                                               .contains(user.getUsername());
            followButton = new JButton(isFollowing ? "Following" : "Follow");
            
            followButton.addActionListener(e -> {
                if (!isFollowing) {
                    userController.followUser(sessionController.getCurrentUser().getUsername(), user.getUsername());
                    followButton.setText("Following");
                }
            });
        } else {
            followButton = new JButton("Follow");
            followButton.setEnabled(false);
        }
        
        followButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        followButton.setFont(new Font("Arial", Font.BOLD, 12));
        followButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, followButton.getMinimumSize().height));
        followButton.setBackground(new Color(225, 228, 232));
        followButton.setForeground(Color.BLACK);
        followButton.setOpaque(true);
        followButton.setBorderPainted(false);
        followButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JPanel statsFollowPanel = new JPanel();
        statsFollowPanel.setLayout(new BoxLayout(statsFollowPanel, BoxLayout.Y_AXIS));
        statsFollowPanel.add(statsPanel);
        statsFollowPanel.add(followButton);
        topHeaderPanel.add(statsFollowPanel, BorderLayout.CENTER);
        
        headerPanel.add(topHeaderPanel);
        
        JPanel profileNameAndBioPanel = new JPanel();
        profileNameAndBioPanel.setLayout(new BorderLayout());
        profileNameAndBioPanel.setBackground(new Color(249, 249, 249));
        
        JLabel profileNameLabel = new JLabel(user.getUsername());
        profileNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        profileNameLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        
        JTextArea profileBio = new JTextArea(user.getBio());
        profileBio.setEditable(false);
        profileBio.setFont(new Font("Arial", Font.PLAIN, 12));
        profileBio.setBackground(new Color(249, 249, 249));
        profileBio.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        
        profileNameAndBioPanel.add(profileNameLabel, BorderLayout.NORTH);
        profileNameAndBioPanel.add(profileBio, BorderLayout.CENTER);
        
        headerPanel.add(profileNameAndBioPanel);
        
        return headerPanel;
    }
    
    /**
     * Loads the user's pictures into the grid
     * 
     * @param username the username of the user
     */
    private void loadUserPictures(String username) {
        contentPanel.removeAll();
        
        List<Picture> pictures = pictureController.getUserPictures(username);
        for (Picture picture : pictures) {
            ImageIcon imageIcon = new ImageIcon(new ImageIcon(picture.getImagePath())
                    .getImage().getScaledInstance(GRID_IMAGE_SIZE, GRID_IMAGE_SIZE, Image.SCALE_SMOOTH));
            JLabel imageLabel = new JLabel(imageIcon);
            
            imageLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    displayFullImage(picture);
                }
            });
            
            contentPanel.add(imageLabel);
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Displays a full-sized image when clicked
     * 
     * @param picture the picture to display
     */
    private void displayFullImage(Picture picture) {
        JDialog dialog = new JDialog(this, picture.getCaption(), true);
        dialog.setLayout(new BorderLayout());
        
        ImageIcon imageIcon = new ImageIcon(picture.getImagePath());
        JLabel imageLabel = new JLabel(imageIcon);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(new JLabel("Posted by: " + picture.getUsername()));
        infoPanel.add(new JLabel(picture.getCaption()));
        infoPanel.add(new JLabel("Likes: " + picture.getLikesCount()));
        
        JButton likeButton = new JButton("Like");
        likeButton.addActionListener(e -> {
            if (sessionController.isLoggedIn()) {
                pictureController.likePicture(sessionController.getCurrentUser().getUsername(), 
                                            picture.getImageId());
                dialog.dispose();
                refreshView();
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
     * Creates a label for profile statistics
     * 
     * @param number the number to display
     * @param text the text label
     * @return a formatted JLabel
     */
    private JLabel createStatLabel(String number, String text) {
        JLabel label = new JLabel("<html><div style='text-align: center;'>" + number + "<br/>" + text + "</div></html>", 
                                SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(Color.BLACK);
        return label;
    }
}