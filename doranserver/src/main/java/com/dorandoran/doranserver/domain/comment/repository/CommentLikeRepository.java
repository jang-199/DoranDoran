package com.dorandoran.doranserver.domain.comment.repository;

import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.comment.domain.CommentLike;
import com.dorandoran.doranserver.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike,Long> {
    List<CommentLike> findCommentLikeByCommentId(@Param("comment") Comment comment);
    @Query("select cl from CommentLike cl where cl.commentId in :comment and cl.checkDelete = false")
    List<CommentLike> findUnDeletedByCommentId(@Param("comment") List<Comment> comment);
    List<CommentLike> findByCommentId(Comment comment);
    @Query("select cl from CommentLike cl where cl.memberId.email = :userEmail and cl.commentId = :commentId")
    Optional<CommentLike> findCommentLikeResult(@Param("userEmail") String userEmail,@Param("commentId") Comment commentId);
    @Query("select cl from CommentLike cl " +
            "where cl.memberId.email = :userEmail and cl.commentId in :commentId and cl.checkDelete=false " +
            "order by cl.commentId.commentId desc")
    List<CommentLike> findCommentList(@Param("userEmail") String userEmail,@Param("commentId") List<Comment> commentId);
    @Query("select cl from CommentLike cl where cl.memberId = :member")
    List<CommentLike> findAllByMember(@Param("member") Member Member);
    @Query("select cl from CommentLike cl where cl.commentId = :comment")
    List<CommentLike> findAllByComment(@Param("comment") Comment comment);
}