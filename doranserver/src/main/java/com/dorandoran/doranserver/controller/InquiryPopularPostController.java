package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.dto.PostResponseDto;
import com.dorandoran.doranserver.entity.PopularPost;
import com.dorandoran.doranserver.entity.imgtype.ImgType;
import com.dorandoran.doranserver.service.CommentServiceImpl;
import com.dorandoran.doranserver.service.DistanceService;
import com.dorandoran.doranserver.service.PopularPostServiceImpl;
import com.dorandoran.doranserver.service.PostLikeServiceImpl;
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

@Tag(name = "인기글 관련 API", description = "InquiryPopularPostController")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
@Controller
public class InquiryPopularPostController {

    @Value("${doran.ip.address}")
    String ipAddress;
    private final PopularPostServiceImpl popularPostService;
    private final PostLikeServiceImpl postLikeService;
    private final CommentServiceImpl commentService;
    private final DistanceService distanceService;

    @Tag(name = "인기글 관련 API")
    @Operation(summary = "인기글 조회",description = "댓글 개수가 10개 이상인 글을 조회하여 반환합니다.")
    @ApiResponse(responseCode = "200",description = "조회된 글들 중 사용자가 요청한 인덱스에 해당하는 글 부터 20개의 글을 반환합니다.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,schema = @Schema(implementation = PostResponseDto.class)))
    @GetMapping("/post/popular")
    ResponseEntity<ArrayList<PostResponseDto>> inquirePopularPost(@Parameter(description = "사용자 이메일",required = true) @RequestParam String userEmail,
                                         @Parameter(description = "요청할 글 인덱스. 첫 조회 시에는 0을 입력.",required = true) @RequestParam Long postCnt,
                                         @Parameter(description = "클라이언트 좌표",required = true) @RequestParam String location){
        log.info("{}",userEmail);
        log.info("{}",postCnt);
        log.info("{}",location);
        ArrayList<PostResponseDto> postResponseDtoList = new ArrayList<>();
        PostResponseDto.PostResponseDtoBuilder builder = PostResponseDto.builder();

        if (postCnt == 0) { //first find
            log.info("조건문0");
            List<PopularPost> firstPost = popularPostService.findFirstPopularPost();
            log.info("{}",firstPost.size());
            return makePopularPostResponseList(userEmail, postResponseDtoList, builder, firstPost, location);
        } else {
            log.info("조건문 else");
            List<PopularPost> postList = popularPostService.findPopularPost(postCnt);
            log.info("{}",postList.size());
            return makePopularPostResponseList(userEmail, postResponseDtoList, builder, postList, location);
        }
    }

    private ResponseEntity<ArrayList<PostResponseDto>> makePopularPostResponseList(String userEmail,
                                                          ArrayList<PostResponseDto> postResponseDtoList,
                                                          PostResponseDto.PostResponseDtoBuilder builder,
                                                          List<PopularPost> postList,
                                                          String location) {
        for (PopularPost popularPost : postList) {
            Integer lIkeCnt = postLikeService.findLIkeCnt(popularPost.getPostId());
            Integer commentCntByPostId = commentService.findCommentAndReplyCntByPostId(popularPost.getPostId());
            if (location.isBlank() || popularPost.getPostId().getLongitude() == null || popularPost.getPostId().getLatitude() == null) { //사용자 위치가 "" 거리 계산 안해서 리턴
                builder.location(null)
                        .font(popularPost.getPostId().getFont())
                        .fontColor(popularPost.getPostId().getFontColor())
                        .fontSize(popularPost.getPostId().getFontSize())
                        .fontBold(popularPost.getPostId().getFontBold())
                        .postId(popularPost.getPostId().getPostId())
                        .contents(popularPost.getPostId().getContent())
                        .postTime(popularPost.getPostId().getPostTime())
                        .ReplyCnt(commentCntByPostId)
                        .likeCnt(lIkeCnt);
            } else {
                String[] userLocation = location.split(",");

                Double distance = distanceService.getDistance(Double.parseDouble(userLocation[0]),
                        Double.parseDouble(userLocation[1]),
                        popularPost.getPostId().getLatitude(),
                        popularPost.getPostId().getLongitude());

                builder.postId(popularPost.getPostId().getPostId())
                        .contents(popularPost.getPostId().getContent())
                        .postTime(popularPost.getPostId().getPostTime())
                        .location(Long.valueOf(Math.round(distance)).intValue())
                        .ReplyCnt(commentCntByPostId)
                        .likeCnt(lIkeCnt)
                        .font(popularPost.getPostId().getFont())
                        .fontColor(popularPost.getPostId().getFontColor())
                        .fontSize(popularPost.getPostId().getFontSize())
                        .fontBold(popularPost.getPostId().getFontBold());
            }

            if (popularPost.getPostId().getSwitchPic() == ImgType.UserUpload) {
                String[] split = popularPost.getPostId().getImgName().split("[.]");
                builder.backgroundPicUri(ipAddress + ":8080/api/userpic/" + split[0]);
            } else {
                String[] split = popularPost.getPostId().getImgName().split("[.]");
                builder.backgroundPicUri(ipAddress + ":8080/api/background/" + split[0]);
            }

            builder.likeResult(postLikeService.findLikeResult(userEmail, popularPost.getPostId()));
            postResponseDtoList.add(builder.build());
            log.info("size: {}",postResponseDtoList.size());
        }
        return ResponseEntity.ok().body(postResponseDtoList);
    }
}
