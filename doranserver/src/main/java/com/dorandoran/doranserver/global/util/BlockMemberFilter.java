package com.dorandoran.doranserver.global.util;

import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.comment.domain.Reply;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.post.domain.PostLike;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlockMemberFilter {

    public List<PostLike> postLikeFilter(List<PostLike> postLikeList, List<Member> memberBlockLists) {
        return postLikeList.stream()
                .peek(postLike -> {
                    boolean isBlocked = memberBlockLists.stream()
                            .anyMatch(blockList -> blockList.equals(postLike.getPostId().getMemberId()));

                    if (isBlocked) {
                        postLike.getPostId().setContent("차단된 사용자가 작성한 내용입니다.");
                    }
                })
                .collect(Collectors.toList());
    }

    public List<Comment> commentFilter(List<Comment> commentList, List<Member> memberBlockLists){
        return commentList.stream()
                .peek(comment -> {
                    boolean isBlocked = memberBlockLists.stream()
                            .anyMatch(blockList -> blockList.equals(comment.getMemberId()));

                    if (isBlocked){
                        comment.setComment("차단된 사용자가 작성한 내용입니다.");
                    }
                })
                .collect(Collectors.toList());
    }

    public List<Reply> replyFilter(List<Reply> replyList, List<Member> memberBlockLists){
        return replyList.stream()
                .peek(reply -> {
                    boolean isBlocked = memberBlockLists.stream()
                            .anyMatch(blockList -> blockList.equals(reply.getMemberId()));

                    if (isBlocked){
                        reply.setReply("차단된 사용자가 작성한 내용입니다.");
                    }
                })
                .collect(Collectors.toList());
    }
}