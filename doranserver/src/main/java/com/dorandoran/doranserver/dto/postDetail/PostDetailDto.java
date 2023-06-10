package com.dorandoran.doranserver.dto.postDetail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "글 상세정보")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDetailDto {
    @Schema(description = "글 내용", example = "글 내용입니다.")
    String content; // 글 내용
    @Schema(description = "글 작성 시간")
    LocalDateTime postTime; // 작성 시간
    @Schema(description = "현재 위치")
    Integer location; //위치(떨어진 거리)
    @Schema(description = "글 좋아요 개수")
    Integer postLikeCnt; //글 좋아요 개수
    @Schema(description = "글 좋아요 유무", example = "True")
    Boolean postLikeResult; //글 좋아요 유무
    @Schema(description = "댓글 개수")
    Integer commentCnt; // 댓글 개수
    @Schema(description = "배경사진 링크")
    String backgroundPicUri; //배경사진 링크
    @Schema(description = "기존 닉네임")
    String postNickname; //글 쓴 사람 닉네임
    @Schema(description = "글 작성자 익명성 여부")
    Boolean postAnonymity; //글 작성자 익명성 여부
    @Schema(description = "글꼴", example = "Jua")
    String font;
    @Schema(description = "글자 색깔", example = "Black")
    String fontColor;
    @Schema(description = "글자 사이즈", example = "30")
    Integer fontSize;
    @Schema(description = "글자 굵기", example = "50")
    Integer fontBold;
    @Schema(description = "댓글 작성 유무", example = "true")
    Boolean checkWrite;
    @Schema(description = "댓글 상세정보 list")
    List<CommentDetailDto> commentDetailDto; //댓글
    @Schema(description = "글의 해시태그 list")
    List<String> postHashes; //글의 해시태그
}
