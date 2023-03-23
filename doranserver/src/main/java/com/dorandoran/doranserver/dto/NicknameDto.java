package com.dorandoran.doranserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "중복체크할 닉네임")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NicknameDto {
    @Schema(example = "This is example nickname")
    String nickname;
}
