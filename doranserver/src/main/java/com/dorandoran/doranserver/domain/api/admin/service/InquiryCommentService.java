package com.dorandoran.doranserver.domain.api.admin.service;

import com.dorandoran.doranserver.domain.api.admin.domain.InquiryComment;
import com.dorandoran.doranserver.domain.api.admin.domain.InquiryPost;

import java.util.List;

public interface InquiryCommentService {
    void saveInquiryComment(InquiryPost inquiryPost, InquiryComment inquiryComment);
    void deleteInquiryComment(InquiryComment inquiryComment);
    void deleteInquiryCommentList(List<InquiryComment> inquiryCommentList);
    List<InquiryComment> findCommentByPost(InquiryPost inquiryPost);
    InquiryComment findCommentById(Long inquiryCommentId);
    void updateInquiryComment(InquiryComment inquiryComment, String content);

}
