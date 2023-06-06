package com.dorandoran.doranserver.dto;

import com.dorandoran.doranserver.entity.HashTag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "해시태그를 조회")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HashTagResponseDto {
    @Schema(description = "요청한 해시태그로 시작하는 해시태그")
    String hashTagName;
    @Schema(description = "해시태그가 사용된 횟수")
    Long hashTagCount;

    public HashTagResponseDto(HashTag hashTag) {
        this.hashTagName = hashTag.getHashTagName();
        this.hashTagCount = hashTag.getHashTagCount();
    }
}
