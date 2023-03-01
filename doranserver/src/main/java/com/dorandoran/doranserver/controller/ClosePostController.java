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

@Controller
@RequiredArgsConstructor
@RequestMapping("api")
@RestController
@Slf4j
public class ClosePostController {

    private final PostServiceImpl postService;
    private final DistanceService distanceService;
    private final PostLikeServiceImpl postLikeService;
    private final CommentServiceImpl commentService;
    @Value("${doran.ip.address}")
    String ipAddress;

    @GetMapping("/post/close")
    ResponseEntity<?> inquiryPost(@RequestParam String userEmail, @RequestParam Long postCnt, @RequestParam String location) {
        ArrayList<PostResponseDto> postResponseDtoList = new ArrayList<>();
        String[] split = location.split(",");
        PostResponseDto.PostResponseDtoBuilder builder = PostResponseDto.builder();
        if (postCnt == 0) {
            List<Post> firstPost = postService.findFirstPost();
            firstPost.iterator().forEachRemaining(e->{
                Integer lIkeCnt = postLikeService.findLIkeCnt(e);

                Boolean likeResult = postLikeService.findLikeResult(userEmail, e);

                Integer commentAndReplyCntByPostId = commentService.findCommentAndReplyCntByPostId(e);

                    Double distance = distanceService.getDistance(Double.parseDouble(split[0]),
                            Double.parseDouble(split[1]),
                            Double.parseDouble(e.getLatitude()),
                            Double.parseDouble(e.getLongitude()));
                    if (distance <= 10) {
                        builder.postId(e.getPostId())
                        .contents(e.getContent())
                        .postTime(e.getPostTime())
                        .location(distance.intValue())
                        .likeCnt(lIkeCnt)
                        .likeResult(likeResult)
                        .ReplyCnt(commentAndReplyCntByPostId);
                        postResponseDtoList.add()
                    }
            });
        }else {
            List<Post> postList = postService.findPost(postCnt);
        }
    }

//    @GetMapping("/post/close")
//    ResponseEntity<?> inquiryPost(@RequestParam String userEmail, @RequestParam Long postCnt, @RequestParam String location) {
//        ArrayList<PostResponseDto> postResponseDtoList = new ArrayList<>();
//        PostResponseDto.PostResponseDtoBuilder builder = PostResponseDto.builder();
//        if (postCnt == 0) {
//            List<Post> firstPost = postService.findFirstPost();
//            return makeClosePostResponseList(userEmail, location, postResponseDtoList, builder, firstPost);
//        }else {
//            List<Post> postList = postService.findPost(postCnt);
//            return makeClosePostResponseList(userEmail, location, postResponseDtoList, builder, postList);
//        }
//    }

//    private ResponseEntity<?> makeClosePostResponseList(String userEmail,
//                                                        String location,
//                                                        ArrayList<PostResponseDto> postResponseDtoList,
//                                                        PostResponseDto.PostResponseDtoBuilder builder,
//                                                        List<Post> postList) {
//        for (Post post :postList) {
//            String location1 = post.getLocation();
//            String[] postLocationSplit = location1.split(",");
//            String[] userLocationSplit = location.split(",");
//
//            Integer lIkeCnt = postLikeService.findLIkeCnt(post);
//
//            Boolean likeResult = postLikeService.findLikeResult(userEmail, post);
//
//            Integer commentAndReplyCntByPostId = commentService.findCommentAndReplyCntByPostId(post);
//
//            Double distance = distanceService.getDistance(Double.parseDouble(postLocationSplit[0]),
//                    Double.parseDouble(postLocationSplit[1]),
//                    Double.parseDouble(userLocationSplit[0]),
//                    Double.parseDouble(userLocationSplit[1]));
//            builder.postId(post.getPostId())
//                    .contents(post.getContent())
//                    .postTime(post.getPostTime())
//                    .location(distance.intValue())
//                    .likeCnt(lIkeCnt)
//                    .likeResult(likeResult)
//                    .ReplyCnt(commentAndReplyCntByPostId);
//
//            String[] split = post.getImgName().split("[.]");
//            if (post.getSwitchPic() == ImgType.UserUpload) {
//                builder.backgroundPicUri(ipAddress + ":8080/api/userpic/" + split[0]);
//            } else {
//                builder.backgroundPicUri(ipAddress + ":8080/api/background/" + split[0]);
//            }
//            postResponseDtoList.add(builder.build());
//        }
//        return ResponseEntity.ok().body(postResponseDtoList);
//    }
}
