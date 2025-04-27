package com.sungchul.blog.controller.admin;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Base controller for all admin controllers
 * Provides common functionality for admin controllers
 */
public abstract class BaseAdminController {
    
    /**
     * Check if the current user has admin privileges
     * @param redirectAttributes RedirectAttributes to add error message if needed
     * @return true if the user is an admin, false otherwise
     */
    protected boolean checkAdminAccess(RedirectAttributes redirectAttributes) {
        if (!isAdmin()) {
            redirectAttributes.addFlashAttribute("errorMessage", "You do not have permission to access the admin area");
            return false;
        }
        return true;
    }
    
    /**
     * Helper method to check if the current user has admin privileges
     */
    protected boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && 
               authentication.isAuthenticated() && 
               authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));
    }
}