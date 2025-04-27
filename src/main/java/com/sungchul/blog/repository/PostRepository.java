package com.sungchul.blog.repository;

import com.sungchul.blog.entity.Category;
import com.sungchul.blog.entity.Post;
import com.sungchul.blog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAll(Pageable pageable);
    Page<Post> findByCategory(Category category, Pageable pageable);
    Page<Post> findByAuthor(User author, Pageable pageable);
    Page<Post> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
    List<Post> findTop5ByOrderByCreatedAtDesc();

    // Find posts by the same author, excluding the current post
    List<Post> findTop3ByAuthorAndIdNot(User author, Long postId, Pageable pageable);

    // Find posts in the same category, excluding the current post
    List<Post> findTop3ByCategoryAndIdNot(Category category, Long postId, Pageable pageable);

    // Find related posts based on title or content similarity, excluding the current post
    @Query("SELECT p FROM Post p WHERE p.id != :postId AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY p.createdAt DESC")
    List<Post> findRelatedPostsByKeyword(@Param("postId") Long postId, @Param("keyword") String keyword, Pageable pageable);
}
