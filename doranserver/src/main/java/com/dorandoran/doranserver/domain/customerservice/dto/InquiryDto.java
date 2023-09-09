package com.dorandoran.doranserver.domain.customerservice.dto;

import com.dorandoran.doranserver.domain.customerservice.domain.InquiryComment;
import com.dorandoran.doranserver.domain.customerservice.domain.inquirytype.InquiryStatus;
import com.dorandoran.doranserver.domain.customerservice.domain.InquiryPost;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class InquiryDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ReadInquiryPostBoard {
        Long inquiryPostId;
        String title;
        String createTime;
        InquiryStatus inquiryStatus;

        @Builder
        public ReadInquiryPostBoard(InquiryPost inquiryPost) {
            this.inquiryPostId = inquiryPost.getInquiryPostId();
            this.title = inquiryPost.getTitle();
            this.createTime = inquiryPost.getCreatedTime().format(DateTimeFormatter.ofPattern("yy/MM/dd HH:mm"));;
            this.inquiryStatus = inquiryPost.getInquiryStatus();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ReadInquiryPost {
        Long inquiryPostId;
        String memberEmail;
        String title;
        String content;
        String postCreateTime;
        List<ReadInquiryComment> inquiryCommentList;

        public ReadInquiryPost toEntity(InquiryPost inquiryPost, List<ReadInquiryComment> inquiryCommentListDto) {
            return InquiryDto.ReadInquiryPost.builder()
                    .inquiryPost(inquiryPost)
                    .inquiryCommentList(inquiryCommentListDto)
                    .build();
        }

        @Builder
        public ReadInquiryPost(InquiryPost inquiryPost, List<ReadInquiryComment> inquiryCommentList) {
            this.inquiryPostId = inquiryPost.getInquiryPostId();
            this.memberEmail = inquiryPost.getMemberId().getEmail();
            this.title = inquiryPost.getTitle();
            this.content = inquiryPost.getContent();
            this.postCreateTime = inquiryPost.getCreatedTime().format(DateTimeFormatter.ofPattern("yy/MM/dd HH:mm"));;
            this.inquiryCommentList =  inquiryCommentList;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ReadInquiryComment{
        Long inquiryCommentId;
        String comment;
        String commentCreateTime;

        @Builder
        public ReadInquiryComment(InquiryComment inquiryComment) {
            this.inquiryCommentId = inquiryComment.getInquiryCommentId();
            this.comment = inquiryComment.getComment();
            this.commentCreateTime = inquiryComment.getCreatedTime().format(DateTimeFormatter.ofPattern("yy/MM/dd HH:mm"));
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateInquiryComment{
        Long inquiryPostId;
        String comment;

        @Builder
        public CreateInquiryComment(Long inquiryPostId, String comment) {
            this.inquiryPostId = inquiryPostId;
            this.comment = comment;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UpdateInquiryComment{
        Long inquiryCommentId;
        String comment;

        @Builder
        public UpdateInquiryComment(Long inquiryCommentId, String comment) {
            this.inquiryCommentId = inquiryCommentId;
            this.comment = comment;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UpdatePageInquiryComment{
        Long inquiryPostId;
        Long inquiryCommentId;
        String comment;

        @Builder
        public UpdatePageInquiryComment(Long inquiryPostId, Long inquiryCommentId, String comment) {
            this.inquiryPostId = inquiryPostId;
            this.inquiryCommentId = inquiryCommentId;
            this.comment = comment;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DeleteInquiryComment{
        Long inquiryCommentId;

        @Builder
        public DeleteInquiryComment(Long inquiryCommentId) {
            this.inquiryCommentId = inquiryCommentId;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateInquiryPost {
        String title;
        String content;

        @Builder
        public CreateInquiryPost(String title, String content) {
            this.title = title;
            this.content = content;
        }
    }
}
