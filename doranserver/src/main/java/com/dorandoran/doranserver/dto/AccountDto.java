package com.dorandoran.doranserver.dto;

import com.dorandoran.doranserver.entity.osType.OsType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class AccountDto {

    @Getter
    @Setter
    public static class CheckNickname{
        private String nickname;

        @Builder
        public CheckNickname(String nickname) {
            this.nickname = nickname;
        }
    }

    @Getter
    @Setter
    public static class CheckRegisteredMember{
        String email;
        OsType osType;

        @Builder
        public CheckRegisteredMember(String email, OsType osType) {
            this.email = email;
            this.osType = osType;
        }
    }

    @Getter
    @Setter
    public static class CheckRegisteredMemberResponse{
        private String email;
        private String nickname;
        private AuthenticationDto.TokenResponse tokenDto;

        @Builder
        public CheckRegisteredMemberResponse(String email, String nickname, AuthenticationDto.TokenResponse tokenDto) {
            this.email = email;
            this.nickname = nickname;
            this.tokenDto = tokenDto;
        }
    }

    @Getter
    @Setter
    public static class ChangeNickname{
        private String nickname;

        @Builder
        public ChangeNickname(String nickname) {
            this.nickname = nickname;
        }
    }

    @Getter
    @Setter
    public static class SignUp{
        @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateOfBirth;
        private String nickname;
        private String firebaseToken;
        private String kakaoAccessToken;
        private OsType osType;

        @Builder
        public SignUp(LocalDate dateOfBirth, String nickname, String firebaseToken, String kakaoAccessToken, OsType osType) {
            this.dateOfBirth = dateOfBirth;
            this.nickname = nickname;
            this.firebaseToken = firebaseToken;
            this.kakaoAccessToken = kakaoAccessToken;
            this.osType = osType;
        }
    }

    @Getter
    @Setter
    public static class SignUpResponse{
        private String email;
        private String nickname;
        private AuthenticationDto.TokenResponse tokenDto;

        @Builder
        public SignUpResponse(String email, String nickname, AuthenticationDto.TokenResponse tokenDto) {
            this.email = email;
            this.nickname = nickname;
            this.tokenDto = tokenDto;
        }
    }
}
