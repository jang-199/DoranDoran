package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.dto.PostResponseDto;
import com.dorandoran.doranserver.dto.RetrieveHashTagPostDto;
import com.dorandoran.doranserver.entity.*;
import com.dorandoran.doranserver.entity.imgtype.ImgType;
import com.dorandoran.doranserver.service.*;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Timed
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api")
public class RetrieveHashTagPostController {

    private final HashTagServiceImpl hashTagService;
    private final PostHashServiceImpl postHashService;
    private final PostLikeServiceImpl postLikeService;
    private final CommentServiceImpl commentService;
    private final DistanceService distanceService;
    private final MemberService memberService;
    private final BlockMemberFilter blockMemberFilter;
    private final MemberBlockListService memberBlockListService;

    @Value("${doran.ip.address}")
    String ipAddress;

    @PostMapping("/hashtag")
    ResponseEntity<ArrayList<PostResponseDto>> retrievePostByHashTag(@RequestBody RetrieveHashTagPostDto retrieveHashTagPostDto,
                                                                     @AuthenticationPrincipal UserDetails userDetails) {

        String encodeTagName = retrieveHashTagPostDto.getHashtagName();
        String encodeLocation;
        if (retrieveHashTagPostDto.getLocation() != null) {
            encodeLocation = URLDecoder.decode(retrieveHashTagPostDto.getLocation(), StandardCharsets.UTF_8);
        }else {
            encodeLocation = "";
        }
        String encodeEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(encodeEmail);

        List<MemberBlockList> memberBlockListByBlockingMember = memberBlockListService.findMemberBlockListByBlockingMember(member);

        ArrayList<PostResponseDto> postResponseDtoList = new ArrayList<>();
        PostResponseDto.PostResponseDtoBuilder builder = PostResponseDto.builder();

        HashTag hashTag = hashTagService.findByHashTagName(encodeTagName);



        if (retrieveHashTagPostDto.getPostCnt() == 0) { //first find
            List<PostHash> postHashes = postHashService.inquiryFirstPostHash(hashTag);
            List<PostHash> postHashFilter = blockMemberFilter.postHashFilter(postHashes, memberBlockListByBlockingMember);
            return makeResponseList(member, encodeLocation, encodeEmail, postResponseDtoList, builder, postHashFilter);

        } else {
            List<PostHash> postHashes = postHashService.inquiryPostHash(hashTag, retrieveHashTagPostDto.getPostCnt());
            List<PostHash> postHashFilter = blockMemberFilter.postHashFilter(postHashes, memberBlockListByBlockingMember);
            return makeResponseList(member, encodeLocation, encodeEmail, postResponseDtoList, builder, postHashFilter);
        }
    }

    private ResponseEntity<ArrayList<PostResponseDto>> makeResponseList(Member member, String encodeLocation, String encodeEmail, ArrayList<PostResponseDto> postResponseDtoList, PostResponseDto.PostResponseDtoBuilder builder, List<PostHash> postHashes) {
        List<Post> postList = postHashes.stream().map((postHash -> postHash.getPostId())).collect(Collectors.toList());

        for (Post post : postList) {
            if (!post.getMemberId().equals(member) && post.getForMe()==true) {
                continue;
            }
            Integer lIkeCnt = postLikeService.findLIkeCnt(post);
            Integer commentCntByPostId = commentService.findCommentAndReplyCntByPostId(post);
            if (encodeLocation.isBlank() || post.getLongitude() == null || post.getLatitude() == null) { //사용자 위치가 "" 거리 계산 안해서 리턴
                builder.location(null)
                        .font(post.getFont())
                        .fontColor(post.getFontColor())
                        .fontSize(post.getFontSize())
                        .fontBold(post.getFontBold())
                        .postId(post.getPostId())
                        .contents(post.getContent())
                        .postTime(post.getPostTime())
                        .ReplyCnt(commentCntByPostId)
                        .likeCnt(lIkeCnt);
            } else {
                String[] userLocation = encodeLocation.split(",");

                Double distance = distanceService.getDistance(Double.parseDouble(userLocation[0]),
                        Double.parseDouble(userLocation[1]),
                        post.getLatitude(),
                        post.getLongitude());

                builder.postId(post.getPostId())
                        .contents(post.getContent())
                        .postTime(post.getPostTime())
                        .location(Long.valueOf(Math.round(distance)).intValue())
                        .ReplyCnt(commentCntByPostId)
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

            builder.likeResult(postLikeService.findLikeResult(encodeEmail, post));
            postResponseDtoList.add(builder.build());
        }
        return ResponseEntity.ok().body(postResponseDtoList);
    }
}
