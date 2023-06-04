package com.dorandoran.doranserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "회원가입 성공 시 리턴되는 정보")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {
    @Schema(description = "email",example = "xxx@xxx.com")
    String email;
    @Schema(description = "nickname",example = "doraning")
    String nickName;
    @Schema(description = "JWT")
    TokenDto tokenDto;
}
