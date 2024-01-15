package com.dorandoran.doranserver.domain.report.repository;

import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.comment.domain.Reply;
import com.dorandoran.doranserver.domain.report.domain.ReportReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReportReplyRepository extends JpaRepository<ReportReply, Long> {
    Optional<ReportReply> findByReplyIdAndMemberId(Reply reply, Member member);

    List<ReportReply> findAllByMemberId(Member member);
    @Query("select rr from ReportReply rr where rr.replyId in :replyList")
    List<ReportReply> findAllByReplyId(@Param("replyList") List<Reply> reply);
}
