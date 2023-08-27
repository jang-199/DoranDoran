package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.dto.RetrieveHashtagDto;
import com.dorandoran.doranserver.dto.RetrievePostDto;
import com.dorandoran.doranserver.entity.*;
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
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Timed
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api")
public class RetrieveHashTagPostController {

    private final HashTagServiceImpl hashTagService;
    private final PostHashServiceImpl postHashService;
    private final PostLikeServiceImpl postLikeService;
    private final CommentServiceImpl commentService;
    private final DistanceService distanceService;
    private final MemberService memberService;
    private final BlockMemberFilter blockMemberFilter;
    private final MemberBlockListService memberBlockListService;

    @Value("${doran.ip.address}")
    String ipAddress;

    @GetMapping("/post/hashtag")
    ResponseEntity<List<RetrieveHashtagDto.ReadHashtagResponse>> retrievePostByHashTag(@RequestBody RetrieveHashtagDto.ReadHashtag retrieveHashTagPostDto,
                                                                     @AuthenticationPrincipal UserDetails userDetails) {

        String hashtagName = retrieveHashTagPostDto.getHashtagName();
        boolean isLocationPresent;

        isLocationPresent = retrieveHashTagPostDto.getLocation() != null;

        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail);

        List<Member> memberBlockListByBlockingMember = memberBlockListService.findMemberBlockListByBlockingMember(member);

        HashTag hashTag = hashTagService.findByHashTagName(hashtagName);


        List<Post> postList;
        if (retrieveHashTagPostDto.getPostCnt() == 0) { //first find
            postList = postHashService.inquiryFirstPostHash(hashTag,member, memberBlockListByBlockingMember);

        } else {
            postList = postHashService.inquiryPostHash(hashTag, retrieveHashTagPostDto.getPostCnt(), member, memberBlockListByBlockingMember);
        }
        List<Integer> lIkeCntList = postLikeService.findLIkeCntByPostList(postList);
        List<Boolean> likeResultByPostList = postLikeService.findLikeResultByPostList(userEmail, postList);
        List<Integer> commentAndReplyCntList = commentService.findCommentAndReplyCntByPostIdByList(postList);
        List<RetrieveHashtagDto.ReadHashtagResponse> postResponseList = new ArrayList<>();
        for (Post post : postList) {
            Integer distance;
            if (isLocationPresent&& post.getLocation()!=null) {
                String latitude = retrieveHashTagPostDto.getLocation().split(",")[0];
                String longitude = retrieveHashTagPostDto.getLocation().split(",")[1];
                distance = distanceService.getDistance(Double.parseDouble(latitude),
                        Double.parseDouble(longitude),
                        post.getLatitude(),
                        post.getLongitude());
            } else {
                distance = null;
            }

            String[] splitImgName = post.getImgName().split("[.]");
            String imgName = splitImgName[0];
            RetrieveHashtagDto.ReadHashtagResponse postResponse = RetrieveHashtagDto.ReadHashtagResponse.builder()
                    .postId(post.getPostId())
                    .contents(post.getContent())
                    .postTime(post.getPostTime())
                    .location(distance)
                    .likeCnt(lIkeCntList.iterator().hasNext()?lIkeCntList.iterator().next():0)
                    .likeResult(likeResultByPostList.iterator().hasNext()?likeResultByPostList.iterator().next():false)
                    .replyCnt(commentAndReplyCntList.iterator().hasNext()?commentAndReplyCntList.iterator().next():0)
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
