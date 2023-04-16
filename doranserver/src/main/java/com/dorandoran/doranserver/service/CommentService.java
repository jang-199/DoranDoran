package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Comment;
import com.dorandoran.doranserver.entity.CommentLike;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.Reply;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    void saveComment(Comment comment);

    public Integer findCommentAndReplyCntByPostId(Post post);
    public List<Comment> findCommentByPost(Post post);
    public Optional<Comment> findCommentByCommentId(Long commentId);
    public void deleteComment(Comment comment);
    public void deleteAllCommentByPost(Optional<Comment> comment, List<CommentLike> commentLikeList, List<Reply> replyList);
    public List<Comment> findFirstComments(Post post);
    public List<Comment> findNextComments(Long postId, Long commentId);
}
