package com.dorandoran.doranserver.domain.api.report.service;

import com.dorandoran.doranserver.domain.api.comment.domain.Comment;
import com.dorandoran.doranserver.domain.api.member.domain.Member;
import com.dorandoran.doranserver.domain.api.report.domain.ReportComment;

public interface ReportCommentService {
    void saveReportComment(ReportComment reportComment);
    Boolean existedReportComment(Comment comment, Member member);
    void commentBlockLogic(Comment comment);
}
