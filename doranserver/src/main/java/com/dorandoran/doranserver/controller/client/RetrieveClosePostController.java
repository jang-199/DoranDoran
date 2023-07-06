package com.dorandoran.doranserver.controller.client;

import com.dorandoran.doranserver.dto.PostResponseDto;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.imgtype.ImgType;
import com.dorandoran.doranserver.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
@Slf4j
public class RetrieveClosePostController {

    private final PostService postService;
    private final DistanceService distanceService;
    private final PostLikeService postLikeService;
    private final CommentService commentService;
    @Value("${doran.ip.address}")
    String ipAddress;

    @GetMapping("/post/close")
    ResponseEntity<ArrayList<PostResponseDto>> retrievePost(@RequestParam String userEmail,
                                                            @RequestParam Long postCnt,
                                                            @RequestParam String location) {
        log.info(location);
        ArrayList<PostResponseDto> postResponseDtoList = new ArrayList<>();
        String[] split = location.split(",");
        double Slat = Double.parseDouble(split[0])-0.1;
        log.info("{}",Slat);
        double Llat = Double.parseDouble(split[0])+0.1;
        log.info("{}",Llat);
        double Slon = Double.parseDouble(split[1])-0.1;
        log.info("{}",Slon);
        double Llon = Double.parseDouble(split[1])+0.1;
        log.info("{}",Llon);

        PostResponseDto.PostResponseDtoBuilder builder = PostResponseDto.builder();
        if (postCnt == 0) {
            List<Post> firstPost = postService.findFirstClosePost(Slat,Llat,Slon,Llon);
            makeClosePostResponseList(userEmail, postResponseDtoList, split, builder, firstPost);
        }else {
            List<Post> postList = postService.findClosePost(Slat, Llat, Slon, Llon, postCnt);
            makeClosePostResponseList(userEmail, postResponseDtoList, split, builder, postList);
        }
        return ResponseEntity.ok().body(postResponseDtoList);
    }

    private void makeClosePostResponseList(String userEmail, ArrayList<PostResponseDto> postResponseDtoList, String[] split, PostResponseDto.PostResponseDtoBuilder builder, List<Post> firstPost) {
        firstPost.iterator().forEachRemaining(e->{
            Integer lIkeCnt = postLikeService.findLIkeCnt(e);

            Boolean likeResult = postLikeService.findLikeResult(userEmail, e);

            Integer commentAndReplyCntByPostId = commentService.findCommentAndReplyCntByPostId(e);

            Double distance = distanceService.getDistance(Double.parseDouble(split[0]),
                    Double.parseDouble(split[1]),
                    e.getLatitude(),
                    e.getLongitude());

            builder.postId(e.getPostId())
                    .contents(e.getContent())
                    .postTime(e.getPostTime())
                    .location(distance.intValue())
                    .likeCnt(lIkeCnt)
                    .likeResult(likeResult)
                    .ReplyCnt(commentAndReplyCntByPostId)
                    .font(e.getFont())
                    .fontColor(e.getFontColor())
                    .fontSize(e.getFontSize())
                    .fontBold(e.getFontBold());


            if (e.getSwitchPic() == ImgType.UserUpload) {
                String[] split1 = e.getImgName().split("[.]");
                builder.backgroundPicUri(ipAddress + ":8080/api/userpic/" + split1[0]);
            } else {
                String[] split1 = e.getImgName().split("[.]");
                builder.backgroundPicUri(ipAddress + ":8080/api/background/" + split1[0]);
            }
            postResponseDtoList.add(builder.build());
        });
    }
}
