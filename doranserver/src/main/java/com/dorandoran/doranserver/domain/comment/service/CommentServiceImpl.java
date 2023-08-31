package com.dorandoran.doranserver.domain.comment.service;

import com.dorandoran.doranserver.domain.comment.dto.CommentDto;
import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.comment.domain.CommentLike;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.comment.domain.Reply;
import com.dorandoran.doranserver.domain.comment.repository.CommentRepository;
import com.dorandoran.doranserver.domain.comment.repository.ReplyRepository;
import com.dorandoran.doranserver.global.util.CommonService;
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
    private final CommonService commonService;

    @Override
    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }

    @Override
    public Integer findCommentAndReplyCntByPostId(Post post) {
        int cnt = 0;
        List<Comment> commentCntByPostId = commentRepository.findCommentCntByPostId(post);
        cnt += commentCntByPostId.size();
        for (Comment comment : commentCntByPostId) {
            List<Reply> replyCntByComment = replyRepository.findReplyCntByComment(comment);
            cnt += replyCntByComment.size();
        }
        return cnt;
    }

    @Override
    public List<Integer> findCommentAndReplyCntByPostIdByList(List<Post> postList) {

        List<Comment> commentByPostList = commentRepository.findCommentByPostList(postList);//글에 해당하는 댓글 리스트

        List<Reply> replyCntByCommentList = replyRepository.findReplyCntByCommentList(commentByPostList);//댓글에 달린 대댓글 개수 리스트

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
    public Optional<Comment> findCommentByCommentId(Long commentId) {
        return commentRepository.findById(commentId);
    }

    @Override
    public void deleteComment(Comment comment) {
        commentRepository.delete(comment);
    }

    @Override
    public void deleteAllCommentByPost(Optional<Comment> comment, List<CommentLike> commentLikeList, List<Reply> replyList) {
        if (replyList.size() != 0) {
            for (Reply reply : replyList) {
                replyService.deleteReply(reply);
                log.info("{}님의 대댓글 삭제", reply.getMemberId().getNickname());
            }
        }
        //댓글 공감 삭제
        if (commentLikeList.size() != 0) {
            for (CommentLike commentLike : commentLikeList) {
                commentLikeService.deleteCommentLike(commentLike);
                log.info("{}님의 댓글 공감 삭제", commentLike.getMemberId().getNickname());
            }
        }
        //댓글 삭제
        deleteComment(comment.get());
    }

    @Override
    public List<Comment> findFirstComments(Post post) {
        PageRequest of = PageRequest.of(0, 10);
        return commentRepository.findFirstComments(post, of);
    }

    @Override
    public List<Comment> findFirstCommentsFetchMember(Post post) {
        PageRequest of = PageRequest.of(0, 10);
        return commentRepository.findFirstCommentsFetchMember(post, of);
    }

    @Override
    public List<Comment> findNextComments(Long postId, Long commentId) {
        PageRequest of = PageRequest.of(0, 10);
        return commentRepository.findNextComments(postId, commentId, of);
    }

    @Override
    public void checkSecretComment(CommentDto.ReadCommentResponse commentDetailDto, Post post, Comment comment, String userEmail) {
        if (comment.checkSecretMode()
                && !commonService.compareEmails(comment.getMemberId().getEmail(), userEmail)
                && !commonService.compareEmails(post.getMemberId().getEmail(), userEmail)
                && !comment.getComment().equals("차단된 사용자가 작성한 내용입니다.")) {
            commentDetailDto.setComment("비밀 댓글입니다.");
        }
    }

    @Override
    public void checkCommentAnonymityMember(List<String> anonymityMemberList, Comment comment, CommentDto.ReadCommentResponse commentDetailDto) {
        if (anonymityMemberList.contains(comment.getMemberId().getEmail())) {
            int commentAnonymityIndex = anonymityMemberList.indexOf(comment.getMemberId().getEmail()) + 1;
            log.info("{}의 index값은 {}이다", comment.getMemberId().getEmail(), commentAnonymityIndex);
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
                .orElseThrow(() -> new NoSuchElementException("해당 댓글이 없습니다."));
    }
}