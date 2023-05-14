package com.dorandoran.doranserver.dto.postDetail;

import com.dorandoran.doranserver.entity.Reply;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "대댓글 상세정보")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplyDetailDto {
    @Schema(description = "대댓글 id값")
    Long replyId; //대댓글 id값
    @Schema(description = "기존 닉네임")
    String replyNickname; //대댓글 작성한 닉네임
    @Schema(description = "대댓글 내용", example = "대댓글 내용입니다.")
    String reply; //대댓글 내용
    @Schema(description = "익명일 때 닉네임", example = "익명1")
    String replyAnonymityNickname; //익명일 때 닉네임
    @Schema(description = "대댓글 삭제 여부", example = "True")
    Boolean replyCheckDelete; //대댓글 삭제 여부
    @Schema(description = "대댓글 작성 시간")
    LocalDateTime replyTime; //대댓글 작성 시간

    public ReplyDetailDto(Reply reply, String content) {
        this.replyId = reply.getReplyId();
        this.replyNickname = reply.getMemberId().getNickname();
        this.reply = content;
        this.replyAnonymityNickname = null;
        this.replyCheckDelete = reply.getCheckDelete();
        this.replyTime = reply.getReplyTime();
    }
}
