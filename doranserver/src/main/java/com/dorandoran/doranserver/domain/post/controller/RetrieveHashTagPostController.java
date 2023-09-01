package com.dorandoran.doranserver.domain.post.controller;

import com.dorandoran.doranserver.domain.comment.service.CommentServiceImpl;
import com.dorandoran.doranserver.domain.hashtag.service.HashTagServiceImpl;
import com.dorandoran.doranserver.domain.member.service.MemberBlockListService;
import com.dorandoran.doranserver.domain.member.service.MemberService;
import com.dorandoran.doranserver.domain.post.dto.RetrieveHashtagDto;
import com.dorandoran.doranserver.domain.hashtag.domain.HashTag;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.post.service.PostHashServiceImpl;
import com.dorandoran.doranserver.domain.post.service.PostLikeServiceImpl;
import com.dorandoran.doranserver.global.util.RetrieveResponseUtils;
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
    private final MemberService memberService;
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

        RetrieveResponseUtils.HashtagPostResponse retrieveResponseUtils = RetrieveResponseUtils.HashtagPostResponse.builder()
                .isLocationPresent(isLocationPresent)
                .splitLocation(splitLocation)
                .ipAddress(ipAddress)
                .userEmail(userEmail)
                .build();

        List<RetrieveHashtagDto.ReadHashtagResponse> responseList = retrieveResponseUtils.makeRetrieveHashtagResponseList(postList, lIkeCntList, likeResultByPostList, commentAndReplyCntList);

        if (responseList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(responseList);
    }
}
