package com.dorandoran.doranserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Schema(description = "글 상세보기에 필요한 데이터")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostRequestDetailDto {
    @Schema(description = "글 id값")
    Long postId;
    @Schema(description = "글 상세보기 클릭한 사용자 email")
    String userEmail;
    @Schema(description = "글 상세보기 클릭한 위치", example = "23.96,25.75")
    String location;
}
