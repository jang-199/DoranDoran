package com.dorandoran.doranserver.domain.post.controller;

import com.dorandoran.doranserver.global.util.annotation.Trace;
import com.dorandoran.doranserver.domain.post.dto.RetrievePostDto;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.background.domain.imgtype.ImgType;
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

import java.util.LinkedList;
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
    ResponseEntity<LinkedList<RetrievePostDto.ReadPostResponse>> getAllPosts(@PathVariable("position") Long position,
                                                            @AuthenticationPrincipal UserDetails userDetails) {

        Member member = memberService.findByEmail(userDetails.getUsername());
        List<Post> myPost;
        if (position == 0) {
            myPost = postService.findFirstMyPost(member);
        } else {
            myPost = postService.findMyPost(member, position);
        }

        LinkedList<RetrievePostDto.ReadPostResponse> postDtoList = new LinkedList<>();
        for (Post post : myPost) {
            String[] split = post.getImgName().split("[.]");

            RetrievePostDto.ReadPostResponse postResponseDto = RetrievePostDto.ReadPostResponse.builder().postId(post.getPostId())
                    .contents(post.getContent())
                    .postTime(post.getCreatedTime())
                    .location(null)
                    .likeCnt(postLikeService.findLIkeCnt(post))
                    .likeResult(null)
                    .replyCnt(null)
                    .backgroundPicUri(
                            post.getSwitchPic() == ImgType.DefaultBackground
                                    ? ipAddress + ":8080/api/pic/default/" + split[0]
                                    : ipAddress + ":8080/api/pic/member/" + split[0])
                    .font(post.getFont())
                    .fontColor(post.getFontColor())
                    .fontSize(post.getFontSize())
                    .fontBold(post.getFontBold())
                    .build();
            postDtoList.add(postResponseDto);
        }

        return ResponseEntity.ok().body(postDtoList);
    }
}
