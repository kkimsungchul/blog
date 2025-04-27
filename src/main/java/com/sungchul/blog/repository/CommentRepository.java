package com.sungchul.blog.repository;

import com.sungchul.blog.entity.Comment;
import com.sungchul.blog.entity.Post;
import com.sungchul.blog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);
    Page<Comment> findByPost(Post post, Pageable pageable);
    List<Comment> findByUser(User user);
    Page<Comment> findByUser(User user, Pageable pageable);
    long countByPost(Post post);
}