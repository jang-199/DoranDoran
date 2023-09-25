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
        Boolean isLocked;

        public ReadReplyResponse toEntity(Reply reply, Boolean isReplyWrittenByUser){
            return ReplyDto.ReadReplyResponse.builder()
                    .reply(reply)
                    .isWrittenByMember(isReplyWrittenByUser)
                    .build();
        }

        @Builder
        public ReadReplyResponse(Reply reply, Boolean isWrittenByMember) {
            this.replyId = reply.getReplyId();
            this.replyNickname = reply.getMemberId().getNickname();
            this.reply = changeReplyByDeletedAndLocked(reply);
            this.replyAnonymityNickname = null;
            this.replyCheckDelete = reply.getCheckDelete();
            this.replyTime = reply.getCreatedTime();
            this.isWrittenByMember = isWrittenByMember;
            this.isLocked = reply.getIsLocked();
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
        String nickname;
        String content;

        @Builder
        public ReadAdminReplyResponse(Reply reply) {
            this.replyId = reply.getReplyId();
            this.userEmail = reply.getMemberId().getEmail();
            this.nickname = reply.getMemberId().getNickname();
            this.content = reply.getReply();
        }
    }

    private static String changeReplyByDeletedAndLocked(Reply reply){
        String replyContent = reply.getReply();

        if (reply.getIsLocked()){
            replyContent = "신고된 댓글입니다.";
        }

        if (reply.getCheckDelete()){
            replyContent = "삭제된 댓글입니다.";
        }

        return replyContent;
    }
}
