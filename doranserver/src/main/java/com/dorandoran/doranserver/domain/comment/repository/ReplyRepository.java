package com.dorandoran.doranserver.domain.comment.repository;

import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.comment.domain.Reply;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply,Long> {
    @Query("select r from Reply r where r.commentId = :commentId")
    List<Reply> findReplyCntByComment(@Param("commentId") Comment comment);
    @Query("select r from Reply r " +
            "join fetch r.commentId " +
            "where r.commentId in :commentList and r.checkDelete = false " +
            "order by r.commentId.commentId desc ")
    List<Reply> findReplyCntByCommentList(@Param("commentList") List<Comment> commentList);

    @Query("select r from Reply r where r.commentId in :commentList")
    List<Reply> findReplyByCommentList(@Param("commentList") List<Comment> commentList);

    @Query("select r from Reply r " +
            "where r.commentId = :commentId " +
            "order by r.replyId desc")
    List<Reply> findFirstReplies(@Param("commentId") Comment comment, Pageable pageable);

    @Query("select r from Reply r " +
            "where r.commentId.commentId = :commentId  and r.replyId < :replyId " +
            "order by r.replyId desc")
    List<Reply> findNextReplies(@Param("commentId") Long commentId, @Param("replyId") Long replyId, Pageable pageable);

    @Query("select r from Reply r join fetch r.memberId m " +
            "where r.commentId in :commentId " +
            "order by r.replyId desc")
    List<Reply> findFirstRepliesFetchMember(@Param("commentId") Comment comment, Pageable pageable);

    @Query("select r from Reply r where r.memberId = :member")
    List<Reply> findAllByMember(@Param("member") Member member);
    @Query("select r from Reply r where r.commentId = :comment")
    List<Reply> findAllByComment(@Param("comment") Comment comment);

    @Query("select distinct r.memberId from Reply r where r.commentId = :commentId")
    List<Member> findReplyMemberByCommentId(@Param("commentId") Comment comment);

    @Query("select r from Reply r join fetch r.memberId where r.isLocked = true order by r.replyId desc")
    List<Reply> findReplyInAdmin(PageRequest of);

    @Query("select r from Reply r join fetch r.memberId where r.commentId = :commentId order by r.replyId desc")
    List<Reply> findReplyInAdminDetail(@Param("commentId") Comment comment);

    @Query("select r from Reply r join fetch r.memberId where r.replyId = :replyId")
    Optional<Reply> findFetchMember(@Param("replyId") Long replyId);

    @Query("select r from Reply r where r.commentId in :commentList")
    List<Reply> findCommentListTest(@Param("commentList") List<Comment> commentList);

    @Query(value = "select * from (" +
            "   select *, rank() over (partition by comment_id order by reply_id desc) as rn " +
            "   from reply) as ranking " +
            "where ranking.rn <= 11 and ranking.comment_id in :comments",nativeQuery = true)
    List<Reply> findRankRepliesByComments(@Param("comments") List<Long> comments);
}
