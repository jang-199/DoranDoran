package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.InquiryComment;
import com.dorandoran.doranserver.entity.InquiryPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryCommentRepository extends JpaRepository<InquiryComment, Long> {
    List<InquiryComment> findByInquiryPostId(InquiryPost inquiryPost);
}
