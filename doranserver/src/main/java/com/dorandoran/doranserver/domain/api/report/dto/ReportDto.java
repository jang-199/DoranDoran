package com.dorandoran.doranserver.domain.api.report.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ReportDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateReportPost {
        private Long postId;
        private String reportContent;

        @Builder
        public CreateReportPost(Long postId, String reportContent) {
            this.postId = postId;
            this.reportContent = reportContent;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateReportComment{
        Long commentId;
        String reportContent;

        @Builder
        public CreateReportComment(Long commentId, String reportContent) {
            this.commentId = commentId;
            this.reportContent = reportContent;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateReportReply{
        Long replyId;
        String reportContent;

        @Builder
        public CreateReportReply(Long replyId, String reportContent) {
            this.replyId = replyId;
            this.reportContent = reportContent;
        }
    }
}
