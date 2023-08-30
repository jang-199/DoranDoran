package com.dorandoran.doranserver.domain.api.report.service;

import com.dorandoran.doranserver.domain.api.member.domain.Member;
import com.dorandoran.doranserver.domain.api.comment.domain.Reply;
import com.dorandoran.doranserver.domain.api.report.domain.ReportReply;

public interface ReportReplyService {
    void saveReportReply(ReportReply reportReply);
    Boolean existedReportReply(Reply reply, Member member);
    void replyBlockLogic(Reply reply);
}
