package com.dorandoran.doranserver.domain.post.service;

import com.dorandoran.doranserver.domain.post.domain.AnonymityMember;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.post.repository.AnonymityMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
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
    public void checkAndSave(Boolean anonymityCheck, List<String> anonymityMembers, String userEmails, AnonymityMember anonymityMember) {
        if (anonymityCheck.equals(Boolean.TRUE)) {
            if (!anonymityMembers.contains(userEmails)) {
                save(anonymityMember);
            }
        }
    }

    @Override
    public void deletePostByPostId(Post post) {
        anonymityMemberRepository.deleteByPostId(post);
    }
}
