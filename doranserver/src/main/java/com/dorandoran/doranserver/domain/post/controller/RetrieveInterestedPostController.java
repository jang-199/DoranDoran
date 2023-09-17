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
import com.dorandoran.doranserver.global.util.annotation.Trace;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

    @Trace
    @GetMapping("/post/interested")
    ResponseEntity<LinkedList<HashMap<String, RetrieveInterestedDto.ReadInterestedResponse>>>
    retrieveInterestedPost(@AuthenticationPrincipal UserDetails userDetails) {

        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail);
        List<Member> memberBlockListByBlockingMember = memberBlockListService.findMemberBlockListByBlockingMember(member);
        List<HashTag> hashTagList = memberHashService.findHashByMember(member).stream()
                .map(MemberHash::getHashTagId)
                .toList();
        log.info("hashTag size: {}",hashTagList.size());
        for (HashTag hashTag : hashTagList) {
            log.info("hashtagid : {}",hashTag.getHashTagId());
        }

        List<Post> postList = postHashService.findTopOfPostHash(hashTagList, member, memberBlockListByBlockingMember);
        for (Post post : postList) {
            log.info("postId : {}",post.getPostId());
        }

        LinkedMultiValueMap<Post, String> stringPostLinkedHashMap = postHashService.makeStringPostHashMap(postList, hashTagList);
        for (Map.Entry<Post, List<String>> postListEntry : stringPostLinkedHashMap.entrySet()) {
            log.info("LinkedHashMap: {} , {}",postListEntry.getKey().getPostId(),postListEntry.getValue());
        }

        List<Integer> lIkeCntList = postLikeService.findLIkeCntByPostList(postList);
        log.info("lIkeCntList size: {}",lIkeCntList.size());

        List<Boolean> likeResultByPostList = postLikeService.findLikeResultByPostList(userEmail, postList);
        log.info("likeResultByPostList size: {}",likeResultByPostList.size());

        List<Integer> commentAndReplyCntList = commentService.findCommentAndReplyCntByPostIdByList(postList);
        log.info("commentAndReplyCntList size: {}",commentAndReplyCntList.size());

        RetrieveResponseUtils.InterestedPostResponse retrieveResponseUtils = RetrieveResponseUtils.InterestedPostResponse.builder()
                .userEmail(userEmail)
                .ipAddress(ipAddress)
                .build();

        LinkedList<HashMap<String, RetrieveInterestedDto.ReadInterestedResponse>> responseList = retrieveResponseUtils.makeRetrieveInterestedResponseList(postList, lIkeCntList, likeResultByPostList, commentAndReplyCntList, stringPostLinkedHashMap);

        if (responseList.getFirst().isEmpty()) {
            return ResponseEntity.noContent().build();
        }//todo aop로 변경
        return ResponseEntity.ok(responseList);
    }
}
