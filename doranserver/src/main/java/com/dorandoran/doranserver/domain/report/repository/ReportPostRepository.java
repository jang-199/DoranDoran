package com.dorandoran.doranserver.domain.report.repository;

import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.report.domain.ReportPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportPostRepository extends JpaRepository<ReportPost, Long> {
    Optional<ReportPost> findReportPostByPostIdAndMemberId(Post post, Member member);

    List<ReportPost> findAllByPostId(Post post);
}
