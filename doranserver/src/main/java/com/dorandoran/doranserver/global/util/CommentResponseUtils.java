package com.dorandoran.doranserver.global.util;

import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.comment.domain.Reply;
import com.dorandoran.doranserver.domain.comment.dto.CommentDto;
import com.dorandoran.doranserver.domain.comment.dto.ReplyDto;
import com.dorandoran.doranserver.domain.comment.service.CommentService;
import com.dorandoran.doranserver.domain.comment.service.ReplyService;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.post.domain.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentResponseUtils {
    private final ReplyService replyService;
    private final CommentService commentService;
    private final BlockMemberFilter blockMemberFilter;
    public void makeCommentList (String userEmail,
                                 Post post,
                                 List < String > anonymityMemberList,
                                 LinkedList< CommentDto.ReadCommentResponse > commentDetailDtoList,
                                 Comment comment,
                                 HashMap<String, Object> replyDetailHashMap,
                                 Boolean commentLikeResult,
                                 Long commentLikeCnt){
        Boolean isCommentWrittenByMember = Boolean.FALSE;
        if (MemberMatcherUtil.compareEmails(comment.getMemberId().getEmail(), userEmail)) {
            isCommentWrittenByMember = Boolean.TRUE;
        }

        CommentDto.ReadCommentResponse commentDetailDto = CommentDto.ReadCommentResponse.builder()
                .comment(comment)
                .content(comment.getComment())
                .commentLikeResult(commentLikeResult)
                .commentLikeCnt(commentLikeCnt)
                .isWrittenByMember(isCommentWrittenByMember)
                .replies(replyDetailHashMap)
                .build();
        commentService.checkSecretComment(commentDetailDto, post, comment, userEmail);
        commentService.checkCommentAnonymityMember(anonymityMemberList, comment, commentDetailDto);
        commentDetailDtoList.add(commentDetailDto);
    }

    public HashMap<String, Object> makeReplyList(String userEmail,
                                                          Post post,
                                                          List<String> anonymityMemberList,
                                                          List<Reply> replies) {
        LinkedList<ReplyDto.ReadReplyResponse> replyDetailDtoList = new LinkedList<>();

        Boolean isExistNextReply = replyService.checkExistAndDelete(replies);

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
        Collections.reverse(replyDetailDtoList);

        HashMap<String, Object> replyDetailHashMap = new HashMap<>();
        replyDetailHashMap.put("replyData", replyDetailDtoList);
        replyDetailHashMap.put("isExistNextReply", isExistNextReply);

        return replyDetailHashMap;
    }

    public HashMap<String,Object> makeCommentAndReplyList(String userEmail, Post post, List<String> anonymityMemberList, List<Comment> comments, List<Member> memberBlockListByBlockingMember, HashMap<Long, Boolean> commentLikeResultHashMap, HashMap<Long, Long> commentLikeCntHashMap, Boolean isExistNextComment) {
        LinkedList<CommentDto.ReadCommentResponse> commentDetailDtoList = new LinkedList<>();

        List<Comment> commentList = blockMemberFilter.commentFilter(comments, memberBlockListByBlockingMember);
        if (!comments.isEmpty()) {
            for (Comment comment : commentList) {
                List<Reply> replies = replyService.findFirstRepliesFetchMember(comment);
                List<Reply> replyList = blockMemberFilter.replyFilter(replies, memberBlockListByBlockingMember);

                HashMap<String, Object> replyDetailHashMap = makeReplyList(userEmail, post, anonymityMemberList, replyList);

                makeCommentList(userEmail, post, anonymityMemberList, commentDetailDtoList, comment, replyDetailHashMap, commentLikeResultHashMap.get(comment.getCommentId()), commentLikeCntHashMap.get(comment.getCommentId()));
            }
        }
        Collections.reverse(commentDetailDtoList);

        HashMap<String, Object> commentDetailHashMap = new HashMap<>();
        commentDetailHashMap.put("commentData",commentDetailDtoList);
        commentDetailHashMap.put("isExistNextComment", isExistNextComment);

        return commentDetailHashMap;
    }
}
