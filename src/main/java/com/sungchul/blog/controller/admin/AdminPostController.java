package com.sungchul.blog.controller.admin;

import com.sungchul.blog.entity.Category;
import com.sungchul.blog.entity.Post;
import com.sungchul.blog.entity.User;
import com.sungchul.blog.service.CategoryService;
import com.sungchul.blog.service.PostService;
import com.sungchul.blog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/posts")
public class AdminPostController extends BaseAdminController {

    private final PostService postService;
    private final CategoryService categoryService;
    private final UserService userService;

    @Autowired
    public AdminPostController(PostService postService, CategoryService categoryService, UserService userService) {
        this.postService = postService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @GetMapping
    public String listPosts(Model model, 
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           RedirectAttributes redirectAttributes) {
        // Check admin access
        if (!checkAdminAccess(redirectAttributes)) {
            return "redirect:/";
        }

        Page<Post> postPage = postService.getAllPosts(
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("totalItems", postPage.getTotalElements());

        return "admin/posts/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model, RedirectAttributes redirectAttributes) {
        // Check admin access
        if (!checkAdminAccess(redirectAttributes)) {
            return "redirect:/";
        }

        model.addAttribute("post", new Post());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("users", userService.getAllUsers());
        return "admin/posts/form";
    }

    @PostMapping("/create")
    public String createPost(@Valid @ModelAttribute("post") Post post,
                            BindingResult result,
                            @RequestParam("categoryId") Long categoryId,
                            @RequestParam("authorId") Long authorId,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        // Check admin access
        if (!checkAdminAccess(redirectAttributes)) {
            return "redirect:/";
        }

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("users", userService.getAllUsers());
            return "admin/posts/form";
        }

        Category category = categoryService.getCategoryById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + categoryId));
        User author = userService.getUserById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + authorId));

        post.setCategory(category);
        post.setAuthor(author);

        postService.createPost(post);
        redirectAttributes.addFlashAttribute("successMessage", "Post created successfully");
        return "redirect:/admin/posts";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        // Check admin access
        if (!checkAdminAccess(redirectAttributes)) {
            return "redirect:/";
        }

        Post post = postService.getPostById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + id));

        model.addAttribute("post", post);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("users", userService.getAllUsers());
        return "admin/posts/form";
    }

    @PostMapping("/edit/{id}")
    public String updatePost(@PathVariable("id") Long id,
                            @Valid @ModelAttribute("post") Post post,
                            BindingResult result,
                            @RequestParam("categoryId") Long categoryId,
                            @RequestParam("authorId") Long authorId,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        // Check admin access
        if (!checkAdminAccess(redirectAttributes)) {
            return "redirect:/";
        }

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("users", userService.getAllUsers());
            return "admin/posts/form";
        }

        Post existingPost = postService.getPostById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + id));

        Category category = categoryService.getCategoryById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + categoryId));
        User author = userService.getUserById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + authorId));

        existingPost.setTitle(post.getTitle());
        existingPost.setContent(post.getContent());
        existingPost.setCategory(category);
        existingPost.setAuthor(author);

        postService.updatePost(existingPost);
        redirectAttributes.addFlashAttribute("successMessage", "Post updated successfully");
        return "redirect:/admin/posts";
    }

    @GetMapping("/delete/{id}")
    public String deletePost(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        // Check admin access
        if (!checkAdminAccess(redirectAttributes)) {
            return "redirect:/";
        }

        postService.deletePost(id);
        redirectAttributes.addFlashAttribute("successMessage", "Post deleted successfully");
        return "redirect:/admin/posts";
    }
}
