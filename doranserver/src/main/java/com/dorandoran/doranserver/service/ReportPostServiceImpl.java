package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.ReportPost;
import com.dorandoran.doranserver.repository.ReportPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportPostServiceImpl implements ReportPostService {
    private final ReportPostRepository reportPostRepository;
    private final ReportCommonService reportCommonService;

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

    @Override
    @Transactional
    public void postBlockLogic(Post post) {
        post.addReportCount();
        if (post.getReportCount() == 7 && post.getIsLocked() == Boolean.FALSE) {
            post.setLocked();
            reportCommonService.memberLockLogic(post.getMemberId());
        }
    }
}
