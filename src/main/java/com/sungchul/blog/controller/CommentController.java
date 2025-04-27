package com.sungchul.blog.controller;

import com.sungchul.blog.entity.Comment;
import com.sungchul.blog.entity.Post;
import com.sungchul.blog.entity.User;
import com.sungchul.blog.service.CommentService;
import com.sungchul.blog.service.PostService;
import com.sungchul.blog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;
    private final UserService userService;

    @Autowired
    public CommentController(CommentService commentService, PostService postService, UserService userService) {
        this.commentService = commentService;
        this.postService = postService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public String createComment(@Valid @ModelAttribute("newComment") Comment comment,
                               BindingResult result,
                               @RequestParam("postId") Long postId,
                               @RequestParam("userId") Long userId,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Comment validation failed");
            return "redirect:/posts/" + postId;
        }

        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + postId));
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + userId));

        comment.setPost(post);
        comment.setUser(user);
        
        commentService.createComment(comment);
        redirectAttributes.addFlashAttribute("successMessage", "Comment added successfully");
        return "redirect:/posts/" + postId;
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Comment comment = commentService.getCommentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment Id:" + id));
        
        model.addAttribute("comment", comment);
        return "comments/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateComment(@PathVariable("id") Long id,
                               @Valid @ModelAttribute("comment") Comment comment,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "comments/edit";
        }

        Comment existingComment = commentService.getCommentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment Id:" + id));
        
        existingComment.setContent(comment.getContent());
        commentService.updateComment(existingComment);
        
        redirectAttributes.addFlashAttribute("successMessage", "Comment updated successfully");
        return "redirect:/posts/" + existingComment.getPost().getId();
    }

    @GetMapping("/delete/{id}")
    public String deleteComment(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Comment comment = commentService.getCommentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment Id:" + id));
        
        Long postId = comment.getPost().getId();
        commentService.deleteComment(id);
        
        redirectAttributes.addFlashAttribute("successMessage", "Comment deleted successfully");
        return "redirect:/posts/" + postId;
    }
}