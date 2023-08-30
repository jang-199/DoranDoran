package com.dorandoran.doranserver.domain.api.hashtag.controller;

import com.dorandoran.doranserver.domain.api.hashtag.dto.HashTagDto;
import com.dorandoran.doranserver.domain.api.hashtag.domain.HashTag;
import com.dorandoran.doranserver.domain.api.member.domain.Member;
import com.dorandoran.doranserver.domain.api.member.domain.MemberHash;
import com.dorandoran.doranserver.domain.api.hashtag.service.HashTagService;
import com.dorandoran.doranserver.domain.api.member.service.MemberHashService;
import com.dorandoran.doranserver.domain.api.member.service.MemberService;
import com.dorandoran.doranserver.global.util.annotation.Trace;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Timed
@RestController
@RequestMapping("api")
@RequiredArgsConstructor
@Slf4j
public class HashTagController {
    private final HashTagService hashTagService;
    private final MemberService memberService;
    private final MemberHashService memberHashService;

    @Trace
    @GetMapping("/hashTag")
    public ResponseEntity<?> searchHashTag(@RequestParam("hashTag") String reqHashTag,
                                           @AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        List<String> memberHashes = memberHashService.findHashByEmail(userEmail);
        List<HashTag> findHashTag = hashTagService.findTop5BySearchHashTag(reqHashTag);
        ArrayList<HashTagDto.ReadHashtagResponse> hashTagResponseDtos = new ArrayList<>();
        for (HashTag hashTag : findHashTag) {
            Boolean hashTagCheck = (memberHashes.contains(hashTag.getHashTagName())) ? Boolean.TRUE : Boolean.FALSE;
            HashTagDto.ReadHashtagResponse hashTagBuild = HashTagDto.ReadHashtagResponse.builder()
                    .hashTagName(hashTag.getHashTagName())
                    .hashTagCount(hashTag.getHashTagCount())
                    .hashTagCheck(hashTagCheck)
                    .build();
            hashTagResponseDtos.add(hashTagBuild);
        }

        return ResponseEntity.ok().body(hashTagResponseDtos);
    }

    @Trace
    @GetMapping("/hashTag/popular")
    public ResponseEntity<?> popularHashTag(){
        List<HashTag> hashTag = hashTagService.findPopularHashTagTop5();
        if (hashTag.size() == 0){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else {
            List<HashTagDto.ReadPopularHashTagResponse> hashTagList = hashTag.stream()
                    .map(h -> new HashTagDto.ReadPopularHashTagResponse(h))
                    .collect(Collectors.toList());
            log.info("인기 있는 태그 검색");
            return ResponseEntity.ok().body(hashTagList);
        }
    }

    @Trace
    @GetMapping("/hashTag/member")
    public ResponseEntity<?> memberHashTag(@AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        List<String> memberHash = memberHashService.findHashByEmail(userEmail);
        if (memberHash.size() == 0){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else {
            HashTagDto.ReadMemberHashTagResponse hashTagList = HashTagDto.ReadMemberHashTagResponse.builder().hashTagList(memberHash).build();
            log.info("{}의 관심 있는 태그 검색",userEmail);
            return ResponseEntity.ok().body(hashTagList);
        }
    }

    @Trace
    @PostMapping("/hashTag/member")
    public ResponseEntity<?> saveHashTag(@RequestBody HashTagDto.CreateHashTag hashTagRequestDto,
                                         @AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail);
        HashTag hashTag = hashTagService.findByHashTagName(hashTagRequestDto.getHashTag());
        List<String> memberHashes = memberHashService.findHashByEmail(userEmail);
        if (memberHashes.contains(hashTagRequestDto.getHashTag())){
            log.info("\"{}\" 해시태그는 이미 즐겨찾기 목록에 있습니다.",hashTagRequestDto.getHashTag());
        }
        else {
            MemberHash memberHashBuild = MemberHash.builder()
                    .member(member)
                    .hashTagId(hashTag)
                    .build();
            memberHashService.saveMemberHash(memberHashBuild);
            log.info("{} 사용자가 해시태그 {}를 즐겨찾기에 추가하였습니다.", userEmail, hashTagRequestDto.getHashTag());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Trace
    @DeleteMapping("/hashTag/member")
    public ResponseEntity<?> deleteHashTag(@RequestBody HashTagDto.DeleteHashTag hashTagRequestDto,
                                           @AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        Optional<MemberHash> memberHash = memberHashService.findMemberHashByEmailAndHashTag(userEmail, hashTagRequestDto.getHashTag());
        if (memberHash.isEmpty()) {
            log.info("즐겨찾기된 해시태그가 없습니다.");
        } else {
            memberHashService.deleteMemberHash(memberHash.get());
            log.info("{} 사용자가 해시태그 {}를 즐겨찾기에 삭제하였습니다.",userEmail, hashTagRequestDto.getHashTag());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}