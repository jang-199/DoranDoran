package com.dorandoran.doranserver.domain.api.report.repository;

import com.dorandoran.doranserver.domain.api.member.domain.Member;
import com.dorandoran.doranserver.domain.api.comment.domain.Reply;
import com.dorandoran.doranserver.domain.api.report.domain.ReportReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportReplyRepository extends JpaRepository<ReportReply, Long> {
    Optional<ReportReply> findByReplyIdAndMemberId(Reply reply, Member member);
}
