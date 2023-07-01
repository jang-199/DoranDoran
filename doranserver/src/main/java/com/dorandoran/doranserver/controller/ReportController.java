package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.dto.ReportRequestDto;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.ReportPost;
import com.dorandoran.doranserver.service.MemberService;
import com.dorandoran.doranserver.service.PostService;
import com.dorandoran.doranserver.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReportController {
    private final MemberService memberService;
    private final PostService postService;
    private final ReportService reportService;

    @PostMapping("/Report")
    @Transactional
    public ResponseEntity<?> saveReportPost(@RequestBody ReportRequestDto reportRequestDto,
                                            @AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        Post post = postService.findSinglePost(reportRequestDto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("해당 글이 존재하지 않습니다."));
        ReportPost reportPost = new ReportPost(post, member, reportRequestDto.getContent());

        if (reportService.existReportPost(post,member)){
            return new ResponseEntity<>("이미 신고한 회원입니다.", HttpStatus.BAD_REQUEST);
        }else {
            reportService.saveReportPost(reportPost);
            post.setReportCount(post.getReportCount()+1);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
}
