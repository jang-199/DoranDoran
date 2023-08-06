package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.MemberBlockList;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public List<Post> findFirstPost(List<MemberBlockList> memberBlockLists) {
        PageRequest of = PageRequest.of(0, 20);
        if (memberBlockLists.isEmpty()) {
            return postRepository.findFirstPost(of);
        }
        List<Member> list = memberBlockLists.stream().map(MemberBlockList::getBlockedMember).toList();
        log.info(list.toString());
        return postRepository.findFirstPostWithoutBlockLists(of,list);
    }

    @Override
    public List<Post> findPost(Long startPost,List<MemberBlockList> memberBlockLists) {
        PageRequest of = PageRequest.of(0, 20);
        if (memberBlockLists.isEmpty()) {
            return postRepository.findPost(startPost, of);
        }
        List<Member> list = memberBlockLists.stream().map(MemberBlockList::getBlockedMember).toList();
        return postRepository.findPostWithoutBlockLists(startPost, of,list);
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
    public List<Post> findFirstClosePost(Double Slatitude,Double Llatitude, Double Slongitude, Double Llongitude) {
        PageRequest of = PageRequest.of(0, 20);
        return postRepository.findFirstClosePost(Slatitude,Llatitude, Slongitude, Llongitude,of);
    }

    @Override
    public List<Post> findClosePost(Double Slatitude, Double Llatitude, Double Slongitude, Double Llongitude,Long startPost) {
        PageRequest of = PageRequest.of(0, 20);
        return postRepository.findClosePost(startPost, Slatitude, Llatitude, Slongitude, Llongitude, of);
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


}
