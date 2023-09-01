package com.dorandoran.doranserver.domain.post.controller;

import com.dorandoran.doranserver.global.util.RetrieveResponseUtils;
import com.dorandoran.doranserver.global.util.annotation.Trace;
import com.dorandoran.doranserver.domain.post.dto.RetrievePostDto;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.member.service.MemberService;
import com.dorandoran.doranserver.domain.post.service.PostLikeService;
import com.dorandoran.doranserver.domain.post.service.PostService;
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
@RequestMapping("/api")
@RestController
@Slf4j
public class RetrieveAllPostsController {

    @Value("${userUpload.Store.path}")
    String userUploadPicServerPath;
    @Value("${background.Store.path}")
    String backgroundPicServerPath;
    @Value("${doran.ip.address}")
    String ipAddress;

    private final MemberService memberService;
    private final PostService postService;
    private final PostLikeService postLikeService;

    @Trace
    @GetMapping("/post/member/{position}")
    ResponseEntity<List<RetrievePostDto.ReadPostResponse>> getAllPosts(@PathVariable("position") Long position,
                                                            @AuthenticationPrincipal UserDetails userDetails) {

        Member member = memberService.findByEmail(userDetails.getUsername());
        List<Post> myPost;
        if (position == 0) {
            myPost = postService.findFirstMyPost(member);
        } else {
            myPost = postService.findMyPost(member, position);
        }
        List<Integer> likeCntList = postLikeService.findLIkeCntByPostList(myPost);

        RetrieveResponseUtils.AllPostsResponse retrieveResponseUtils = RetrieveResponseUtils.AllPostsResponse.builder()
                .ipAddress(ipAddress)
                .build();

        List<RetrievePostDto.ReadPostResponse> responseList = retrieveResponseUtils.makeAllPostsResponseList(myPost, likeCntList);

        return ResponseEntity.ok().body(responseList);
    }
}
