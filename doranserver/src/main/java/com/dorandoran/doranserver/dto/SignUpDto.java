package com.dorandoran.doranserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpDto {
    LocalDate dateOfBirth; //생년월일
    String nickName; //닉네임
    String firebaseToken;
    String kakaoAccessToken;
}
