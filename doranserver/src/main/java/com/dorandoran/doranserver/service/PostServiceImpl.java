package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    @Override
    public void savePost(Post post) {
        postRepository.save(post);
    }

    @Override
    public List<Post> findFirstPost() {
        PageRequest of = PageRequest.of(0, 20);
        return postRepository.findFirstPost(of);
    }

    @Override
    public List<Post> findPost(Long startPost) {
//        return postRepository.findPost(startPost-19L, startPost);
        return postRepository.findPost(startPost, PageRequest.of(0, 20));
    }
}
