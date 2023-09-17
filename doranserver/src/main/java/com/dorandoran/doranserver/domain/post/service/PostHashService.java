package com.dorandoran.doranserver.domain.post.service;

import com.dorandoran.doranserver.domain.hashtag.domain.HashTag;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.hashtag.domain.PostHash;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;

public interface PostHashService {
    public void savePostHash(PostHash postHash);
    void saveAllPostHash(List<PostHash> postHash);
    List<PostHash> findPostHash(Post post);
    public void deletePostHash(PostHash postHash);

    List<Post> findTopOfPostHash(List<HashTag> hashTagList, Member member, List<Member> memberBlockListByBlockingMember);

    List<Post> inquiryFirstPostHash(HashTag hashTag, Member member, List<Member> memberBlockListByBlockingMember);
    List<Post> inquiryPostHash(HashTag hashTag, Long postCnt,Member member, List<Member> memberBlockListByBlockingMember);
    void makePostHashList(List<PostHash> postHashList, List<String> postHashListDto);
    LinkedMultiValueMap<Long, String> makeStringPostHashMap(List<Post> postList, List<HashTag> hashTagList);
}
