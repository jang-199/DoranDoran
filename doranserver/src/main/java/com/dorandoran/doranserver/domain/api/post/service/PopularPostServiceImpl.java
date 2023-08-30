package com.dorandoran.doranserver.domain.api.post.service;

import com.dorandoran.doranserver.domain.api.member.domain.Member;
import com.dorandoran.doranserver.domain.api.post.domain.PopularPost;
import com.dorandoran.doranserver.domain.api.post.domain.Post;
import com.dorandoran.doranserver.domain.api.post.repository.PopularPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PopularPostServiceImpl implements PopularPostService{
    private final PopularPostRepository popularPostRepository;

    @Override
    public List<Post> findFirstPopularPost(Member member, List<Member> memberBlockListByBlockingMember) {
        PageRequest of = PageRequest.of(0, 20);
        if (memberBlockListByBlockingMember.isEmpty()) {
            return popularPostRepository.findFirstPopularPost(member, of);
        }
        return popularPostRepository.findFirstPopularPostWithoutBockLists(member, memberBlockListByBlockingMember, of);


    }

    @Override
    public List<Post> findPopularPost(Long startPost,Member member, List<Member> memberBlockListByBlockingMember) {
        PageRequest of = PageRequest.of(0, 20);
        if (memberBlockListByBlockingMember.isEmpty()) {
            return popularPostRepository.findPopularPost(startPost, member, of);
        }
//        List<Member> list = memberBlockListByBlockingMember.stream().map(MemberBlockList::getBlockedMember).toList();
        return popularPostRepository.findPopularPostWithoutBlockLists(startPost, memberBlockListByBlockingMember, of);

    }

    @Override
    public void savePopularPost(PopularPost popularPost) {
        popularPostRepository.save(popularPost);
    }

    @Override
    public List<PopularPost> findPopularPostByPost(Post post) {
        return popularPostRepository.findByPostId(post);
    }

    @Override
    public void deletePopularPost(PopularPost popularPost) {
        popularPostRepository.delete(popularPost);
    }
}
