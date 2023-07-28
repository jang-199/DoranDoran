package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.AnonymityMember;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.repository.AnonymityMemberRepository;
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
