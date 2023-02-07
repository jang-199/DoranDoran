package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Comment;
import com.dorandoran.doranserver.entity.Post;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    void saveComment(Comment comment);

    public Integer findCommentAndReplyCntByPostId(Post post);
    public Optional<List<Comment>> findCommentByPost(Post post);
    public Optional<Comment> findCommentByCommentId(Long commentId);
}
