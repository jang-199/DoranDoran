package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public List<Post> findFirstClosePost(Double Slatitude,Double Llatitude, Double Slongitude, Double Llongitude,List<Member> memberBlockListByBlockingMember) {
        PageRequest of = PageRequest.of(0, 20);
//        List<Member> list = memberBlockListByBlockingMember.stream().map(MemberBlockList::getBlockedMember).toList();
        if (memberBlockListByBlockingMember.isEmpty()) {
            return postRepository.findFirstClosePost(Slatitude,Llatitude, Slongitude, Llongitude, of);
        }
        return postRepository.findFirstClosePostWithoutBlockLists(Slatitude, Llatitude, Slongitude, Llongitude, memberBlockListByBlockingMember, of);
    }

    @Override
    public List<Post> findClosePost(Double Slatitude, Double Llatitude, Double Slongitude, Double Llongitude,Long startPost,List<Member> memberBlockListByBlockingMember) {
        PageRequest of = PageRequest.of(0, 20);
//        List<Member> list = memberBlockListByBlockingMember.stream().map(MemberBlockList::getBlockedMember).toList();
        if (memberBlockListByBlockingMember.isEmpty()) {
            return postRepository.findClosePost(startPost, Slatitude, Llatitude, Slongitude, Llongitude, of);
        }
        return postRepository.findClosePostWithoutBlockLists(startPost, Slatitude, Llatitude, Slongitude, Llongitude, memberBlockListByBlockingMember, of);
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
