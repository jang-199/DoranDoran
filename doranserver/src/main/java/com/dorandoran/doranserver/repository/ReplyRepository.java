package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.Comment;
import com.dorandoran.doranserver.entity.Reply;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply,Long> {
    @Query("select c from Reply c where c.commentId = :commentId")
    List<Reply> findReplyCntByComment(@Param("commentId") Comment comment);

    @Query("select r from Reply r " +
            "where r.commentId = :commentId " +
            "order by r.replyId desc")
    List<Reply> findFirstReplies(@Param("commentId") Comment comment, Pageable pageable);

    @Query("select r from Reply r " +
            "where r.commentId.commentId = :commentId  and r.replyId < :replyId " +
            "order by r.replyId desc")
    List<Reply> findNextReplies(@Param("commentId") Long commentId, @Param("replyId") Long replyId, Pageable pageable);
}
