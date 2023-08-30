package com.dorandoran.doranserver.domain.api.common.service;

import com.dorandoran.doranserver.domain.api.background.domain.imgtype.ImgType;
import com.dorandoran.doranserver.domain.api.comment.domain.Comment;
import com.dorandoran.doranserver.domain.api.comment.domain.Reply;
import com.dorandoran.doranserver.domain.api.comment.repository.CommentRepository;
import com.dorandoran.doranserver.domain.api.comment.repository.ReplyRepository;
import com.dorandoran.doranserver.domain.api.hashtag.domain.PostHash;
import com.dorandoran.doranserver.domain.api.post.domain.AnonymityMember;
import com.dorandoran.doranserver.domain.api.post.domain.PopularPost;
import com.dorandoran.doranserver.domain.api.post.domain.Post;
import com.dorandoran.doranserver.domain.api.post.domain.PostLike;
import com.dorandoran.doranserver.domain.api.post.repository.*;
import com.dorandoran.doranserver.global.util.annotation.Trace;
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
public class CommonServiceImpl implements CommonService{
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostHashRepository postHashRepository;
    private final PopularPostRepository popularPostRepository;
    private final AnonymityMemberRepository anonymityMemberRepository;
    @Override
    public Boolean compareEmails(String objectEmail, String userEmail){
        return objectEmail.equals(userEmail) ? Boolean.TRUE : Boolean.FALSE;
    }
    @Transactional
    @Override
    public void deletePost(Post post) throws IOException {
        List<Comment> commentList = commentRepository.findCommentByPostId(post);
        List<Reply> replyList = replyRepository.findReplyByCommentList(commentList);
        List<PostLike> postLikeList = postLikeRepository.findByPostId(post);
        List<PostHash> postHashList = postHashRepository.findPostHashByPostId(post);
        List<PopularPost> popularPostList = popularPostRepository.findByPostId(post);
        List<AnonymityMember> anonymityMemberList = anonymityMemberRepository.findAllByPost(post);

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
            log.info("사용자 pic 삭제중..");
            Path path = Paths.get("home\\jw1010110\\DoranDoranPic\\UserUploadPic\\" + post.getImgName());
            Files.deleteIfExists(path);
        }

        postRepository.delete(post);
    }
}
