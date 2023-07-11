package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.LockMember;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.repository.LockMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LockMemberServiceImpl implements LockMemberService{
    private final LockMemberRepository lockMemberRepository;
    @Override
    public void saveLockMember(LockMember lockMember) {
        Optional<LockMember> findLockMember = lockMemberRepository.findById(lockMember.getLockMemberId());
        Duration duration = lockMember.RetrieveLockDuration(lockMember);
        if (findLockMember.isPresent()){
            LockMember updateLockMember = new LockMember(
                    findLockMember.get().getMemberId(),
                    findLockMember.get().getLockStartTime(),
                    findLockMember.get().getLockEndTime().plusDays(duration.toDays()),
                    lockMember.getLockType());
            lockMemberRepository.save(updateLockMember);
            log.info("{}님의 정지 기한이 {}일 늘어났습니다.", lockMember.getMemberId().getEmail(), duration.toDaysPart());
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
    public Boolean CheckLocked(Member member) {
        Optional<LockMember> lockMember = lockMemberRepository.findLockMemberByMemberId(member);
        return lockMember.isPresent() ? Boolean.TRUE : Boolean.FALSE;
    }
}
