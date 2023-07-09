package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.Reply;
import com.dorandoran.doranserver.entity.ReportReply;
import com.dorandoran.doranserver.repository.ReportReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportReplyServiceImpl implements ReportReplyService{
    private final ReportReplyRepository reportReplyRepository;

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
}
