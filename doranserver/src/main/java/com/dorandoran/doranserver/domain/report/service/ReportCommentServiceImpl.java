package com.dorandoran.doranserver.domain.report.service;

import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.report.domain.ReportComment;
import com.dorandoran.doranserver.domain.report.repository.ReportCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReportCommentServiceImpl implements ReportCommentService{

    private final ReportCommentRepository reportCommentRepository;
    private final ReportCommonService reportCommonService;

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

    @Override
    @Transactional
    public void commentBlockLogic(Comment comment) {
        comment.addReportCount();
        if (comment.getReportCount() == 5 && comment.getIsLocked() == Boolean.FALSE){
            comment.setLocked();
            reportCommonService.memberLockLogic(comment.getMemberId());
        }
    }
}
