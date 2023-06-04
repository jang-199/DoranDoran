package com.dorandoran.doranserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "발급된 jwt")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {
    @Schema(description = "RefreshToken",example = "xxx.xxx.xxx")
    String refreshToken;

    @Schema(description = "AccessToken",example = "xxx.xxx.xxx")
    String accessToken;
}
