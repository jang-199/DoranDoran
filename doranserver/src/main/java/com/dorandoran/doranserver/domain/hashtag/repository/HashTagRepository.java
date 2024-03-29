package com.dorandoran.doranserver.domain.hashtag.repository;

import com.dorandoran.doranserver.domain.hashtag.domain.HashTag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HashTagRepository extends JpaRepository<HashTag,Long> {
    Optional<HashTag> findByHashTagName(String hashTag);

    @Query("select h from HashTag h where h.hashTagName like :hashTag% order by h.hashTagCount desc")
    List<HashTag> findTop5BySearchHashTag(Pageable pageable, @Param("hashTag") String hashTag);

    @Query("select h from HashTag h order by h.hashTagCount desc")
    List<HashTag> findPopularHashTagTop5(Pageable pageable);

    @Query("select h from HashTag  h where h.hashTagName in :hashTagList")
    List<HashTag> findHashTagList(@Param("hashTagList") List<String> hashTagList);
}
