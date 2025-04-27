package com.sungchul.blog.controller;

import com.sungchul.blog.entity.Comment;
import com.sungchul.blog.entity.Post;
import com.sungchul.blog.entity.User;
import com.sungchul.blog.service.CategoryService;
import com.sungchul.blog.service.CommentService;
import com.sungchul.blog.service.LikeService;
import com.sungchul.blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;
    private final CategoryService categoryService;

    @Autowired
    public PostController(PostService postService, CommentService commentService, 
                         LikeService likeService, CategoryService categoryService) {
        this.postService = postService;
        this.commentService = commentService;
        this.likeService = likeService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listPosts(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           Model model) {
        var postPage = postService.getAllPosts(
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("totalItems", postPage.getTotalElements());
        model.addAttribute("categories", categoryService.getAllCategories());

        return "posts/list";
    }

    @GetMapping("/{id}")
    public String getPostDetails(@PathVariable("id") Long id, Model model) {
        // Increment view count first
        Post post = postService.incrementViewCount(id);

        // Get related posts (up to 5)
        List<Post> relatedPosts = postService.getRelatedPosts(post, 5);

        model.addAttribute("post", post);
        model.addAttribute("comments", commentService.getCommentsByPost(post));
        model.addAttribute("commentCount", commentService.countCommentsByPost(post));
        model.addAttribute("likeCount", likeService.countLikesByPost(post));
        model.addAttribute("newComment", new Comment());
        model.addAttribute("relatedPosts", relatedPosts);

        return "posts/detail";
    }

    @GetMapping("/search")
    public String searchPosts(@RequestParam("query") String query,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             Model model) {
        var postPage = postService.searchPosts(
            query, 
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("totalItems", postPage.getTotalElements());
        model.addAttribute("searchQuery", query);
        model.addAttribute("categories", categoryService.getAllCategories());

        return "posts/search";
    }
}
