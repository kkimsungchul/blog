package com.sungchul.blog.controller;

import com.sungchul.blog.entity.Category;
import com.sungchul.blog.service.CategoryService;
import com.sungchul.blog.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;
    private final PostService postService;

    @Autowired
    public CategoryController(CategoryService categoryService, PostService postService) {
        this.categoryService = categoryService;
        this.postService = postService;
    }

    @GetMapping
    public String listCategories(Model model) {
        logger.info("Accessing category list page");
        var categories = categoryService.getAllCategories();
        logger.debug("Found {} categories", categories.size());
        model.addAttribute("categories", categories);
        return "categories/list";
    }

    @GetMapping("/{id}")
    public String getCategoryPosts(@PathVariable("id") Long id,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  Model model) {
        logger.info("Accessing posts for category with ID: {}", id);

        try {
            Category category = categoryService.getCategoryById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));

            logger.debug("Found category: {}", category.getName());

            var postPage = postService.getPostsByCategory(
                category, 
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
            );

            logger.debug("Found {} posts in category, page {} of {}", 
                    postPage.getNumberOfElements(), page + 1, postPage.getTotalPages());

            model.addAttribute("category", category);
            model.addAttribute("posts", postPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", postPage.getTotalPages());
            model.addAttribute("totalItems", postPage.getTotalElements());

            return "categories/posts";
        } catch (IllegalArgumentException e) {
            logger.error("Error accessing category: {}", e.getMessage());
            throw e;
        }
    }
}
