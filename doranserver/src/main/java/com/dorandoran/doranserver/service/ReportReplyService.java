package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.Reply;
import com.dorandoran.doranserver.entity.ReportReply;

public interface ReportReplyService {
    public void saveReportReply(ReportReply reportReply);
    public Boolean existedReportReply(Reply reply, Member member);
}
