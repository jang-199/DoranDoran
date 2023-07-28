package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Comment;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.ReportComment;

public interface ReportCommentService {
    void saveReportComment(ReportComment reportComment);
    Boolean existedReportComment(Comment comment, Member member);
    void commentBlockLogic(Comment comment);
}
