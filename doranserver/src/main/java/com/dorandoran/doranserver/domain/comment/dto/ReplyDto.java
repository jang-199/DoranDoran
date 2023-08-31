package com.dorandoran.doranserver.domain.comment.dto;

import com.dorandoran.doranserver.domain.comment.domain.Reply;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

public class ReplyDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateReply{
        private Long commentId;
        private String reply;
        private Boolean anonymity;
        private Boolean secretMode;

        @Builder
        public CreateReply(Long commentId, String reply, Boolean anonymity, Boolean secretMode) {
            this.commentId = commentId;
            this.reply = reply;
            this.anonymity = anonymity;
            this.secretMode = secretMode;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ReadReplyResponse{
        Long replyId;
        String replyNickname;
        String reply;
        String replyAnonymityNickname;
        Boolean replyCheckDelete;
        Boolean isWrittenByMember;
        LocalDateTime replyTime;

        @Builder
        public ReadReplyResponse(Reply reply, String content, Boolean isWrittenByMember) {
            this.replyId = reply.getReplyId();
            this.replyNickname = reply.getMemberId().getNickname();
            this.reply = reply.getIsLocked().equals(Boolean.TRUE) ? "신고된 댓글입니다." : content;
            this.replyAnonymityNickname = null;
            this.replyCheckDelete = reply.getCheckDelete();
            this.isWrittenByMember = isWrittenByMember;
            this.replyTime = reply.getReplyTime();
        }
    }
    @Getter
    @Setter
    @NoArgsConstructor
    public static class DeleteReply{
        private Long replyId;

        @Builder
        public DeleteReply(Long replyId) {
            this.replyId = replyId;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ReadAdminReplyResponse{
        Long replyId;
        String userEmail;
        String content;

        @Builder
        public ReadAdminReplyResponse(Reply reply) {
            this.replyId = reply.getReplyId();
            this.userEmail = reply.getMemberId().getEmail();
            this.content = reply.getReply();
        }
    }
}
