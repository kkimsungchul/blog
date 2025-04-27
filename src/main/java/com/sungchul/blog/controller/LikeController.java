package com.sungchul.blog.controller;

import com.sungchul.blog.entity.Post;
import com.sungchul.blog.entity.User;
import com.sungchul.blog.service.LikeService;
import com.sungchul.blog.service.PostService;
import com.sungchul.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/likes")
public class LikeController {

    private final LikeService likeService;
    private final PostService postService;
    private final UserService userService;

    @Autowired
    public LikeController(LikeService likeService, PostService postService, UserService userService) {
        this.likeService = likeService;
        this.postService = postService;
        this.userService = userService;
    }

    @PostMapping("/toggle")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleLike(@RequestParam("postId") Long postId,
                                                         @RequestParam("userId") Long userId) {
        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + postId));
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + userId));

        likeService.toggleLike(user, post);
        long likeCount = likeService.countLikesByPost(post);
        boolean userLiked = likeService.existsByUserAndPost(user, post);

        Map<String, Object> response = new HashMap<>();
        response.put("likeCount", likeCount);
        response.put("userLiked", userLiked);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getLikeStatus(@RequestParam("postId") Long postId,
                                                           @RequestParam("userId") Long userId) {
        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + postId));
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + userId));

        long likeCount = likeService.countLikesByPost(post);
        boolean userLiked = likeService.existsByUserAndPost(user, post);

        Map<String, Object> response = new HashMap<>();
        response.put("likeCount", likeCount);
        response.put("userLiked", userLiked);

        return ResponseEntity.ok(response);
    }
}