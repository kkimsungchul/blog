package com.sungchul.blog.controller.admin;

import com.sungchul.blog.entity.Category;
import com.sungchul.blog.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController extends BaseAdminController {

    private final CategoryService categoryService;

    @Autowired
    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listCategories(Model model, RedirectAttributes redirectAttributes) {
        // Check admin access
        if (!checkAdminAccess(redirectAttributes)) {
            return "redirect:/";
        }

        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/categories/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model, RedirectAttributes redirectAttributes) {
        // Check admin access
        if (!checkAdminAccess(redirectAttributes)) {
            return "redirect:/";
        }

        model.addAttribute("category", new Category());
        return "admin/categories/form";
    }

    @PostMapping("/create")
    public String createCategory(@Valid @ModelAttribute("category") Category category,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        // Check admin access
        if (!checkAdminAccess(redirectAttributes)) {
            return "redirect:/";
        }

        if (result.hasErrors()) {
            return "admin/categories/form";
        }

        if (categoryService.existsByName(category.getName())) {
            result.rejectValue("name", "error.category", "Category name already exists");
            return "admin/categories/form";
        }

        categoryService.createCategory(category);
        redirectAttributes.addFlashAttribute("successMessage", "Category created successfully");
        return "redirect:/admin/categories";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        // Check admin access
        if (!checkAdminAccess(redirectAttributes)) {
            return "redirect:/";
        }

        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));
        model.addAttribute("category", category);
        return "admin/categories/form";
    }

    @PostMapping("/edit/{id}")
    public String updateCategory(@PathVariable("id") Long id,
                                @Valid @ModelAttribute("category") Category category,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        // Check admin access
        if (!checkAdminAccess(redirectAttributes)) {
            return "redirect:/";
        }

        if (result.hasErrors()) {
            return "admin/categories/form";
        }

        Category existingCategory = categoryService.getCategoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));

        // Check if name is changed and already exists
        if (!existingCategory.getName().equals(category.getName()) && 
            categoryService.existsByName(category.getName())) {
            result.rejectValue("name", "error.category", "Category name already exists");
            return "admin/categories/form";
        }

        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());
        categoryService.updateCategory(existingCategory);

        redirectAttributes.addFlashAttribute("successMessage", "Category updated successfully");
        return "redirect:/admin/categories";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        // Check admin access
        if (!checkAdminAccess(redirectAttributes)) {
            return "redirect:/";
        }

        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));

        if (!category.getPosts().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Cannot delete category with associated posts. Remove posts first.");
            return "redirect:/admin/categories";
        }

        categoryService.deleteCategory(id);
        redirectAttributes.addFlashAttribute("successMessage", "Category deleted successfully");
        return "redirect:/admin/categories";
    }
}
