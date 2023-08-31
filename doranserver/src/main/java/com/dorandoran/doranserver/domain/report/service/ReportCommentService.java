package com.dorandoran.doranserver.domain.report.service;

import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.report.domain.ReportComment;

public interface ReportCommentService {
    void saveReportComment(ReportComment reportComment);
    Boolean existedReportComment(Comment comment, Member member);
    void commentBlockLogic(Comment comment);
}
