package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.PopularPost;

import java.util.List;

public interface PopularPostService {
    public List<PopularPost> findFirstPopularPost();

    public List<PopularPost> findPopularPost(Long startPost);
    public void savePopularPost(PopularPost popularPost);
}
