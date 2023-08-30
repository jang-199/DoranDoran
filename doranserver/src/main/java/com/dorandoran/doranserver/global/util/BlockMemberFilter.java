package com.dorandoran.doranserver.global.util;

import com.dorandoran.doranserver.domain.api.comment.domain.Comment;
import com.dorandoran.doranserver.domain.api.comment.domain.Reply;
import com.dorandoran.doranserver.domain.api.hashtag.domain.PostHash;
import com.dorandoran.doranserver.domain.api.member.domain.Member;
import com.dorandoran.doranserver.domain.api.member.domain.MemberBlockList;
import com.dorandoran.doranserver.domain.api.post.domain.PopularPost;
import com.dorandoran.doranserver.domain.api.post.domain.PostLike;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BlockMemberFilter {

//    public List<Post> postFilter(PostService postService, List<Post> postList, List<MemberBlockList> memberBlockLists) {
//        List<Post> postFilter = removePostFilter(postList, memberBlockLists);
//        return addPostFilterEmptySpace(postService, postFilter.size() - 1L, postFilter, memberBlockLists);
//    }
//    public List<Post> removePostFilter(List<Post> postList, List<MemberBlockList> memberBlockLists) {
//        return postList.stream()
//                .peek(post -> {
//                    boolean isBlocked = memberBlockLists.stream()
//                            .anyMatch(blockList -> blockList.getBlockedMember().equals(post.getMemberId()));
//
//                    if (isBlocked) {
//                        postList.remove(post);
//                    }
//                })
//                .toList();
//    }

//    public List<Post> addPostFilterEmptySpace(PostService postService, Long position, List<Post> postList, List<MemberBlockList> memberBlockLists) {
//        List<Post> servicePost = postService.findPost(position,memberBlockLists);
//        List<Post> postFilter = removePostFilter(servicePost, memberBlockLists);
//        postList.addAll(postFilter);
//        if (servicePost.size()<20){
//            return addPostFilterEmptySpace(postService, postList.size() - 1L, postList, memberBlockLists);
//        } else if (postList.size() < 20) {
//            addPostFilterEmptySpace(postService, postList.size() - 1L, postList, memberBlockLists);
//        }else {
//            int repeatCnt = postList.size() - 20;
//            int size = postList.size();
//            for (int j = 0; j < repeatCnt; j++) {
//                postList.remove(size-1);
//                size--;
//            }
//        }
//        return postList;
//    }

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

    public List<PostHash> postHashFilter(List<PostHash> postList, List<MemberBlockList> memberBlockLists) {
        return postList.stream()
                .peek(postHash -> {
                    boolean isBlocked = memberBlockLists.stream()
                            .anyMatch(blockList -> blockList.getBlockedMember().equals(postHash.getPostId().getMemberId()));

                    if (isBlocked) {
                        postHash.getPostId().setContent("차단된 사용자가 작성한 내용입니다.");
                    }
                })
                .collect(Collectors.toList());
    }

    public List<PopularPost> popularPostFilter(List<PopularPost> popularPostList, List<MemberBlockList> memberBlockLists) {
        return popularPostList.stream()
                .peek(popularPost -> {
                    boolean isBlocked = memberBlockLists.stream()
                            .anyMatch(blockList -> blockList.getBlockedMember().equals(popularPost.getPostId().getMemberId()));

                    if (isBlocked) {
                        popularPost.getPostId().setContent("차단된 사용자가 작성한 내용입니다.");
                    }
                })
                .collect(Collectors.toList());
    }

    public List<Optional<PostHash>> optionalPostHashFilter(List<Optional<PostHash>> optionalPostHashList, List<MemberBlockList> memberBlockLists) {
        return optionalPostHashList.stream()
                .peek(optionalPostHash -> {
                    boolean isBlocked = memberBlockLists.stream()
                            .anyMatch(blockList -> blockList.getBlockedMember().equals(optionalPostHash.orElseThrow().getPostId().getMemberId()));

                    if (isBlocked) {
                        optionalPostHash.orElseThrow().getPostId().setContent("차단된 사용자가 작성한 내용입니다.");
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
