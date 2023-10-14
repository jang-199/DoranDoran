package com.dorandoran.doranserver.domain.customerservice.service.repository;

import com.dorandoran.doranserver.domain.customerservice.domain.InquiryPost;
import com.dorandoran.doranserver.domain.customerservice.domain.inquirytype.InquiryStatus;
import com.dorandoran.doranserver.domain.member.domain.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InquiryPostRepository extends JpaRepository<InquiryPost, Long> {
    List<InquiryPost> findByMemberId(Member member);
    @Query("select ip from InquiryPost ip order by ip.inquiryPostId DESC")
    List<InquiryPost> findAllAdmin(Pageable pageable);

    List<InquiryPost> findByContentContainsOrderByInquiryPostId(String content, Pageable pageable);
    List<InquiryPost> findByTitleContainingOrderByInquiryPostId(String title, Pageable pageable);
    @Query("select ip from InquiryPost ip where ip.createdTime >= :postTime order by ip.inquiryPostId desc")
    List<InquiryPost> findByCreatedTimeContains(@Param("postTime") LocalDateTime postTime, Pageable pageable);

    @Query("SELECT ip from InquiryPost ip where ip.inquiryStatus = :answerType order by ip.inquiryPostId desc")
    List<InquiryPost> findByInquiryStatusContains(@Param("answerType") InquiryStatus answerType, Pageable pageable);

    @Query("select ip from InquiryPost ip join fetch ip.memberId where ip.inquiryPostId = :inquiryPostId")
    Optional<InquiryPost> findFetchMember(@Param("inquiryPostId") Long inquiryPostId);

    List<InquiryPost> findAllByMemberId(Member member);
}
