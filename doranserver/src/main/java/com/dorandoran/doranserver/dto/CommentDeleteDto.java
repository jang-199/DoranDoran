package com.dorandoran.doranserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "댓글 삭제 시 필요한 데이터")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDeleteDto {
    @Schema(description = "삭제 요청한 댓글 id")
    Long commentId;
    @Schema(description = "삭제 요청한 사용자 email", example = "dorandoran@gmail.com")
    String userEmail;
}
