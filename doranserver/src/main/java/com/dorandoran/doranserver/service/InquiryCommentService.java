package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.InquiryComment;
import com.dorandoran.doranserver.entity.InquiryPost;

import java.util.List;

public interface InquiryCommentService {
    void saveInquiryComment(InquiryPost inquiryPost, InquiryComment inquiryComment);
    void deleteInquiryComment(InquiryComment inquiryComment);
    List<InquiryComment> findCommentByPost(InquiryPost inquiryPost);
    InquiryComment findCommentById(Long inquiryCommentId);
    void updateInquiryComment(InquiryComment inquiryComment, String content);

}
