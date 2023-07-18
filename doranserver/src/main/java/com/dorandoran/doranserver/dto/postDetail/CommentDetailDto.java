package com.dorandoran.doranserver.dto.postDetail;

import com.dorandoran.doranserver.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDetailDto {
    Long commentId; //댓글 pk값
    String comment; //댓글 내용
    Integer commentLike; //댓글 좋아요 개수
    Boolean commentLikeResult; //댓글 좋아요 유뮤
    String commentNickname; //댓글 쓴 사람 닉네임
    String CommentAnonymityNickname; //익명일 때 닉네임
    Boolean commentCheckDelete; //댓글 삭제 여부
    int countReply;
    Boolean isWrittenByMember; //내가 쓴 댓글인지 확인
    LocalDateTime commentTime; //댓글 작성 시간
    List<ReplyDetailDto> replies; //대댓글

    @Builder
    public CommentDetailDto(Comment comment, String content, Integer commentLikeCnt, Boolean commentLikeResult, Boolean isWrittenByMember, List<ReplyDetailDto> replies) {
        this.commentId = comment.getCommentId();
        this.comment = comment.getIsLocked().equals(Boolean.TRUE) ? "신고된 댓글입니다." : content;
        this.commentLike = commentLikeCnt;
        this.commentLikeResult = commentLikeResult;
        this.commentNickname = comment.getMemberId().getNickname();
        this.CommentAnonymityNickname = null;
        this.commentCheckDelete = comment.getCheckDelete();
        this.commentTime = comment.getCommentTime();
        this.countReply = comment.getCountReply();
        this.isWrittenByMember = isWrittenByMember;
        this.replies = replies;
    }
}
