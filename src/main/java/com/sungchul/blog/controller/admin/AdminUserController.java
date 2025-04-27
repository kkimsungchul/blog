package com.sungchul.blog.controller.admin;

import com.sungchul.blog.entity.User;
import com.sungchul.blog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;

    @Autowired
    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/users/form";
    }

    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute("user") User user,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/users/form";
        }

        // Check if username already exists
        if (userService.existsByUsername(user.getUsername())) {
            result.rejectValue("username", "error.user", "Username already exists");
            return "admin/users/form";
        }

        // Check if email already exists
        if (userService.existsByEmail(user.getEmail())) {
            result.rejectValue("email", "error.user", "Email already exists");
            return "admin/users/form";
        }

        userService.createUser(user);
        redirectAttributes.addFlashAttribute("successMessage", "User created successfully");
        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        model.addAttribute("user", user);
        return "admin/users/form";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable("id") Long id,
                            @Valid @ModelAttribute("user") User user,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/users/form";
        }

        User existingUser = userService.getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

        // Check if username is changed and already exists
        if (!existingUser.getUsername().equals(user.getUsername()) && 
            userService.existsByUsername(user.getUsername())) {
            result.rejectValue("username", "error.user", "Username already exists");
            return "admin/users/form";
        }

        // Check if email is changed and already exists
        if (!existingUser.getEmail().equals(user.getEmail()) && 
            userService.existsByEmail(user.getEmail())) {
            result.rejectValue("email", "error.user", "Email already exists");
            return "admin/users/form";
        }

        // Update user fields
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setName(user.getName());
        
        // Only update password if it's not empty
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(user.getPassword());
        }
        
        existingUser.setAdmin(user.isAdmin());
        
        userService.updateUser(existingUser);
        
        redirectAttributes.addFlashAttribute("successMessage", "User updated successfully");
        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        
        // Check if user has associated posts
        if (!user.getPosts().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Cannot delete user with associated posts. Remove posts first.");
            return "redirect:/admin/users";
        }
        
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully");
        return "redirect:/admin/users";
    }
}