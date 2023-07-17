package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Comment;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.ReportComment;
import com.dorandoran.doranserver.repository.ReportCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReportCommentServiceImpl implements ReportCommentService{

    private final ReportCommentRepository reportCommentRepository;

    @Override
    public void saveReportComment(ReportComment reportComment) {
        reportCommentRepository.save(reportComment);
    }

    @Override
    public Boolean existedReportComment(Comment comment, Member member) {
        Optional<ReportComment> reportComment = reportCommentRepository.findByCommentIdAndMemberId(comment, member);
        if (reportComment.isPresent()){
            return Boolean.TRUE;
        }else {
            return Boolean.FALSE;
        }
    }
}
