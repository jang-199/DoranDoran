package com.dorandoran.doranserver.domain.post.service;

import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.comment.domain.Reply;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.post.dto.PostDto;
import org.locationtech.jts.geom.Point;

import java.io.IOException;
import java.util.List;

public interface PostService {
    void savePost(Post post);
    void saveMemberPost(Post post, PostDto.CreatePost postDto) throws IOException;
    List<Post> findFirstPost(Member member, List<Member> memberBlockLists);
    List<Post> findPost(Long startPost, Member member, List<Member> memberBlockLists);
    Post findSinglePost(Long postId);
    void deletePost(Post post);
    List<Post> findFirstClosePost(Point point, double distance, List<Member> memberBlockListByBlockingMember);
    List<Post> findClosePost(Point point,double distance,Long startPost,List<Member> memberBlockListByBlockingMember);
    List<Post> findFirstMyPost(Member member);
    List<Post> findMyPost(Member member, Long startPost);
    List<Post> findBlockedPost(Integer page);
    Post findFetchMember(Long postId);
    void setUnLocked(Post post);
    void setPostPic(PostDto.CreatePost postDto, Post post) throws IOException;
    Boolean isCommentReplyAuthor(List<Comment> commentList, List<Reply> replyList, Member member);
}
