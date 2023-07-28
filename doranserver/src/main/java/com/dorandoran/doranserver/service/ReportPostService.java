package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.ReportPost;

public interface ReportPostService {
    void saveReportPost(ReportPost reportPost);
    Boolean existReportPost(Post post, Member member);
    void postBlockLogic(Post post);

}
