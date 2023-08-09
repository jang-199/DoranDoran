package com.dorandoran.doranserver.controller.admin;

import com.dorandoran.doranserver.dto.InquiryDto;
import com.dorandoran.doranserver.entity.InquiryComment;
import com.dorandoran.doranserver.entity.InquiryPost;
import com.dorandoran.doranserver.service.InquiryCommentService;
import com.dorandoran.doranserver.service.InquiryPostService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Timed
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class InquiryCommentController {
    private final InquiryPostService inquiryPostService;
    private final InquiryCommentService inquiryCommentService;
    @GetMapping("/inquiryComment/{inquiryCommentId}")
    public ResponseEntity<?> getInquiryComment(@PathVariable Long inquiryCommentId){
        InquiryComment findinquiryComment = inquiryCommentService.findCommentById(inquiryCommentId);
        InquiryDto.UpdatePageInquiryComment inquiryComment = InquiryDto.UpdatePageInquiryComment.builder()
                .inquiryPostId(findinquiryComment.getInquiryPostId().getInquiryPostId())
                .inquiryCommentId(findinquiryComment.getInquiryCommentId())
                .comment(findinquiryComment.getComment())
                .build();

        return ResponseEntity.ok().body(inquiryComment);
    }

    @PostMapping("/inquiryComment")
    public ResponseEntity<?> saveInquiryComment(@RequestBody InquiryDto.CreateInquiryComment inquiryCommentDto){
        InquiryPost inquiryPost = inquiryPostService.findInquiryPost(inquiryCommentDto.getInquiryPostId());
        InquiryComment inquiryComment = InquiryComment.builder()
                .inquiryPostId(inquiryPost)
                .comment(inquiryCommentDto.getComment())
                .build();

        inquiryCommentService.saveInquiryComment(inquiryPost, inquiryComment);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/inquiryComment/update")
    public ResponseEntity<?> patchInquiryComment(@RequestBody InquiryDto.UpdateInquiryComment updateInquiryComment){
        InquiryComment inquiryComment = inquiryCommentService.findCommentById(updateInquiryComment.getInquiryCommentId());
        inquiryCommentService.updateInquiryComment(inquiryComment, updateInquiryComment.getComment());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/inquiryComment/{inquiryCommentId}")
    public ResponseEntity<?> deleteInquiryComment(@PathVariable Long inquiryCommentId){
        InquiryComment inquiryComment = inquiryCommentService.findCommentById(inquiryCommentId);
        inquiryCommentService.deleteInquiryComment(inquiryComment);
        return ResponseEntity.ok().build();
    }
}
