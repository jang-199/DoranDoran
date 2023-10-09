package com.dorandoran.doranserver.domain.post.service;

import com.dorandoran.doranserver.domain.post.dto.PostDto;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.post.domain.PostLike;
import com.dorandoran.doranserver.domain.post.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {
    private final PostLikeRepository postLikeRepository;

    @Override
    public void savePostLike(PostLike postLike) {
        postLikeRepository.save(postLike);
    }

    @Transactional(readOnly = true)
    @Override
    public Integer findLIkeCnt(Post post) {
        List<PostLike> byPostId = postLikeRepository.findUnDeletedByPost(post);

        return byPostId.size();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Integer> findLIkeCntByPostList(List<Post> postList) {
        List<PostLike> postLikeByPostList = postLikeRepository.findPostLikeByPostList(postList);
        ArrayList<Integer> likeCntList = new ArrayList<>();
        for (Post post :
                postList) {
            long count = postLikeByPostList.stream().filter(postLike -> postLike.getPostId().equals(post)).count();
            likeCntList.add((int) count);
        }
        return likeCntList;
    }

    @Transactional(readOnly = true)
    @Override
    public Boolean findLikeResult(String email, Post postId) {
        Optional<PostLike> likeResult = postLikeRepository.findLikeResult(email, postId);
        if (likeResult.isPresent() && likeResult.get().getCheckDelete().equals(Boolean.FALSE)) {
            return Boolean.TRUE;
        }else {
            return Boolean.FALSE;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Boolean> findLikeResultByPostList(String email, List<Post> postList) {
        List<PostLike> likeResultByPostList = postLikeRepository.findLikeResultByPostList(email, postList);

        List<Boolean> likeResultList = new ArrayList<>();

        int position=0;
        for (Post post : postList) {

            if (likeResultByPostList.isEmpty()) {
                likeResultList.add(Boolean.FALSE);
                continue;
            }

            if (post.equals(likeResultByPostList.get(position).getPostId())) {
                likeResultList.add(likeResultByPostList.get(position).getCheckDelete().equals(Boolean.TRUE) ? Boolean.FALSE : Boolean.TRUE);
                if (likeResultByPostList.size()-1 != position) {
                    position++;
                }
            } else {
                likeResultList.add(Boolean.FALSE);
            }
        }
        return likeResultList;
    }

    @Transactional(readOnly = true)
    @Override
    public List<PostLike> findByMemberId(String email) {
        return postLikeRepository.findByMemberId_Email(email);
    }

    @Override
    public void deletePostLike(PostLike postLike) {
        postLikeRepository.delete(postLike);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PostLike> findByPost(Post post) {
        return postLikeRepository.findByPostId(post);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Post> findFirstMyLikedPosts(String email,List<Member> memberBlockListByBlockingMember) {
        PageRequest of = PageRequest.of(0, 20);
        if (memberBlockListByBlockingMember.isEmpty()) {
            return postLikeRepository.findFirstMyLikedPosts(email, of);
        }
        return postLikeRepository.findFirstMyLikedPostsWithoutBlockLists(email, memberBlockListByBlockingMember, of);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Post> findMyLikedPosts(String email, Long position,List<Member> memberBlockListByBlockingMember) {
        PageRequest of = PageRequest.of(0, 20);
        if (memberBlockListByBlockingMember.isEmpty()) {
            return postLikeRepository.findMyLikedPosts(email,position, of);
        }
        return postLikeRepository.findMyLikedPostsWithoutBlockLists(email, position, memberBlockListByBlockingMember, of);
    }

    @Override
    public Optional<PostLike> findLikeOne(String email, Post post) {
        return postLikeRepository.findLikeResult(email, post);
    }

    @Override
    @Transactional
    public void checkPostLike(Post post, Member member, Optional<PostLike> postLike) {
        if (postLike.isPresent()){
            if (postLike.get().getCheckDelete().equals(Boolean.TRUE)){
                postLike.get().restore();
            }else {
                postLike.get().delete();
            }
        }else {
            PostLike postLikeBuild = PostLike.builder()
                    .postId(post)
                    .memberId(member)
                    .checkDelete(Boolean.FALSE)
                    .build();
            savePostLike(postLikeBuild);
        }
    }

}
