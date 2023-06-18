package com.dorandoran.doranserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {

    Long postId; //글 번호

    String contents; // 글 내용

    LocalDateTime postTime;

    Integer location;

    Integer likeCnt;

    Boolean likeResult;

    Integer ReplyCnt;

    String backgroundPicUri;

    String font;

    String fontColor;

    Integer fontSize;

    Integer fontBold;
}
