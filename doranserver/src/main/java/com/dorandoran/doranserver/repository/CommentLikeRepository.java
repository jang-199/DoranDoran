package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.Comment;
import com.dorandoran.doranserver.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike,Long> {
    List<CommentLike> findCommentLikeByCommentId(Long commentId);
    Optional<List<CommentLike>> findByCommentId(Comment comment);
    Optional<List<CommentLike>> findByMemberId_Email(String userEmail);
}