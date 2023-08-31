package com.dorandoran.doranserver.domain.admin.service.repository;

import com.dorandoran.doranserver.domain.admin.domain.InquiryComment;
import com.dorandoran.doranserver.domain.admin.domain.InquiryPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryCommentRepository extends JpaRepository<InquiryComment, Long> {
    List<InquiryComment> findByInquiryPostIdOrderByInquiryCommentIdDesc(InquiryPost inquiryPost);
}
