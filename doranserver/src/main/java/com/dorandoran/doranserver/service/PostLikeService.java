package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.PostLike;

import java.util.List;

public interface PostLikeService {
    public void savePostLike(PostLike postLike);
    public Integer findLIkeCnt(Post post);
    public Boolean findLikeResult(String email, Post postId);
    public List<PostLike> findByMemberId(String email);
    public void deletePostLike(PostLike postLike);
    public List<PostLike> findByPost(Post post);


}
