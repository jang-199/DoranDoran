package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.Comment;
import com.dorandoran.doranserver.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select c from Comment c where c.postId = :postId")
    List<Comment> findCommentCntByPostId(@Param("postId") Post post);
    List<Comment> findCommentByPostId(Post post);

    @Query("select c from Comment c where c.postId = :postId order by c.commentId desc")
    List<Comment> findFirstComments(@Param("postId") Post post, Pageable pageable);

    @Query("select c from Comment c join fetch c.memberId m where c.postId = :postId order by c.commentId desc")
    List<Comment> findFirstCommentsFetchMember(@Param("postId") Post post, Pageable pageable);

    @Query("select c from Comment c where c.postId.postId = :postId and c.commentId < :commentId order by c.commentId desc")
    List<Comment> findNextComments(@Param("postId") Long postId,
                                   @Param("commentId") Long commentId,
                                   Pageable pageable);
}
