package com.dorandoran.doranserver.domain.api.report.service;

import com.dorandoran.doranserver.domain.api.member.dto.LockDto;
import com.dorandoran.doranserver.domain.api.member.domain.LockMember;
import com.dorandoran.doranserver.domain.api.member.domain.Member;
import com.dorandoran.doranserver.domain.api.member.domain.lockType.LockType;
import com.dorandoran.doranserver.domain.api.member.service.LockMemberService;
import com.dorandoran.doranserver.domain.api.notification.service.FirebaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportCommonService {
    private final LockMemberService lockMemberService;
    private final FirebaseService firebaseService;

    public void memberLockLogic(Member member) {
        member.addTotalReportTime();
        log.info("해당 사용자의 신고 횟수가 증가했습니다.");
        if (checkReached(member.getTotalReportCount())){
            LockDto lockDto = setLockDto(member.getTotalReportCount());
            LockMember lockMember = new LockMember(member, lockDto.getLockTime(), lockDto.getLockType());
            lockMemberService.saveLockMember(lockMember);
            firebaseService.notifyBlockedMember(lockMember);
        }
    }

    private Boolean checkReached(int totalReportTime){
        List<Integer> checkNum = new ArrayList<>(){{
            add(3);
            add(5);
            add(7);
            add(10);
        }};

        return checkNum.contains(totalReportTime) ? Boolean.TRUE : Boolean.FALSE;
    }

    private LockDto setLockDto(int totalReportTime) {
        if (totalReportTime == 3){
            return new LockDto(Duration.ofDays(1), LockType.Day1);
        } else if (totalReportTime == 5) {
            return new LockDto(Duration.ofDays(7), LockType.Day7);
        } else if (totalReportTime == 7) {
            return new LockDto(Duration.ofDays(30), LockType.Day30);
        } else {
            return new LockDto(Duration.ZERO, LockType.Ban); // totalReportTime == 10
        }
    }

}
