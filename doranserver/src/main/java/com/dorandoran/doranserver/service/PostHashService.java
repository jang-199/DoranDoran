package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.PostHash;

import java.util.List;
import java.util.Optional;

public interface PostHashService {
    public void savePostHash(PostHash postHash);
    List<PostHash> findPostHash(Long postId);
    public void deletePostHash(PostHash postHash);
}
