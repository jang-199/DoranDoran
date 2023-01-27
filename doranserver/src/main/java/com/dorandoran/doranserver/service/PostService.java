package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Post;

import java.util.Optional;

public interface PostService {
    public void savePost(Post post);
    public Optional<Post> findPost(Long postId);
}
