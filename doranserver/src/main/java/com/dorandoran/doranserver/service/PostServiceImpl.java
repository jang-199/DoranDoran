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

    @Override
    public Optional<Post> findSinglePost(Long postId) {
        return postRepository.findById(postId);
    }

    @Override
    public void deletePost(Post post) {
        postRepository.delete(post);
    }

    @Override
    public List<Post> findFirstClosePost(Double Slatitude,Double Llatitude, Double Slongitude, Double Llongitude) {
        PageRequest of = PageRequest.of(0, 20);
        return postRepository.findFirstClosePost(Slatitude,Llatitude, Slongitude, Llongitude,of);
    }

    @Override
    public List<Post> findClosePost(Double Slatitude, Double Llatitude, Double Slongitude, Double Llongitude,Long startPost) {
        PageRequest of = PageRequest.of(0, 20);
        return postRepository.findClosePost(startPost, Slatitude, Llatitude, Slongitude, Llongitude, of);
    }


}
