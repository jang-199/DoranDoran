package com.dorandoran.doranserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "좋아요할 때 필요한 데이터")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostLikeDto {
    @Schema(description = "좋아요할 글 id")
    Long postId;
    @Schema(description = "좋아요 요청한 사용자 email", example = "dorandoran@gmail.com")
    String email;
}
