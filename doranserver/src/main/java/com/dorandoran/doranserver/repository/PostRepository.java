package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.Post;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("select m from Post m " +
            "where m.forMe = false " +
            "order by m.postId desc")
    List<Post> findFirstPost(PageRequest pageRequest);

    @Query(value = "select m from Post m " +
            "where m.postId <= :pos  and m.forMe = false " +
            "order by m.postId desc ")
    List<Post> findPost(@Param("pos") Long pos, PageRequest pageRequest);

    @Query("select m from Post m " +
            "where m.latitude >= :Slat and m.latitude <= :Llat and m.longitude >= :Slon and m.longitude <= :Llon and m.forMe = false " +
            "order by m.postId desc")
    List<Post> findFirstClosePost(@Param("Slat") Double Slat,
                                  @Param("Llat") Double Llat,
                                  @Param("Slon") Double Slon,
                                  @Param("Llon") Double Llon,
                                  PageRequest pageRequest);
    @Query("select m from Post m " +
            "where m.postId <= :pos  and m.latitude >= :Slat and m.latitude <= :Llat and m.longitude >= :Slon and m.longitude <= :Llon and m.forMe = false " +
            "order by m.postId desc ")
    List<Post> findClosePost(@Param("pos") Long pos,
                             @Param("Slat") Double Slat,
                             @Param("Llat") Double Llat,
                             @Param("Slon") Double Slon,
                             @Param("Llon") Double Llon,
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
}
