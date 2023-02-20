package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Comment;
import com.dorandoran.doranserver.entity.CommentLike;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.Reply;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    void saveComment(Comment comment);

    public Integer findCommentAndReplyCntByPostId(Post post);
    public List<Comment> findCommentByPost(Post post);
    public Optional<Comment> findCommentByCommentId(Long commentId);
    public void deleteComment(Comment comment);
    public void deleteAllCommentByPost(Optional<Comment> comment, List<CommentLike> commentLikeList, List<Reply> replyList);
}
