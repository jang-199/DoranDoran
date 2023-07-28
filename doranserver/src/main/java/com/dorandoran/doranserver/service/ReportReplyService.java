package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.Reply;
import com.dorandoran.doranserver.entity.ReportReply;

public interface ReportReplyService {
    void saveReportReply(ReportReply reportReply);
    Boolean existedReportReply(Reply reply, Member member);
    void replyBlockLogic(Reply reply);
}
