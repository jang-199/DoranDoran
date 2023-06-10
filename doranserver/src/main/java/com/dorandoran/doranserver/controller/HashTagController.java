package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.config.jwt.TokenProvider;
import com.dorandoran.doranserver.dto.HashTagRequestDto;
import com.dorandoran.doranserver.dto.HashTagResponseDto;
import com.dorandoran.doranserver.entity.HashTag;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.MemberHash;
import com.dorandoran.doranserver.service.HashTagServiceImpl;
import com.dorandoran.doranserver.service.MemberHashServiceImpl;
import com.dorandoran.doranserver.service.MemberServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Tag(name = "해시태그 관련 API", description = "HashTagController")
@RestController
@RequestMapping("api")
@RequiredArgsConstructor
@Slf4j
public class HashTagController {
    private final TokenProvider tokenProvider;
    private final HashTagServiceImpl hashTagService;
    private final MemberServiceImpl memberService;
    private final MemberHashServiceImpl memberHashService;

    @Tag(name = "해시태그 관련 API")
    @Operation(summary = "해시태그 최대 5개 조회", description = "요청한 해시태그로 시작하는 해시태그들 중 많이 사용된 5개를 반환하는 API입니다." +
            "\n\n" + "Ex. 홍으로 해당 api을 요청하면 홍길동, 홍당무, 홍시, 홍단, 홍기처럼 홍으로 시작하는 해시태그들을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "최대 5개의 해시태그를 리턴합니다.",
    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = HashTagResponseDto.class)))
    @GetMapping("/hashTag")
    public ResponseEntity<?> searchHashTag(@Parameter(description = "검색 요청하는 해시태그", required = true) @RequestParam("hashTag") String reqHashTag,
                                           @AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        List<String> memberHashes = memberHashService.findHashByEmail(userEmail);
        List<HashTag> findHashTag = hashTagService.findTop5BySearchHashTag(reqHashTag);
        ArrayList<HashTagResponseDto> hashTagResponseDtos = new ArrayList<>();
        for (HashTag hashTag : findHashTag) {
            Boolean hashTagCheck = (memberHashes.contains(hashTag.getHashTagName())) ? Boolean.TRUE : Boolean.FALSE;
            HashTagResponseDto hashTagBuild = HashTagResponseDto.builder()
                    .hashTagName(hashTag.getHashTagName())
                    .hashTagCount(hashTag.getHashTagCount())
                    .hashTagCheck(hashTagCheck)
                    .build();
            hashTagResponseDtos.add(hashTagBuild);
        }

        return ResponseEntity.ok().body(hashTagResponseDtos);
    }

    @Tag(name = "해시태그 관련 API")
    @Operation(summary = "사용자 즐겨찾기 해시태그 추가", description = "요청한 해시태그들을 사용자 즐겨찾기 해시태그 목록에 추가하는 API입니다.")
    @ApiResponse(responseCode = "200", description = "사용자 즐겨찾기 해시태그가 추가됩니다.")
    @PostMapping("/hashTag")
    public ResponseEntity<?> saveHashTag(@Parameter(description = "추가하려는 해시태그들") @RequestBody HashTagRequestDto hashTagRequestDto,
                                         @AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        Optional<Member> member = memberService.findByEmail(userEmail);
        List<String> hashTagList = hashTagRequestDto.getHashTagList();
        List<HashTag> findHashTagList = hashTagService.findByHashTagNameList(hashTagList);
        List<String> memberHashes = memberHashService.findHashByEmail(userEmail);
        if (hashTagList.size() != 0) {
            for (HashTag hashTag : findHashTagList) {
                if (memberHashes.contains(hashTag.getHashTagName())){
                    log.info("\"{}\" 해시태그는 이미 즐겨찾기 목록에 있습니다.",hashTag.getHashTagName());
                }
                else {
                    MemberHash memberHashBuild = MemberHash.builder()
                            .member(member.get())
                            .hashTagId(hashTag)
                            .build();
                    memberHashService.saveMemberHash(memberHashBuild);
                    log.info("{} 사용자가 해시태그 {}를 즐겨찾기에 추가하였습니다.", userEmail, hashTag.getHashTagName());
                }
            }
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Tag(name = "해시태그 관련 API")
    @Operation(summary = "사용자 즐겨찾기 해시태그 삭제", description = "요청한 해시태그들을 사용자 즐겨찾기 해시태그 목록에서 삭제하는 API입니다.")
    @ApiResponse(responseCode = "200", description = "사용자 즐겨찾기 해시태그가 삭제됩니다.")
    @DeleteMapping("/hashTag")
    public ResponseEntity<?> deleteHashTag(@Parameter(description = "삭제하려는 해시태그들") @RequestBody HashTagRequestDto hashTagRequestDto,
                                           @AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        List<String> hashTagList = hashTagRequestDto.getHashTagList();
        if (hashTagList.size() != 0) {
            for (String hashTag : hashTagList) {
                Optional<MemberHash> memberHash = memberHashService.findMemberHashByEmailAndHashTag(userEmail, hashTag);
                if (memberHash.isEmpty()) {
                    log.info("즐겨찾기된 해시태그가 없습니다.");
                } else {
                    memberHashService.deleteMemberHash(memberHash.get());
                    log.info("{} 사용자가 해시태그 {}를 즐겨찾기에 삭제하였습니다.",userEmail, hashTag);
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
