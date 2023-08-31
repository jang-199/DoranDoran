package com.dorandoran.doranserver.domain.post.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

public class RetrievePopularDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ReadPopularResponse{
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
        public ReadPopularResponse(Long postId, String contents, LocalDateTime postTime, Integer location, Integer likeCnt, Boolean likeResult, Integer replyCnt, String backgroundPicUri, String font, String fontColor, Integer fontSize, Integer fontBold, Boolean isWrittenByMember) {
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
