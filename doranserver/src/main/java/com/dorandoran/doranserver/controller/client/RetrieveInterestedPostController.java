package com.dorandoran.doranserver.controller.client;

import com.dorandoran.doranserver.dto.PostResponseDto;
import com.dorandoran.doranserver.entity.*;
import com.dorandoran.doranserver.entity.imgtype.ImgType;
import com.dorandoran.doranserver.service.*;
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

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class RetrieveInterestedPostController {

    private final MemberHashService memberHashService;
    private final MemberService memberService;
    private final PostHashService postHashService;
    private final PostLikeService postLikeService;
    private final CommentService commentService;

    @Value("${doran.ip.address}")
    String ipAddress;

    @GetMapping("/interestedPost")
    ResponseEntity<LinkedList<Map>> retrieveInterestedPost(@AuthenticationPrincipal UserDetails userDetails) {
        log.info(userDetails.getUsername());
        log.info(userDetails.getAuthorities().toString());

        String username = userDetails.getUsername();
        Optional<Member> byEmail = memberService.findByEmail(username);
        List<MemberHash> hashByMember = memberHashService.findHashByMember( //즐겨찾기한 해시태그 리스트(맴버해시)
                byEmail.orElseThrow(()->new RuntimeException("userName not found err"))
        );
        log.info("{}",hashByMember);
        List<HashTag> hashTagList = hashByMember.stream() //맴버해시에서 해시태그 id 추출
                .map(m -> m.getHashTagId())
                .collect(Collectors.toList());

        List<Optional<PostHash>> optionalPostHashList = hashTagList.stream()
                .map(hashTag -> postHashService.findTopOfPostHash(hashTag))
                .collect(Collectors.toList());
        log.info("{}",optionalPostHashList);

        HashMap<String, PostResponseDto> stringPostResponseDtoHashMap = new LinkedHashMap<>();
        LinkedList<Map> mapLinkedList = new LinkedList<>();

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
        mapLinkedList.add(stringPostResponseDtoHashMap);

        return ResponseEntity.ok(mapLinkedList);
    }
}
