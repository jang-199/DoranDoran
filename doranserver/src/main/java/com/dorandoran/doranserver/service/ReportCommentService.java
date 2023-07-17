package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Comment;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.ReportComment;

public interface ReportCommentService {
    public void saveReportComment(ReportComment reportComment);
    public Boolean existedReportComment(Comment comment, Member member);
}
