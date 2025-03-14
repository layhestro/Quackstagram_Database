// File: com/quackstagram/util/NavigationController.java
package com.quackstagram.util;

import com.quackstagram.view.BaseView;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for navigating between views
 */
public class NavigationController {
    private final Map<String, BaseView> views = new HashMap<>();
    private BaseView currentView;
    
    /**
     * Navigate to a view by name
     * 
     * @param viewName the name of the view to navigate to
     */
    public void navigateTo(String viewName) {
        // Hide current view if any
        if (currentView != null) {
            currentView.setVisible(false);
        }
        
        // Show the requested view
        BaseView view = views.get(viewName);
        if (view != null) {
            view.refreshView();
            view.setVisible(true);
            currentView = view;
        }
    }
    
    /**
     * Register a view with a name
     * 
     * @param viewName the name to register the view with
     * @param view the view to register
     */
    public void registerView(String viewName, BaseView view) {
        views.put(viewName, view);
    }
}

