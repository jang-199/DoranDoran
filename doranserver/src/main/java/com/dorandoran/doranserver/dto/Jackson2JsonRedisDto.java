package com.dorandoran.doranserver.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Jackson2JsonRedisDto {
    String FileName;
    byte[] pic;

    public Jackson2JsonRedisDto() {
    }
    @Builder
    public Jackson2JsonRedisDto(String FileName, byte[] pic) {
        this.FileName = FileName;
        this.pic = pic;
    }
}
