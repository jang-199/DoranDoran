package com.dorandoran.doranserver.domain.hashtag.dto;

import com.dorandoran.doranserver.domain.hashtag.domain.HashTag;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class HashTagDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateHashTag{
        private String hashTag;

        @Builder
        public CreateHashTag(String hashTag) {
            this.hashTag = hashTag;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DeleteHashTag{
        private String hashTag;

        @Builder
        public DeleteHashTag(String hashTag) {
            this.hashTag = hashTag;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ReadHashtagResponse{
        String hashTagName;
        Long hashTagCount;
        Boolean hashTagCheck;

        @Builder
        public ReadHashtagResponse(String hashTagName, Long hashTagCount, Boolean hashTagCheck) {
            this.hashTagName = hashTagName;
            this.hashTagCount = hashTagCount;
            this.hashTagCheck = hashTagCheck;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ReadPopularHashTagResponse {
        private String hashTagName;
        private Long hashTagCount;

        @Builder
        public ReadPopularHashTagResponse(HashTag hashTag) {
            this.hashTagName = hashTag.getHashTagName();
            this.hashTagCount = hashTag.getHashTagCount();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ReadMemberHashTagResponse{
        private List<String> hashTagList;

        @Builder
        public ReadMemberHashTagResponse(List<String> hashTagList) {
            this.hashTagList = hashTagList;
        }
    }
}
