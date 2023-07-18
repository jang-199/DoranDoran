package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.dto.postDetail.ReplyDetailDto;
import com.dorandoran.doranserver.entity.Comment;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.Reply;
import com.dorandoran.doranserver.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReplyServiceImpl implements ReplyService{

    private final ReplyRepository replyRepository;
    private final CommonService commonService;

    @Override
    public Integer findReplyCntByComment(Comment comment) {
        List<Reply> replyCntByComment = replyRepository.findReplyCntByComment(comment);
        return replyCntByComment.size();
    }

    @Override
    public List<Reply> findReplyList(Comment comment) {
        return replyRepository.findReplyCntByComment(comment);
    }

    @Override
    public void deleteReply(Reply reply) {
        replyRepository.delete(reply);
    }

    @Override
    public void saveReply(Reply reply) {
        replyRepository.save(reply);
    }

    @Override
    public Optional<Reply> findReplyByReplyId(Long replyId) {
        return replyRepository.findById(replyId);
    }

    @Override
    public List<Reply> findFirstReplies(Comment comment) {
        PageRequest of = PageRequest.of(0, 10);
        return replyRepository.findFirstReplies(comment, of);
    }

    @Override
    public List<Reply> findFirstRepliesFetchMember(Comment comment) {
        PageRequest of = PageRequest.of(0, 10);
        return replyRepository.findFirstRepliesFetchMember(comment, of);
    }

    @Override
    public List<Reply> findNextReplies(Long commentId, Long replyId) {
        PageRequest of = PageRequest.of(0, 10);
        return replyRepository.findNextReplies(commentId, replyId, of);
    }

    @Override
    public void checkSecretReply(ReplyDetailDto replyDetailDto, Post post, Reply reply, String userEmail) {
        if (reply.checkSecretMode()
                && !commonService.compareEmails(reply.getMemberId().getEmail(), userEmail)
                && !commonService.compareEmails(post.getMemberId().getEmail(), userEmail)) {
            replyDetailDto.setReply("비밀 댓글입니다.");
        }
    }

    @Override
    public void checkReplyAnonymityMember(List<String> anonymityMemberList, Reply reply, ReplyDetailDto replyDetailDto) {
        if (anonymityMemberList.contains(reply.getMemberId().getEmail())) {
            int replyAnonymityIndex = anonymityMemberList.indexOf(reply.getMemberId().getEmail()) + 1;
            log.info("{}의 index값은 {}이다", reply.getMemberId().getEmail(), replyAnonymityIndex);
            replyDetailDto.setReplyAnonymityNickname("익명" + replyAnonymityIndex);
        }
    }


}
