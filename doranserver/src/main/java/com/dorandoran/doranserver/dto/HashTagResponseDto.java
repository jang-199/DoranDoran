package com.dorandoran.doranserver.dto;

import com.dorandoran.doranserver.entity.HashTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HashTagResponseDto {
    String hashTagName;
    Long hashTagCount;
    Boolean hashTagCheck;

    public HashTagResponseDto(HashTag hashTag) {
        this.hashTagName = hashTag.getHashTagName();
        this.hashTagCount = hashTag.getHashTagCount();
    }
}
