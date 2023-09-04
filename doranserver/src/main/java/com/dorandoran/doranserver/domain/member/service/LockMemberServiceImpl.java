package com.dorandoran.doranserver.domain.member.service;

import com.dorandoran.doranserver.domain.member.domain.LockMember;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.member.repository.LockMemberRepository;
import com.dorandoran.doranserver.global.util.annotation.Trace;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LockMemberServiceImpl implements LockMemberService{
    private final LockMemberRepository lockMemberRepository;

    @Override
    @Trace
    @Transactional
    public void saveLockMember(LockMember lockMember) {
        Optional<LockMember> findLockMember = lockMemberRepository.findLockMemberByMemberId(lockMember.getMemberId());
        Duration duration = lockMember.RetrieveLockDuration(lockMember);
        if (findLockMember.isPresent()){
            findLockMember.get().updateLockMember(duration, lockMember.getLockType());
            log.info("{}님의 정지 기한이 {}로 수정되었습니다.", lockMember.getMemberId().getEmail(), lockMember.getLockType());
        }else {
            lockMemberRepository.save(lockMember);
        }
    }

    @Override
    public Optional<LockMember> findLockMember(Member member) {
        return lockMemberRepository.findLockMemberByMemberId(member);
    }

    @Override
    public void deleteLockMember(LockMember lockMember) {
        lockMemberRepository.delete(lockMember);
    }

    public Boolean checkCurrentLocked(LockMember lockMember){
        LocalDateTime now = LocalDateTime.now();
        return lockMember.getLockStartTime().isBefore(now) && lockMember.getLockEndTime().isAfter(now)
                ? Boolean.TRUE
                : Boolean.FALSE;
    }
}
