package com.dorandoran.doranserver.controller;

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

    @DeleteMapping("/account-closure")
    public ResponseEntity.BodyBuilder deleteAccount(@AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        Member member = memberService.findByEmail(username).orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        member.setAccountClosureRequestTime();

        memberService.saveMember(member);

        AccountClosureMember build = AccountClosureMember.builder().closureMemberId(member).build();
        accountClosureMemberService.saveClosureMember(build);

        return ResponseEntity.ok();
    }
}
