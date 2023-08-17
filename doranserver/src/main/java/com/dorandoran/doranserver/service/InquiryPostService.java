package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.InquiryPost;
import com.dorandoran.doranserver.entity.Member;

import java.util.List;

public interface InquiryPostService {
    void saveInquiryPost(InquiryPost inquiryPost);
    void deleteInquiryPost(InquiryPost inquiryPost);
    List<InquiryPost> findAll(Integer page);
    List<InquiryPost> findByMember(Member member);
    InquiryPost findInquiryPost(Long inquiryPostId);
    List<InquiryPost> findByPostTitle(Integer page, String title);
    List<InquiryPost> findByPostContent(Integer page, String content);
    List<InquiryPost> findByPostTime(Integer page, String postTime);

    List<InquiryPost> findByAnswerType(Integer page, String answerType);
}
