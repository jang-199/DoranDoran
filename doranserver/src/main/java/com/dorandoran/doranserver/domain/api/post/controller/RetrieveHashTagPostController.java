package com.dorandoran.doranserver.domain.api.post.controller;

import com.dorandoran.doranserver.domain.api.comment.service.CommentServiceImpl;
import com.dorandoran.doranserver.domain.api.hashtag.service.HashTagServiceImpl;
import com.dorandoran.doranserver.domain.api.member.service.MemberBlockListService;
import com.dorandoran.doranserver.domain.api.member.service.MemberService;
import com.dorandoran.doranserver.domain.api.post.dto.RetrieveHashtagDto;
import com.dorandoran.doranserver.domain.api.hashtag.domain.HashTag;
import com.dorandoran.doranserver.domain.api.member.domain.Member;
import com.dorandoran.doranserver.domain.api.post.domain.Post;
import com.dorandoran.doranserver.domain.api.post.service.PostHashServiceImpl;
import com.dorandoran.doranserver.domain.api.post.service.PostLikeServiceImpl;
import com.dorandoran.doranserver.global.util.BlockMemberFilter;
import com.dorandoran.doranserver.global.util.RetrieveResponseUtil;
import com.dorandoran.doranserver.global.util.distance.DistanceUtil;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    private final DistanceUtil distanceService;
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
        String[] splitLocation = null;
        if (isLocationPresent) {
            splitLocation = retrieveHashTagPostDto.getLocation().split(",");
        }

        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail);

        List<Member> memberBlockListByBlockingMember = memberBlockListService.findMemberBlockListByBlockingMember(member);

        HashTag hashTag = hashTagService.findByHashTagName(hashtagName);


        List<Post> postList;
        if (retrieveHashTagPostDto.getPostCnt() == 0) {
            postList = postHashService.inquiryFirstPostHash(hashTag,member, memberBlockListByBlockingMember);

        } else {
            postList = postHashService.inquiryPostHash(hashTag, retrieveHashTagPostDto.getPostCnt(), member, memberBlockListByBlockingMember);
        }
        List<Integer> lIkeCntList = postLikeService.findLIkeCntByPostList(postList);

        List<Boolean> likeResultByPostList = postLikeService.findLikeResultByPostList(userEmail, postList);

        List<Integer> commentAndReplyCntList = commentService.findCommentAndReplyCntByPostIdByList(postList);

        RetrieveResponseUtil retrieveResponseUtil = RetrieveResponseUtil.builder()
                .isLocationPresent(isLocationPresent)
                .splitLocation(splitLocation)
                .ipAddress(ipAddress)
                .userEmail(userEmail)
                .build();

        List<RetrieveHashtagDto.ReadHashtagResponse> readHashtagResponses = retrieveResponseUtil.makeRetrieveHashtagResponseList(postList, lIkeCntList, likeResultByPostList, commentAndReplyCntList);

        return ResponseEntity.ok().body(readHashtagResponses);
    }
}
