package com.dorandoran.doranserver.domain.report.repository;

import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.report.domain.ReportComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReportCommentRepository extends JpaRepository<ReportComment, Long> {
    Optional<ReportComment> findByCommentIdAndMemberId(Comment comment, Member member);

    List<ReportComment> findAllByMemberId(Member member);
    @Query("select rc from ReportComment rc where rc.commentId = :commentList")
    List<ReportComment> findAllByCommentId(@Param("commentList") List<Comment> comment);
}
