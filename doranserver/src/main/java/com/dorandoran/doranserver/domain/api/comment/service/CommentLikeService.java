package com.dorandoran.doranserver.domain.api.comment.service;

import com.dorandoran.doranserver.domain.api.comment.dto.CommentDto;
import com.dorandoran.doranserver.domain.api.comment.domain.Comment;
import com.dorandoran.doranserver.domain.api.comment.domain.CommentLike;
import com.dorandoran.doranserver.domain.api.member.domain.Member;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface CommentLikeService {
    Integer findCommentLikeCnt(Comment comment);
    List<CommentLike> findCommentLikeListByCommentId(Comment comment);
    List<CommentLike> findByCommentId(Comment comment);
    void saveCommentLike(CommentLike commentLike);
    void deleteCommentLike(CommentLike commentLike);
    Boolean findCommentLikeResult(String userEmail, Comment commentId);
    Optional<CommentLike> findCommentLikeOne(String userEmail, Comment comment);
    void checkCommentLike(CommentDto.LikeComment commentLikeDto, UserDetails userDetails, Comment comment, Member member, Optional<CommentLike> commentLike);
}
