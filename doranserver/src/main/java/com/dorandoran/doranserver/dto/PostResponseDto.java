package com.dorandoran.doranserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {

    String contents; // 글 내용
    LocalDateTime postTime; // 작성 시간
    Integer location; //위치(떨어진 거리)
    Integer likeCnt; //좋아요 개수
    Integer ReplyCnt; // 댓글 개수
    String backgroundPicUri; //배경사진 링크
}
