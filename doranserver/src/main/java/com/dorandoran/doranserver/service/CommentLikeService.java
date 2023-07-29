package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.dto.CommentDto;
import com.dorandoran.doranserver.entity.Comment;
import com.dorandoran.doranserver.entity.CommentLike;
import com.dorandoran.doranserver.entity.Member;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface CommentLikeService {
    public Integer findCommentLikeCnt(Comment comment);
    public List<CommentLike> findCommentLikeListByCommentId(Comment comment);
    public List<CommentLike> findByCommentId(Comment comment);
    public void saveCommentLike(CommentLike commentLike);
    public void deleteCommentLike(CommentLike commentLike);
    public Boolean findCommentLikeResult(String userEmail, Comment commentId);
    Optional<CommentLike> findCommentLikeOne(String userEmail, Comment comment);
    void checkCommentLike(CommentDto.LikeComment commentLikeDto, UserDetails userDetails, Comment comment, Member member, Optional<CommentLike> commentLike);
}
