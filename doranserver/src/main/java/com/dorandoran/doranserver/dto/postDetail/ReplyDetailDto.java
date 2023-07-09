package com.dorandoran.doranserver.dto.postDetail;

import com.dorandoran.doranserver.entity.Reply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplyDetailDto {
    Long replyId; //대댓글 id값
    String replyNickname; //대댓글 작성한 닉네임
    String reply; //대댓글 내용
    String replyAnonymityNickname; //익명일 때 닉네임
    Boolean replyCheckDelete; //대댓글 삭제 여부
    LocalDateTime replyTime; //대댓글 작성 시간

    public ReplyDetailDto(Reply reply, String content) {
        this.replyId = reply.getReplyId();
        this.replyNickname = reply.getMemberId().getNickname();
        this.reply = reply.getReportCount()>=5 ? "신고된 댓글입니다." : content;
        this.replyAnonymityNickname = null;
        this.replyCheckDelete = reply.getCheckDelete();
        this.replyTime = reply.getReplyTime();
    }
}
