package com.dorandoran.doranserver.domain.customerservice.service;

import com.dorandoran.doranserver.domain.customerservice.domain.InquiryComment;
import com.dorandoran.doranserver.domain.customerservice.domain.InquiryPost;
import com.dorandoran.doranserver.domain.customerservice.service.repository.InquiryCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class InquiryCommentServiceImpl implements InquiryCommentService{
    private final InquiryCommentRepository inquiryCommentRepository;
    @Override
    @Transactional
    public void saveInquiryComment(InquiryPost inquiryPost, InquiryComment inquiryComment) {
        inquiryPost.updateStatus();
        inquiryCommentRepository.save(inquiryComment);
    }

    @Override
    public void deleteInquiryComment(InquiryComment inquiryComment) {
        inquiryCommentRepository.delete(inquiryComment);
    }

    @Override
    public void deleteInquiryCommentList(List<InquiryComment> inquiryCommentList) {
        inquiryCommentRepository.deleteAllInBatch(inquiryCommentList);
    }

    @Override
    public List<InquiryComment> findCommentByPost(InquiryPost inquiryPost) {
        return inquiryCommentRepository.findByInquiryPostIdOrderByInquiryCommentIdDesc(inquiryPost);
    }

    @Override
    public InquiryComment findCommentById(Long inquiryCommentId) {
        return inquiryCommentRepository.findById(inquiryCommentId).orElseThrow(() -> new NoSuchElementException("해당 문의 댓글은 존재하지 않습니다."));
    }

    @Override
    public InquiryComment findCommentFetchPost(Long inquiryCommentId){
        return inquiryCommentRepository.findFetchPost(inquiryCommentId);
    }

    @Override
    @Transactional
    public void updateInquiryComment(InquiryComment inquiryComment, String content) {
        inquiryComment.setComment(content);
    }
}
