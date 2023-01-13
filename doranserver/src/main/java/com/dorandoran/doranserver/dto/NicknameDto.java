package com.dorandoran.doranserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class NicknameDto {
    String nickname;
}
