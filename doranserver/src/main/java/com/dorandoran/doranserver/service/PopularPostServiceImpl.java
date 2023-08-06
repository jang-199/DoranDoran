package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.MemberBlockList;
import com.dorandoran.doranserver.entity.PopularPost;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.repository.PopularPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PopularPostServiceImpl implements PopularPostService{
    private final PopularPostRepository popularPostRepository;

    @Override
    public List<PopularPost> findFirstPopularPost(List<MemberBlockList> memberBlockListByBlockingMember) {
        PageRequest of = PageRequest.of(0, 20);
        if (memberBlockListByBlockingMember.isEmpty()) {
            return popularPostRepository.findFirstPopularPost(of);
        }
        List<Member> list = memberBlockListByBlockingMember.stream().map(MemberBlockList::getBlockedMember).toList();
        return popularPostRepository.findFirstPopularPostWithoutBockLists(of, list);


    }

    @Override
    public List<PopularPost> findPopularPost(Long startPost, List<MemberBlockList> memberBlockListByBlockingMember) {
        PageRequest of = PageRequest.of(0, 20);
        if (memberBlockListByBlockingMember.isEmpty()) {
            return popularPostRepository.findPopularPost(startPost, of);
        }
        List<Member> list = memberBlockListByBlockingMember.stream().map(MemberBlockList::getBlockedMember).toList();
        return popularPostRepository.findPopularPostWithoutBlockLists(startPost, of, list);

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
