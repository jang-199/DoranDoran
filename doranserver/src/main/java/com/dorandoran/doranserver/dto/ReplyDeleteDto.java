package com.dorandoran.doranserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "대댓글 삭제 시 필요한 데이터")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplyDeleteDto {
    @Schema(description = "삭제할 대댓글 id")
    Long replyId;
    @Schema(description = "삭제 요청한 사용자 email", example = "dorandoran@gmail.com")
    String userEmail;
}