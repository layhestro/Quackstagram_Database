package com.quackstagram.view;

import com.quackstagram.controller.NotificationController;
import com.quackstagram.controller.SessionController;
import com.quackstagram.model.Notification;
import com.quackstagram.model.NotificationType;
import com.quackstagram.controller.NavigationController;

import javax.swing.*;
import java.awt.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.time.LocalDateTime;

/**
 * View for displaying user notifications.
 */
public class NotificationsView extends BaseView {
    private final NotificationController notificationController;
    private JPanel contentPanel;

    /**
     * Constructor for NotificationsView
     * 
     * @param sessionController controller for user session management
     * @param navigationController controller for view navigation
     * @param notificationController controller for notification operations
     */
    public NotificationsView(SessionController sessionController, NavigationController navigationController,
                            NotificationController notificationController) {
        super(sessionController, navigationController);
        this.notificationController = notificationController;
        
        setTitle("Notifications");
        initialize();
    }

    /**
     * Initializes the UI components
     */
    @Override
    public void initialize() {
        getContentPane().removeAll();
        
        JPanel headerPanel = createHeaderPanel("Notifications");
        add(headerPanel, BorderLayout.NORTH);
        
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);
        
        JPanel navigationPanel = createNavigationPanel();
        add(navigationPanel, BorderLayout.SOUTH);
        
        loadNotifications();
        
        revalidate();
        repaint();
    }

    /**
     * Refreshes the view with current data
     */
    @Override
    public void refreshView() {
        contentPanel.removeAll();
        loadNotifications();
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Loads notifications for the current user
     */
    private void loadNotifications() {
        if (!sessionController.isLoggedIn()) {
            displayLoginPrompt();
            return;
        }
        
        List<Notification> notifications = notificationController.getNotifications(
                sessionController.getCurrentUser().getUsername());
        
        if (notifications.isEmpty()) {
            displayEmptyNotificationsMessage();
            return;
        }
        
        for (Notification notification : notifications) {
            contentPanel.add(createNotificationPanel(notification));
        }
    }
    
    /**
     * Creates a panel for displaying a notification
     * 
     * @param notification the notification to display
     * @return a panel containing the notification
     */
    private JPanel createNotificationPanel(Notification notification) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        
        JLabel iconLabel = new JLabel();
        if (notification.getType() == NotificationType.LIKE) {
            iconLabel.setText("❤");
            iconLabel.setForeground(new Color(255, 90, 95));
        } else if (notification.getType() == NotificationType.FOLLOW) {
            iconLabel.setText("👤");
        } else if (notification.getType() == NotificationType.COMMENT) {
            iconLabel.setText("💬");
        }
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        panel.add(iconLabel, BorderLayout.WEST);
        
        JPanel messagePanel = new JPanel(new BorderLayout());
        
        String message = formatNotificationMessage(notification);
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        String timeAgo = getTimeAgo(notification.getTimestamp());
        JLabel timeLabel = new JLabel(timeAgo);
        timeLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        messagePanel.add(timeLabel, BorderLayout.SOUTH);
        
        panel.add(messagePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Formats a notification message based on its type
     * 
     * @param notification the notification to format
     * @return a formatted message string
     */
    private String formatNotificationMessage(Notification notification) {
        String message;
        
        if (notification.getType() == NotificationType.LIKE) {
            message = notification.getSenderUsername() + " liked your picture";
        } else if (notification.getType() == NotificationType.FOLLOW) {
            message = notification.getSenderUsername() + " followed you";
        } else if (notification.getType() == NotificationType.COMMENT) {
            message = notification.getSenderUsername() + " commented on your picture";
        } else {
            message = "You have a new notification from " + notification.getSenderUsername();
        }
        
        return message;
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
    
    /**
     * Displays a message when user is not logged in
     */
    private void displayLoginPrompt() {
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));
        
        JLabel messageLabel = new JLabel("Please sign in to see your notifications");
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JButton signInButton = new JButton("Sign In");
        signInButton.addActionListener(e -> navigateTo("auth"));
        
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        messagePanel.add(signInButton, BorderLayout.SOUTH);
        
        contentPanel.add(messagePanel);
    }
    
    /**
     * Displays a message when there are no notifications
     */
    private void displayEmptyNotificationsMessage() {
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));
        
        JLabel messageLabel = new JLabel("You don't have any notifications yet");
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        contentPanel.add(messagePanel);
    }
}