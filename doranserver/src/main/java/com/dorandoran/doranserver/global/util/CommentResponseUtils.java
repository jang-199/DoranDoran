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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentResponseUtils {
    private final ReplyService replyService;
    private final CommentService commentService;
    private final BlockMemberFilter blockMemberFilter;
    public void makeCommentList (String userEmail,
                                 Post post,
                                 List < String > anonymityMemberList,
                                 List < CommentDto.ReadCommentResponse > commentDetailDtoList,
                                 Comment comment,
                                 List < ReplyDto.ReadReplyResponse > replyDetailDtoList,
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
                .replies(replyDetailDtoList)
                .build();
        commentService.checkSecretComment(commentDetailDto, post, comment, userEmail);
        commentService.checkCommentAnonymityMember(anonymityMemberList, comment, commentDetailDto);
        commentDetailDtoList.add(commentDetailDto);
    }

    public List<ReplyDto.ReadReplyResponse> makeReplyList(String userEmail,
                                                          Post post,
                                                          List<String> anonymityMemberList,
                                                          List<Reply> replies) {
        List<ReplyDto.ReadReplyResponse> replyDetailDtoList = new ArrayList<>();
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
    public List<CommentDto.ReadCommentResponse> makeCommentAndReplyList(String userEmail, Post post, List<String> anonymityMemberList, List<Comment> comments, List<Member> memberBlockListByBlockingMember, HashMap<Long, Boolean> commentLikeResultHashMap, HashMap<Long, Long> commentLikeCntHashMap) {
        List<CommentDto.ReadCommentResponse> commentDetailDtoList = new ArrayList<>();

        List<Comment> commentList = blockMemberFilter.commentFilter(comments, memberBlockListByBlockingMember);
        if (!comments.isEmpty()) {
            for (Comment comment : commentList) {
                //대댓글 10개 저장 로직
                List<Reply> replies = replyService.findFirstRepliesFetchMember(comment);
                List<Reply> replyList = blockMemberFilter.replyFilter(replies, memberBlockListByBlockingMember);

                List<ReplyDto.ReadReplyResponse> replyDetailDtoList = makeReplyList(userEmail, post, anonymityMemberList, replyList);
                Collections.reverse(replyDetailDtoList);

                //댓글 10개 저장 로직
                makeCommentList(userEmail, post, anonymityMemberList, commentDetailDtoList, comment, replyDetailDtoList, commentLikeResultHashMap.get(comment.getCommentId()), commentLikeCntHashMap.get(comment.getCommentId()));
                Collections.reverse(commentDetailDtoList);
            }
        }

        return commentDetailDtoList;
    }
}
