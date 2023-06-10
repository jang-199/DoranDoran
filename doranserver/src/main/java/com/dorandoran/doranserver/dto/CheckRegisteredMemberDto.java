package com.dorandoran.doranserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "가입 여부를 확인할 이메일")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckRegisteredMemberDto {
    @Schema(example = "example@gmail.com")
    String email;
}
