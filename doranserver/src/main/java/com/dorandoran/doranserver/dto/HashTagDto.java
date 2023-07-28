package com.dorandoran.doranserver.dto;

import com.dorandoran.doranserver.entity.HashTag;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class HashTagDto {
    @Getter
    @Setter
    public static class CreateHashTag{
        private List<String> hashTagList;

        @Builder
        public CreateHashTag(List<String> hashTagList) {
            this.hashTagList = hashTagList;
        }
    }

    @Getter
    @Setter
    public static class DeleteHashTag{
        private List<String> hashTagList;

        @Builder
        public DeleteHashTag(List<String> hashTagList) {
            this.hashTagList = hashTagList;
        }
    }

    @Getter
    @Setter
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
    public static class ReadMemberHashTagResponse{
        private List<String> hashTagList;

        @Builder
        public ReadMemberHashTagResponse(List<String> hashTagList) {
            this.hashTagList = hashTagList;
        }
    }
}
