package com.dorandoran.doranserver.domain.post.service.common;

import com.dorandoran.doranserver.domain.background.domain.imgtype.ImgType;
import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.comment.domain.Reply;
import com.dorandoran.doranserver.domain.comment.repository.CommentRepository;
import com.dorandoran.doranserver.domain.comment.repository.ReplyRepository;
import com.dorandoran.doranserver.domain.hashtag.domain.PostHash;
import com.dorandoran.doranserver.domain.post.domain.AnonymityMember;
import com.dorandoran.doranserver.domain.post.domain.PopularPost;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.post.domain.PostLike;
import com.dorandoran.doranserver.domain.post.repository.*;
import com.dorandoran.doranserver.domain.report.domain.ReportComment;
import com.dorandoran.doranserver.domain.report.domain.ReportPost;
import com.dorandoran.doranserver.domain.report.domain.ReportReply;
import com.dorandoran.doranserver.domain.report.repository.ReportCommentRepository;
import com.dorandoran.doranserver.domain.report.repository.ReportPostRepository;
import com.dorandoran.doranserver.domain.report.repository.ReportReplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostCommonServiceImpl implements PostCommonService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostHashRepository postHashRepository;
    private final PopularPostRepository popularPostRepository;
    private final AnonymityMemberRepository anonymityMemberRepository;
    private final ReportPostRepository reportPostRepository;
    private final ReportCommentRepository reportCommentRepository;
    private final ReportReplyRepository reportReplyRepository;

    @Transactional
    @Override
    public void deletePost(Post post) throws IOException {
        List<Comment> commentList = commentRepository.findCommentByPostId(post);
        List<Reply> replyList = replyRepository.findReplyByCommentList(commentList);
        List<PostLike> postLikeList = postLikeRepository.findByPostId(post);
        List<PostHash> postHashList = postHashRepository.findPostHashByPostId(post);
        List<PopularPost> popularPostList = popularPostRepository.findByPostId(post);
        List<AnonymityMember> anonymityMemberList = anonymityMemberRepository.findAllByPost(post);
        List<ReportPost> reportPostList = reportPostRepository.findAllByPostId(post);
        List<ReportComment> reportCommentList = reportCommentRepository.findAllByCommentId(commentList);
        List<ReportReply> reportReplyList = reportReplyRepository.findAllByReplyId(replyList);

        if(!reportReplyList.isEmpty()){
            reportReplyRepository.deleteAllInBatch(reportReplyList);
        }

        if(!reportCommentList.isEmpty()){
            reportCommentRepository.deleteAllInBatch(reportCommentList);
        }

        if(!reportPostList.isEmpty()){
            reportPostRepository.deleteAllInBatch(reportPostList);
        }

        if (!replyList.isEmpty()) {
            replyRepository.deleteAllInBatch(replyList);
        }

        if (!commentList.isEmpty()){
            commentRepository.deleteAllInBatch(commentList);
        }

        if (!postLikeList.isEmpty()){
            postLikeRepository.deleteAllInBatch(postLikeList);
        }

        if (!postHashList.isEmpty()){
            postHashRepository.deleteAllInBatch(postHashList);
        }

        if (!popularPostList.isEmpty()){
            popularPostRepository.deleteAllInBatch(popularPostList);
        }

        if (!anonymityMemberList.isEmpty()){
            anonymityMemberRepository.deleteAllInBatch(anonymityMemberList);
        }

        if (post.getSwitchPic().equals(ImgType.UserUpload)){
            Path path = Paths.get("home\\jw1010110\\DoranDoranPic\\UserUploadPic\\" + post.getImgName());
            Files.deleteIfExists(path);
        }

        postRepository.delete(post);
    }
}
