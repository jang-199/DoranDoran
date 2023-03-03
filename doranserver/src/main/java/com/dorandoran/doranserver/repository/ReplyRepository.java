package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.Comment;
import com.dorandoran.doranserver.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply,Long> {
    @Query("select c from Reply c where c.commentId = :commentId")
    List<Reply> findReplyCntByComment(@Param("commentId") Comment comment);

    @Query("select r.reply from Reply r where r.commentId = :commentId")
    List<String> findReplyContents(@Param("commentId") Comment comment);
}
