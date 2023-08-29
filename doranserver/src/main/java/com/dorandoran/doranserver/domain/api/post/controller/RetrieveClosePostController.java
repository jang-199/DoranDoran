package com.dorandoran.doranserver.domain.api.post.controller;

import com.dorandoran.doranserver.domain.api.comment.service.CommentService;
import com.dorandoran.doranserver.domain.api.member.service.MemberBlockListService;
import com.dorandoran.doranserver.domain.api.member.service.MemberService;
import com.dorandoran.doranserver.domain.api.post.dto.RetrievePostDto;
import com.dorandoran.doranserver.domain.api.member.domain.Member;
import com.dorandoran.doranserver.domain.api.post.domain.Post;
import com.dorandoran.doranserver.domain.api.background.domain.imgtype.ImgType;
import com.dorandoran.doranserver.domain.api.post.service.PostLikeService;
import com.dorandoran.doranserver.domain.api.post.service.PostService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Timed
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
@Slf4j
public class RetrieveClosePostController {

    private final PostService postService;
    private final DistanceUtil distanceService;
    private final PostLikeService postLikeService;
    private final CommentService commentService;
    private final MemberService memberService;
    private final BlockMemberFilter blockMemberFilter;
    private final MemberBlockListService memberBlockListService;
    @Value("${doran.ip.address}")
    String ipAddress;

    @GetMapping("/post/close")
    ResponseEntity<?> retrievePost(@RequestParam Long postCnt,
                                   @RequestParam String location,
                                   @RequestParam double range,
                                   @AuthenticationPrincipal UserDetails userDetails) {

        boolean isLocationPresent = !location.isBlank();

        if (!isLocationPresent) {
            return ResponseEntity.badRequest().body("location does not exist");
        }
        String userEmail = userDetails.getUsername();

        String[] splitLocation = location.split(",");

        Member member = memberService.findByEmail(userEmail);
        List<Member> memberBlockListByBlockingMember = memberBlockListService.findMemberBlockListByBlockingMember(member);


        GeometryFactory geometryFactory = new GeometryFactory();
        String latitude = splitLocation[0];
        String longitude = splitLocation[1];
        Coordinate coordinate = new Coordinate(Double.parseDouble(latitude), Double.parseDouble(longitude));
        Point clientPoint = geometryFactory.createPoint(coordinate);




        List<Post> postList;
        if (postCnt == 0) {
            postList = postService.findFirstClosePost(clientPoint, range,memberBlockListByBlockingMember);
        }else {
            postList = postService.findClosePost(clientPoint, range,postCnt,memberBlockListByBlockingMember);
        }

        List<Integer> lIkeCntList = postLikeService.findLIkeCntByPostList(postList);
        Iterator<Integer> likeCntListIter = lIkeCntList.iterator();

        List<Boolean> likeResultByPostList = postLikeService.findLikeResultByPostList(userEmail, postList);
        Iterator<Boolean> likeResultByPostListIter = likeResultByPostList.iterator();

        List<Integer> commentAndReplyCntList = commentService.findCommentAndReplyCntByPostIdByList(postList);
        Iterator<Integer> commentAndReplyCntListIter = commentAndReplyCntList.iterator();

        List<RetrievePostDto.ReadPostResponse> postResponseList = new ArrayList<>();
        for (Post post : postList) {

            Integer distance = (int) Math.round(clientPoint.distance(post.getLocation()) * 100);

            String[] splitImgName = post.getImgName().split("[.]");
            String imgName = splitImgName[0];
            RetrievePostDto.ReadPostResponse postResponse = RetrievePostDto.ReadPostResponse.builder()
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
