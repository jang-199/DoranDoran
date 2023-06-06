package com.dorandoran.doranserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "사용자 즐겨찾기 해시태그")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HashTagRequestDto {
    @Schema(description = "즐겨찾기에 추가하는 해시태그")
    List<String> hashTagList;
    //임의로 넣어둠
    String token;
}
