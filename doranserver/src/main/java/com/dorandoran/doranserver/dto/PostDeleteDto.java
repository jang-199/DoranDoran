package com.dorandoran.doranserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "글 삭제 시 필요한 데이터")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDeleteDto {
    @Schema(description = "삭제하려는 글의 id 값")
    Long postId;
    @Schema(description = "삭제 요청한 사용자 email", example = "dorandoran@gmail.com")
    String userEmail;
}
