package com.sungchul.blog.controller;

import com.sungchul.blog.service.CategoryService;
import com.sungchul.blog.service.PostService;
import com.sungchul.blog.service.VisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final PostService postService;
    private final CategoryService categoryService;
    private final VisitorService visitorService;

    @Autowired
    public HomeController(PostService postService, CategoryService categoryService, VisitorService visitorService) {
        this.postService = postService;
        this.categoryService = categoryService;
        this.visitorService = visitorService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("recentPosts", postService.getRecentPosts());
        model.addAttribute("categories", categoryService.getAllCategories());

        // Add top posts by view count in the last week
        model.addAttribute("topWeeklyPosts", postService.getTopPostsByViewCountInLastWeek(3));

        // Add top posts by total view count
        model.addAttribute("topAllTimePosts", postService.getTopPostsByTotalViewCount(3));

        return "home";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
