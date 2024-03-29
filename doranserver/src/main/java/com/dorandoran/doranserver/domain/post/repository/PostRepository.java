package com.dorandoran.doranserver.domain.post.repository;

import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.post.domain.Post;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("select m from Post m " +
            "join fetch m.memberId " +
            "where m.isLocked = false and m.memberId Not in :memberBlockLists and ((m.memberId = :member and m.forMe = true) or (m.forMe = false)) " +
            "order by m.postId desc")
    List<Post> findFirstPostWithoutBlockLists(@Param("member") Member member, @Param("memberBlockLists") List<Member> memberBlockLists, PageRequest pageRequest);
    @Query("select m from Post m " +
            "join fetch m.memberId " +
            "where m.isLocked = false and ((m.memberId = :member and m.forMe = true) or (m.forMe = false)) " +
            "order by m.postId desc")
    List<Post> findFirstPost(@Param("member") Member member, PageRequest pageRequest);


    @Query(value = "select m from Post m " +
            "where m.postId <= :pos  and m.isLocked = false and m.memberId Not in :memberBlockLists and ((m.memberId = :member and m.forMe = true) or (m.forMe = false)) " +
            "order by m.postId desc ")
    List<Post> findPostWithoutBlockLists(@Param("pos") Long pos, @Param("member") Member member, @Param("memberBlockLists") List<Member> memberBlockLists, PageRequest pageRequest);
    @Query(value = "select m from Post m " +
                "join fetch m.memberId " +
                "where m.postId <= :pos  and m.isLocked = false and ((m.memberId = :member and m.forMe = true) or (m.forMe = false)) " +
                "order by m.postId desc ")
    List<Post> findPost(@Param("pos") Long pos,@Param("member") Member member, PageRequest pageRequest);

    @Query(value = "select p from Post p " +
            "where ((ST_Distance(:point,p.location)) <= :distance) and p.isLocked = false and ((p.memberId = :member and p.forMe = true ) or (p.forMe = false)) " +
            "order by p.postId desc")
    List<Post> findFirstClosePost(@Param("point") Point point,
                                  @Param("distance") double distance,
                                  @Param("member") Member member,
                                  PageRequest pageRequest);

    @Query("select p from Post p " +
            "where ((ST_Distance(:point,p.location)) <= :distance) and p.isLocked = false and p.memberId not in :members and ((p.memberId = :member and p.forMe = true ) or (p.forMe = false)) " +
            "order by p.postId desc")
    List<Post> findFirstClosePostWithoutBlockLists(@Param("point") Point point,
                                                   @Param("distance") double distance,
                                                   @Param("members") List<Member> members,
                                                   @Param("member") Member member,
                                                   PageRequest pageRequest);
    @Query("select p from Post p " +
            "where p.postId <= :pos and (ST_Distance(:Point,p.location)) <= :distance and p.isLocked = false and ((p.memberId = :member and p.forMe = true ) or (p.forMe = false)) " +
            "order by p.postId desc ")
    List<Post> findClosePost(@Param("pos") Long pos,
                             @Param("Point") Point point,
                             @Param("distance") double distance,
                             @Param("member") Member member,
                             PageRequest pageRequest);
    @Query("select p from Post p " +
            "where p.postId <= :pos  and (ST_Distance(:point,p.location)) <= :distance and p.isLocked = false and p.memberId not in :members and ((p.memberId = :member and p.forMe = true ) or (p.forMe = false)) " +
            "order by p.postId desc ")
    List<Post> findClosePostWithoutBlockLists(@Param("pos") Long pos,
                                              @Param("point") Point point,
                                              @Param("distance") double distance,
                                              @Param("members") List<Member> members,
                                              @Param("member") Member member,
                                              PageRequest pageRequest);
    @Query("select m from Post m " +
            "where m.memberId = :member " +
            "order by m.postId desc ")
    List<Post> findMyFirstPost(@Param("member") Member member,
                               PageRequest pageRequest);

    @Query("select m from Post m " +
            "where m.memberId = :member and m.postId <= :pos " +
            "order by m.postId desc ")
    List<Post> findMyPost(@Param("member") Member member,
                          @Param("pos") Long pos,
                          PageRequest pageRequest);

    @Query("select p from Post p where p.memberId = :member")
    List<Post> findAllByMember(@Param("member") Member member);

    @Query("select p from Post p join fetch p.memberId where p.isLocked = true order by p.postId desc")
    List<Post> findBlockedPost(Pageable pageable);

    @Query("select p from Post p join fetch p.memberId where p.postId = :postId")
    Optional<Post> findFetchMember(@Param("postId") Long postId);

    @Query("select p from Post p join fetch p.memberId where p.postId in :postList ")
    List<Post> findAllByIdFetchMember(@Param("postList") List<Long> postList);
}
