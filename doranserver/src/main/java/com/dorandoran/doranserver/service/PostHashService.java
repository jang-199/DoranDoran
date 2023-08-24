package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.*;

import java.util.List;
import java.util.Optional;

public interface PostHashService {
    void savePostHash(PostHash postHash);
    void saveAllPostHash(List<PostHash> postHash);
    List<PostHash> findPostHash(Post post);
    void deletePostHash(PostHash postHash);

    Optional<PostHash> findTopOfPostHash(HashTag hashTag, List<MemberBlockList> members);

    List<PostHash> inquiryFirstPostHash(HashTag hashTag,List<MemberBlockList> memberBlockListByBlockingMember);
    List<PostHash> inquiryPostHash(HashTag hashTag, Long postCnt,List<MemberBlockList> memberBlockListByBlockingMember);

}
