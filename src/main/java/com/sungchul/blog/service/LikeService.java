package com.sungchul.blog.service;

import com.sungchul.blog.entity.Like;
import com.sungchul.blog.entity.Post;
import com.sungchul.blog.entity.User;
import com.sungchul.blog.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LikeService {

    private final LikeRepository likeRepository;

    @Autowired
    public LikeService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    @Transactional(readOnly = true)
    public List<Like> getAllLikes() {
        return likeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Like> getLikeById(Long id) {
        return likeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Like> getLikesByPost(Post post) {
        return likeRepository.findByPost(post);
    }

    @Transactional(readOnly = true)
    public List<Like> getLikesByUser(User user) {
        return likeRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public Optional<Like> getLikeByUserAndPost(User user, Post post) {
        return likeRepository.findByUserAndPost(user, post);
    }

    @Transactional(readOnly = true)
    public boolean existsByUserAndPost(User user, Post post) {
        return likeRepository.existsByUserAndPost(user, post);
    }

    @Transactional(readOnly = true)
    public long countLikesByPost(Post post) {
        return likeRepository.countByPost(post);
    }

    @Transactional
    public Like createLike(Like like) {
        return likeRepository.save(like);
    }

    @Transactional
    public void deleteLike(Long id) {
        likeRepository.deleteById(id);
    }

    @Transactional
    public void deleteLikeByUserAndPost(User user, Post post) {
        likeRepository.deleteByUserAndPost(user, post);
    }

    @Transactional
    public Like toggleLike(User user, Post post) {
        Optional<Like> existingLike = likeRepository.findByUserAndPost(user, post);
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            return null;
        } else {
            Like newLike = new Like();
            newLike.setUser(user);
            newLike.setPost(post);
            return likeRepository.save(newLike);
        }
    }
}