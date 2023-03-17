package com.dorandoran.doranserver.dto.postDetail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDetailDto {
    Long commentId; //댓글 pk값
    String comment; //댓글 내용
    Integer commentLike; //댓글 좋아요 개수
    Boolean commentLikeResult; //댓글 좋아요 유뮤
    String commentNickname; //댓글 쓴 사람 닉네임
    LocalDateTime commentTime; //댓글 작성 시간
    List<ReplyDetailDto> replies; //대댓글
}
