package com.dorandoran.doranserver.dto.postDetail;

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
    Boolean replyAnonymity; //대댓글 작성자 익명성 여부
    Boolean replyCheckDelete; //대댓글 삭제 여부
    LocalDateTime replyTime; //대댓글 작성 시간
}
