package com.dorandoran.doranserver.domain.api.post.service;

import com.dorandoran.doranserver.domain.api.post.domain.AnonymityMember;
import com.dorandoran.doranserver.domain.api.post.domain.Post;
import com.dorandoran.doranserver.domain.api.post.repository.AnonymityMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnonymityMemberServiceImpl implements AnonymityMemberService{
    private final AnonymityMemberRepository anonymityMemberRepository;
    @Override
    public List<String> findAllUserEmail(Post post) {
        return anonymityMemberRepository.findAllUserEmail(post);
    }

    @Override
    public void save(AnonymityMember anonymityMember) {
        anonymityMemberRepository.save(anonymityMember);
    }

    @Override
    public void deletePostByPostId(Post post) {
        anonymityMemberRepository.deleteByPostId(post);
    }
}
