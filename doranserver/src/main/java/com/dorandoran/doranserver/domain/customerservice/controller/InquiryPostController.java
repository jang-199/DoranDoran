package com.dorandoran.doranserver.domain.customerservice.controller;

import com.dorandoran.doranserver.domain.customerservice.domain.InquiryComment;
import com.dorandoran.doranserver.domain.customerservice.domain.InquiryPost;
import com.dorandoran.doranserver.domain.customerservice.domain.inquirytype.InquiryStatus;
import com.dorandoran.doranserver.domain.customerservice.dto.InquiryDto;
import com.dorandoran.doranserver.domain.customerservice.service.InquiryCommentService;
import com.dorandoran.doranserver.domain.customerservice.service.InquiryPostService;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.member.service.MemberService;
import com.dorandoran.doranserver.global.util.InquiryResponseUtils;
import com.dorandoran.doranserver.global.util.annotation.Trace;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Timed
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class InquiryPostController {
    private final MemberService memberService;
    private final InquiryPostService inquiryPostService;
    private final InquiryCommentService inquiryCommentService;
    @Value("${doran.ip.address}")
    String address;

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

        String locationUri = "http://" + address + ":8080/api/inquiryPost/" + inquiryPost.getInquiryPostId() + "/read";
        return ResponseEntity.created(URI.create(locationUri)).build();
    }

    @Trace
    @GetMapping("/inquiryPost")
    public ResponseEntity<?> getInquiryPost(@AuthenticationPrincipal UserDetails userDetails){
        String username = userDetails.getUsername();
        Member member = memberService.findByEmail(username);
        List<InquiryPost> inquiryPostList = inquiryPostService.findByMember(member);

        return ResponseEntity.ok().body(InquiryResponseUtils.makeInquiryPostList(inquiryPostList));
    }

    @Trace
    @GetMapping("/inquiryPost/{inquiryPostId}/read")
    public ResponseEntity<?> readInquiryPost(@PathVariable(name = "inquiryPostId") Long inquiryPostId,
                                             @AuthenticationPrincipal UserDetails userDetails){
        String username = userDetails.getUsername();
        Member member = memberService.findByEmail(username);

        InquiryPost inquiryPost = inquiryPostService.findInquiryPostFetchMember(inquiryPostId);

        if (inquiryPost.getMemberId().equals(member)) {
            List<InquiryComment> inquiryComments = inquiryCommentService.findCommentByPost(inquiryPost);

            List<InquiryDto.ReadInquiryComment> inquiryCommentListDto = InquiryResponseUtils.makeInquiryCommentList(inquiryComments);
            InquiryDto.ReadInquiryPost readInquiryPost = new InquiryDto.ReadInquiryPost().toEntity(inquiryPost, inquiryCommentListDto);

            return ResponseEntity.ok().body(readInquiryPost);
        }else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Trace
    @DeleteMapping("/inquiryPost/{inquiryPostId}")
    public ResponseEntity<?> deleteInquiryPost(@PathVariable(name = "inquiryPostId") Long inquiryPostId,
                                               @AuthenticationPrincipal UserDetails userDetails){
        String username = userDetails.getUsername();
        Member member = memberService.findByEmail(username);

        InquiryPost inquiryPost = inquiryPostService.findInquiryPost(inquiryPostId);

        if (inquiryPost.getInquiryStatus().equals(InquiryStatus.NotAnswered) && inquiryPost.getMemberId().equals(member)){
            inquiryPostService.deleteInquiryPost(inquiryPost);
            return ResponseEntity.noContent().build();
        }else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
