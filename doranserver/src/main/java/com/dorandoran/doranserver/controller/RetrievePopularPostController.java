package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.dto.RetrievePopularDto;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.MemberBlockList;
import com.dorandoran.doranserver.entity.PopularPost;
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
public class RetrievePopularPostController {

    @Value("${doran.ip.address}")
    String ipAddress;
    private final PopularPostService popularPostService;
    private final PostLikeService postLikeService;
    private final CommentService commentService;
    private final DistanceService distanceService;
    private final BlockMemberFilter blockMemberFilter;
    private final MemberService memberService;
    private final MemberBlockListService memberBlockListService;

    @GetMapping("/post/popular")
    ResponseEntity<ArrayList<RetrievePopularDto.ReadPopularResponse>> retrievePopularPost(@RequestParam Long postCnt,
                                                                                          @RequestParam(required = false, defaultValue = "") String location,
                                                                                          @AuthenticationPrincipal UserDetails userDetails){

        String userEmail = userDetails.getUsername();
        log.info("{}",postCnt);
        log.info("{}",location);
        ArrayList<RetrievePopularDto.ReadPopularResponse> postResponseDtoList = new ArrayList<>();
        RetrievePopularDto.ReadPopularResponse.ReadPopularResponseBuilder builder = RetrievePopularDto.ReadPopularResponse.builder();

        Member member = memberService.findByEmail(userDetails.getUsername());
        List<MemberBlockList> memberBlockListByBlockingMember = memberBlockListService.findMemberBlockListByBlockingMember(member);

        if (postCnt == 0) { //first find
            log.info("조건문0");
            List<PopularPost> firstPost = popularPostService.findFirstPopularPost();
            List<PopularPost> popularPostFilter = blockMemberFilter.popularPostFilter(firstPost, memberBlockListByBlockingMember);
            return makePopularPostResponseList(member, userEmail, postResponseDtoList, builder, popularPostFilter, location);
        } else {
            log.info("조건문 else");
            List<PopularPost> postList = popularPostService.findPopularPost(postCnt);
            List<PopularPost> popularPostFilter = blockMemberFilter.popularPostFilter(postList, memberBlockListByBlockingMember);
            log.info("{}",postList.size());
            return makePopularPostResponseList(member, userEmail, postResponseDtoList, builder, popularPostFilter, location);
        }
    }

    private ResponseEntity<ArrayList<RetrievePopularDto.ReadPopularResponse>> makePopularPostResponseList(Member member,
                                                                                                          String userEmail,
                                                                                                          ArrayList<RetrievePopularDto.ReadPopularResponse> postResponseDtoList,
                                                                                                          RetrievePopularDto.ReadPopularResponse.ReadPopularResponseBuilder builder,
                                                                                                          List<PopularPost> postList,
                                                                                                          String location) {
        for (PopularPost popularPost : postList) {
            if (!popularPost.getPostId().getMemberId().equals(member) && popularPost.getPostId().getForMe()) {
                continue;
            }
            Integer lIkeCnt = postLikeService.findLIkeCnt(popularPost.getPostId());
            Integer commentCntByPostId = commentService.findCommentAndReplyCntByPostId(popularPost.getPostId());
            if (location.isBlank() || popularPost.getPostId().getLongitude() == null || popularPost.getPostId().getLatitude() == null) { //사용자 위치가 "" 거리 계산 안해서 리턴
                builder.location(null)
                        .font(popularPost.getPostId().getFont())
                        .isWrittenByMember(popularPost.getPostId().getMemberId().getEmail().equals(userEmail) ? Boolean.TRUE : Boolean.FALSE)
                        .fontColor(popularPost.getPostId().getFontColor())
                        .fontSize(popularPost.getPostId().getFontSize())
                        .fontBold(popularPost.getPostId().getFontBold())
                        .postId(popularPost.getPostId().getPostId())
                        .contents(popularPost.getPostId().getContent())
                        .postTime(popularPost.getPostId().getPostTime())
                        .replyCnt(commentCntByPostId)
                        .likeCnt(lIkeCnt);
            } else {
                String[] userLocation = location.split(",");

                Double distance = distanceService.getDistance(Double.parseDouble(userLocation[0]),
                        Double.parseDouble(userLocation[1]),
                        popularPost.getPostId().getLatitude(),
                        popularPost.getPostId().getLongitude());

                builder.postId(popularPost.getPostId().getPostId())
                        .contents(popularPost.getPostId().getContent())
                        .postTime(popularPost.getPostId().getPostTime())
                        .location(Long.valueOf(Math.round(distance)).intValue())
                        .replyCnt(commentCntByPostId)
                        .likeCnt(lIkeCnt)
                        .font(popularPost.getPostId().getFont())
                        .fontColor(popularPost.getPostId().getFontColor())
                        .fontSize(popularPost.getPostId().getFontSize())
                        .fontBold(popularPost.getPostId().getFontBold());
            }

            if (popularPost.getPostId().getSwitchPic() == ImgType.UserUpload) {
                String[] split = popularPost.getPostId().getImgName().split("[.]");
                builder.backgroundPicUri(ipAddress + ":8080/api/userpic/" + split[0]);
            } else {
                String[] split = popularPost.getPostId().getImgName().split("[.]");
                builder.backgroundPicUri(ipAddress + ":8080/api/background/" + split[0]);
            }

            builder.likeResult(postLikeService.findLikeResult(userEmail, popularPost.getPostId()));
            postResponseDtoList.add(builder.build());
            log.info("size: {}",postResponseDtoList.size());
        }
        return ResponseEntity.ok().body(postResponseDtoList);
    }
}
