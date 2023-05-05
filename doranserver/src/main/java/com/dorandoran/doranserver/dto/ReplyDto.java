package com.dorandoran.doranserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "대댓글 상세 정보")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyDto {
    @Schema(description = "대댓글 작성할 댓글 id")
    Long commentId;
    @Schema(description = "대댓글 저장 요청한 사용자 email", example = "dorandoran@gmail.com")
    String userEmail;
    @Schema(description = "대댓글 내용")
    String reply;
    @Schema(description = "사용자 닉네임 익명성 여부", example = "true")
    Boolean anonymity;
    @Schema(description = "비밀댓글 여부", example = "true")
    Boolean secretMode;
}

