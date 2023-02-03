package com.dorandoran.doranserver.dto.commentdetail;

import com.dorandoran.doranserver.entity.Comment;
import com.dorandoran.doranserver.entity.Reply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDetailDto {
    String comment; //댓글 내용
    Integer commentLike; //댓글 좋아요 개수
    Boolean commentLikeResult; //댓글 좋아요 유뮤
    List<Reply> replies; //대댓글
}
