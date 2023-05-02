package com.dorandoran.doranserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "하나의 글에 관한 상세 정보")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {

    @Schema(description = "글 번호",example = "0")
    Long postId; //글 번호

    @Schema(description = "글 내용",example = "This is example")
    String contents; // 글 내용

    @Schema(description = "작성 시간",example = "99-01-01T00:00:00")
    LocalDateTime postTime;

    @Schema(description = "위치(떨어진 거리)",example = "120")
    Integer location;

    @Schema(description = "좋아요 개수",example = "10")
    Integer likeCnt;

    @Schema(description = "좋아요 유무",example = "true")
    Boolean likeResult;

    @Schema(description = "댓글 개수",example = "10")
    Integer ReplyCnt;

    @Schema(description = "배경사진 링크",example = "localhost:8080/api/background/1")
    String backgroundPicUri;

    @Schema(description = "폰트",example = "Jua")
    String font;

    @Schema(description = "글 색상",example = "Black")
    String fontColor;

    @Schema(description = "글 크기",example = "20")
    Integer fontSize;

    @Schema(description = "글 굵기",example = "400")
    Integer fontBold;
}
