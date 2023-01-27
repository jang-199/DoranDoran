package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.PostLike;
import com.dorandoran.doranserver.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {
    private final PostLikeRepository postLikeRepository;

    @Override
    public void savePostLike(PostLike postLike) {
        postLikeRepository.save(postLike);
    }

    @Override
    public Integer findLIkeCnt(Post post) {
        List<PostLike> byPostId = postLikeRepository.findByPostId(post);
        return byPostId.size();
    }

    @Override
    public Boolean findLikeResult(Member memberId, Post postId) {
        Optional<PostLike> likeResult = postLikeRepository.findLikeResult(memberId, postId);
        if (likeResult.isPresent()) {
            return true;
        }else {
            return false;
        }
    }

    @Override
    public Optional<List<PostLike>> findByMemberId(String email) {
        return postLikeRepository.findByMemberId_Email(email);
    }

    @Override
    public void deletePostLike(PostLike postLike) {
        postLikeRepository.delete(postLike);
    }

}
