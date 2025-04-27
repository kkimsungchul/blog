package com.sungchul.blog.interceptor;

import com.sungchul.blog.service.VisitorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class VisitorCounterInterceptor implements HandlerInterceptor {

    private final VisitorService visitorService;
    private static final String VISITOR_COUNTED_SESSION_KEY = "VISITOR_COUNTED";

    @Autowired
    public VisitorCounterInterceptor(VisitorService visitorService) {
        this.visitorService = visitorService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Check if this is a page request (not a resource or API call)
        if (isPageRequest(request)) {
            HttpSession session = request.getSession();
            
            // Check if this visitor has already been counted in this session
            if (session.getAttribute(VISITOR_COUNTED_SESSION_KEY) == null) {
                // Increment visitor count
                visitorService.incrementVisitorCount();
                
                // Mark this visitor as counted in this session
                session.setAttribute(VISITOR_COUNTED_SESSION_KEY, true);
            }
        }
        
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // Add visitor counts to model for all page requests
        if (modelAndView != null && isPageRequest(request)) {
            modelAndView.addObject("todayVisitorCount", visitorService.getTodayVisitorCount());
            modelAndView.addObject("totalVisitorCount", visitorService.getTotalVisitorCount());
        }
    }

    private boolean isPageRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String contentType = request.getContentType();
        
        // Exclude API calls, resources, and AJAX requests
        return !requestURI.startsWith("/api/") 
                && !requestURI.contains(".") 
                && (contentType == null || contentType.contains("text/html"));
    }
}