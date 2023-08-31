package com.dorandoran.doranserver.domain.comment.service;

import com.dorandoran.doranserver.domain.comment.dto.CommentDto;
import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.comment.domain.CommentLike;
import com.dorandoran.doranserver.domain.member.domain.Member;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface CommentLikeService {
    HashMap<Comment, Long> findCommentLikeCnt(List<Comment> commentList);
    List<CommentLike> findCommentLikeListByCommentId(Comment comment);
    List<CommentLike> findByCommentId(Comment comment);
    void saveCommentLike(CommentLike commentLike);
    void deleteCommentLike(CommentLike commentLike);
    HashMap<Comment, Boolean> findCommentLikeResult(String userEmail, List<Comment> commentList);
    Optional<CommentLike> findCommentLikeOne(String userEmail, Comment comment);
    void checkCommentLike(CommentDto.LikeComment commentLikeDto, UserDetails userDetails, Comment comment, Member member, Optional<CommentLike> commentLike);
}
