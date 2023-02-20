package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.dto.PostResponseDto;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.imgtype.ImgType;
import com.dorandoran.doranserver.service.CommentServiceImpl;
import com.dorandoran.doranserver.service.DistanceService;
import com.dorandoran.doranserver.service.PostLikeServiceImpl;
import com.dorandoran.doranserver.service.PostServiceImpl;
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
public class InquiryPostController {

    @Value("${doran.ip.address}")
    String ipAddress;
    private final PostServiceImpl postService;
    private final PostLikeServiceImpl postLikeService;
    private final CommentServiceImpl commentService;
    private final DistanceService distanceService;

    @GetMapping("/post")
    ResponseEntity<?> inquirePost(@RequestParam String userEmail, @RequestParam Long postCnt, @RequestParam String location) {
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

    private ResponseEntity<?> makePostResponseList(String userEmail,
                                                   ArrayList<PostResponseDto> postResponseDtoList,
                                                   PostResponseDto.PostResponseDtoBuilder builder,
                                                   List<Post> postList,
                                                   String location) {
        for (Post post : postList) {
            Integer lIkeCnt = postLikeService.findLIkeCnt(post);
            Integer commentCntByPostId = commentService.findCommentAndReplyCntByPostId(post);
            if (location.isBlank() || post.getLocation().isBlank()) { //사용자 위치가 "" 거리 계산 안해서 리턴
                builder.location(null)
                        .postId(post.getPostId())
                        .contents(post.getContent())
                        .postTime(post.getPostTime())
                        .ReplyCnt(commentCntByPostId)
                        .likeCnt(lIkeCnt);
            } else {
                String[] userLocation = location.split(",");
                String[] postLocation = post.getLocation().split(",");

                Double distance = distanceService.getDistance(Double.parseDouble(userLocation[0]),
                        Double.parseDouble(userLocation[1]),
                        Double.parseDouble(postLocation[0]),
                        Double.parseDouble(postLocation[1]));

                builder.postId(post.getPostId())
                        .contents(post.getContent())
                        .postTime(post.getPostTime())
                        .location(Long.valueOf(Math.round(distance)).intValue())
                        .ReplyCnt(commentCntByPostId)
                        .likeCnt(lIkeCnt);
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
