package com.sungchul.blog.service;

import com.sungchul.blog.entity.Comment;
import com.sungchul.blog.entity.Post;
import com.sungchul.blog.entity.User;
import com.sungchul.blog.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Transactional(readOnly = true)
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPost(Post post) {
        return commentRepository.findByPost(post);
    }

    @Transactional(readOnly = true)
    public Page<Comment> getCommentsByPost(Post post, Pageable pageable) {
        return commentRepository.findByPost(post, pageable);
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByUser(User user) {
        return commentRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public Page<Comment> getCommentsByUser(User user, Pageable pageable) {
        return commentRepository.findByUser(user, pageable);
    }

    @Transactional(readOnly = true)
    public long countCommentsByPost(Post post) {
        return commentRepository.countByPost(post);
    }

    @Transactional
    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Transactional
    public Comment updateComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}