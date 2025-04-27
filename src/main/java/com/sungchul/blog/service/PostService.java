package com.sungchul.blog.service;

import com.sungchul.blog.entity.Category;
import com.sungchul.blog.entity.Post;
import com.sungchul.blog.entity.User;
import com.sungchul.blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional(readOnly = true)
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Post> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Page<Post> getPostsByCategory(Category category, Pageable pageable) {
        return postRepository.findByCategory(category, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Post> getPostsByAuthor(User author, Pageable pageable) {
        return postRepository.findByAuthor(author, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Post> searchPosts(String searchTerm, Pageable pageable) {
        return postRepository.findByTitleContainingOrContentContaining(searchTerm, searchTerm, pageable);
    }

    @Transactional(readOnly = true)
    public List<Post> getRecentPosts() {
        return postRepository.findTop5ByOrderByCreatedAtDesc();
    }

    @Transactional
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    @Transactional
    public Post updatePost(Post post) {
        return postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    /**
     * Get related posts for a given post
     * This method combines different strategies to find related posts:
     * 1. Posts in the same category
     * 2. Posts by the same author
     * 3. Posts with similar content (based on keywords from the title)
     * 
     * @param post The post to find related posts for
     * @param maxResults The maximum number of related posts to return
     * @return A list of related posts
     */
    @Transactional(readOnly = true)
    public List<Post> getRelatedPosts(Post post, int maxResults) {
        Set<Post> relatedPosts = new HashSet<>();

        // Get posts from the same category
        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Post> categoryPosts = postRepository.findTop3ByCategoryAndIdNot(post.getCategory(), post.getId(), pageable);
        relatedPosts.addAll(categoryPosts);

        // If we don't have enough posts, get posts by the same author
        if (relatedPosts.size() < maxResults) {
            List<Post> authorPosts = postRepository.findTop3ByAuthorAndIdNot(post.getAuthor(), post.getId(), pageable);
            relatedPosts.addAll(authorPosts);
        }

        // If we still don't have enough posts, try to find posts with similar content
        if (relatedPosts.size() < maxResults) {
            // Extract keywords from the title
            String[] titleWords = post.getTitle().split("\\s+");
            for (String word : titleWords) {
                if (word.length() > 3 && relatedPosts.size() < maxResults) {  // Only use words longer than 3 characters
                    List<Post> keywordPosts = postRepository.findRelatedPostsByKeyword(
                            post.getId(), word, PageRequest.of(0, 2));
                    relatedPosts.addAll(keywordPosts);

                    if (relatedPosts.size() >= maxResults) {
                        break;
                    }
                }
            }
        }

        // If we still don't have enough posts, get the most recent posts
        if (relatedPosts.size() < maxResults) {
            List<Post> recentPosts = getRecentPosts();
            recentPosts.removeIf(p -> p.getId().equals(post.getId()) || relatedPosts.contains(p));
            relatedPosts.addAll(recentPosts.subList(0, Math.min(recentPosts.size(), maxResults - relatedPosts.size())));
        }

        // Convert to list and limit to maxResults
        return relatedPosts.stream()
                .limit(maxResults)
                .collect(Collectors.toList());
    }
}
