package com.dorandoran.doranserver.domain.api.report.service;

import com.dorandoran.doranserver.domain.api.member.domain.Member;
import com.dorandoran.doranserver.domain.api.post.domain.Post;
import com.dorandoran.doranserver.domain.api.report.domain.ReportPost;

public interface ReportPostService {
    void saveReportPost(ReportPost reportPost);
    Boolean existReportPost(Post post, Member member);
    void postBlockLogic(Post post);

}
