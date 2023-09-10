package com.dorandoran.doranserver.domain.post.controller;

import com.dorandoran.doranserver.domain.comment.service.CommentService;
import com.dorandoran.doranserver.domain.member.service.MemberBlockListService;
import com.dorandoran.doranserver.domain.member.service.MemberService;
import com.dorandoran.doranserver.domain.post.dto.RetrievePopularDto;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.post.service.PopularPostService;
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
public class RetrievePopularPostController {
    @Value("${doran.ip.address}")
    String ipAddress;
    private final PopularPostService popularPostService;
    private final PostLikeService postLikeService;
    private final CommentService commentService;
    private final MemberService memberService;
    private final MemberBlockListService memberBlockListService;

    @Trace
    @GetMapping("/post/popular")
    ResponseEntity<List<RetrievePopularDto.ReadPopularResponse>> retrievePopularPost(@RequestParam Long postCnt,
                                                                                     @RequestParam(required = false, defaultValue = "") String location,
                                                                                     @AuthenticationPrincipal UserDetails userDetails){

        boolean isLocationPresent = !location.isBlank();

        String[] splitLocation = location.split(",");
        String userEmail = userDetails.getUsername();

        Member member = memberService.findByEmail(userEmail);
        List<Member> memberBlockListByBlockingMember = memberBlockListService.findMemberBlockListByBlockingMember(member);

        List<Post> postList;
        if (postCnt.equals(0L)) {
            postList = popularPostService.findFirstPopularPost(member, memberBlockListByBlockingMember);

        } else {
            postList = popularPostService.findPopularPost(postCnt,member, memberBlockListByBlockingMember);
        }

        List<Integer> lIkeCntList = postLikeService.findLIkeCntByPostList(postList);

        List<Boolean> likeResultByPostList = postLikeService.findLikeResultByPostList(userEmail, postList);

        List<Integer> commentAndReplyCntList = commentService.findCommentAndReplyCntByPostIdByList(postList);

        RetrieveResponseUtils.PopularPostResponse retrieveResponseUtils = RetrieveResponseUtils.PopularPostResponse.builder()
                .isLocationPresent(isLocationPresent)
                .splitLocation(splitLocation)
                .ipAddress(ipAddress)
                .userEmail(userEmail)
                .build();

        List<RetrievePopularDto.ReadPopularResponse> responseList = retrieveResponseUtils.makeRetrievePopularResponseList(postList, lIkeCntList, likeResultByPostList, commentAndReplyCntList);

        if (responseList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(responseList);
    }
}
