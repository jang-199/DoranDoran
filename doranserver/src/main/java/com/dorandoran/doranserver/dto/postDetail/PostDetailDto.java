package com.dorandoran.doranserver.dto.postDetail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDetailDto {
    String content; // 글 내용
    LocalDateTime postTime; // 작성 시간
    Integer location; //위치(떨어진 거리)
    Integer postLikeCnt; //글 좋아요 개수
    Boolean postLikeResult; //글 좋아요 유무
    Integer commentCnt; // 댓글 개수
    String backgroundPicUri; //배경사진 링크
    String postNickname; //글 쓴 사람 닉네임
    Boolean postAnonymity; //글 작성자 익명성 여부
    String font;
    String fontColor;
    Integer fontSize;
    Integer fontBold;
    Boolean checkWrite;
    List<CommentDetailDto> commentDetailDto; //댓글
    List<String> postHashes; //글의 해시태그
}
