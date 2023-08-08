package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.InquiryPost;
import com.dorandoran.doranserver.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface InquiryPostRepository extends JpaRepository<InquiryPost, Long> {
    List<InquiryPost> findByMemberId(Member member);
    @Query("select ip from InquiryPost ip order by ip.inquiryPostId DESC")
    List<InquiryPost> findAllAdmin(Pageable pageable);

    List<InquiryPost> findByContentContains(String content, Pageable pageable);
    List<InquiryPost> findByTitleContaining(String title, Pageable pageable);
    @Query("select ip from InquiryPost ip where ip.createdTime >= :postTime order by ip.inquiryPostId desc")
    List<InquiryPost> findByCreatedTimeContains(@Param("postTime") LocalDateTime postTime, Pageable pageable);
}
