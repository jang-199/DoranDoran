package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.Reply;
import com.dorandoran.doranserver.entity.ReportReply;
import com.dorandoran.doranserver.repository.ReportReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportReplyServiceImpl implements ReportReplyService{
    private final ReportReplyRepository reportReplyRepository;
    private final ReportCommonService reportCommonService;

    @Override
    public void saveReportReply(ReportReply reportReply) {
        reportReplyRepository.save(reportReply);
    }

    @Override
    public Boolean existedReportReply(Reply reply, Member member) {
        Optional<ReportReply> reportReply = reportReplyRepository.findByReplyIdAndMemberId(reply, member);
        if (reportReply.isPresent()){
            return Boolean.TRUE;
        }else {
            return Boolean.FALSE;
        }
    }

    @Override
    @Transactional
    public void replyBlockLogic(Reply reply) {
        reply.addReportCount();
        if (reply.getReportCount() == 5 && reply.getIsLocked() == Boolean.FALSE){
            reply.setLocked();
            reportCommonService.memberLockLogic(reply.getMemberId());
        }
    }
}
