package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.InquiryPost;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.repository.InquiryPostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        return inquiryPostRepository.findById(inquiryPostId).orElseThrow(() -> new NoSuchElementException("해당 문의 글이 존재하지 않습니다."));
    }

    @Override
    public List<InquiryPost> findByPostTitle(Integer page, String title) {
        PageRequest of = PageRequest.of(page, 20);
        return inquiryPostRepository.findByTitleContaining(title, of);
    }

    @Override
    public List<InquiryPost> findByPostContent(Integer page, String content) {
        PageRequest of = PageRequest.of(page, 20);
        return inquiryPostRepository.findByContentContains(content, of);
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
}
