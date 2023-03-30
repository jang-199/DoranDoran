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

@Tag(name = "근처글 관련 API", description = "InquiryClosePostController")
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
@Slf4j
public class InquiryClosePostController {

    private final PostServiceImpl postService;
    private final DistanceService distanceService;
    private final PostLikeServiceImpl postLikeService;
    private final CommentServiceImpl commentService;
    @Value("${doran.ip.address}")
    String ipAddress;

    @Tag(name = "근처글 관련 API")
    @Operation(summary = "근처 글 조회", description = "클라이언트의 좌표와 작성된 글의 좌표를 비교하여 클라이언트의 위치와 가까운 곳에서 작성된 글을 조회하여 반환합니다.")
    @ApiResponse(responseCode = "200",description = "조회된 글들 중 사용자가 요청한 인덱스에 해당하는 글 부터 20개의 글을 리턴합니다.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PostResponseDto.class)))
    @GetMapping("/post/close")
    ResponseEntity<ArrayList<PostResponseDto>> inquiryPost(@Parameter(description = "사용자 이메일",required = true) @RequestParam String userEmail,
                                  @Parameter(description = "요청할 글 인덱스. 첫 조회 시에는 0을 입력.",required = true) @RequestParam Long postCnt,
                                  @Parameter(description = "클라이언트 좌표",required = true) @RequestParam String location) {
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

//            Double distance = distanceService.getDistance(Double.parseDouble(split[0]),
//                    Double.parseDouble(split[1]),
//                    Double.parseDouble(e.getLatitude()),
//                    Double.parseDouble(e.getLongitude()));

//            builder.postId(e.getPostId())
//                    .contents(e.getContent())
//                    .postTime(e.getPostTime())
//                    .location(distance.intValue())
//                    .likeCnt(lIkeCnt)
//                    .likeResult(likeResult)
//                    .ReplyCnt(commentAndReplyCntByPostId)
//                    .font(e.getFont())
//                    .fontColor(e.getFontColor())
//                    .fontSize(e.getFontSize())
//                    .fondBold(e.getFontBold());


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
