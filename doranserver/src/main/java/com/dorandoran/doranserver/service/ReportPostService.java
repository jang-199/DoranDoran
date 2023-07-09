package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.ReportPost;

public interface ReportPostService {
    public void saveReportPost(ReportPost reportPost);
    public Boolean existReportPost(Post post, Member member);
}
