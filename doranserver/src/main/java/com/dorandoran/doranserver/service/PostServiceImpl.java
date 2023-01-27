package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    @Override
    public void savePost(Post post) {
        postRepository.save(post);
    }
    @Override
    public Optional<Post> findPost(Long postId) {
        return postRepository.findById(postId);
    }
}