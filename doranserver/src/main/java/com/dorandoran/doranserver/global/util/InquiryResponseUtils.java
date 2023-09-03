package com.dorandoran.doranserver.global.util;

import com.dorandoran.doranserver.domain.customerservice.domain.InquiryComment;
import com.dorandoran.doranserver.domain.customerservice.domain.InquiryPost;
import com.dorandoran.doranserver.domain.customerservice.dto.InquiryDto;

import java.util.List;

public class InquiryResponseUtils {
    public static List<InquiryDto.ReadInquiryPostBoard> makeInquiryPostList(List<InquiryPost> inquiryPostPage) {
        return inquiryPostPage.stream()
                .map(inquiryPost -> InquiryDto.ReadInquiryPostBoard.builder()
                        .inquiryPost(inquiryPost)
                        .build())
                .toList();
    }

    public static List<InquiryDto.ReadInquiryComment> makeInquiryCommentList(List<InquiryComment> inquiryCommentList) {
        return inquiryCommentList.stream()
                .map((inquiryComment -> InquiryDto.ReadInquiryComment.builder()
                        .inquiryComment(inquiryComment)
                        .build()))
                .toList();
    }
}
