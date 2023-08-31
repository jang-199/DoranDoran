package com.dorandoran.doranserver.domain.post.controller;

import com.dorandoran.doranserver.global.util.BlockMemberFilter;
import com.dorandoran.doranserver.global.util.MemberMatcherUtil;
import com.dorandoran.doranserver.global.util.annotation.Trace;
import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.comment.domain.Reply;
import com.dorandoran.doranserver.domain.comment.service.CommentLikeService;
import com.dorandoran.doranserver.domain.comment.service.CommentService;
import com.dorandoran.doranserver.domain.comment.service.ReplyService;
import com.dorandoran.doranserver.domain.hashtag.domain.HashTag;
import com.dorandoran.doranserver.domain.hashtag.domain.PostHash;
import com.dorandoran.doranserver.domain.hashtag.service.HashTagService;
import com.dorandoran.doranserver.domain.member.domain.LockMember;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.member.service.LockMemberService;
import com.dorandoran.doranserver.domain.member.service.MemberBlockListService;
import com.dorandoran.doranserver.domain.member.service.MemberService;
import com.dorandoran.doranserver.domain.notification.service.FirebaseService;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.post.domain.PostLike;
import com.dorandoran.doranserver.domain.comment.dto.CommentDto;
import com.dorandoran.doranserver.domain.post.dto.PostDto;
import com.dorandoran.doranserver.domain.comment.dto.ReplyDto;
import com.dorandoran.doranserver.domain.background.domain.imgtype.ImgType;
import com.dorandoran.doranserver.domain.post.service.*;
import com.dorandoran.doranserver.domain.post.service.common.PostCommonService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.*;

@Timed
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
@Slf4j
public class PostController {
    @Value("${userUpload.Store.path}")
    String userUploadPicServerPath;
    @Value("${background.Store.path}")
    String backgroundPicServerPath;
    @Value("${doran.ip.address}")
    String ipAddress;

    private final MemberService memberService;
    private final PostLikeService postLikeService;
    private final HashTagService hashTagService;
    private final PostService postService;
    private final PostHashService postHashService;
    private final CommentService commentService;
    private final CommentLikeService commentLikeService;
    private final ReplyService replyService;
    private final AnonymityMemberService anonymityMemberService;
    private final LockMemberService lockMemberService;
    private final PostCommonService commonService;
    private final MemberBlockListService memberBlockListService;
    private final BlockMemberFilter blockMemberFilter;
    private final FirebaseService firebaseService;

    @Trace
    @PostMapping("/post")
    public ResponseEntity<?> savePost(PostDto.CreatePost postDto,
                                      @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        Member member = memberService.findByEmail(userDetails.getUsername());
        Optional<LockMember> lockMember = lockMemberService.findLockMember(member);
        if (lockMember.isPresent()){
            if (lockMemberService.checkCurrentLocked(lockMember.get())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("정지된 회원은 글을 작성할 수 없습니다.");
            }else {
                lockMemberService.deleteLockMember(lockMember.get());
            }
        }

        Post post = Post.builder()
                .content(postDto.getContent())
                .forMe(postDto.getForMe())
                .memberId(member)
                .anonymity(postDto.getAnonymity())
                .font(postDto.getFont())
                .fontColor(postDto.getFontColor())
                .fontSize(postDto.getFontSize())
                .fontBold(postDto.getFontBold())
                .isLocked(Boolean.FALSE)
                .build();

        postService.saveMemberPost(post, postDto);

        if (postDto.getHashTagName() != null) {
            hashTagService.saveHashtagList(postDto.getHashTagName());
        }

        Post hashTagPost = postService.findSinglePost(post.getPostId());
        List<HashTag> byHashTagName = hashTagService.findHashtagList(postDto.getHashTagName());
        savePostHash(hashTagPost, byHashTagName);

        return ResponseEntity.created(URI.create("")).build();
    }

    private void savePostHash(Post hashTagPost, List<HashTag> hashTagList) {
        ArrayList<PostHash> postHashList = new ArrayList<>();
        for (HashTag hashTag : hashTagList) {
            PostHash postHash = PostHash.builder()
                    .postId(hashTagPost)
                    .hashTagId(hashTag)
                    .build();
            postHashList.add(postHash);
        }
            postHashService.saveAllPostHash(postHashList);
        }

    /**
     * 댓글 삭제 -> 글 공감 삭제 -> 글 해시 태그 삭제 -> 인기있는 글 삭제 -> 익명 테이블 삭제 -> 사용자 이미지 삭제 -> 글 삭제
     * 삭제하려는 사용자가 본인 글이 아닐 경우 bad request
     * @param postDeleteDto Long postId, String userEmail
     * @return Ok
     */
    @Trace
    @DeleteMapping("/post")
    public ResponseEntity<?> postDelete(@RequestBody PostDto.DeletePost postDeleteDto,
                                        @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        Post post = postService.findFetchMember(postDeleteDto.getPostId());

        if (post.getMemberId().getEmail().equals(userDetails.getUsername())) {
            commonService.deletePost(post);
        }
        else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("글 작성자만 글을 삭제할 수 있습니다.");
        }

        return ResponseEntity.noContent().build();
    }

    @Trace
    @PostMapping("/post/like")
    ResponseEntity<?> postLike(@RequestBody PostDto.LikePost postLikeDto,
                               @AuthenticationPrincipal UserDetails userDetails) {
        Post post = postService.findSinglePost(postLikeDto.getPostId());
        Member member = memberService.findByEmail(userDetails.getUsername());
        Optional<PostLike> postLike = postLikeService.findLikeOne(userDetails.getUsername(), post);

        if (post.getMemberId().equals(member)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("자신의 글에 추천은 불가능합니다.");
        }

        postLikeService.checkPostLike(postLikeDto, userDetails, post, member, postLike);

        if (postLike.isEmpty() && post.getMemberId().checkNotification()) {
            firebaseService.notifyPostLike(post.getMemberId(), post);
        }

        return ResponseEntity.noContent().build();
    }

    //글 내용, 작성자, 공감수, 위치, 댓글수, 작성 시간, 댓글
    @Trace
    @PostMapping("/post/detail")
    ResponseEntity<?> postDetails(@RequestBody PostDto.ReadPost postRequestDetailDto,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        //todo 여기부터 해야됨
        String userEmail = userDetails.getUsername();
        Post post = postService.findFetchMember(postRequestDetailDto.getPostId());
        List<String> anonymityMemberList = anonymityMemberService.findAllUserEmail(post);
        Member member = memberService.findByEmail(userEmail);
        List<Member> memberBlockListByBlockingMember = memberBlockListService.findMemberBlockListByBlockingMember(member);

        Boolean isWrittenByUser = post.getMemberId().getEmail().equals(userEmail) ? Boolean.TRUE : Boolean.FALSE;
        //리턴할 postDetail builder
        PostDto.ReadPostResponse postDetailDto = PostDto.ReadPostResponse.builder()
                .content(post.getContent())
                .postLikeCnt(postLikeService.findLIkeCnt(post))
                .postLikeResult(postLikeService.findLikeResult(userEmail, post))
                .commentCnt(commentService.findCommentAndReplyCntByPostId(post))
                .postAnonymity(post.getAnonymity())
                .postNickname(post.getMemberId().getNickname())
                .isWrittenByMember(isWrittenByUser)
                .font(post.getFont())
                .fontColor(post.getFontColor())
                .fontSize(post.getFontSize())
                .fontBold(post.getFontBold())
                .build();

        //글의 위치 데이터와 현재 내 위치 거리 계산
        Boolean isLocationPresent = postRequestDetailDto.getLocation().isBlank() ? Boolean.FALSE : Boolean.TRUE;
        Integer distance;
        if (isLocationPresent && post.getLocation() != null) {
            String[] splitLocation = postRequestDetailDto.getLocation().split(",");
            GeometryFactory geometryFactory = new GeometryFactory();
            String latitude = splitLocation[0];
            String longitude = splitLocation[1];
            Coordinate coordinate = new Coordinate(Double.parseDouble(latitude), Double.parseDouble(longitude));
            Point point = geometryFactory.createPoint(coordinate);

            distance = (int) Math.round(point.distance(post.getLocation()) * 100);
        } else {
            distance = null;
        }
        postDetailDto.setLocation(distance);

        boolean checkWrite = Boolean.FALSE;
        //댓글 builder
        List<Comment> comments = commentService.findFirstCommentsFetchMember(post);
        List<Comment> commentList = blockMemberFilter.commentFilter(comments, memberBlockListByBlockingMember);

        //todo 대댓글 한번에 가져와서 처리하는 걸로 바꿔야함 쿼리 너무 많이 나감..
        List<CommentDto.ReadCommentResponse> commentDetailDtoList = new ArrayList<>();
        if (comments.size() != 0) {
            for (Comment comment : commentList) {
                //대댓글 10개 저장 로직
                List<Reply> replies = replyService.findFirstRepliesFetchMember(comment);
                List<Reply> replyList = blockMemberFilter.replyFilter(replies, memberBlockListByBlockingMember);
                List<ReplyDto.ReadReplyResponse> replyDetailDtoList = new ArrayList<>();
                log.info("대댓글 로직 실행");
                for (Reply reply : replyList) {
                    Boolean isReplyWrittenByUser = Boolean.FALSE;
                    if (MemberMatcherUtil.compareEmails(reply.getMemberId().getEmail(), userEmail)) {
                        checkWrite = Boolean.TRUE;
                        isReplyWrittenByUser = Boolean.TRUE;
                    }
                    ReplyDto.ReadReplyResponse replyDetailDto = ReplyDto.ReadReplyResponse.builder()
                            .reply(reply)
                            .content(reply.getReply())
                            .isWrittenByMember(isReplyWrittenByUser)
                            .build();
                    replyService.checkSecretReply(replyDetailDto, post, reply, userEmail);
                    replyService.checkReplyAnonymityMember(anonymityMemberList, reply, replyDetailDto);
                    replyDetailDtoList.add(replyDetailDto);
                }
                Collections.reverse(replyDetailDtoList);

                Integer commentLikeCnt = commentLikeService.findCommentLikeCnt(comment);
                Boolean commentLikeResult = commentLikeService.findCommentLikeResult(userEmail, comment);
                Boolean isCommentWrittenByMember = Boolean.FALSE;
                if (MemberMatcherUtil.compareEmails(comment.getMemberId().getEmail(), userEmail)) {
                    checkWrite = Boolean.TRUE;
                    isCommentWrittenByMember = Boolean.TRUE;
                }

                CommentDto.ReadCommentResponse commentDetailDto = CommentDto.ReadCommentResponse.builder()
                        .comment(comment)
                        .content(comment.getComment())
                        .commentLikeResult(commentLikeResult)
                        .commentLikeCnt(commentLikeCnt)
                        .isWrittenByMember(isCommentWrittenByMember)
                        .replies(replyDetailDtoList)
                        .build();
                commentService.checkSecretComment(commentDetailDto, post, comment, userEmail);
                commentService.checkCommentAnonymityMember(anonymityMemberList, comment, commentDetailDto);
                commentDetailDtoList.add(commentDetailDto);
            }
        }
        Collections.reverse(commentDetailDtoList);
        postDetailDto.setCommentDetailDto(commentDetailDtoList);
        postDetailDto.setCheckWrite(checkWrite);

        //해시태그 builder
        List<String> postHashListDto = new ArrayList<>();
        List<PostHash> postHashList = postHashService.findPostHash(post);
        //todo fetch join으로 hashtag 같이 가져오게끔 수정
        if (!postHashList.isEmpty()){
            for (PostHash postHash : postHashList) {
                String hashTagName = postHash.getHashTagId().getHashTagName();
                postHashListDto.add(hashTagName);
            }
        }
        postDetailDto.setPostHashes(postHashListDto);

        //배경사진 builder
        String[] split = post.getImgName().split("[.]");
        if (post.getSwitchPic().equals(ImgType.DefaultBackground)) {
            postDetailDto.setBackgroundPicUri(ipAddress + ":8080/api/pic/default/" + split[0]);
        } else {
            postDetailDto.setBackgroundPicUri(ipAddress + ":8080/api/pic/member/" + split[0]);
        }

        return ResponseEntity.ok().body(postDetailDto);
    }


}