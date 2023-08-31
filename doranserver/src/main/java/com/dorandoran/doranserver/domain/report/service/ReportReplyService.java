package com.dorandoran.doranserver.domain.report.service;

import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.comment.domain.Reply;
import com.dorandoran.doranserver.domain.report.domain.ReportReply;

public interface ReportReplyService {
    void saveReportReply(ReportReply reportReply);
    Boolean existedReportReply(Reply reply, Member member);
    void replyBlockLogic(Reply reply);
}
