package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.dto.PostResponseDto;
import com.dorandoran.doranserver.entity.*;
import com.dorandoran.doranserver.entity.imgtype.ImgType;
import com.dorandoran.doranserver.service.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "관심있는 글 관련 API", description = "InquiryInterestedPostController")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class InquiryInterestedPostController {

    private final MemberHashServiceImpl memberHashService;
    private final MemberServiceImpl memberService;
    private final PostHashServiceImpl postHashService;
    private final PostLikeServiceImpl postLikeService;
    private final CommentServiceImpl commentService;

    @Value("${doran.ip.address}")
    String ipAddress;

    @GetMapping("/interestedPost")
    ResponseEntity<Map> inquiryInterestedPost(@AuthenticationPrincipal UserDetails userDetails) {
        log.info(userDetails.getUsername());
        log.info(userDetails.getAuthorities().toString());

        String username = userDetails.getUsername();
        Optional<Member> byEmail = memberService.findByEmail(username);
        List<MemberHash> hashByMember = memberHashService.findHashByMember(byEmail.orElseThrow(()->new RuntimeException("userName not found err")));


        List<HashTag> hashTagList = hashByMember.stream()
                .map(m -> m.getHashTagId())
                .collect(Collectors.toList());

        List<Optional<PostHash>> optionalPostHashList = hashTagList.stream()
                .map(hashTag -> postHashService.findTopOfPostHash(hashTag))
                .collect(Collectors.toList());



        HashMap<String, PostResponseDto> stringPostResponseDtoHashMap = new HashMap<>();

        for (Optional<PostHash> optionalPostHash : optionalPostHashList) {
            if (optionalPostHash.isPresent()) {
                PostResponseDto responseDto = PostResponseDto.builder()
                        .backgroundPicUri(
                                optionalPostHash.get().getPostId().getSwitchPic() == ImgType.DefaultBackground ? ipAddress + ":8080/api/background/" + Arrays.stream(optionalPostHash.get().getPostId().getImgName().split("[.]")).collect(Collectors.toList()).get(0)
                                        : ipAddress + ":8080/api/userpic/" + Arrays.stream(optionalPostHash.get().getPostId().getImgName().split("[.]")).collect(Collectors.toList()).get(0)
                        )
                        .location(null)
                        .font(optionalPostHash.get().getPostId().getFont())
                        .fontColor(optionalPostHash.get().getPostId().getFontColor())
                        .fontBold(optionalPostHash.get().getPostId().getFontBold())
                        .postId(optionalPostHash.get().getPostId().getPostId())
                        .contents(optionalPostHash.get().getPostId().getContent())
                        .postTime(optionalPostHash.get().getPostId().getPostTime())
                        .ReplyCnt(commentService.findCommentAndReplyCntByPostId(optionalPostHash.get().getPostId()))
                        .likeCnt(postLikeService.findLIkeCnt(optionalPostHash.get().getPostId())).build();


                stringPostResponseDtoHashMap.put(optionalPostHash.get().getHashTagId().getHashTagName(), responseDto);
            }
        }

        return ResponseEntity.ok(stringPostResponseDtoHashMap);
    }
}
