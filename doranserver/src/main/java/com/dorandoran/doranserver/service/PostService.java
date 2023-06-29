package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {
    public void savePost(Post post);

    public List<Post> findFirstPost();

    public List<Post> findPost(Long startPost);
    public Optional<Post> findSinglePost(Long postId);
    public void deletePost(Post post);

    public List<Post> findFirstClosePost(Double Slatitude,Double Llatitude, Double Slongitude, Double Llongitude);

    public List<Post> findClosePost(Double Slatitude,Double Llatitude, Double Slongitude, Double Llongitude,Long startPost);

    List<Post> findFirstMyPost(Member member);
    List<Post> findMyPost(Member member, Long startPost);
}
