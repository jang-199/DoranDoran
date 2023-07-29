package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.dto.RetrievePostDto;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.MemberBlockList;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.imgtype.ImgType;
import com.dorandoran.doranserver.service.*;
import com.dorandoran.doranserver.service.distance.DistanceService;
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

import java.util.ArrayList;
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
    private final DistanceService distanceService;
    private final MemberBlockListService memberBlockListService;
    private final MemberService memberService;
    private final BlockMemberFilter blockMemberFilter;

    @GetMapping("/post")
    ResponseEntity<ArrayList<RetrievePostDto.ReadPostResponse>> retrievePost(@RequestParam Long postCnt,
                                                            @RequestParam(required = false, defaultValue = "") String location,
                                                            @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        log.info("getAuthorities : {}",userDetails.getAuthorities());
        log.info("getUsername : {}",userDetails.getUsername());

        ArrayList<RetrievePostDto.ReadPostResponse> postResponseDtoList = new ArrayList<>();
        RetrievePostDto.ReadPostResponse.ReadPostResponseBuilder builder = RetrievePostDto.ReadPostResponse.builder();

        Member member = memberService.findByEmail(userDetails.getUsername());
        List<MemberBlockList> memberBlockListByBlockingMember = memberBlockListService.findMemberBlockListByBlockingMember(member);

        if (postCnt == 0) { //first find
            List<Post> firstPost = postService.findFirstPost();
            List<Post> postFilter = blockMemberFilter.postFilter(firstPost, memberBlockListByBlockingMember);
            return makePostResponseList(member, userEmail, postResponseDtoList, builder, postFilter, location);
        } else {
            List<Post> postList = postService.findPost(postCnt);
            List<Post> postFilter = blockMemberFilter.postFilter(postList, memberBlockListByBlockingMember);
            return makePostResponseList(member, userEmail, postResponseDtoList, builder, postFilter, location);
        }
    }

    private ResponseEntity<ArrayList<RetrievePostDto.ReadPostResponse>> makePostResponseList(Member member,
                                                                                             String userEmail,
                                                                                             ArrayList<RetrievePostDto.ReadPostResponse> postResponseDtoList,
                                                                                             RetrievePostDto.ReadPostResponse.ReadPostResponseBuilder builder,
                                                                                             List<Post> postList,
                                                                                             String location) {
        for (Post post : postList) {
            if (!post.getMemberId().equals(member) && post.getForMe()) {
                continue;
            }
            Integer lIkeCnt = postLikeService.findLIkeCnt(post);
            Integer commentCntByPostId = commentService.findCommentAndReplyCntByPostId(post);
            if (location.isBlank() || post.getLongitude() == null || post.getLatitude() == null) { //사용자 위치가 "" 거리 계산 안해서 리턴
                builder.location(null)
                        .font(post.getFont())
                        .isWrittenByMember(post.getMemberId().getEmail().equals(userEmail) ? Boolean.TRUE : Boolean.FALSE)
                        .fontColor(post.getFontColor())
                        .fontSize(post.getFontSize())
                        .fontBold(post.getFontBold())
                        .postId(post.getPostId())
                        .contents(post.getContent())
                        .postTime(post.getPostTime())
                        .replyCnt(commentCntByPostId)
                        .likeCnt(lIkeCnt);
            } else {
                String[] userLocation = location.split(",");

                Double distance = distanceService.getDistance(Double.parseDouble(userLocation[0]),
                        Double.parseDouble(userLocation[1]),
                        post.getLatitude(),
                        post.getLongitude());

                builder.postId(post.getPostId())
                        .contents(post.getContent())
                        .postTime(post.getPostTime())
                        .location(Long.valueOf(Math.round(distance)).intValue())
                        .replyCnt(commentCntByPostId)
                        .likeCnt(lIkeCnt)
                        .font(post.getFont())
                        .fontColor(post.getFontColor())
                        .fontSize(post.getFontSize())
                        .fontBold(post.getFontBold());
            }

            String[] split = post.getImgName().split("[.]");
            if (post.getSwitchPic() == ImgType.UserUpload) {
                builder.backgroundPicUri(ipAddress + ":8080/api/userpic/" + split[0]);
            } else {
                builder.backgroundPicUri(ipAddress + ":8080/api/background/" + split[0]);
            }

            builder.likeResult(postLikeService.findLikeResult(userEmail, post));
            postResponseDtoList.add(builder.build());
        }
        return ResponseEntity.ok().body(postResponseDtoList);
    }
}
