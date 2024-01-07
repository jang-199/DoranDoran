package com.dorandoran.doranserver.domain.member.dto;

import com.dorandoran.doranserver.domain.notification.domain.osType.OsType;
import com.dorandoran.doranserver.domain.auth.dto.AuthenticationDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class AccountDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CheckNickname{
        private String nickname;

        @Builder
        public CheckNickname(String nickname) {
            this.nickname = nickname;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CheckRegisteredMember{
        String email;
        OsType osType;
        String firebaseToken;

        @Builder
        public CheckRegisteredMember(String email, OsType osType, String firebaseToken) {
            this.email = email;
            this.osType = osType;
            this.firebaseToken = firebaseToken;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
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
    @NoArgsConstructor
    public static class ChangeNickname{
        private String nickname;

        @Builder
        public ChangeNickname(String nickname) {
            this.nickname = nickname;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SignUp{
        @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateOfBirth;
        private String nickname;
        private String firebaseToken;
        private String kakaoAccessToken;
        private OsType osType;
        private Boolean notifyStatus;

        @Builder
        public SignUp(LocalDate dateOfBirth, String nickname, String firebaseToken, String kakaoAccessToken, OsType osType, Boolean notifyStatus) {
            this.dateOfBirth = dateOfBirth;
            this.nickname = nickname;
            this.firebaseToken = firebaseToken;
            this.kakaoAccessToken = kakaoAccessToken;
            this.osType = osType;
            this.notifyStatus = notifyStatus;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
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

    @Getter
    @Setter
    @NoArgsConstructor
    public static class notificationStatusResponse{
        private Boolean notificationStatus;

        @Builder
        public notificationStatusResponse(Boolean notificationStatus) {
            this.notificationStatus = notificationStatus;
        }
    }
}
