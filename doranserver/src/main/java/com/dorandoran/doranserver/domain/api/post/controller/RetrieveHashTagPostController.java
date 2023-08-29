package com.dorandoran.doranserver.domain.api.post.controller;

import com.dorandoran.doranserver.domain.api.comment.service.CommentServiceImpl;
import com.dorandoran.doranserver.domain.api.hashtag.service.HashTagServiceImpl;
import com.dorandoran.doranserver.domain.api.member.service.MemberBlockListService;
import com.dorandoran.doranserver.domain.api.member.service.MemberService;
import com.dorandoran.doranserver.domain.api.post.dto.RetrieveHashtagDto;
import com.dorandoran.doranserver.domain.api.hashtag.domain.HashTag;
import com.dorandoran.doranserver.domain.api.member.domain.Member;
import com.dorandoran.doranserver.domain.api.post.domain.Post;
import com.dorandoran.doranserver.domain.api.post.service.PostHashServiceImpl;
import com.dorandoran.doranserver.domain.api.post.service.PostLikeServiceImpl;
import com.dorandoran.doranserver.domain.api.background.domain.imgtype.ImgType;
import com.dorandoran.doranserver.global.util.BlockMemberFilter;
import com.dorandoran.doranserver.global.util.distance.DistanceUtil;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    private final DistanceUtil distanceService;
    private final MemberService memberService;
    private final BlockMemberFilter blockMemberFilter;
    private final MemberBlockListService memberBlockListService;

    @Value("${doran.ip.address}")
    String ipAddress;

    @GetMapping("/post/hashtag")
    ResponseEntity<List<RetrieveHashtagDto.ReadHashtagResponse>> retrievePostByHashTag(@RequestBody RetrieveHashtagDto.ReadHashtag retrieveHashTagPostDto,
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

        List<Member> memberBlockListByBlockingMember = memberBlockListService.findMemberBlockListByBlockingMember(member);

        HashTag hashTag = hashTagService.findByHashTagName(hashtagName);


        List<Post> postList;
        if (retrieveHashTagPostDto.getPostCnt() == 0) { //first find
            postList = postHashService.inquiryFirstPostHash(hashTag,member, memberBlockListByBlockingMember);

        } else {
            postList = postHashService.inquiryPostHash(hashTag, retrieveHashTagPostDto.getPostCnt(), member, memberBlockListByBlockingMember);
        }
        List<Integer> lIkeCntList = postLikeService.findLIkeCntByPostList(postList);
        Iterator<Integer> likeCntListIter = lIkeCntList.iterator();

        List<Boolean> likeResultByPostList = postLikeService.findLikeResultByPostList(userEmail, postList);
        Iterator<Boolean> likeResultByPostListIter = likeResultByPostList.iterator();

        List<Integer> commentAndReplyCntList = commentService.findCommentAndReplyCntByPostIdByList(postList);
        Iterator<Integer> commentAndReplyCntListIter = commentAndReplyCntList.iterator();
        List<RetrieveHashtagDto.ReadHashtagResponse> postResponseList = new ArrayList<>();
        for (Post post : postList) {
            Integer distance;
            if (isLocationPresent && post.getLocation() != null) {
                GeometryFactory geometryFactory = new GeometryFactory();
                String latitude = splitLocation[0];
                String longitude = splitLocation[1];
                Coordinate coordinate = new Coordinate(Double.parseDouble(latitude), Double.parseDouble(longitude));
                Point point = geometryFactory.createPoint(coordinate);

                distance = (int) Math.round(point.distance(post.getLocation()) * 100);
            } else {
                distance = null;
            }

            String[] splitImgName = post.getImgName().split("[.]");
            String imgName = splitImgName[0];
            RetrieveHashtagDto.ReadHashtagResponse postResponse = RetrieveHashtagDto.ReadHashtagResponse.builder()
                    .postId(post.getPostId())
                    .contents(post.getContent())
                    .postTime(post.getPostTime())
                    .location(distance)
                    .likeCnt(likeCntListIter.hasNext()?likeCntListIter.next():0)
                    .likeResult(likeResultByPostListIter.hasNext()?likeResultByPostListIter.next():false)
                    .replyCnt(commentAndReplyCntListIter.hasNext()?commentAndReplyCntListIter.next():0)
                    .backgroundPicUri(ipAddress + (post.getSwitchPic().equals(ImgType.DefaultBackground) ? ":8080/api/pic/default/" : ":8080/api/pic/member/") + imgName)
                    .font(post.getFont())
                    .fontColor(post.getFontColor())
                    .fontSize(post.getFontSize())
                    .fontBold(post.getFontBold())
                    .isWrittenByMember(post.getMemberId().getEmail().equals(userEmail) ? Boolean.TRUE : Boolean.FALSE)
                    .build();
            postResponseList.add(postResponse);
        }

        return ResponseEntity.ok().body(postResponseList);
    }
}
