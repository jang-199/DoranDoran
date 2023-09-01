package com.dorandoran.doranserver.domain.comment.controller;

import com.dorandoran.doranserver.global.util.CommentResponseUtils;
import com.dorandoran.doranserver.global.util.MemberMatcherUtil;
import com.dorandoran.doranserver.global.util.annotation.Trace;
import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.comment.domain.CommentLike;
import com.dorandoran.doranserver.domain.comment.domain.Reply;
import com.dorandoran.doranserver.domain.comment.service.CommentLikeService;
import com.dorandoran.doranserver.domain.comment.service.CommentService;
import com.dorandoran.doranserver.domain.comment.service.ReplyService;
import com.dorandoran.doranserver.domain.member.domain.LockMember;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.member.service.LockMemberService;
import com.dorandoran.doranserver.domain.member.service.MemberBlockListService;
import com.dorandoran.doranserver.domain.member.service.MemberService;
import com.dorandoran.doranserver.domain.notification.service.FirebaseService;
import com.dorandoran.doranserver.domain.post.domain.AnonymityMember;
import com.dorandoran.doranserver.domain.post.domain.PopularPost;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.comment.dto.CommentDto;
import com.dorandoran.doranserver.domain.comment.dto.ReplyDto;
import com.dorandoran.doranserver.domain.post.service.AnonymityMemberService;
import com.dorandoran.doranserver.domain.post.service.PopularPostService;
import com.dorandoran.doranserver.domain.post.service.PostService;
import com.dorandoran.doranserver.global.util.BlockMemberFilter;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Timed
@Controller
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {
    private final MemberService memberService;
    private final PostService postService;
    private final CommentService commentService;
    private final CommentLikeService commentLikeService;
    private final ReplyService replyService;
    private final PopularPostService popularPostService;
    private final AnonymityMemberService anonymityMemberService;
    private final LockMemberService lockMemberService;
    private final MemberBlockListService memberBlockListService;
    private final BlockMemberFilter blockMemberFilter;
    private final FirebaseService firebaseService;
    private final CommentResponseUtils commentResponseUtils;
    @Value("${doran.ip.address}")
    String ipAddress;

    @Trace
    @GetMapping("/comment")
    public ResponseEntity<?> inquiryComment(@RequestParam("postId") Long postId,
                                            @RequestParam("commentId") Long commentId,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        Post post = postService.findSinglePost(postId);
        Member member = memberService.findByEmail(userEmail);
        List<Member> memberBlockListByBlockingMember = memberBlockListService.findMemberBlockListByBlockingMember(member);
        List<String> anonymityMemberList = anonymityMemberService.findAllUserEmail(post);

        List<Comment> comments = commentService.findNextComments(postId, commentId);

        HashMap<Comment, Long> commentLikeCntHashMap = commentLikeService.findCommentLikeCnt(comments);
        HashMap<Comment, Boolean> commentLikeResultHashMap = commentLikeService.findCommentLikeResult(userEmail, comments);
        List<CommentDto.ReadCommentResponse> commentDetailDtoList = commentResponseUtils.makeCommentAndReplyList(userEmail, post, anonymityMemberList, comments, memberBlockListByBlockingMember, commentLikeResultHashMap, commentLikeCntHashMap);
        return ResponseEntity.ok().body(commentDetailDtoList);
    }


    @Trace
    @PostMapping("/comment")
    ResponseEntity<?> saveComment(@RequestBody CommentDto.CreateComment createCommentDto,
                              @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail);

        Optional<LockMember> lockMember = lockMemberService.findLockMember(member);
        if (lockMember.isPresent()){
            if (lockMemberService.checkCurrentLocked(lockMember.get())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("정지된 회원은 댓글을 작성할 수 없습니다.");
            }else {
                lockMemberService.deleteLockMember(lockMember.get());
            }
        }

        Post post = postService.findSinglePost(createCommentDto.getPostId());
        List<String> anonymityMembers = anonymityMemberService.findAllUserEmail(post);
        Long nextIndex = anonymityMembers.size() + 1L;

        Comment comment = new Comment().toEntity(createCommentDto, post, member);
        commentService.saveComment(comment);

        if (!post.getMemberId().equals(member) && post.getMemberId().checkNotification()){
            firebaseService.notifyComment(post.getMemberId(), comment);
        }

        Post singlePost = postService.findSinglePost(createCommentDto.getPostId());
        List<Comment> commentByPost = commentService.findCommentByPost(singlePost);
        if (commentByPost.size() >= 10 && popularPostService.findPopularPostByPost(singlePost).isEmpty()) {
            PopularPost build = PopularPost.builder().postId(singlePost).build();
            popularPostService.savePopularPost(build);
        }

        AnonymityMember anonymityMember = new AnonymityMember().toEntity(userEmail, post, nextIndex);
        anonymityMemberService.checkAndSave(createCommentDto.getAnonymity(), anonymityMembers, userEmail, anonymityMember);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Trace
    @DeleteMapping("/comment")
    @Transactional
    public ResponseEntity<?> deleteComment(@RequestBody CommentDto.DeleteComment commentDeleteDto,
                                           @AuthenticationPrincipal UserDetails userDetails){
        Optional<Comment> comment = commentService.findCommentByCommentId(commentDeleteDto.getCommentId());
        if (comment.get().getMemberId().getEmail().equals(userDetails.getUsername())) {
            //댓글 checkDelete 삭제로 표시
            comment.get().setCheckDelete(Boolean.TRUE);
            log.info("댓글 숨김 처리");
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else {
            log.info("댓글을 작성한 사용자가 아닙니다.");
            return new ResponseEntity<>("댓글 작성자가 아닙니다.",HttpStatus.BAD_REQUEST);
        }
    }

    @Trace
    @PostMapping("/comment/like")
    ResponseEntity<?> commentLike(@RequestBody CommentDto.LikeComment commentLikeDto,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        Comment comment = commentService.findCommentByCommentId(commentLikeDto.getCommentId())
                .orElseThrow(() -> new NoSuchElementException("해당 댓글이 존재하지 않습니다."));
        Member member = memberService.findByEmail(userDetails.getUsername());
        //todo 쿼리 확인 한번 해보기 checkdelete가 필요없는지..
        Optional<CommentLike> commentLike = commentLikeService.findCommentLikeOne(userDetails.getUsername(), comment);

        if (comment.getMemberId().equals(member)){
            return new ResponseEntity<>("자신의 댓글에 추천은 불가능합니다.",HttpStatus.BAD_REQUEST);
        }

        commentLikeService.checkCommentLike(commentLikeDto, userDetails, comment, member, commentLike);

        if (commentLike.isEmpty() && comment.getMemberId().checkNotification()) {
            firebaseService.notifyCommentLike(comment.getMemberId(), comment);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Trace
    @GetMapping("/reply")
    public ResponseEntity<?> inquiryReply(@RequestParam("postId") Long postId,
                                          @RequestParam("commentId") Long commentId,
                                          @RequestParam("replyId") Long replyId,
                                          @AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        Post post = postService.findSinglePost(postId);
        List<String> anonymityMemberList = anonymityMemberService.findAllUserEmail(post);
        Member member = memberService.findByEmail(userEmail);
        List<Member> memberBlockListByBlockingMember = memberBlockListService.findMemberBlockListByBlockingMember(member);
        List<Reply> replies = replyService.findNextReplies(commentId, replyId);
        List<Reply> replyList = blockMemberFilter.replyFilter(replies, memberBlockListByBlockingMember);

        List<ReplyDto.ReadReplyResponse> replyDetailDtoList = commentResponseUtils.makeReplyList(userEmail, post, anonymityMemberList, replyList);

        return ResponseEntity.ok().body(replyDetailDtoList);
    }

    @Trace
    @Transactional
    @PostMapping("/reply")
    public ResponseEntity<?> reply(@RequestBody ReplyDto.CreateReply replyDto,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        Member member = memberService.findByEmail(userDetails.getUsername());
        Comment comment = commentService.findCommentByCommentId(replyDto.getCommentId())
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
        Optional<LockMember> lockMember = lockMemberService.findLockMember(member);
        if (lockMember.isPresent()){
            if (lockMemberService.checkCurrentLocked(lockMember.get())){
                return new ResponseEntity<>("정지된 회원은 댓글을 작성할 수 없습니다.", HttpStatus.BAD_REQUEST);
            }else {
                lockMemberService.deleteLockMember(lockMember.get());
            }
        }

        comment.setCountReply(comment.getCountReply()+1);
        List<String> anonymityMembers = anonymityMemberService.findAllUserEmail(comment.getPostId());
        Long nextIndex = anonymityMembers.size() + 1L;

        Reply buildReply = Reply.builder()
                .reply(replyDto.getReply())
                .anonymity(replyDto.getAnonymity())
                .commentId(comment)
                .memberId(member)
                .checkDelete(Boolean.FALSE)
                .secretMode(replyDto.getSecretMode())
                .isLocked(Boolean.FALSE)
                .build();


        replyService.saveReply(buildReply);

        List<Member> replyMemberList = replyService.findReplyMemberByComment(comment);
        replyMemberList.add(comment.getMemberId());
        List<Member> fcmMemberList = checkMyComment(replyMemberList, member);
        if (!fcmMemberList.isEmpty()) {
            firebaseService.notifyReply(fcmMemberList, buildReply);
        }

//        extracted(replyDto.getAnonymity(), anonymityMembers, userDetails, AnonymityMember.builder()
//                .userEmail(member.getEmail())
//                .postId(comment.getPostId())
//                .anonymityIndex(nextIndex)
//                .build());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Trace
    @DeleteMapping("/reply")
    @Transactional
    public ResponseEntity<?> replyDelete(@RequestBody ReplyDto.DeleteReply replyDeleteDto,
                                         @AuthenticationPrincipal UserDetails userDetails){
        Reply reply = replyService.findReplyByReplyId(replyDeleteDto.getReplyId()).orElseThrow(() -> new NoSuchElementException("에러 발생"));
        if (reply.getMemberId().getEmail().equals(userDetails.getUsername())){
            //대댓글 checkDelete 삭제로 표시
            reply.setCheckDelete(Boolean.TRUE);
            log.info("대댓글 숨김 처리");
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else {
            log.info("대댓글을 작성한 사용자가 아닙니다.");
            return new ResponseEntity<>("대댓글 작성자가 아닙니다.",HttpStatus.BAD_REQUEST);
        }
    }

    private List<CommentDto.ReadCommentResponse> makeCommentAndReplyList(String userEmail, Post post, List<String> anonymityMemberList, List<Comment> comments, List<Member> memberBlockListByBlockingMember, HashMap<Comment, Boolean> commentLikeResultHashMap, HashMap<Comment, Long> commentLikeCntHashMap) {
        List<CommentDto.ReadCommentResponse> commentDetailDtoList = new ArrayList<>();
        //todo reply 가져오는 로직 수정
        if (comments.size() != 0) {
            for (Comment comment : comments) {
                //대댓글 10개 저장 로직
                List<Reply> replies = replyService.findFirstRepliesFetchMember(comment);
                List<Reply> replyList = blockMemberFilter.replyFilter(replies, memberBlockListByBlockingMember);

                List<ReplyDto.ReadReplyResponse> replyDetailDtoList = makeReplyList(userEmail, post, anonymityMemberList, replyList);
                Collections.reverse(replyDetailDtoList);

                //댓글 10개 저장 로직
                makeCommentList(userEmail, post, anonymityMemberList, commentDetailDtoList, comment, replyDetailDtoList, commentLikeResultHashMap.get(comment), commentLikeCntHashMap.get(comment));
                Collections.reverse(commentDetailDtoList);
            }
        }

        return commentDetailDtoList;
    }

    private void makeCommentList(String userEmail, Post post, List<String> anonymityMemberList, List<CommentDto.ReadCommentResponse> commentDetailDtoList, Comment comment, List<ReplyDto.ReadReplyResponse> replyDetailDtoList, Boolean aBoolean, Long aLong) {
//        Integer commentLikeCnt = commentLikeService.findCommentLikeCnt(comment);
        //todo hashmap으로 만든 commentlike, commentResult로 바꿔야함
//        Boolean commentLikeResult = commentLikeService.findCommentLikeResult(userEmail, comment);
        Boolean isCommentWrittenByMember = Boolean.FALSE;
        if (MemberMatcherUtil.compareEmails(comment.getMemberId().getEmail(), userEmail)) {
            isCommentWrittenByMember = Boolean.TRUE;
        }

        CommentDto.ReadCommentResponse commentDetailDto = CommentDto.ReadCommentResponse.builder()
                .comment(comment)
                .content(comment.getComment())
//                .commentLikeResult(commentLikeResult)
//                .commentLikeCnt(commentLikeCnt)
                .isWrittenByMember(isCommentWrittenByMember)
                .replies(replyDetailDtoList)
                .build();
        commentService.checkSecretComment(commentDetailDto, post, comment, userEmail);
        commentService.checkCommentAnonymityMember(anonymityMemberList, comment, commentDetailDto);
        commentDetailDtoList.add(commentDetailDto);
    }

    private List<ReplyDto.ReadReplyResponse> makeReplyList(String userEmail, Post post, List<String> anonymityMemberList, List<Reply> replies) {
        List<ReplyDto.ReadReplyResponse> replyDetailDtoList = new ArrayList<>();
        log.info("대댓글 로직 실행");
        for (Reply reply : replies) {
            Boolean isReplyWrittenByUser = Boolean.FALSE;
            if (MemberMatcherUtil.compareEmails(reply.getMemberId().getEmail(), userEmail)) {
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
        return replyDetailDtoList;
    }

    private static List<Member> checkMyComment(List<Member> memberList, Member writeMember) {
        return memberList.stream()
                .distinct()
                .filter((member) ->
                        !member.equals(writeMember)
                        && member.checkNotification())
                .collect(Collectors.toList());
    }
}