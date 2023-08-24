package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.*;
import com.dorandoran.doranserver.repository.PostHashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostHashServiceImpl implements PostHashService{
    private final PostHashRepository postHashRepository;

    @Override
    public void savePostHash(PostHash postHash) {
        postHashRepository.save(postHash);
    }
    @Override
    public void saveAllPostHash(List<PostHash> postHash) {
        postHashRepository.saveAll(postHash);
    }

    @Override
    public List<PostHash> findPostHash(Post post) {
        return postHashRepository.findPostHashByPostId(post);
    }

    @Override
    public void deletePostHash(PostHash postHash) {
        postHashRepository.delete(postHash);
    }

    @Override
    public Optional<PostHash> findTopOfPostHash(HashTag hashTag, List<MemberBlockList> members) {
        List<Member> list = members.stream().map(MemberBlockList::getBlockedMember).toList();
        if (members.isEmpty()) {
            return postHashRepository.findTopByHashTag(hashTag);
        }
        return postHashRepository.findTopByHashTagWithoutBlockLists(hashTag,list);
    }

    @Override
    public List<PostHash> inquiryFirstPostHash(HashTag hashTag,List<MemberBlockList> memberBlockListByBlockingMember) {
        PageRequest of = PageRequest.of(0, 20);
        List<Member> list = memberBlockListByBlockingMember.stream().map(MemberBlockList::getBlockedMember).toList();
        if (list.isEmpty()) {
            return postHashRepository.findFirstPostHash(hashTag, of);
        }
        return postHashRepository.findFirstPostHashWithoutBlockLists(hashTag, of, list);

    }

    @Override
    public List<PostHash> inquiryPostHash(HashTag hashTag, Long postCnt,List<MemberBlockList> memberBlockListByBlockingMember) {
        PageRequest of = PageRequest.of(0, 20);
        List<Member> list = memberBlockListByBlockingMember.stream().map(MemberBlockList::getBlockedMember).toList();
        if (list.isEmpty()) {
            return postHashRepository.findPostHash(hashTag, postCnt, of);
        }
        return postHashRepository.findPostHashWithoutBlockLists(hashTag, postCnt, of, list);
    }


}
