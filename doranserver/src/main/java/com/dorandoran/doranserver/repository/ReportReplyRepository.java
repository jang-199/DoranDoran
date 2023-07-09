package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.Reply;
import com.dorandoran.doranserver.entity.ReportReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportReplyRepository extends JpaRepository<ReportReply, Long> {
    Optional<ReportReply> findByReplyIdAndMemberId(Reply reply, Member member);
}
