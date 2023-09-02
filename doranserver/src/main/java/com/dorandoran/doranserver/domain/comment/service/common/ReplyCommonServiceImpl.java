package com.dorandoran.doranserver.domain.comment.service.common;

import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.comment.domain.Reply;
import com.dorandoran.doranserver.domain.comment.dto.CommentDto;
import com.dorandoran.doranserver.domain.comment.dto.ReplyDto;
import com.dorandoran.doranserver.domain.comment.service.CommentService;
import com.dorandoran.doranserver.domain.comment.service.ReplyService;
import com.dorandoran.doranserver.domain.post.domain.AnonymityMember;
import com.dorandoran.doranserver.domain.post.domain.PopularPost;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.post.service.AnonymityMemberService;
import com.dorandoran.doranserver.domain.post.service.PopularPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReplyCommonServiceImpl implements ReplyCommonService{
    private final ReplyService replyService;
    private final AnonymityMemberService anonymityMemberService;

    @Override
    @Transactional
    public void saveReply(ReplyDto.CreateReply replyDto, Comment comment, Reply buildReply, List<String> anonymityMembers, String userEmail, AnonymityMember anonymityMember) {
        comment.setCountReply(comment.getCountReply()+1);

        replyService.saveReply(buildReply);
        anonymityMemberService.checkAndSave(replyDto.getAnonymity(), anonymityMembers, userEmail, anonymityMember);
    }
}
