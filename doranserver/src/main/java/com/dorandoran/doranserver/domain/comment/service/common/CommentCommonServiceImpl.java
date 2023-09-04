package com.dorandoran.doranserver.domain.comment.service.common;

import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.comment.dto.CommentDto;
import com.dorandoran.doranserver.domain.comment.service.CommentService;
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
public class CommentCommonServiceImpl implements CommentCommonService{
    private final CommentService commentService;
    private final PopularPostService popularPostService;
    private final AnonymityMemberService anonymityMemberService;
    @Override
    @Transactional
    public void saveComment(CommentDto.CreateComment createCommentDto, Comment comment, List<Comment> commentByPost, List<PopularPost> popularPostByPost, Post post, String userEmail, Long nextIndex, List<String> anonymityMembers) {
        commentService.saveComment(comment);
        if (commentByPost.size() >= 10 && popularPostByPost.isEmpty()) {
            PopularPost build = PopularPost.builder().postId(post).build();
            popularPostService.savePopularPost(build);
        }

        AnonymityMember anonymityMember = new AnonymityMember().toEntity(userEmail, post, nextIndex);
        anonymityMemberService.checkAndSave(createCommentDto.getAnonymity(), anonymityMembers, userEmail, anonymityMember);
    }
}
