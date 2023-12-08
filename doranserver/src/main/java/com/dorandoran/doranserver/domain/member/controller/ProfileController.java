package com.dorandoran.doranserver.domain.member.controller;

import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.member.dto.AccountDto;
import com.dorandoran.doranserver.domain.member.service.MemberService;
import com.dorandoran.doranserver.global.util.annotation.Trace;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Timed
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class ProfileController {
    private final MemberService memberService;

    @Trace
    @GetMapping("/notificationStatus")
    public ResponseEntity<?> searchNotificationStatus(@AuthenticationPrincipal UserDetails userDetails){
        String username = userDetails.getUsername();

        Member member = memberService.findByEmail(username);
        Boolean notificationStatus = member.getCheckNotification();

        AccountDto.notificationStatusResponse notificationStatusResponse = new AccountDto.notificationStatusResponse(notificationStatus);

        return ResponseEntity.ok().body(notificationStatusResponse);
    }

    @Trace
    @PatchMapping("/notificationStatus")
    public ResponseEntity<?> updateNotificationStatus(@AuthenticationPrincipal UserDetails userDetails){
        String username = userDetails.getUsername();

        Member member = memberService.findByEmail(username);
        memberService.updateNotificationStatus(member);

        return ResponseEntity.noContent().build();
    }
}
