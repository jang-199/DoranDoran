package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.*;

import java.util.List;
import java.util.Optional;

public interface PostHashService {
    public void savePostHash(PostHash postHash);
    List<PostHash> findPostHash(Post post);
    public void deletePostHash(PostHash postHash);

    Optional<PostHash> findTopOfPostHash(HashTag hashTag, List<Member> members);

    List<Post> inquiryFirstPostHash(HashTag hashTag, Member member, List<Member> memberBlockListByBlockingMember);
    List<Post> inquiryPostHash(HashTag hashTag, Long postCnt,Member member, List<Member> memberBlockListByBlockingMember);

}
