package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.ReportPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<ReportPost, Long> {
    Optional<ReportPost> findReportPostByPostIdAndMemberId(Post post, Member member);
}
