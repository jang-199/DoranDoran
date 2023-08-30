package com.dorandoran.doranserver.domain.api.background.dto;

import com.dorandoran.doranserver.domain.api.background.domain.imgtype.ImgType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class Img2CacheServerDto {
    ImgType imgType;
    String FileName; //확장자까지
    MultipartFile pic;

    public Img2CacheServerDto() {
    }
    @Builder
    public Img2CacheServerDto(ImgType imgType, String fileName, MultipartFile pic) {
        this.imgType = imgType;
        FileName = fileName;
        this.pic = pic;
    }
}
