package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.PopularPost;
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
    public List<PopularPost> findFirstPopularPost() {
        PageRequest of = PageRequest.of(0, 20);
        return popularPostRepository.findFirstPopularPost(of);
    }

    @Override
    public List<PopularPost> findPopularPost(Long startPost) {
        return popularPostRepository.findPopularPost(startPost, PageRequest.of(0, 20));
    }

    @Override
    public void savePopularPost(PopularPost popularPost) {
        popularPostRepository.save(popularPost);
    }
}
