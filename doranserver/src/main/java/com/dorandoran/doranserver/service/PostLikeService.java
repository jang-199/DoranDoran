package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.PostLike;

public interface PostLikeService {
    public void savePostLike(PostLike postLike);

    public Integer findLIkeCnt(Post post);

    public Boolean findLikeResult(Post post);
}
