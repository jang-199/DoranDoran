package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.dto.RetrieveInterestedDto;
import com.dorandoran.doranserver.entity.*;
import com.dorandoran.doranserver.entity.imgtype.ImgType;
import com.dorandoran.doranserver.service.*;
import io.micrometer.core.annotation.Timed;
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

@Timed
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
    private final BlockMemberFilter blockMemberFilter;
    private final MemberBlockListService memberBlockListService;

    @Value("${doran.ip.address}")
    String ipAddress;

    @GetMapping("/post/interested")
    ResponseEntity<LinkedList<Map>> retrieveInterestedPost(@AuthenticationPrincipal UserDetails userDetails) {
        log.info(userDetails.getUsername());
        log.info(userDetails.getAuthorities().toString());

        String username = userDetails.getUsername();
        Member byEmail = memberService.findByEmail(username);
        List<MemberHash> hashByMember = memberHashService.findHashByMember(byEmail); //즐겨찾기한 해시태그 리스트(맴버해시)
        log.info("{}",hashByMember);
        List<HashTag> hashTagList = hashByMember.stream() //맴버해시에서 해시태그 id 추출
                .map(m -> m.getHashTagId())
                .collect(Collectors.toList());

        List<MemberBlockList> memberBlockListByBlockingMember = memberBlockListService.findMemberBlockListByBlockingMember(byEmail);

        List<Optional<PostHash>> optionalPostHashList = hashTagList.stream()
                .map(hashTag -> postHashService.findTopOfPostHash(hashTag))
                .collect(Collectors.toList());
        List<Optional<PostHash>> optionalPostHashFilter = blockMemberFilter.optionalPostHashFilter(optionalPostHashList, memberBlockListByBlockingMember);

        HashMap<String, RetrieveInterestedDto.ReadInterestedResponse> stringPostResponseDtoHashMap = new LinkedHashMap<>();
        LinkedList<Map> mapLinkedList = new LinkedList<>();

        for (Optional<PostHash> optionalPostHash : optionalPostHashFilter) {
            if (optionalPostHash.isPresent()) {
                if (!optionalPostHash.get().getPostId().getMemberId().equals(byEmail) && optionalPostHash.get().getPostId().getForMe()==true) {
                    continue;
                }
                RetrieveInterestedDto.ReadInterestedResponse responseDto = RetrieveInterestedDto.ReadInterestedResponse.builder()
                        .backgroundPicUri(
                                optionalPostHash.get().getPostId().getSwitchPic() == ImgType.DefaultBackground ? ipAddress + ":8080/api/background/" + Arrays.stream(optionalPostHash.get().getPostId().getImgName().split("[.]")).collect(Collectors.toList()).get(0)
                                        : ipAddress + ":8080/api/userpic/" + Arrays.stream(optionalPostHash.get().getPostId().getImgName().split("[.]")).collect(Collectors.toList()).get(0)
                        )
                        .location(null)
                        .isWrittenByMember(optionalPostHash.get().getPostId().getMemberId().getEmail().equals(userDetails.getUsername()) ? Boolean.TRUE : Boolean.FALSE)
                        .font(optionalPostHash.get().getPostId().getFont())
                        .fontSize(optionalPostHash.get().getPostId().getFontSize())
                        .likeResult(postLikeService.findLikeResult(userDetails.getUsername(),optionalPostHash.get().getPostId()))
                        .fontColor(optionalPostHash.get().getPostId().getFontColor())
                        .fontBold(optionalPostHash.get().getPostId().getFontBold())
                        .postId(optionalPostHash.get().getPostId().getPostId())
                        .contents(optionalPostHash.get().getPostId().getContent())
                        .postTime(optionalPostHash.get().getPostId().getPostTime())
                        .replyCnt(commentService.findCommentAndReplyCntByPostId(optionalPostHash.get().getPostId()))
                        .likeCnt(postLikeService.findLIkeCnt(optionalPostHash.get().getPostId())).build();


                stringPostResponseDtoHashMap.put(optionalPostHash.get().getHashTagId().getHashTagName(), responseDto);
            }
        }
        mapLinkedList.add(stringPostResponseDtoHashMap);

        return ResponseEntity.ok(mapLinkedList);
    }
}
