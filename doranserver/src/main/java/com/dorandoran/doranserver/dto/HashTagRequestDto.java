package com.dorandoran.doranserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Schema(description = "사용자 즐겨찾기 해시태그")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HashTagRequestDto {
    @Schema(description = "즐겨찾기에 추가/삭제하는 해시태그")
    List<String> hashTagList;
}
