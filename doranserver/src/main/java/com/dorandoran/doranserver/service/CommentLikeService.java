package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Comment;
import com.dorandoran.doranserver.entity.CommentLike;

import java.util.List;
import java.util.Optional;

public interface CommentLikeService {
    public Integer findCommentLikeCnt(Long commentId);
    public List<CommentLike> findCommentLikeListByCommentId(Long commentId);
    public Optional<List<CommentLike>> findByCommentId(Comment comment);
    public void saveCommentLike(CommentLike commentLike);
    public void deleteCommentLike(CommentLike commentLike);
}
