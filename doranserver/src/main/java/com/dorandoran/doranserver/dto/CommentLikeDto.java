package com.dorandoran.doranserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Schema(description = "좋아요 시 필요한 데이터")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class CommentLikeDto {
    @Schema(description = "해당 댓글이 있는 글의 id")
    Long postId;
    @Schema(description = "좋아요할 댓글 id")
    Long commentId;
    @Schema(description = "좋아요 요청한 사용자 email", example = "dorandoran@gmail.com")
    String userEmail;
}


