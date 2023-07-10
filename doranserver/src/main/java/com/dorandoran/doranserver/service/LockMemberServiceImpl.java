package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.LockMember;
import com.dorandoran.doranserver.repository.LockMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LockMemberServiceImpl implements LockMemberService{
    private final LockMemberRepository lockMemberRepository;
    @Override
    public void saveLockMember(LockMember lockMember) {
        Optional<LockMember> findLockMember = lockMemberRepository.findById(lockMember.getLockMemberId());
//        if (findLockMember.isPresent()){
//            new LockMember()
//        }else {
//
//        }
        lockMemberRepository.save(lockMember);
    }
}
