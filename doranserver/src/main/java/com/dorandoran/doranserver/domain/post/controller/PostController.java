package com.dorandoran.doranserver.domain.post.controller;

import com.dorandoran.doranserver.domain.comment.domain.Reply;
import com.dorandoran.doranserver.global.util.CommentResponseUtils;
import com.dorandoran.doranserver.global.util.MemberMatcherUtil;
import com.dorandoran.doranserver.global.util.annotation.Trace;
import com.dorandoran.doranserver.domain.comment.domain.Comment;
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
import com.dorandoran.doranserver.domain.background.domain.imgtype.ImgType;
import com.dorandoran.doranserver.domain.post.service.*;
import com.dorandoran.doranserver.domain.post.service.common.PostCommonService;
import com.dorandoran.doranserver.global.util.distance.DistanceUtil;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final PostCommonService postCommonService;
    private final MemberBlockListService memberBlockListService;
    private final FirebaseService firebaseService;
    private final CommentResponseUtils commentResponseUtils;

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

    @Trace
    @DeleteMapping("/post")
    public ResponseEntity<?> postDelete(@RequestBody PostDto.DeletePost postDeleteDto,
                                        @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        Post post = postService.findFetchMember(postDeleteDto.getPostId());

        if (post.getMemberId().getEmail().equals(userDetails.getUsername())) {
            postCommonService.deletePost(post);
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

    @Trace
    @PostMapping("/post/detail")
    ResponseEntity<?> postDetails(@RequestBody PostDto.ReadPost postRequestDetailDto,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        Post post = postService.findFetchMember(postRequestDetailDto.getPostId());
        List<String> anonymityMemberList = anonymityMemberService.findAllUserEmail(post);
        Member member = memberService.findByEmail(userEmail);
        List<Member> memberBlockListByBlockingMember = memberBlockListService.findMemberBlockListByBlockingMember(member);

        Boolean isWrittenByUser = MemberMatcherUtil.compareEmails(post.getMemberId().getEmail(), userEmail);
        Integer lIkeCnt = postLikeService.findLIkeCnt(post);
        Boolean likeResult = postLikeService.findLikeResult(userEmail, post);

        List<Comment> commentList = commentService.findCommentByPost(post);

        List<Reply> replyList = replyService.findReplyByCommentList(commentList);
        Integer commentCnt = commentService.findCommentAndReplyCntByPostId(commentList, replyList);
        Boolean checkWrite = postService.isCommentReplyAuthor(commentList, replyList, member);

        PostDto.ReadPostResponse postDetailDto = new PostDto.ReadPostResponse().toEntity(post, lIkeCnt, likeResult, commentCnt, isWrittenByUser, checkWrite);

        Boolean isLocationPresent = postRequestDetailDto.getLocation().isBlank() ? Boolean.FALSE : Boolean.TRUE;
        Integer distance;
        if (isLocationPresent && post.getLocation() != null) {
            DistanceUtil distanceUtil = new DistanceUtil();
            String[] splitLocation = postRequestDetailDto.getLocation().split(",");
            distance = distanceUtil.getDistance(splitLocation, post.getLocation());
        } else {
            distance = null;
        }
        postDetailDto.setLocation(distance);

        List<Comment> comments = commentService.findFirstCommentsFetchMember(post);
        Boolean isExistNext = commentService.checkExistAndDelete(comments);
        List<Reply> replies = replyService.findRankRepliesByComments(comments);

        HashMap<Long, Long> commentLikeCntHashMap = commentLikeService.findCommentLikeCnt(comments);
        HashMap<Long, Boolean> commentLikeResultHashMap = commentLikeService.findCommentLikeResult(userEmail, comments);

        HashMap<String, Object> commentDetailDtoList = commentResponseUtils.makeCommentAndReplyList(userEmail, post, anonymityMemberList, comments, memberBlockListByBlockingMember, commentLikeResultHashMap, commentLikeCntHashMap, replies, isExistNext);
        postDetailDto.setCommentDetailDto(commentDetailDtoList);

        List<String> postHashListDto = new ArrayList<>();
        List<PostHash> postHashList = postHashService.findPostHash(post);
        postHashService.makePostHashList(postHashList, postHashListDto);
        postDetailDto.setPostHashes(postHashListDto);

        String imgName = post.getImgName().split("[.]")[0];
        postDetailDto.setBackgroundPicUri(ipAddress + (post.getSwitchPic().equals(ImgType.DefaultBackground) ? ":8080/api/pic/default/" : ":8080/api/pic/member/") + imgName);

        return ResponseEntity.ok().body(postDetailDto);
    }
}