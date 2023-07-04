package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.ReportPost;
import com.dorandoran.doranserver.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService{
    private final ReportRepository reportRepository;

    @Override
    public void saveReportPost(ReportPost reportPost) {
        reportRepository.save(reportPost);
    }

    @Override
    public Boolean existReportPost(Post post, Member member) {
        Optional<ReportPost> reportPost = reportRepository.findReportPostByPostIdAndMemberId(post, member);
        if (reportPost.isPresent()){
            return Boolean.TRUE;
        }else {
            return Boolean.FALSE;
        }
    }
}
