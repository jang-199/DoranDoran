package com.dorandoran.doranserver.domain.post.controller;

import com.dorandoran.doranserver.domain.comment.service.CommentService;
import com.dorandoran.doranserver.domain.member.service.MemberBlockListService;
import com.dorandoran.doranserver.domain.member.service.MemberHashService;
import com.dorandoran.doranserver.domain.member.service.MemberService;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.post.dto.RetrieveInterestedDto;
import com.dorandoran.doranserver.domain.hashtag.domain.HashTag;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.member.domain.MemberHash;
import com.dorandoran.doranserver.domain.post.service.PostHashService;
import com.dorandoran.doranserver.domain.post.service.PostLikeService;
import com.dorandoran.doranserver.global.util.RetrieveResponseUtils;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Timed
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class RetrieveInterestedPostController {

    private final MemberHashService memberHashService;
    private final MemberService memberService;
    private final PostHashService postHashService;
    private final PostLikeService postLikeService;
    private final CommentService commentService;
    private final MemberBlockListService memberBlockListService;

    @Value("${doran.ip.address}")
    String ipAddress;

    @GetMapping("/post/interested")
    ResponseEntity<LinkedList<HashMap<String, RetrieveInterestedDto.ReadInterestedResponse>>>
    retrieveInterestedPost(@AuthenticationPrincipal UserDetails userDetails) {

        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail);
        List<Member> memberBlockListByBlockingMember = memberBlockListService.findMemberBlockListByBlockingMember(member);
        List<HashTag> hashTagList = memberHashService.findHashByMember(member).stream()
                .map(MemberHash::getHashTagId)
                .toList();

        List<Post> postList = postHashService.findTopOfPostHash(hashTagList, member, memberBlockListByBlockingMember);

        List<Integer> lIkeCntList = postLikeService.findLIkeCntByPostList(postList);

        List<Boolean> likeResultByPostList = postLikeService.findLikeResultByPostList(userEmail, postList);

        List<Integer> commentAndReplyCntList = commentService.findCommentAndReplyCntByPostIdByList(postList);

        RetrieveResponseUtils.InterestedPostResponse retrieveResponseUtils = RetrieveResponseUtils.InterestedPostResponse.builder()
                .userEmail(userEmail)
                .ipAddress(ipAddress)
                .build();

        LinkedList<HashMap<String, RetrieveInterestedDto.ReadInterestedResponse>> responseList = retrieveResponseUtils.makeRetrieveInterestedResponseList(postList, lIkeCntList, likeResultByPostList, commentAndReplyCntList, hashTagList);

        if (responseList.getFirst().isEmpty()) {
            return ResponseEntity.noContent().build();
        }//todo aop로 변경
        return ResponseEntity.ok(responseList);
    }
}
