package com.dorandoran.doranserver.domain.customerservice.service;

import com.dorandoran.doranserver.domain.customerservice.domain.InquiryPost;
import com.dorandoran.doranserver.domain.member.domain.Member;

import java.util.List;

public interface InquiryPostService {
    void saveInquiryPost(InquiryPost inquiryPost);
    void deleteInquiryPost(InquiryPost inquiryPost);
    List<InquiryPost> findAll(Integer page);
    List<InquiryPost> findByMember(Member member);
    InquiryPost findInquiryPost(Long inquiryPostId);

    InquiryPost findInquiryPostFetchMember(Long inquiryPostId);

    List<InquiryPost> findByPostTitle(Integer page, String title);
    List<InquiryPost> findByPostContent(Integer page, String content);
    List<InquiryPost> findByPostTime(Integer page, String postTime);

    List<InquiryPost> findByAnswerType(Integer page, String answerType);
}
