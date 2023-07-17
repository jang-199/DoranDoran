package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BlockMemberFilter {
    public List<Post> postFilter(List<Post> postList, List<MemberBlockList> memberBlockLists) {
        return postList.stream()
                .peek(post -> {
                    boolean isBlocked = memberBlockLists.stream()
                            .anyMatch(blockList -> blockList.getBlockedMember().equals(post.getMemberId()));

                    if (isBlocked) {
                        post.setContent("차단된 사용자가 작성한 내용입니다.");
                    }
                })
                .collect(Collectors.toList());
    }

    public List<PostLike> postLikeFilter(List<PostLike> postLikeList, List<MemberBlockList> memberBlockLists) {
        return postLikeList.stream()
                .peek(postLike -> {
                    boolean isBlocked = memberBlockLists.stream()
                            .anyMatch(blockList -> blockList.getBlockedMember().equals(postLike.getPostId().getMemberId()));

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
}
