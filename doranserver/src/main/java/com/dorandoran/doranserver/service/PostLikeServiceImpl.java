package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.PostLike;
import com.dorandoran.doranserver.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


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
    public Boolean findLikeResult(String email) {
        Optional<PostLike> byMemberId_email = postLikeRepository.findByMemberId_Email(email);
        if (byMemberId_email.isPresent()) {
            return true;
        }else {
            return false;
        }
    }

    public Optional<PostLike> findByMemberId(String email) {
        return postLikeRepository.findByMemberId_Email(email);
    }

    @Override
    public void deletePostLike(String email) {
        postLikeRepository.deleteByMemberId_Email(email);
    }

}
