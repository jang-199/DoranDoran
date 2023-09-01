package com.dorandoran.doranserver.domain.post.controller;

import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.global.util.RetrieveResponseUtils;
import com.dorandoran.doranserver.global.util.annotation.Trace;
import com.dorandoran.doranserver.domain.post.dto.RetrievePostDto;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.post.domain.PostLike;
import com.dorandoran.doranserver.domain.background.domain.imgtype.ImgType;
import com.dorandoran.doranserver.domain.member.service.MemberBlockListService;
import com.dorandoran.doranserver.domain.member.service.MemberService;
import com.dorandoran.doranserver.domain.post.service.PostLikeService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

@Timed
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
@Slf4j
public class RetrieveAllLikedPostsController {

    @Value("${userUpload.Store.path}")
    String userUploadPicServerPath;
    @Value("${background.Store.path}")
    String backgroundPicServerPath;
    @Value("${doran.ip.address}")
    String ipAddress;

    private final PostLikeService postLikeService;
    private final MemberService memberService;
    private final MemberBlockListService memberBlockListService;

    @Trace
    @GetMapping("/post/member/like/{position}")
    public ResponseEntity<List<RetrievePostDto.ReadPostResponse>> getAllLikedPosts(@PathVariable("position") Long position,
                                                                                         @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        Member member = memberService.findByEmail(username);
        List<Member> memberBlockListByBlockingMember = memberBlockListService.findMemberBlockListByBlockingMember(member);

        List<Post> myPost;
        if (position == 0) {
            myPost = postLikeService.findFirstMyLikedPosts(username,memberBlockListByBlockingMember);
        } else {
            myPost = postLikeService.findMyLikedPosts(username, position,memberBlockListByBlockingMember);
        }

        List<Integer> likeCntList = postLikeService.findLIkeCntByPostList(myPost);

        RetrieveResponseUtils.AllLikedPostsResponse retrieveResponseUtils = RetrieveResponseUtils.AllLikedPostsResponse.builder()
                .ipAddress(ipAddress)
                .build();
        List<RetrievePostDto.ReadPostResponse> responseList = retrieveResponseUtils.makeAllPostsResponseList(myPost, likeCntList);

        return ResponseEntity.ok().body(responseList);
    }
}
