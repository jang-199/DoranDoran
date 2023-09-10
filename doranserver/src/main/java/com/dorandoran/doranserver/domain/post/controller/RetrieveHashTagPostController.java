package com.dorandoran.doranserver.domain.post.controller;

import com.dorandoran.doranserver.domain.comment.service.CommentService;
import com.dorandoran.doranserver.domain.hashtag.service.HashTagService;
import com.dorandoran.doranserver.domain.member.domain.MemberHash;
import com.dorandoran.doranserver.domain.member.service.MemberBlockListService;
import com.dorandoran.doranserver.domain.member.service.MemberHashService;
import com.dorandoran.doranserver.domain.member.service.MemberService;
import com.dorandoran.doranserver.domain.post.dto.RetrieveHashtagDto;
import com.dorandoran.doranserver.domain.hashtag.domain.HashTag;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.post.domain.Post;
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
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Timed
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api")
public class RetrieveHashTagPostController {

    private final HashTagService hashTagService;
    private final PostHashService postHashService;
    private final PostLikeService postLikeService;
    private final CommentService commentService;
    private final MemberService memberService;
    private final MemberHashService memberHashService;
    private final MemberBlockListService memberBlockListService;

    @Value("${doran.ip.address}")
    String ipAddress;

    @Trace
    @GetMapping("/post/hashtag")
    ResponseEntity<HashMap<String, Object>> retrievePostByHashTag(@RequestBody RetrieveHashtagDto.ReadHashtag retrieveHashTagPostDto,
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

        Optional<MemberHash> memberHashByEmailAndHashTag = memberHashService.findMemberHashByEmailAndHashTag(userEmail, retrieveHashTagPostDto.getHashtagName());
        Boolean isBookmarked = memberHashByEmailAndHashTag.isPresent() ? Boolean.TRUE : Boolean.FALSE;

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

        HashMap<String, Object> responseMap = retrieveResponseUtils.makeRetrieveHashtagResponseList(isBookmarked, postList, lIkeCntList, likeResultByPostList, commentAndReplyCntList);

        List<RetrieveHashtagDto.ReadHashtagResponse> data = (List<RetrieveHashtagDto.ReadHashtagResponse>) responseMap.get("Data");
        if (data.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(responseMap);
    }
}
