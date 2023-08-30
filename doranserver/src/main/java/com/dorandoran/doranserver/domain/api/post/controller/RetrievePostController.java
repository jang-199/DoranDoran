package com.dorandoran.doranserver.domain.api.post.controller;

import com.dorandoran.doranserver.domain.api.comment.service.CommentService;
import com.dorandoran.doranserver.domain.api.member.service.MemberBlockListService;
import com.dorandoran.doranserver.domain.api.member.service.MemberService;
import com.dorandoran.doranserver.domain.api.post.dto.RetrievePostDto;
import com.dorandoran.doranserver.domain.api.member.domain.Member;
import com.dorandoran.doranserver.domain.api.post.domain.Post;
import com.dorandoran.doranserver.domain.api.post.service.PostLikeService;
import com.dorandoran.doranserver.domain.api.post.service.PostService;
import com.dorandoran.doranserver.global.util.RetrieveResponseUtil;
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


        RetrieveResponseUtil retrieveResponseUtil = RetrieveResponseUtil.builder()
                .isLocationPresent(isLocationPresent)
                .splitLocation(splitLocation)
                .ipAddress(ipAddress)
                .userEmail(userEmail)
                .build();

        List<RetrievePostDto.ReadPostResponse> responseList = retrieveResponseUtil.makeRetrievePostResponseList(postList, lIkeCntList, likeResultByPostList, commentAndReplyCntList);

        return ResponseEntity.ok().body(responseList);
    }
}
