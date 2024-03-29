package com.dorandoran.doranserver.domain.post.dto;

import com.dorandoran.doranserver.domain.comment.dto.CommentDto;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public class PostDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreatePost {
        private String content;
        private Boolean forMe;
        private String location;
        private String backgroundImgName;
        private List<String> hashTagName;
        private MultipartFile file;
        private Boolean anonymity;
        private String font;
        private String fontColor;
        private Integer fontSize;
        private Integer fontBold;

        @Builder
        public CreatePost(String content, Boolean forMe, String location, String backgroundImgName, List<String> hashTagName, MultipartFile file, Boolean anonymity, String font, String fontColor, Integer fontSize, Integer fontBold) {
            this.content = content;
            this.forMe = forMe;
            this.location = location;
            this.backgroundImgName = backgroundImgName;
            this.hashTagName = hashTagName;
            this.file = file;
            this.anonymity = anonymity;
            this.font = font;
            this.fontColor = fontColor;
            this.fontSize = fontSize;
            this.fontBold = fontBold;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DeletePost{
        private Long postId;

        @Builder
        public DeletePost(Long postId) {
            this.postId = postId;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class LikePost{
        private Long postId;

        @Builder
        public LikePost(Long postId) {
            this.postId = postId;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class ReadPost {
        private Long postId;
        private String location;

        @Builder
        public ReadPost(Long postId, String location) {
            this.postId = postId;
            this.location = location;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ReadPostResponse {
        private String content;

        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        private LocalDateTime postTime;
        private Integer location;
        private Integer postLikeCnt;
        private Boolean postLikeResult;
        private Integer commentCnt;
        private String backgroundPicUri;
        private String postNickname;
        private Boolean postAnonymity;
        private String font;
        private String fontColor;
        private Integer fontSize;
        private Integer fontBold;
        private Boolean checkWrite;
        private Boolean isWrittenByMember;
        private HashMap<String, Object> commentDetailDto;
        private List<String> postHashes;

        public ReadPostResponse toEntity(Post post, Integer lIkeCnt, Boolean likeResult, Integer commentCnt, Boolean isWrittenByUser, Boolean checkWrite){
            return ReadPostResponse.builder()
                    .content(post.getContent())
                    .postLikeCnt(lIkeCnt)
                    .postLikeResult(likeResult)
                    .postTime(post.getCreatedTime())
                    .commentCnt(commentCnt)
                    .postAnonymity(post.getAnonymity())
                    .postNickname(post.getMemberId().getNickname())
                    .isWrittenByMember(isWrittenByUser)
                    .checkWrite(checkWrite)
                    .font(post.getFont())
                    .fontColor(post.getFontColor())
                    .fontSize(post.getFontSize())
                    .fontBold(post.getFontBold())
                    .build();
        }

        @Builder
        public ReadPostResponse(String content, LocalDateTime postTime, Integer location, Integer postLikeCnt, Boolean postLikeResult, Integer commentCnt, String backgroundPicUri, String postNickname, Boolean postAnonymity, String font, String fontColor, Integer fontSize, Integer fontBold, Boolean checkWrite, Boolean isWrittenByMember, HashMap<String, Object> commentDetailDto, List<String> postHashes) {
            this.content = content;
            this.postTime = postTime;
            this.location = location;
            this.postLikeCnt = postLikeCnt;
            this.postLikeResult = postLikeResult;
            this.commentCnt = commentCnt;
            this.backgroundPicUri = backgroundPicUri;
            this.postNickname = postNickname;
            this.postAnonymity = postAnonymity;
            this.font = font;
            this.fontColor = fontColor;
            this.fontSize = fontSize;
            this.fontBold = fontBold;
            this.checkWrite = checkWrite;
            this.isWrittenByMember = isWrittenByMember;
            this.commentDetailDto = commentDetailDto;
            this.postHashes = postHashes;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ReadAdminPostResponse{
        String content;
        String userEmail;
        String nickname;
        List<CommentDto.ReadAdminCommentResponse> commentList;

        @Builder
        public ReadAdminPostResponse(Post post, List<CommentDto.ReadAdminCommentResponse> commentList) {
            this.content = post.getContent();
            this.userEmail = post.getMemberId().getEmail();
            this.nickname = post.getMemberId().getNickname();
            this.commentList = commentList;
        }
    }
}
