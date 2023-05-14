package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.dto.PostResponseDto;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.imgtype.ImgType;
import com.dorandoran.doranserver.service.CommentServiceImpl;
import com.dorandoran.doranserver.service.DistanceService;
import com.dorandoran.doranserver.service.PostLikeServiceImpl;
import com.dorandoran.doranserver.service.PostServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "새로운 글 관련 API", description = "InquiryPostController")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
@Controller
public class InquiryPostController {

    @Value("${doran.ip.address}")
    String ipAddress;
    private final PostServiceImpl postService;
    private final PostLikeServiceImpl postLikeService;
    private final CommentServiceImpl commentService;
    private final DistanceService distanceService;

    @Tag(name = "새로운 글 관련 API")
    @Operation(summary = "모든 새로운 글 조회",description = "새롭게 작성된 모든 글을 조회하여 반환합니다.")
    @ApiResponse(responseCode = "200",description = "조회된 글들 중 사용자가 요청한 인덱스에 해당하는 글 부터 20개를 반환합니다.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,schema = @Schema(implementation = PostResponseDto.class)))
    @GetMapping("/post")
    ResponseEntity<ArrayList<PostResponseDto>> inquirePost(@Parameter(description = "사용자 이메일",required = true) @RequestParam String userEmail,
                                  @Parameter(description = "요청할 글 인덱스. 첫 조회 시에는 0을 입력.",required = true) @RequestParam Long postCnt,
                                  @Parameter(description = "클라이언트 좌표",required = true) @RequestParam String location) {
        ArrayList<PostResponseDto> postResponseDtoList = new ArrayList<>();
        PostResponseDto.PostResponseDtoBuilder builder = PostResponseDto.builder();

        if (postCnt == 0) { //first find
            List<Post> firstPost = postService.findFirstPost();
            return makePostResponseList(userEmail, postResponseDtoList, builder, firstPost, location);
        } else {
            List<Post> postList = postService.findPost(postCnt);
            return makePostResponseList(userEmail, postResponseDtoList, builder, postList, location);
        }
    }

    private ResponseEntity<ArrayList<PostResponseDto>> makePostResponseList(String userEmail,
                                                   ArrayList<PostResponseDto> postResponseDtoList,
                                                   PostResponseDto.PostResponseDtoBuilder builder,
                                                   List<Post> postList,
                                                   String location) {
        for (Post post : postList) {
            Integer lIkeCnt = postLikeService.findLIkeCnt(post);
            Integer commentCntByPostId = commentService.findCommentAndReplyCntByPostId(post);
            if (location.isBlank() || post.getLongitude() == null || post.getLatitude() == null) { //사용자 위치가 "" 거리 계산 안해서 리턴
                builder.location(null)
                        .font(post.getFont())
                        .fontColor(post.getFontColor())
                        .fontSize(post.getFontSize())
                        .fontBold(post.getFontBold())
                        .postId(post.getPostId())
                        .contents(post.getContent())
                        .postTime(post.getPostTime())
                        .ReplyCnt(commentCntByPostId)
                        .likeCnt(lIkeCnt);
            } else {
                String[] userLocation = location.split(",");

                Double distance = distanceService.getDistance(Double.parseDouble(userLocation[0]),
                        Double.parseDouble(userLocation[1]),
                        post.getLatitude(),
                        post.getLongitude());

                builder.postId(post.getPostId())
                        .contents(post.getContent())
                        .postTime(post.getPostTime())
                        .location(Long.valueOf(Math.round(distance)).intValue())
                        .ReplyCnt(commentCntByPostId)
                        .likeCnt(lIkeCnt)
                        .font(post.getFont())
                        .fontColor(post.getFontColor())
                        .fontSize(post.getFontSize())
                        .fontBold(post.getFontBold());
            }

            if (post.getSwitchPic() == ImgType.UserUpload) {
                String[] split = post.getImgName().split("[.]");
                builder.backgroundPicUri(ipAddress + ":8080/api/userpic/" + split[0]);
            } else {
                String[] split = post.getImgName().split("[.]");
                builder.backgroundPicUri(ipAddress + ":8080/api/background/" + split[0]);
            }

            builder.likeResult(postLikeService.findLikeResult(userEmail, post));
            postResponseDtoList.add(builder.build());
        }
        return ResponseEntity.ok().body(postResponseDtoList);
    }
}
