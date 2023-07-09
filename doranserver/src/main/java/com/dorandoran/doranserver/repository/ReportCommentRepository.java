package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.Comment;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.ReportComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportCommentRepository extends JpaRepository<ReportComment, Long> {
    Optional<ReportComment> findByCommentIdAndMemberId(Comment comment, Member member);
}
