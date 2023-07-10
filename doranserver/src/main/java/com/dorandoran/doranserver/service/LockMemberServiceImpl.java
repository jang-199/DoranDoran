package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.LockMember;
import com.dorandoran.doranserver.repository.LockMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LockMemberServiceImpl implements LockMemberService{
    private final LockMemberRepository lockMemberRepository;
    @Override
    public void saveLockMember(LockMember lockMember) {
        lockMemberRepository.save(lockMember);
    }
}
