package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.PopularPost;
import com.dorandoran.doranserver.entity.Post;

import java.util.List;

public interface PopularPostService {
    public List<PopularPost> findFirstPopularPost();

    public List<PopularPost> findPopularPost(Long startPost);
    public void savePopularPost(PopularPost popularPost);
    public List<PopularPost> findPopularPostByPost(Post post);
    public void deletePopularPost(PopularPost popularPost);
}
