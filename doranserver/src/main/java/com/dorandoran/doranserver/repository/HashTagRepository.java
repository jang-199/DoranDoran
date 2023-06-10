package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.HashTag;
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

    @Query("select h from HashTag h where h.hashTagName in (:hashTag)")
    List<HashTag> findByHashTagNameList (@Param("hashTag") List<String> hashTag);
}
