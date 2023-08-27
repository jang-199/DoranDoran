package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.dto.RetrievePostDto;
import com.dorandoran.doranserver.entity.Member;
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
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class RetrievePostController {
    @Value("${doran.ip.address}")
    String ipAddress;
    private final PostService postService;
    private final PostLikeService postLikeService;
    private final CommentService commentService;
    private final DistanceService distanceService;
    private final MemberBlockListService memberBlockListService;
    private final MemberService memberService;

    @GetMapping("/post")
    ResponseEntity<List<RetrievePostDto.ReadPostResponse>> retrievePost(@RequestParam Long postCnt,
                                                                             @RequestParam(required = false, defaultValue = "") String location,
                                                                             @AuthenticationPrincipal UserDetails userDetails) {
        boolean isLocationPresent = !location.isBlank();
        String[] splitLocation = location.split(",");
        String userEmail = userDetails.getUsername();

        Member member = memberService.findByEmail(userEmail);
        List<Member> memberBlockList = memberBlockListService.findMemberBlockListByBlockingMember(member);
        List<Post> postList;
        if (postCnt.equals(0L)) {
            postList= postService.findFirstPost(member,memberBlockList);
        }else {
            postList = postService.findPost(postCnt, member, memberBlockList);
        }

        List<Integer> lIkeCntList = postLikeService.findLIkeCntByPostList(postList);
        List<Boolean> likeResultByPostList = postLikeService.findLikeResultByPostList(userEmail, postList);
        List<Integer> commentAndReplyCntList = commentService.findCommentAndReplyCntByPostIdByList(postList);

        List<RetrievePostDto.ReadPostResponse> postResponseList = new ArrayList<>();
        for (Post post : postList) {
            Integer distance;
            if (isLocationPresent) {
                String latitude = splitLocation[0];
                String longitude = splitLocation[1];
                distance = distanceService.getDistance(Double.parseDouble(latitude),
                        Double.parseDouble(longitude),
                        post.getLatitude(),
                        post.getLongitude());
            } else {
                distance = null;
            }

            String[] splitImgName = post.getImgName().split("[.]");
            String imgName = splitImgName[0];
            RetrievePostDto.ReadPostResponse postResponse = RetrievePostDto.ReadPostResponse.builder()
                    .postId(post.getPostId())
                    .contents(post.getContent())
                    .postTime(post.getPostTime())
                    .location(distance)
                    .likeCnt(lIkeCntList.iterator().next())
                    .likeResult(likeResultByPostList.iterator().next())
                    .replyCnt(commentAndReplyCntList.iterator().next())
                    .backgroundPicUri(ipAddress + (post.getSwitchPic().equals(ImgType.DefaultBackground) ? ":8080/api/pic/default/" : ":8080/api/pic/member/") + imgName)
                    .font(post.getFont())
                    .fontColor(post.getFontColor())
                    .fontSize(post.getFontSize())
                    .fontBold(post.getFontBold())
                    .isWrittenByMember(post.getMemberId().getEmail().equals(userEmail) ? Boolean.TRUE : Boolean.FALSE)
                    .build();
            postResponseList.add(postResponse);
        }

        return ResponseEntity.ok().body(postResponseList);
    }
}
