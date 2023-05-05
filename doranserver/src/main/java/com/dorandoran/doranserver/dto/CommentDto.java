package com.dorandoran.doranserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "댓글의 상세 정보")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {
    @Schema(description = "댓글 저장할 글 id")
    Long postId;
    @Schema(description = "댓글 저장 요청한 사용자 email", example = "dorandoran@gmail.com")
    String email;
    @Schema(description = "댓글 내용")
    String comment;
    @Schema(description = "사용자 닉네임 익명성 여부", example = "true")
    Boolean anonymity;
    @Schema(description = "비밀댓글 여부", example = "true")
    Boolean secretMode;
}
