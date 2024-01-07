package com.dorandoran.doranserver.domain.comment.service;

import com.dorandoran.doranserver.domain.comment.dto.CommentDto;
import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.comment.domain.CommentLike;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.comment.domain.Reply;
import com.dorandoran.doranserver.domain.comment.repository.CommentRepository;
import com.dorandoran.doranserver.domain.comment.repository.ReplyRepository;
import com.dorandoran.doranserver.global.util.MemberMatcherUtil;
import com.dorandoran.doranserver.global.util.exception.customexception.comment.NotFoundCommentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final ReplyService replyService;
    private final CommentLikeService commentLikeService;

    @Override
    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }

    @Override
    public Integer findCommentAndReplyCntByPostId(List<Comment> commentByPostList, List<Reply> replyCntByCommentList) {
        return commentByPostList.size() + replyCntByCommentList.size();
    }

    @Override
    public List<Integer> findCommentAndReplyCntByPostIdByList(List<Post> postList) {

        List<Comment> commentByPostList = commentRepository.findCommentByPostList(postList);

        List<Reply> replyCntByCommentList = replyRepository.findReplyCntByCommentList(commentByPostList);

        ArrayList<Integer> commentAndReplyCntList = new ArrayList<>();
        for (Post post :
                postList) {
            long commentCnt = commentByPostList.stream().filter(comment -> comment.getPostId().equals(post)).count();
            long replyCnt = replyCntByCommentList.stream().filter(reply -> reply.getCommentId().getPostId().equals(post)).count();
            commentAndReplyCntList.add((int)(commentCnt + replyCnt));
        }
        return commentAndReplyCntList;
    }

    @Override
    public List<Comment> findCommentByPost(Post post) {
        return commentRepository.findCommentByPostId(post);
    }

    @Override
    public Comment findCommentByCommentId(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new NotFoundCommentException("해당 댓글이 존재하지 않습니다."));
    }

    @Override
    public void deleteComment(Comment comment) {
        commentRepository.delete(comment);
    }

    @Override
    public void deleteAllCommentByPost(Optional<Comment> comment, List<CommentLike> commentLikeList, List<Reply> replyList) {
        if (!replyList.isEmpty()) {
            for (Reply reply : replyList) {
                replyService.deleteReply(reply);
            }
        }
        if (!commentLikeList.isEmpty()) {
            for (CommentLike commentLike : commentLikeList) {
                commentLikeService.deleteCommentLike(commentLike);
            }
        }
        deleteComment(comment.get());
    }

    @Override
    public List<Comment> findFirstComments(Post post) {
        PageRequest of = PageRequest.of(0, 10);
        return commentRepository.findFirstComments(post, of);
    }

    @Override
    public List<Comment> findFirstCommentsFetchMember(Post post) {
        PageRequest of = PageRequest.of(0, 11);
        return commentRepository.findFirstCommentsFetchMember(post, of);
    }

    @Override
    public List<Comment> findNextComments(Long postId, Long commentId) {
        PageRequest of = PageRequest.of(0, 11);
        return commentRepository.findNextComments(postId, commentId, of);
    }

    @Override
    public void checkSecretComment(CommentDto.ReadCommentResponse commentDetailDto, Post post, Comment comment, Member userMember) {
        if (comment.checkSecretMode()
                && !comment.getMemberId().equals(userMember)
                && !post.getMemberId().equals(userMember)
                && !comment.getComment().equals("차단된 사용자가 작성한 내용입니다.")
                && !comment.getIsLocked().equals(Boolean.TRUE)) {
            commentDetailDto.setComment("비밀 댓글입니다.");
        }
    }

    @Override
    public void checkCommentAnonymityMember(List<String> anonymityMemberList, Comment comment, CommentDto.ReadCommentResponse commentDetailDto) {
        if (anonymityMemberList.contains(comment.getMemberId().getEmail())) {
            int commentAnonymityIndex = anonymityMemberList.indexOf(comment.getMemberId().getEmail()) + 1;
            commentDetailDto.setCommentAnonymityNickname("익명" + commentAnonymityIndex);
        }
    }

    @Override
    public List<Comment> findBlockedComment(Integer page) {
        PageRequest of = PageRequest.of(page, 20);
        return commentRepository.findCommentInAdmin(of);
    }

    @Override
    public List<Comment> findBlockedCommentDetail(Post post) {
        return commentRepository.findCommentInAdminDetail(post);
    }

    @Override
    @Transactional
    public void setUnLocked(Comment comment) {
        comment.setUnLocked();
    }

    @Override
    public Comment findFetchMember(Long commentId) {
        return commentRepository.findFetchMember(commentId)
                .orElseThrow(() -> new NotFoundCommentException("해당 댓글이 없습니다."));
    }

    @Override
    @Transactional
    public void setCheckDelete(Comment comment) {
        comment.setCheckDelete(Boolean.TRUE);
    }

    @Override
    public Boolean checkExistAndDelete(List<Comment> commentList) {
        int size = commentList.size();

        if (checkCommentSize(commentList)) {
            deleteLastIndex(commentList);
        }

        return size == 11 ? Boolean.TRUE : Boolean.FALSE;
    }

    private void deleteLastIndex(List<Comment> commentList) {
        commentList.remove(commentList.size() - 1);
    }


    private boolean checkCommentSize(List<Comment> commentList) {
        return commentList.size() == 11;
    }
}