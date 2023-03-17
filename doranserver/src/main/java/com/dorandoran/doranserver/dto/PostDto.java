package com.dorandoran.doranserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    String email;
    String content;
    Boolean forMe;
    String location;
    String backgroundImgName;
    List<String> hashTagName;
    MultipartFile file;
    String font;
    String fontColor;
    Integer fontSize;
    Integer fontBold;
}