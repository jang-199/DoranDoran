package com.dorandoran.doranserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
public class SignUpDto {
    Date dateOfBirth; //생년월일
    String nickName; //닉네임
    String firebaseToken;
    String kakaoAccessToken;
}
