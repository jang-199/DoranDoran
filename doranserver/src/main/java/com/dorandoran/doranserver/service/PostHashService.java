package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.HashTag;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.PostHash;

import java.util.List;
import java.util.Optional;

public interface PostHashService {
    public void savePostHash(PostHash postHash);
    List<PostHash> findPostHash(Post post);
    public void deletePostHash(PostHash postHash);

    Optional<PostHash> findTopOfPostHash(HashTag hashTag);
}
