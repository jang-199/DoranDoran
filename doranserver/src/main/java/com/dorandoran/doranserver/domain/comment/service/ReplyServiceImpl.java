package com.dorandoran.doranserver.domain.comment.service;

import com.dorandoran.doranserver.domain.post.service.common.PostCommonService;
import com.dorandoran.doranserver.domain.comment.dto.ReplyDto;
import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.comment.domain.Reply;
import com.dorandoran.doranserver.domain.comment.repository.ReplyRepository;
import com.dorandoran.doranserver.global.util.MemberMatcherUtil;
import com.dorandoran.doranserver.global.util.exception.customexception.reply.NotFoundReplyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReplyServiceImpl implements ReplyService{

    private final ReplyRepository replyRepository;
    private final PostCommonService commonService;

    @Override
    public Integer findReplyCntByComment(Comment comment) {
        List<Reply> replyCntByComment = replyRepository.findReplyCntByComment(comment);
        return replyCntByComment.size();
    }

    @Override
    public List<Reply> findReplyByCommentList(List<Comment> commentByPostList) {
        return replyRepository.findReplyCntByCommentList(commentByPostList);//댓글에 달린 대댓글 개수 리스트
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
    public Reply findReplyByReplyId(Long replyId) {
        return replyRepository.findById(replyId).orElseThrow(() -> new NotFoundReplyException("해당 대댓글이 없습니다."));
    }

    @Override
    public List<Reply> findFirstReplies(Comment comment) {
        PageRequest of = PageRequest.of(0, 10);
        return replyRepository.findFirstReplies(comment, of);
    }

    @Override
    public List<Reply> findFirstRepliesFetchMember(Comment comment) {
        PageRequest of = PageRequest.of(0, 11);
        return replyRepository.findFirstRepliesFetchMember(comment, of);
    }

    @Override
    public List<Reply> findNextReplies(Long commentId, Long replyId) {
        PageRequest of = PageRequest.of(0, 11);
        return replyRepository.findNextReplies(commentId, replyId, of);
    }

    @Override
    public void checkSecretReply(ReplyDto.ReadReplyResponse replyDetailDto, Post post, Reply reply, String userEmail) {
        if (reply.checkSecretMode()
                && !MemberMatcherUtil.compareEmails(reply.getMemberId().getEmail(), userEmail)
                && !MemberMatcherUtil.compareEmails(post.getMemberId().getEmail(), userEmail)
                && !reply.getReply().equals("차단된 사용자가 작성한 내용입니다.")
                && !reply.getIsLocked().equals(Boolean.FALSE)) {
            replyDetailDto.setReply("비밀 댓글입니다.");
        }
    }

    @Override
    public void checkReplyAnonymityMember(List<String> anonymityMemberList, Reply reply, ReplyDto.ReadReplyResponse replyDetailDto) {
        if (anonymityMemberList.contains(reply.getMemberId().getEmail())) {
            int replyAnonymityIndex = anonymityMemberList.indexOf(reply.getMemberId().getEmail()) + 1;
            replyDetailDto.setReplyAnonymityNickname("익명" + replyAnonymityIndex);
        }
    }

    @Override
    public List<Member> findReplyMemberByComment(Comment comment) {
        return replyRepository.findReplyMemberByCommentId(comment);
    }

    @Override
    public List<Reply> findBlockedReply(Integer page) {
        PageRequest of = PageRequest.of(page, 20);
        return replyRepository.findReplyInAdmin(of);
    }

    @Override
    public List<Reply> findBlockedReplyDetail(Comment comment) {
        return replyRepository.findReplyInAdminDetail(comment);
    }

    @Override
    @Transactional
    public void setUnLocked(Reply reply) {
        reply.setUnLocked();
    }

    @Override
    public Reply findFetchMember(Long replyId) {
        return replyRepository.findFetchMember(replyId)
                .orElseThrow(() -> new NotFoundReplyException("해당 대댓글이 없습니다."));
    }

    @Override
    public List<Reply> findTest(List<Comment> commentList) {
        return replyRepository.findCommentListTest(commentList);
    }

    @Override
    @Transactional
    public void setCheckDelete(Reply reply) {
        reply.setCheckDelete(Boolean.TRUE);
    }

    @Override
    public Boolean checkExistAndDelete(List<Reply> replyList) {
        int size = replyList.size();

        if (checkReplySize(replyList)) {
            deleteLastIndex(replyList);
        }

        return size == 11 ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public List<Reply> findRankRepliesByComments(List<Comment> comments) {
        List<Long> commentIdList = comments.stream().map(Comment::getCommentId).toList();
        return replyRepository.findRankRepliesByComments(commentIdList);
    }

    private void deleteLastIndex(List<Reply> replyList) {
        replyList.remove(replyList.size() - 1);
    }

    private boolean checkReplySize(List<Reply> replyList) {
        return replyList.size() == 11;
    }
}
