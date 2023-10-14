package com.dorandoran.doranserver.domain.customerservice.service.repository;

import com.dorandoran.doranserver.domain.customerservice.domain.InquiryComment;
import com.dorandoran.doranserver.domain.customerservice.domain.InquiryPost;
import com.dorandoran.doranserver.domain.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InquiryCommentRepository extends JpaRepository<InquiryComment, Long> {
    List<InquiryComment> findByInquiryPostIdOrderByInquiryCommentIdDesc(InquiryPost inquiryPost);

    @Query("select ic from InquiryComment ic where ic.inquiryCommentId = :inquiryCommentId")
    InquiryComment findFetchPost(@Param("inquiryCommentId") Long inquiryCommentId);

    @Query("select ic from InquiryComment ic where ic.inquiryPostId in :inquiryPost")
    List<InquiryComment> findAllByInquiryPostId(@Param("inquiryPost") List<InquiryPost> inquiryPost);
}
