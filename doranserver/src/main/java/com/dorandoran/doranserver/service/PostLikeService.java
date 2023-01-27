package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.PostLike;

import java.util.Optional;

public interface PostLikeService {
    public void savePostLike(PostLike postLike);
    public Optional<PostLike> findByMemberId(String email);
    public void deletePostLike(String email);
}
