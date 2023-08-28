package com.dorandoran.doranserver.domain.api.admin.service.repository;

import com.dorandoran.doranserver.domain.api.admin.domain.InquiryComment;
import com.dorandoran.doranserver.domain.api.admin.domain.InquiryPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryCommentRepository extends JpaRepository<InquiryComment, Long> {
    List<InquiryComment> findByInquiryPostIdOrderByInquiryCommentIdDesc(InquiryPost inquiryPost);
}
