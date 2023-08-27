package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.Post;
import org.locationtech.jts.geom.LineString;
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
            "where m.isLocked = false and m.memberId Not in :memberBlockLists and (m.memberId = :member and m.forMe = true) or (m.forMe = false) " +
            "order by m.postId desc")
    List<Post> findFirstPostWithoutBlockLists(@Param("member") Member member, @Param("memberBlockLists") List<Member> memberBlockLists, PageRequest pageRequest);
    @Query("select m from Post m " +
            "join fetch m.memberId " +
            "where m.isLocked = false and (m.memberId = :member and m.forMe = true) or (m.forMe = false) " +
            "order by m.postId desc")
    List<Post> findFirstPost(@Param("member") Member member, PageRequest pageRequest);


    @Query(value = "select m from Post m " +
            "where m.postId <= :pos  and m.isLocked = false and m.memberId Not in :memberBlockLists and (m.memberId = :member and m.forMe = true) or (m.forMe = false) " +
            "order by m.postId desc ")
    List<Post> findPostWithoutBlockLists(@Param("pos") Long pos, @Param("member") Member member, @Param("memberBlockLists") List<Member> memberBlockLists, PageRequest pageRequest);
    @Query(value = "select m from Post m " +
                "join fetch m.memberId " +
                "where m.postId <= :pos  and m.isLocked = false and ((m.memberId = :member and m.forMe = true) or (m.forMe = false)) " +
                "order by m.postId desc ")
    List<Post> findPost(@Param("pos") Long pos,@Param("member") Member member, PageRequest pageRequest);

    @Query("select m from Post m " +
            "where m.latitude >= :Slat and m.latitude <= :Llat and m.longitude >= :Slon and m.longitude <= :Llon and  m.isLocked = false " +
            "order by m.postId desc")
    List<Post> findFirstClosePost(@Param("Slat") Double Slat,
                                  @Param("Llat") Double Llat,
                                  @Param("Slon") Double Slon,
                                  @Param("Llon") Double Llon,
                                  PageRequest pageRequest);

    @Query(value = "select m from Post m " +
            "where ST_Distance(:location,m.location) <= 1000 and m.isLocked = false " +
            "order by m.postId desc")
    List<Post> findFirstClosePostV2(@Param("location") Point point,
                                    PageRequest pageRequest);

    @Query("select m from Post m " +
            "where m.latitude >= :Slat and m.latitude <= :Llat and m.longitude >= :Slon and m.longitude <= :Llon and  m.isLocked = false and m.memberId not in :members " +
            "order by m.postId desc")
    List<Post> findFirstClosePostWithoutBlockLists(@Param("Slat") Double Slat,
                                  @Param("Llat") Double Llat,
                                  @Param("Slon") Double Slon,
                                  @Param("Llon") Double Llon,
                                  @Param("members") List<Member> members,
                                  PageRequest pageRequest);
    @Query("select m from Post m " +
            "where m.postId <= :pos  and m.latitude >= :Slat and m.latitude <= :Llat and m.longitude >= :Slon and m.longitude <= :Llon  and m.isLocked = false " +
            "order by m.postId desc ")
    List<Post> findClosePost(@Param("pos") Long pos,
                             @Param("Slat") Double Slat,
                             @Param("Llat") Double Llat,
                             @Param("Slon") Double Slon,
                             @Param("Llon") Double Llon,
                             PageRequest pageRequest);
    @Query("select m from Post m " +
            "where m.postId <= :pos  and m.latitude >= :Slat and m.latitude <= :Llat and m.longitude >= :Slon and m.longitude <= :Llon  and m.isLocked = false and m.memberId not in :members " +
            "order by m.postId desc ")
    List<Post> findClosePostWithoutBlockLists(@Param("pos") Long pos,
                             @Param("Slat") Double Slat,
                             @Param("Llat") Double Llat,
                             @Param("Slon") Double Slon,
                             @Param("Llon") Double Llon,
                             @Param("members") List<Member> members,
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
}
