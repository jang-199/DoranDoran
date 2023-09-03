package com.dorandoran.doranserver.domain.customerservice.controller.admin;

import com.dorandoran.doranserver.domain.customerservice.domain.InquiryPost;
import com.dorandoran.doranserver.domain.customerservice.dto.InquiryDto;
import com.dorandoran.doranserver.domain.customerservice.service.InquiryPostService;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.member.service.MemberService;
import com.dorandoran.doranserver.global.util.annotation.Trace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class InquiryPostController {
    private final MemberService memberService;
    private final InquiryPostService inquiryPostService;
    @Trace
    @PostMapping("/inquiryPost")
    public ResponseEntity<?> saveInquiryPost(@RequestBody InquiryDto.CreateInquiryPost createInquiryPost,
                                             @AuthenticationPrincipal UserDetails userDetails){
        Member member = memberService.findByEmail(userDetails.getUsername());
        InquiryPost inquiryPost = InquiryPost.builder()
                .title(createInquiryPost.getTitle())
                .content(createInquiryPost.getContent())
                .memberId(member)
                .build();
        inquiryPostService.saveInquiryPost(inquiryPost);
        return ResponseEntity.ok().build();
    }
}
