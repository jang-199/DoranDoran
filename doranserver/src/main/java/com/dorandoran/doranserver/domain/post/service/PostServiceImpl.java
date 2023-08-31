package com.dorandoran.doranserver.domain.post.service;

import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    @Override
    public void savePost(Post post) {
        postRepository.save(post);
    }

    @Override
    public List<Post> findFirstPost(Member member, List<Member> memberBlockLists) {
        PageRequest of = PageRequest.of(0, 20);
        if (memberBlockLists.isEmpty()) {
            return postRepository.findFirstPost(member, of);
        }
        return postRepository.findFirstPostWithoutBlockLists(member, memberBlockLists,of);
    }

    @Override
    public List<Post> findPost(Long startPost, Member member, List<Member> memberBlockLists) {
        PageRequest of = PageRequest.of(0, 20);
        if (memberBlockLists.isEmpty()) {
            return postRepository.findPost(startPost,member, of);
        }
        return postRepository.findPostWithoutBlockLists(startPost,  member , memberBlockLists, of);
    }

    @Override
    public Post findSinglePost(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당 글이 존재하지 않습니다."));
    }

    @Override
    public void deletePost(Post post) {
        postRepository.delete(post);
    }

    @Override
    public List<Post> findFirstClosePost(Point point,double distance,List<Member> memberBlockListByBlockingMember) {
        PageRequest of = PageRequest.of(0, 20);
        if (memberBlockListByBlockingMember.isEmpty()) {
            return postRepository.findFirstClosePostV2(point, distance, of);
        }
        return postRepository.findFirstClosePostWithoutBlockListsV2(point, distance, memberBlockListByBlockingMember, of);
    }

    @Override
    public List<Post> findClosePost(Point point,double distance, Long startPost, List<Member> memberBlockListByBlockingMember) {
        PageRequest of = PageRequest.of(0, 20);
//        List<Member> list = memberBlockListByBlockingMember.stream().map(MemberBlockList::getBlockedMember).toList();
        if (memberBlockListByBlockingMember.isEmpty()) {
            return postRepository.findClosePostV2(startPost, point, distance, of);
        }
        return postRepository.findClosePostWithoutBlockListsV2(startPost, point, distance,memberBlockListByBlockingMember, of);
    }

    @Override
    public List<Post> findFirstMyPost(Member member) {
        PageRequest of = PageRequest.of(0, 20);
        return postRepository.findMyFirstPost(member, of);
    }

    @Override
    public List<Post> findMyPost(Member member, Long startPost) {
        PageRequest of = PageRequest.of(0, 20);
        return postRepository.findMyPost(member, startPost, of);
    }

    @Override
    public List<Post> findBlockedPost(Integer page) {
        PageRequest of = PageRequest.of(page, 20);
        return postRepository.findBlockedPost(of);
    }

    @Override
    public Post findFetchMember(Long postId){
        return postRepository.findFetchMember(postId)
                .orElseThrow(() -> new NoSuchElementException("해당 글이 없습니다."));
    }

    @Override
    @Transactional
    public void setUnLocked(Post post) {
        post.setUnLocked();
    }
}
