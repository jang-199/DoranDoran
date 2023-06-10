package com.dorandoran.doranserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Schema(description = "글에 관한 상세 정보")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    @Schema(description = "글 저장 요청한 사용자 이메일", example = "dorandoran@gmail.com")
    String email;
    @Schema(description = "글 내용")
    String content;
    @Schema(description = "나만 보기 기능", example = "true")
    Boolean forMe;
    @Schema(description = "글을 작성한 위치의 좌표", example = "11.323,12.332")
    String location;
    @Schema(description = "기존에 제공하는 배경 사진 이름", example = "1")
    String backgroundImgName;
    @Schema(description = "글에 저장하는 해시태그", example = "도란도란, 해시, 태그")
    List<String> hashTagName;
    @Schema(description = "사용자의 요청 사진")
    MultipartFile file;
    @Schema(description = "사용자 낙네임 익명 여부", example = "true")
    Boolean anonymity;
    @Schema(description = "글꼴", example = "Jua")
    String font;
    @Schema(description = "글자 색", example = "black")
    String fontColor;
    @Schema(description = "글자 사아즈", example = "30")
    Integer fontSize;
    @Schema(description = "글자 굵기", example = "400")
    Integer fontBold;
}