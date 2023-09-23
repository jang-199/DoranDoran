package com.dorandoran.doranserver.domain.customerservice.service;

import com.dorandoran.doranserver.domain.customerservice.domain.InquiryComment;
import com.dorandoran.doranserver.domain.customerservice.domain.InquiryPost;
import com.dorandoran.doranserver.domain.customerservice.exception.NotFoundInquiryPostException;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.customerservice.domain.inquirytype.InquiryStatus;
import com.dorandoran.doranserver.domain.customerservice.service.repository.InquiryPostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class InquiryPostServiceImpl implements InquiryPostService{
    private final InquiryPostRepository inquiryPostRepository;
    @Override
    @Transactional
    public void saveInquiryPost(InquiryPost inquiryPost) {
        inquiryPostRepository.save(inquiryPost);
    }

    @Override
    @Transactional
    public void deleteInquiryPost(InquiryPost inquiryPost) {
        inquiryPostRepository.delete(inquiryPost);
    }

    @Override
    public List<InquiryPost> findAll(Integer page) {
        PageRequest of = PageRequest.of(page, 20);
        return inquiryPostRepository.findAllAdmin(of);
    }

    @Override
    public List<InquiryPost> findByMember(Member member) {
        return inquiryPostRepository.findByMemberId(member);
    }

    @Override
    public InquiryPost findInquiryPost(Long inquiryPostId) {
        return inquiryPostRepository.findById(inquiryPostId).orElseThrow(() -> new NotFoundInquiryPostException("해당 문의 글이 존재하지 않습니다."));
    }

    @Override
    public InquiryPost findInquiryPostFetchMember(Long inquiryPostId){
        return inquiryPostRepository.findFetchMember(inquiryPostId).orElseThrow(() -> new NotFoundInquiryPostException("해당 문의 글이 존재하지 않습니다."));
    }

    @Override
    public List<InquiryPost> findByPostTitle(Integer page, String title) {
        PageRequest of = PageRequest.of(page, 20);
        return inquiryPostRepository.findByTitleContainingOrderByInquiryPostId(title, of);
    }

    @Override
    public List<InquiryPost> findByPostContent(Integer page, String content) {
        PageRequest of = PageRequest.of(page, 20);
        return inquiryPostRepository.findByContentContainsOrderByInquiryPostId(content, of);
    }

    @Override
    public List<InquiryPost> findByPostTime(Integer page, String postTime) {
        PageRequest of = PageRequest.of(page, 20);
        String[] datePats = postTime.split("[.]");
        LocalDateTime searchDate = LocalDateTime.of(
                Integer.parseInt(datePats[0]),
                Integer.parseInt(datePats[1]),
                Integer.parseInt(datePats[2]),
                0,
                0);

        return inquiryPostRepository.findByCreatedTimeContains(searchDate, of);
    }

    @Override
    public List<InquiryPost> findByAnswerType(Integer page, String answerType) {
        PageRequest of = PageRequest.of(page, 20);
        InquiryStatus findAnswerType = answerType.equals("NotAnswered") ? InquiryStatus.NotAnswered : InquiryStatus.Answered;
        return inquiryPostRepository.findByInquiryStatusContains(findAnswerType, of);
    }

    @Override
    @Transactional
    public void setAnsweredType(InquiryPost inquiryPost, List<InquiryComment> inquiryCommentList) {
        if (inquiryPost.getInquiryStatus().equals(InquiryStatus.Answered) && inquiryCommentList.size() == 1) {
            inquiryPost.setInquiryStatus(InquiryStatus.NotAnswered);
        }
    }
}
