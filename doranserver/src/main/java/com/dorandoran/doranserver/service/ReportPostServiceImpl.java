package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.ReportPost;
import com.dorandoran.doranserver.repository.ReportPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportPostServiceImpl implements ReportPostService {
    private final ReportPostRepository reportPostRepository;

    @Override
    public void saveReportPost(ReportPost reportPost) {
        reportPostRepository.save(reportPost);
    }

    @Override
    public Boolean existReportPost(Post post, Member member) {
        Optional<ReportPost> reportPost = reportPostRepository.findReportPostByPostIdAndMemberId(post, member);
        if (reportPost.isPresent()){
            return Boolean.TRUE;
        }else {
            return Boolean.FALSE;
        }
    }
}
