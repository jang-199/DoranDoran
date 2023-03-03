package com.dorandoran.doranserver.dto;

import com.dorandoran.doranserver.dto.commentdetail.CommentDetailDto;
import com.dorandoran.doranserver.entity.PostHash;
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
    String font;
    String fontColor;
    Integer fontSize;
    Integer fondBold;
    List<CommentDetailDto> commentDetailDto; //댓글
    List<String> postHashes; //글의 해시태그
}
