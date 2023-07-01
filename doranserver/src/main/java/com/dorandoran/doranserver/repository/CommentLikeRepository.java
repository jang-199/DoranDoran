package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.Comment;
import com.dorandoran.doranserver.entity.CommentLike;
import com.dorandoran.doranserver.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike,Long> {
    List<CommentLike> findCommentLikeByCommentId(Comment comment);
    List<CommentLike> findByCommentId(Comment comment);
    List<CommentLike> findByMemberId_Email(String userEmail);
    @Query("select cl from CommentLike cl where cl.memberId.email = :userEmail and cl.commentId = :commentId")
    Optional<CommentLike> findCommentLikeResult(@Param("userEmail") String userEmail,@Param("commentId") Comment commentId);
    @Query("select cl from CommentLike cl where cl.memberId = :member")
    List<CommentLike> findAllByMember(@Param("member") Member Member);
    @Query("select cl from CommentLike cl where cl.commentId = :comment")
    List<CommentLike> findAllByComment(@Param("comment") Comment comment);
}