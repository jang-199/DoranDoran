package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.CommentLike;

import java.util.List;

public interface CommentLikeService {
    public Integer findCommentLikeCnt(Long commentId);
    public List<CommentLike> findCommentLikeList(Long commentId);
}
