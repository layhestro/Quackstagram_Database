package com.quackstagram.view;

import com.quackstagram.controller.SessionController;
import com.quackstagram.util.NavigationController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Abstract base class for all views in the application.
 * Provides common functionality and UI elements.
 */
public abstract class BaseView extends JFrame {
    protected static final int WIDTH = 300;
    protected static final int HEIGHT = 500;
    protected static final int NAV_ICON_SIZE = 20;
    
    protected final SessionController sessionController;
    protected final NavigationController navigationController;
    
    /**
     * Constructor for BaseView
     * 
     * @param sessionController controller for user session management
     * @param navigationController controller for view navigation
     */
    public BaseView(SessionController sessionController, NavigationController navigationController) {
        this.sessionController = sessionController;
        this.navigationController = navigationController;
        
        setTitle("Quackstagram");
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }
    
    /**
     * Initialize the UI components
     */
    public abstract void initialize();
    
    /**
     * Refresh the view with current data
     */
    public abstract void refreshView();
    
    /**
     * Navigate to another view
     * 
     * @param viewName the name of the view to navigate to
     */
    protected void navigateTo(String viewName) {
        setVisible(false);
        navigationController.navigateTo(viewName);
    }
    
    /**
     * Create standard header panel
     * 
     * @param title the title to display in the header
     * @return a JPanel containing the header
     */
    protected JPanel createHeaderPanel(String title) {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(51, 51, 51));
        JLabel headerLabel = new JLabel(title + " 🐥");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        headerPanel.setPreferredSize(new Dimension(WIDTH, 40));
        return headerPanel;
    }
    
    /**
     * Create standard navigation panel
     * 
     * @return a JPanel containing the navigation bar
     */
    protected JPanel createNavigationPanel() {
        JPanel navigationPanel = new JPanel();
        navigationPanel.setBackground(new Color(249, 249, 249));
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.X_AXIS));
        navigationPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        navigationPanel.add(createIconButton("img/icons/home.png", e -> navigateTo("home")));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("img/icons/search.png", e -> navigateTo("explore")));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("img/icons/add.png", e -> navigateTo("upload")));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("img/icons/heart.png", e -> navigateTo("notifications")));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("img/icons/profile.png", e -> navigateTo("profile")));

        return navigationPanel;
    }
    
    /**
     * Create an icon button for navigation
     * 
     * @param iconPath the path to the icon image
     * @param actionListener the action listener for the button
     * @return a JButton with the specified icon and action
     */
    protected JButton createIconButton(String iconPath, ActionListener actionListener) {
        ImageIcon iconOriginal = new ImageIcon(iconPath);
        Image iconScaled = iconOriginal.getImage().getScaledInstance(NAV_ICON_SIZE, NAV_ICON_SIZE, Image.SCALE_SMOOTH);
        JButton button = new JButton(new ImageIcon(iconScaled));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        button.addActionListener(actionListener);
        return button;
    }
}