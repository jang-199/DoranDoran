package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.dto.PostDto;
import com.dorandoran.doranserver.dto.PostResponseDto;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.imgtype.ImgType;
import com.dorandoran.doranserver.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

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

    @GetMapping("/all-posts/{position}")
    ResponseEntity<LinkedList<PostResponseDto>> getAllPosts(@PathVariable("position") Long position,
                                                            @AuthenticationPrincipal UserDetails userDetails) {

        Member member = memberService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        List<Post> myPost;
        if (position == 0) {
            myPost = postService.findFirstMyPost(member);
        } else {
            myPost = postService.findMyPost(member, position);
        }

        LinkedList<PostResponseDto> postDtoList = new LinkedList<>();
        for (Post post : myPost) {
            String[] split = post.getImgName().split("[.]");

            PostResponseDto postResponseDto = PostResponseDto.builder().postId(post.getPostId())
                    .contents(post.getContent())
                    .postTime(post.getPostTime())
                    .location(null)
                    .likeCnt(postLikeService.findLIkeCnt(post))
                    .likeResult(null)
                    .ReplyCnt(null)
                    .backgroundPicUri(
                            post.getSwitchPic() == ImgType.DefaultBackground
                                    ?
                                    ipAddress + ":8080/api/userpic/" + split[0]
                                    :
                                    ipAddress + ":8080/api/background/" + split[0])
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
