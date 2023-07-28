package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.dto.RetrievePostDto;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.MemberBlockList;
import com.dorandoran.doranserver.entity.PostLike;
import com.dorandoran.doranserver.entity.imgtype.ImgType;
import com.dorandoran.doranserver.service.BlockMemberFilter;
import com.dorandoran.doranserver.service.MemberBlockListService;
import com.dorandoran.doranserver.service.MemberService;
import com.dorandoran.doranserver.service.PostLikeService;
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
    private final BlockMemberFilter blockMemberFilter;
    private final MemberBlockListService memberBlockListService;

    @GetMapping("/post/member/like/{position}")
    public ResponseEntity<LinkedList<RetrievePostDto.ReadPostResponse>> getAllLikedPosts(@PathVariable("position") Long position,
                                                                @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        Member member = memberService.findByEmail(username);
        List<MemberBlockList> memberBlockListByBlockingMember = memberBlockListService.findMemberBlockListByBlockingMember(member);

        List<PostLike> myPost;
        if (position == 0) {
            myPost = postLikeService.findFirstMyLikedPosts(username);
            myPost = blockMemberFilter.postLikeFilter(myPost, memberBlockListByBlockingMember);
        } else {
            myPost = postLikeService.findMyLikedPosts(username, position);
            myPost = blockMemberFilter.postLikeFilter(myPost, memberBlockListByBlockingMember);
        }

        LinkedList<RetrievePostDto.ReadPostResponse> postDtoList = new LinkedList<>();
        for (PostLike post : myPost) {
            String[] split = post.getPostId().getImgName().split("[.]");

            RetrievePostDto.ReadPostResponse postResponseDto = RetrievePostDto.ReadPostResponse.builder()
                    .postId(post.getPostId().getPostId())
                    .contents(post.getPostId().getContent())
                    .postTime(post.getPostId().getPostTime())
                    .location(null)
                    .likeCnt(postLikeService.findLIkeCnt(post.getPostId()))
                    .likeResult(null)
                    .replyCnt(null)
                    .backgroundPicUri(
                            post.getPostId().getSwitchPic() == ImgType.DefaultBackground
                                    ?
                                    ipAddress + ":8080/api/userpic/" + split[0]
                                    :
                                    ipAddress + ":8080/api/background/" + split[0])
                    .font(post.getPostId().getFont())
                    .fontColor(post.getPostId().getFontColor())
                    .fontSize(post.getPostId().getFontSize())
                    .fontBold(post.getPostId().getFontBold())
                    .build();
            postDtoList.add(postResponseDto);
        }

        return ResponseEntity.ok().body(postDtoList);
    }
}
