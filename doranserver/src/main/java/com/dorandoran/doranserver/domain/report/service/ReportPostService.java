package com.dorandoran.doranserver.domain.report.service;

import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.report.domain.ReportPost;

public interface ReportPostService {
    void saveReportPost(ReportPost reportPost);
    Boolean existReportPost(Post post, Member member);
    void postBlockLogic(Post post);

}
