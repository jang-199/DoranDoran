package com.dorandoran.doranserver.dto;

import com.dorandoran.doranserver.entity.HashTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HashTagPopularDto {
    String hashTagName;
    Long hashTagCount;

    @Builder
    public HashTagPopularDto(HashTag hashTag) {
        this.hashTagName = hashTag.getHashTagName();
        this.hashTagCount = hashTag.getHashTagCount();
    }
}
