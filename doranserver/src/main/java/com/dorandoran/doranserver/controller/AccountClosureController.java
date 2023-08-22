package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.controller.annotation.Trace;
import com.dorandoran.doranserver.entity.AccountClosureMember;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.service.AccountClosureMemberService;
import com.dorandoran.doranserver.service.MemberService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Timed
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
@Slf4j
public class AccountClosureController {

    private final MemberService memberService;
    private final AccountClosureMemberService accountClosureMemberService;

    @Trace
    @DeleteMapping("/member")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        Member member = memberService.findByEmail(username);
        if (!member.isEmptyClosureDate()) {
            return ResponseEntity.badRequest().body("이미 탈퇴 예정인 회원입니다.");
        }

        member.setAccountClosureRequestTime();

        memberService.saveMember(member);

        AccountClosureMember build = AccountClosureMember.builder().closureMemberId(member).build();
        accountClosureMemberService.saveClosureMember(build);

        return ResponseEntity.ok().build();
    }
}
