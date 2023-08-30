package com.dorandoran.doranserver.domain.api.comment.repository;

import com.dorandoran.doranserver.domain.api.comment.domain.Comment;
import com.dorandoran.doranserver.domain.api.member.domain.Member;
import com.dorandoran.doranserver.domain.api.post.domain.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select c from Comment c where c.postId = :postId")
    List<Comment> findCommentCntByPostId(@Param("postId") Post post);
    @Query("select COUNT(c) from Comment c " +
            "where c.postId in :postList group by c.postId " +
            "order by c.postId.postId desc ")
    List<Integer> findCommentCntByPostList(@Param("postList") List<Post> postList);
    @Query("select c from Comment c " +
            "where c.postId in :postList and c.checkDelete = false " +
            "order by c.postId.postId")
    List<Comment> findCommentByPostList(@Param("postList") List<Post> postList);
    List<Comment> findCommentByPostId(Post post);

    @Query("select c from Comment c where c.postId = :postId order by c.commentId desc")
    List<Comment> findFirstComments(@Param("postId") Post post, Pageable pageable);

    @Query("select c from Comment c join fetch c.memberId m where c.postId = :postId order by c.commentId desc")
    List<Comment> findFirstCommentsFetchMember(@Param("postId") Post post, Pageable pageable);

    @Query("select c from Comment c where c.postId.postId = :postId and c.commentId < :commentId order by c.commentId desc")
    List<Comment> findNextComments(@Param("postId") Long postId,
                                   @Param("commentId") Long commentId,
                                   Pageable pageable);

    @Query("select c from Comment c where c.memberId = :member")
    List<Comment> findAllByMember(@Param("member") Member member);
    @Query("select c from Comment c where c.postId = :post")
    List<Comment> findAllByPost(@Param("post") Post post);

    @Query("select c from Comment c join fetch c.memberId where c.isLocked = true order by c.commentId desc")
    List<Comment> findCommentInAdmin(Pageable pageable);

    @Query("select c from Comment c join fetch c.memberId " +
            "where c.postId = :postId " +
            "order by c.commentId desc")
    List<Comment> findCommentInAdminDetail(@Param("postId") Post post);

    @Query("select c from Comment c join fetch c.memberId where c.commentId = :commentId")
    Optional<Comment> findFetchMember(@Param("commentId") Long commentId);
}
