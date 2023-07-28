package com.dorandoran.doranserver.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class RetrieveHashtagDto {
    @Getter
    @Setter
    public static class ReadHashtag{
        private String hashtagName;
        private Long postCnt;
        private String location;

        @Builder
        public ReadHashtag(String hashtagName, Long postCnt, String location) {
            this.hashtagName = hashtagName;
            this.postCnt = postCnt;
            this.location = location;
        }
    }

    @Getter
    @Setter
    public static class ReadHashtagResponse{
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

        @Builder
        public ReadHashtagResponse(Long postId, String contents, LocalDateTime postTime, Integer location, Integer likeCnt, Boolean likeResult, Integer replyCnt, String backgroundPicUri, String font, String fontColor, Integer fontSize, Integer fontBold) {
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
        }
    }
}
