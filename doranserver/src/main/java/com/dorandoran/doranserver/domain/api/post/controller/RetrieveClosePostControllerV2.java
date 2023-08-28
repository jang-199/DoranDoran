//package com.dorandoran.doranserver.controller;
//
//import com.dorandoran.doranserver.dto.RetrievePostDto;
//import com.dorandoran.doranserver.entity.Member;
//import com.dorandoran.doranserver.entity.Post;
//import com.dorandoran.doranserver.entity.imgtype.ImgType;
//import com.dorandoran.doranserver.repository.PostRepository;
//import com.dorandoran.doranserver.service.*;
//import com.dorandoran.doranserver.service.distance.DistanceService;
//import io.micrometer.core.annotation.Timed;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.locationtech.jts.geom.Coordinate;
//import org.locationtech.jts.geom.GeometryFactory;
//import org.locationtech.jts.geom.LineString;
//import org.locationtech.jts.geom.Point;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Timed
//@RequiredArgsConstructor
//@RequestMapping("/api")
//@RestController
//@Slf4j
//public class RetrieveClosePostControllerV2 {
//    private final PostService postService;
//    private final DistanceService distanceService;
//    private final PostLikeService postLikeService;
//    private final CommentService commentService;
//    private final MemberService memberService;
//    private final PostRepository postRepository;
//    private final MemberBlockListService memberBlockListService;
//    @Value("${doran.ip.address}")
//    String ipAddress;
//
//    @GetMapping("/post/close")
//    ResponseEntity<ArrayList<RetrievePostDto.ReadPostResponse>> retrievePost(@RequestParam Long postCnt,
//                                                                             @RequestParam(required = false, defaultValue = "") String location,
//                                                                             @AuthenticationPrincipal UserDetails userDetails) {
//
//        String userEmail = userDetails.getUsername();
//        Member member = memberService.findByEmail(userDetails.getUsername());
//        List<Member> memberBlockListByBlockingMember = memberBlockListService.findMemberBlockListByBlockingMember(member);
//
//        log.info(location);
//        ArrayList<RetrievePostDto.ReadPostResponse> postResponseDtoList = new ArrayList<>();
//        String[] split = location.split(",");
//        GeometryFactory geometryFactory = new GeometryFactory();
//        Coordinate coordinate = new Coordinate(37.51412962521665, 127.02140804696435);
//        Point point = geometryFactory.createPoint(coordinate);
//        List<Post> firstClosePostV2 = postRepository.findFirstClosePostV2(point,PageRequest.of(0, 20));
//        for (Post post :
//                firstClosePostV2) {
//            Point location1 = post.getLocation();
//            double distance = location1.distance(point);
//            log.info("{}",distance);
//        }
//        log.info("{}",firstClosePostV2.size());
//        return ResponseEntity.ok().body(postResponseDtoList);
//    }
//
//    private void makeClosePostResponseList(Member member, String userEmail, ArrayList<RetrievePostDto.ReadPostResponse> postResponseDtoList, String[] split, RetrievePostDto.ReadPostResponse.ReadPostResponseBuilder builder, List<Post> firstPost) {
//        firstPost.iterator().forEachRemaining(e->{
//            if (!e.getMemberId().equals(member) && e.getForMe() == Boolean.TRUE) {
//                return;
//            }
//            Integer lIkeCnt = postLikeService.findLIkeCnt(e);
//
//            Boolean likeResult = postLikeService.findLikeResult(userEmail, e);
//
//            Integer commentAndReplyCntByPostId = commentService.findCommentAndReplyCntByPostId(e);
//
//            Integer distance = distanceService.getDistance(Double.parseDouble(split[0]),
//                    Double.parseDouble(split[1]),
//                    e.getLatitude(),
//                    e.getLongitude());
//
//            builder.postId(e.getPostId())
//                    .isWrittenByMember(e.getMemberId().getEmail().equals(userEmail) ? Boolean.TRUE : Boolean.FALSE)
//                    .contents(e.getContent())
//                    .postTime(e.getPostTime())
//                    .location(distance)
//                    .likeCnt(lIkeCnt)
//                    .likeResult(likeResult)
//                    .replyCnt(commentAndReplyCntByPostId)
//                    .font(e.getFont())
//                    .fontColor(e.getFontColor())
//                    .fontSize(e.getFontSize())
//                    .fontBold(e.getFontBold());
//
//
//            if (e.getSwitchPic() == ImgType.UserUpload) {
//                String[] split1 = e.getImgName().split("[.]");
//                builder.backgroundPicUri(ipAddress + ":8080/api/pic/member/" + split1[0]);
//            } else {
//                String[] split1 = e.getImgName().split("[.]");
//                builder.backgroundPicUri(ipAddress + ":8080/api/pic/default/" + split1[0]);
//            }
//            postResponseDtoList.add(builder.build());
//        });
//    }
//}
