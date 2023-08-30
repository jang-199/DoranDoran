package com.dorandoran.doranserver.domain.api.post.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


public class RetrieveCloseDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ReadCloseResponse{
        private Long postId;
        private String contents;
        private LocalDateTime postTime;
        private Integer location;
        private Integer likeCnt;
        private Boolean likeResult;
        private Integer replyCnt;
        private String backgroundPicUri;
        private String font;
        private String fontColor;
        private Integer fontSize;
        private Integer fontBold;
        private Boolean isWrittenByMember;

        @Builder
        public ReadCloseResponse(Long postId, String contents, LocalDateTime postTime, Integer location, Integer likeCnt, Boolean likeResult, Integer replyCnt, String backgroundPicUri, String font, String fontColor, Integer fontSize, Integer fontBold, Boolean isWrittenByMember) {
            this.postId = postId;
            this.contents = contents;
            this.postTime = postTime;
            this.location = location;
            this.likeCnt = likeCnt;
            this.likeResult = likeResult;
            this.replyCnt = replyCnt;
            this.backgroundPicUri = backgroundPicUri;
            this.font = font;
            this.fontColor = fontColor;
            this.fontSize = fontSize;
            this.fontBold = fontBold;
            this.isWrittenByMember = isWrittenByMember;
        }
    }
}
