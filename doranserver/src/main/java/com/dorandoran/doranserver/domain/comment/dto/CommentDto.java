package com.dorandoran.doranserver.domain.comment.dto;

import com.dorandoran.doranserver.domain.comment.domain.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public class CommentDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateComment {
        private Long postId;
        private String comment;
        private Boolean anonymity;
        private Boolean secretMode;

        @Builder
        public CreateComment(Long postId, String comment, Boolean anonymity, Boolean secretMode) {
            this.postId = postId;
            this.comment = comment;
            this.anonymity = anonymity;
            this.secretMode = secretMode;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ReadCommentResponse{
        private Long commentId; //댓글 pk값
        private String comment; //댓글 내용
        private Long commentLike; //댓글 좋아요 개수
        private Boolean commentLikeResult; //댓글 좋아요 유뮤
        private String commentNickname; //댓글 쓴 사람 닉네임
        private String CommentAnonymityNickname; //익명일 때 닉네임
        private Boolean commentCheckDelete; //댓글 삭제 여부
        private int countReply;
        private Boolean isWrittenByMember; //내가 쓴 댓글인지 확인
        private LocalDateTime commentTime; //댓글 작성 시간
        private HashMap<String, Object> replies; //대댓글

        public ReadCommentResponse toEntity(Comment comment, Boolean commentLikeResult, Long commentLikeCnt, Boolean isCommentWrittenByMember, HashMap<String, Object> replies){
            return CommentDto.ReadCommentResponse.builder()
                    .comment(comment)
                    .content(comment.getComment())
                    .commentLikeResult(commentLikeResult)
                    .commentLikeCnt(commentLikeCnt)
                    .isWrittenByMember(isCommentWrittenByMember)
                    .replies(replies)
                    .build();
        }


        @Builder
        public ReadCommentResponse(Comment comment, String content, Long commentLikeCnt, Boolean commentLikeResult, Boolean isWrittenByMember, HashMap<String, Object> replies) {
            this.commentId = comment.getCommentId();
            this.comment = comment.getIsLocked().equals(Boolean.TRUE) ? "신고된 댓글입니다." : content;
            this.commentLike = commentLikeCnt;
            this.commentLikeResult = commentLikeResult;
            this.commentNickname = comment.getMemberId().getNickname();
            this.CommentAnonymityNickname = null;
            this.commentCheckDelete = comment.getCheckDelete();
            this.countReply = comment.getCountReply();
            this.isWrittenByMember = isWrittenByMember;
            this.commentTime = comment.getCreatedTime();
            this.replies = replies;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DeleteComment{
        private Long commentId;

        @Builder
        public DeleteComment(Long commentId) {
            this.commentId = commentId;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class LikeComment{
        private Long postId;
        private Long commentId;

        @Builder
        public LikeComment(Long postId, Long commentId) {
            this.postId = postId;
            this.commentId = commentId;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ReadAdminCommentResponse{
        Long commentId;
        String userEmail;
        String content;
        List<ReplyDto.ReadAdminReplyResponse> replyList;

        @Builder
        public ReadAdminCommentResponse(Comment comment, List<ReplyDto.ReadAdminReplyResponse> replyList) {
            this.commentId = comment.getCommentId();
            this.userEmail = comment.getMemberId().getEmail();
            this.content = comment.getComment();
            this.replyList = replyList;
        }
    }
}
