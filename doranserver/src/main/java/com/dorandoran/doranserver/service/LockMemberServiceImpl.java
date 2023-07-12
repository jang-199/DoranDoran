package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.LockMember;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.lockType.LockType;
import com.dorandoran.doranserver.repository.LockMemberRepository;
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
    @Transactional
    @Override
    public void saveLockMember(LockMember lockMember) {
        Optional<LockMember> findLockMember = lockMemberRepository.findById(lockMember.getLockMemberId());
        Duration duration = lockMember.RetrieveLockDuration(lockMember);
        if (findLockMember.isPresent()){
            findLockMember.get().updateLockMember(duration, lockMember.getLockType());
            log.info("{}님의 정지 기한이 {}로 수정되었습니다.", lockMember.getMemberId().getEmail(), lockMember.getLockType());
        }else {
            lockMemberRepository.save(lockMember);
            log.info("{}님이 {}일 정지되었습니다.", lockMember.getMemberId().getEmail(), duration.toDaysPart());
        }

/*        LockMember findLockMember = lockMemberRepository.findById(lockMember.getLockMemberId()).orElse(lockMember);
        Duration duration = lockMember.RetrieveLockDuration(lockMember);
        LockMember saveLockMember = new LockMember(
                findLockMember.getMemberId(),
                findLockMember.getLockStartTime(),
                findLockMember.getLockEndTime().plusDays(duration.toDays()),
                lockMember.getLockType());
        lockMemberRepository.save(saveLockMember);*/
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
