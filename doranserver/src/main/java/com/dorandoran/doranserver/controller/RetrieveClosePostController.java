package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.dto.RetrievePostDto;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.MemberBlockList;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.imgtype.ImgType;
import com.dorandoran.doranserver.service.*;
import com.dorandoran.doranserver.service.distance.DistanceService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Timed
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
@Slf4j
public class RetrieveClosePostController {

    private final PostService postService;
    private final DistanceService distanceService;
    private final PostLikeService postLikeService;
    private final CommentService commentService;
    private final MemberService memberService;
    private final BlockMemberFilter blockMemberFilter;
    private final MemberBlockListService memberBlockListService;
    @Value("${doran.ip.address}")
    String ipAddress;

    @GetMapping("/post/close")
    ResponseEntity<ArrayList<RetrievePostDto.ReadPostResponse>> retrievePost(@RequestParam Long postCnt,
                                                                             @RequestParam(required = false, defaultValue = "") String location,
                                                                             @AuthenticationPrincipal UserDetails userDetails) {

        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userDetails.getUsername());
        List<MemberBlockList> memberBlockListByBlockingMember = memberBlockListService.findMemberBlockListByBlockingMember(member);

        log.info(location);
        ArrayList<RetrievePostDto.ReadPostResponse> postResponseDtoList = new ArrayList<>();
        String[] split = location.split(",");
        double Slat = Double.parseDouble(split[0])-0.1;
        log.info("{}",Slat);
        double Llat = Double.parseDouble(split[0])+0.1;
        log.info("{}",Llat);
        double Slon = Double.parseDouble(split[1])-0.1;
        log.info("{}",Slon);
        double Llon = Double.parseDouble(split[1])+0.1;
        log.info("{}",Llon);

        RetrievePostDto.ReadPostResponse.ReadPostResponseBuilder builder = RetrievePostDto.ReadPostResponse.builder();
        if (postCnt == 0) {
            List<Post> firstPost = postService.findFirstClosePost(Slat,Llat,Slon,Llon);
//            List<Post> postFilter = blockMemberFilter.postFilter(postService,firstPost, memberBlockListByBlockingMember);
            makeClosePostResponseList(member, userEmail, postResponseDtoList, split, builder, firstPost);
        }else {
            List<Post> postList = postService.findClosePost(Slat, Llat, Slon, Llon, postCnt);
//            List<Post> postFilter = blockMemberFilter.postFilter(postService,postList, memberBlockListByBlockingMember);
            makeClosePostResponseList(member, userEmail, postResponseDtoList, split, builder, postList);
        }
        return ResponseEntity.ok().body(postResponseDtoList);
    }

    private void makeClosePostResponseList(Member member, String userEmail, ArrayList<RetrievePostDto.ReadPostResponse> postResponseDtoList, String[] split, RetrievePostDto.ReadPostResponse.ReadPostResponseBuilder builder, List<Post> firstPost) {
        firstPost.iterator().forEachRemaining(e->{
            if (!e.getMemberId().equals(member) && e.getForMe() == Boolean.TRUE) {
                return;
            }
            Integer lIkeCnt = postLikeService.findLIkeCnt(e);

            Boolean likeResult = postLikeService.findLikeResult(userEmail, e);

            Integer commentAndReplyCntByPostId = commentService.findCommentAndReplyCntByPostId(e);

            Double distance = distanceService.getDistance(Double.parseDouble(split[0]),
                    Double.parseDouble(split[1]),
                    e.getLatitude(),
                    e.getLongitude());

            builder.postId(e.getPostId())
                    .isWrittenByMember(e.getMemberId().getEmail().equals(userEmail) ? Boolean.TRUE : Boolean.FALSE)
                    .contents(e.getContent())
                    .postTime(e.getPostTime())
                    .location(distance.intValue())
                    .likeCnt(lIkeCnt)
                    .likeResult(likeResult)
                    .replyCnt(commentAndReplyCntByPostId)
                    .font(e.getFont())
                    .fontColor(e.getFontColor())
                    .fontSize(e.getFontSize())
                    .fontBold(e.getFontBold());


            if (e.getSwitchPic() == ImgType.UserUpload) {
                String[] split1 = e.getImgName().split("[.]");
                builder.backgroundPicUri(ipAddress + ":8080/api/pic/member/" + split1[0]);
            } else {
                String[] split1 = e.getImgName().split("[.]");
                builder.backgroundPicUri(ipAddress + ":8080/api/pic/default/" + split1[0]);
            }
            postResponseDtoList.add(builder.build());
        });
    }
}
