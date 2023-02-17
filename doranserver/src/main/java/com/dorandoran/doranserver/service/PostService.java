package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {
    public void savePost(Post post);

    public List<Post> findFirstPost();

    public List<Post> findPost(Long startPost);
    public Optional<Post> findSinglePost(Long postId);
    public void deletePost(Post post);
}
