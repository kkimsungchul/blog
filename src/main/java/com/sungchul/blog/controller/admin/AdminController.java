package com.sungchul.blog.controller.admin;

import com.sungchul.blog.service.CategoryService;
import com.sungchul.blog.service.PostService;
import com.sungchul.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController extends BaseAdminController {

    private final CategoryService categoryService;
    private final PostService postService;
    private final UserService userService;

    @Autowired
    public AdminController(CategoryService categoryService, PostService postService, UserService userService) {
        this.categoryService = categoryService;
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping
    public String adminDashboard(Model model, RedirectAttributes redirectAttributes) {
        // Additional security check
        if (!checkAdminAccess(redirectAttributes)) {
            return "redirect:/";
        }

        model.addAttribute("categoryCount", categoryService.getAllCategories().size());
        model.addAttribute("postCount", postService.getAllPosts().size());
        model.addAttribute("userCount", userService.getAllUsers().size());

        // Add system information
        model.addAttribute("javaVersion", System.getProperty("java.version"));
        model.addAttribute("osName", System.getProperty("os.name"));
        model.addAttribute("serverTime", java.time.LocalDateTime.now());

        return "admin/dashboard";
    }
}
