package com.dorandoran.doranserver.domain.post.service;

import com.dorandoran.doranserver.domain.hashtag.domain.HashTag;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.hashtag.domain.PostHash;
import com.dorandoran.doranserver.domain.post.repository.PostHashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostHashServiceImpl implements PostHashService {
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
    public List<Post> findTopOfPostHash(List<HashTag> hashTag, Member member, List<Member> memberBlockListByBlockingMember) {
//        List<Member> list = members.stream().map(MemberBlockList::getBlockedMember).toList();
        if (memberBlockListByBlockingMember.isEmpty()) {
            return postHashRepository.findTopByHashTag(hashTag, member);
        }
        return postHashRepository.findTopByHashTagWithoutBlockLists(hashTag, member, memberBlockListByBlockingMember);
    }

    @Override
    public List<Post> inquiryFirstPostHash(HashTag hashTag, Member member, List<Member> memberBlockListByBlockingMember) {
        PageRequest of = PageRequest.of(0, 20);
        if (memberBlockListByBlockingMember.isEmpty()) {
            return postHashRepository.findFirstPostHash(hashTag, member, of);
        }
        return postHashRepository.findFirstPostHashWithoutBlockLists(hashTag, member, memberBlockListByBlockingMember, of);

    }

    @Override
    public List<Post> inquiryPostHash(HashTag hashTag, Long postCnt, Member member, List<Member> memberBlockListByBlockingMember) {
        PageRequest of = PageRequest.of(0, 20);
        if (memberBlockListByBlockingMember.isEmpty()) {
            return postHashRepository.findPostHash(hashTag, member, postCnt, of);
        }
        return postHashRepository.findPostHashWithoutBlockLists(hashTag, member, postCnt, memberBlockListByBlockingMember, of);
    }

    @Override
    public void makePostHashList(List<PostHash> postHashList, List<String> postHashListDto) {
        if (!postHashList.isEmpty()) {
            for (PostHash postHash : postHashList) {
                String hashTagName = postHash.getHashTagId().getHashTagName();
                postHashListDto.add(hashTagName);
            }
        }
    }
}
