package com.dorandoran.doranserver.domain.api.post.controller;

import com.dorandoran.doranserver.domain.api.comment.service.CommentService;
import com.dorandoran.doranserver.domain.api.member.service.MemberBlockListService;
import com.dorandoran.doranserver.domain.api.member.service.MemberHashService;
import com.dorandoran.doranserver.domain.api.member.service.MemberService;
import com.dorandoran.doranserver.domain.api.post.dto.RetrieveInterestedDto;
import com.dorandoran.doranserver.domain.api.hashtag.domain.HashTag;
import com.dorandoran.doranserver.domain.api.member.domain.Member;
import com.dorandoran.doranserver.domain.api.member.domain.MemberHash;
import com.dorandoran.doranserver.domain.api.hashtag.domain.PostHash;
import com.dorandoran.doranserver.domain.api.post.service.PostHashService;
import com.dorandoran.doranserver.domain.api.post.service.PostLikeService;
import com.dorandoran.doranserver.domain.api.background.domain.imgtype.ImgType;
import com.dorandoran.doranserver.global.util.BlockMemberFilter;
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
    ResponseEntity<LinkedList<HashMap<String,RetrieveInterestedDto.ReadInterestedResponse>>>
    retrieveInterestedPost(@AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        Member member = memberService.findByEmail(username);
        List<MemberHash> hashByMember = memberHashService.findHashByMember(member); //즐겨찾기한 해시태그 리스트(맴버해시)
        List<HashTag> hashTagList = hashByMember.stream() //맴버해시에서 해시태그 id 추출
                .map(MemberHash::getHashTagId)
                .toList();

        List<Member> memberBlockListByBlockingMember = memberBlockListService.findMemberBlockListByBlockingMember(member);

        List<Optional<PostHash>> optionalPostHashList = hashTagList.stream()
                .map(hashTag -> postHashService.findTopOfPostHash(hashTag, memberBlockListByBlockingMember))
                .toList();

        HashMap<String, RetrieveInterestedDto.ReadInterestedResponse> stringPostResponseDtoHashMap = new LinkedHashMap<>();
        LinkedList<HashMap<String,RetrieveInterestedDto.ReadInterestedResponse>> mapLinkedList = new LinkedList<>();

        for (Optional<PostHash> optionalPostHash : optionalPostHashList) {
            if (optionalPostHash.isPresent()) {
                if (!optionalPostHash.get().getPostId().getMemberId().equals(member) && optionalPostHash.get().getPostId().getForMe()==Boolean.TRUE) {
                    continue;
                }
                RetrieveInterestedDto.ReadInterestedResponse responseDto = RetrieveInterestedDto.ReadInterestedResponse.builder()
                        .backgroundPicUri(
                                optionalPostHash.get().getPostId().getSwitchPic() == ImgType.DefaultBackground ? ipAddress + ":8080/api/pic/default/" + Arrays.stream(optionalPostHash.get().getPostId().getImgName().split("[.]")).toList().get(0)
                                        : ipAddress + ":8080/api/pic/member/" + Arrays.stream(optionalPostHash.get().getPostId().getImgName().split("[.]")).toList().get(0)
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
