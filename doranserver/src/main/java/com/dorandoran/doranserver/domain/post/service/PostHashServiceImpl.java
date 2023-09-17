package com.dorandoran.doranserver.domain.post.service;

import com.dorandoran.doranserver.domain.hashtag.domain.HashTag;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.hashtag.domain.PostHash;
import com.dorandoran.doranserver.domain.post.repository.PostHashRepository;
import com.dorandoran.doranserver.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

import java.util.LinkedHashMap;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostHashServiceImpl implements PostHashService {
    private final PostHashRepository postHashRepository;
    private final PostRepository postRepository;

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
        List<Long> hashTagIdList = hashTag.stream().map(HashTag::getHashTagId).toList();
        Long memberId = member.getMemberId();
        List<Long> memberBlockListIdList = memberBlockListByBlockingMember.stream().map(Member::getMemberId).toList();
        if (memberBlockListByBlockingMember.isEmpty()) {
            List<Long> topByHashTag = postHashRepository.findTopByHashTag(hashTagIdList, memberId);
            return postRepository.findAllById(topByHashTag);
        }
        List<Long> topByHashTagWithoutBlockLists = postHashRepository.findTopByHashTagWithoutBlockLists(hashTagIdList, memberId, memberBlockListIdList);
        return postRepository.findAllById(topByHashTagWithoutBlockLists);
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

    @Override
    public LinkedMultiValueMap<Post, String> makeStringPostHashMap(List<Post> postList, List<HashTag> hashTagList) {
        LinkedMultiValueMap<Post, String> stringPostLinkedHashMap = new LinkedMultiValueMap<>();

        List<PostHash> postHashList = postHashRepository.findAllByPostId(postList);
        for (PostHash postHash : postHashList) {
            log.info("postHashList : {}", postHash);
        }
        for (PostHash postHash : postHashList) {
            String hashTagName = postHash.getHashTagId().getHashTagName();
            Post post = postHash.getPostId();
            stringPostLinkedHashMap.add(post, hashTagName);
            log.info("stringPostLinkedHashMap.size : {}",stringPostLinkedHashMap.size());
        }
        return stringPostLinkedHashMap;
    }
}
