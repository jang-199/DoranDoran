package com.dorandoran.doranserver.dto.postDetail;

import com.dorandoran.doranserver.entity.Comment;
import com.dorandoran.doranserver.entity.Reply;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "댓글 상세정보")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDetailDto {
    @Schema(description = "댓글 pk값")
    Long commentId; //댓글 pk값
    @Schema(description = "댓글 내용", example = "댓글입니다.")
    String comment; //댓글 내용
    @Schema(description = "댓글 좋아요 개수")
    Integer commentLike; //댓글 좋아요 개수
    @Schema(description = "댓글 좋아요 유뮤", example = "True")
    Boolean commentLikeResult; //댓글 좋아요 유뮤
    @Schema(description = "기존 닉네임",example = "nickname1")
    String commentNickname; //댓글 쓴 사람 닉네임
    @Schema(description = "익명일 때 닉네임", example = "익명1")
    String CommentAnonymityNickname; //익명일 때 닉네임
    @Schema(description = "댓글 삭제 여부", example = "True")
    Boolean commentCheckDelete; //댓글 삭제 여부
    @Schema(description = "댓글 작성 시간")
    LocalDateTime commentTime; //댓글 작성 시간
    @Schema(description = "대댓글 상세 정보 list")
    List<ReplyDetailDto> replies; //대댓글

    public CommentDetailDto(Comment comment, String content, Integer commentLikeCnt, Boolean commentLikeResult, List<ReplyDetailDto> replies) {
        this.commentId = comment.getCommentId();
        this.comment = content;
        this.commentLike = commentLikeCnt;
        this.commentLikeResult = commentLikeResult;
        this.commentNickname = comment.getMemberId().getNickname();
        this.CommentAnonymityNickname = null;
        this.commentCheckDelete = comment.getCheckDelete();
        this.commentTime = comment.getCommentTime();
        this.replies = replies;
    }
}
