package com.dorandoran.doranserver.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Schema(description = "가입할 회원의 상세 정보")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpDto {

    @Schema(description = "생년월일",example = "2099-01-01")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate dateOfBirth;

    @Schema(description = "닉네임",example = "SampleNick")
    String nickName;

    @Schema(description = "firebaseToken")
    String firebaseToken;

    @Schema(description = "kakaoAccessToken")
    String kakaoAccessToken;
}
