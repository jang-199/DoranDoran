package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.dto.PostResponseDto;
import com.dorandoran.doranserver.entity.PopularPost;
import com.dorandoran.doranserver.entity.imgtype.ImgType;
import com.dorandoran.doranserver.service.CommentServiceImpl;
import com.dorandoran.doranserver.service.DistanceService;
import com.dorandoran.doranserver.service.PopularPostServiceImpl;
import com.dorandoran.doranserver.service.PostLikeServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("api")
@RestController
@Controller
public class InquiryPopularPostController {

    @Value("${doran.ip.address}")
    String ipAddress;
    private final PopularPostServiceImpl popularPostService;
    private final PostLikeServiceImpl postLikeService;
    private final CommentServiceImpl commentService;
    private final DistanceService distanceService;

    @GetMapping("/post/popular")
    ResponseEntity<?> inquirePopularPost(@RequestParam String userEmail, @RequestParam Long postCnt, @RequestParam String location){
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

    private ResponseEntity<?> makePopularPostResponseList(String userEmail,
                                                          ArrayList<PostResponseDto> postResponseDtoList,
                                                          PostResponseDto.PostResponseDtoBuilder builder,
                                                          List<PopularPost> postList,
                                                          String location) {
        for (PopularPost popularPost : postList) {
            Integer lIkeCnt = postLikeService.findLIkeCnt(popularPost.getPostId());
            Integer commentCntByPostId = commentService.findCommentAndReplyCntByPostId(popularPost.getPostId());
            if (location.isBlank() || popularPost.getPostId().getLongitude().isBlank() || popularPost.getPostId().getLatitude().isBlank()) { //사용자 위치가 "" 거리 계산 안해서 리턴
                builder.location(null)
                        .postId(popularPost.getPostId().getPostId())
                        .contents(popularPost.getPostId().getContent())
                        .postTime(popularPost.getPostId().getPostTime())
                        .ReplyCnt(commentCntByPostId)
                        .likeCnt(lIkeCnt);
            } else {
                String[] userLocation = location.split(",");

                Double distance = distanceService.getDistance(Double.parseDouble(userLocation[0]),
                        Double.parseDouble(userLocation[1]),
                        Double.parseDouble(popularPost.getPostId().getLatitude()),
                        Double.parseDouble(popularPost.getPostId().getLongitude()));

                builder.postId(popularPost.getPostId().getPostId())
                        .contents(popularPost.getPostId().getContent())
                        .postTime(popularPost.getPostId().getPostTime())
                        .location(Long.valueOf(Math.round(distance)).intValue())
                        .ReplyCnt(commentCntByPostId)
                        .likeCnt(lIkeCnt);
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
