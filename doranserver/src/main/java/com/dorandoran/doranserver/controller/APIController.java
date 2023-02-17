package com.dorandoran.doranserver.controller;

import ch.qos.logback.core.joran.conditional.IfAction;
import com.dorandoran.doranserver.dto.*;
import com.dorandoran.doranserver.dto.commentdetail.CommentDetailDto;
import com.dorandoran.doranserver.entity.*;
import com.dorandoran.doranserver.entity.imgtype.ImgType;
import com.dorandoran.doranserver.repository.PopularPostRepository;
import com.dorandoran.doranserver.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("api")
@RestController
@Controller
public class APIController {
    @Value("${userUpload.Store.path}")
    String userUploadPicServerPath;
    @Value("${background.Store.path}")
    String backgroundPicServerPath;
    @Value("${background.cnt}")
    Integer backgroundPicCnt;
    @Value("${doran.ip.address}")
    String ipAddress;
    private final SignUpImpl signUp;
    private final PolicyTermsCheckImpl policyTermsCheck;
    private final BackGroundPicServiceImpl backGroundPicService;
    private final MemberServiceImpl memberService;
    private final UserUploadPicServiceImpl userUploadPicService;
    private final PostLikeServiceImpl postLikeService;
    private final HashTagServiceImpl hashTagService;
    private final PostServiceImpl postService;
    private final PostHashServiceImpl postHashService;
    private final CommentServiceImpl commentService;
    private final CommentLikeServiceImpl commentLikeService;
    private final DistanceService distanceService;
    private final ReplyServiceImpl replyService;
    private final PopularPostServiceImpl popularPostService;

    @PostMapping("/check-nickname")
    ResponseEntity<?> CheckNickname(@RequestBody NicknameDto nicknameDto) {
        log.info("nicknameDto.getNickname: {}", nicknameDto.getNickname());
        Optional<Member> nickname = signUp.findByNickname(nicknameDto.getNickname());
        if (nickname.isPresent()) {
            log.info("bad req response");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        log.info("ok response");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //회원가입(출생년도, 닉네임, udid)
    @PostMapping("/signup")
    ResponseEntity<?> SignUp(@RequestBody SignUpDto loginDto) { //파베 토큰, 엑세스 토큰, 디바이스 아디 받아옴

        String KAKAO_USERINFO_REQUEST_URL = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + loginDto.getKakaoAccessToken());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(KAKAO_USERINFO_REQUEST_URL, HttpMethod.GET, request, String.class);
            log.info("response : {}", response);
            log.info("response.getBody() : {}", response.getBody());
            JSONObject jsonObject = new JSONObject(response.getBody());

            JSONObject kakao_account = jsonObject.getJSONObject("kakao_account");
            String email = kakao_account.getString("email");
            log.info("email : {}", email);
            if (memberService.findByEmail(email).isEmpty()) {
                PolicyTerms policyTerms = PolicyTerms.builder().policy1(true)
                        .policy2(true)
                        .policy3(true)
                        .build();

                policyTermsCheck.policyTermsSave(policyTerms);


                Member member = Member.builder().dateOfBirth(loginDto.getDateOfBirth())
                        .signUpDate(LocalDateTime.now())
                        .firebaseToken(loginDto.getFirebaseToken())
                        .nickname(loginDto.getNickName())
                        .policyTermsId(policyTerms)
                        .email(email).build();

                signUp.saveMember(member);

                return new ResponseEntity<>(member.getEmail(), HttpStatus.OK);
            }

        } catch (HttpClientErrorException e) {
            log.error("access token err : {}", e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/background/maxcount")
    ResponseEntity<Integer> backgroundPic() {
        return ResponseEntity.ok().body(backgroundPicCnt);

    }


    @GetMapping("/background/{picName}")
    ResponseEntity<Resource> eachBackground(@PathVariable Long picName) throws MalformedURLException {

        Optional<BackgroundPic> backgroundPic = backGroundPicService.getBackgroundPic(picName);
        if (backgroundPic.isPresent()) {
            UrlResource urlResource = new UrlResource("file:" + backgroundPic.get().getServerPath());
            log.info("{}", backgroundPic.get().getBackgroundPicId());
            log.info("{}", backgroundPic.get().getImgName());
            log.info("{}", backgroundPic.get().getServerPath());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + backgroundPic.get().getImgName() + "\"")
                    .body(urlResource);
        } else {
            throw new RuntimeException("해당 사진이 존재하지 않습니다.");
        }
    }

    @GetMapping("/userpic/{picName}")
    ResponseEntity<Resource> findUserUploadPic(@PathVariable String picName) {

        try {
            UserUploadPic userUploadPic = userUploadPicService.findUserUploadPicByName(picName);
            UrlResource urlResource = new UrlResource("file:" + userUploadPic.getServerPath());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + userUploadPic.getImgName() + "\"")
                    .body(urlResource);
        } catch (Exception e) {
            log.error("{}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/post")
    ResponseEntity<?> Post(PostDto postDto) throws IOException {

        Optional<Member> memberEmail = memberService.findByEmail(postDto.getEmail());

        log.info("postDto : {}", postDto);
        log.info("{}의 글 생성", memberEmail.get().getNickname());
        Post post = Post.builder()
                .content(postDto.getContent())
                .forMe(postDto.getForMe())
                .postTime(LocalDateTime.now())
                .memberId(memberEmail.get())
                .build();

        //location null 처리
        if (postDto.getLocation() == null) {
            post.setLocation("");
        } else {
            post.setLocation(postDto.getLocation());
        }

        //파일 처리
        if (postDto.getFile() != null) {
            log.info("사용자 지정 이미지 생성");
            String fileName = postDto.getFile().getName();
            String fileNameSubstring = fileName.substring(fileName.lastIndexOf(".") + 1);
            String userUploadImgName = UUID.randomUUID() + fileNameSubstring;
            post.setSwitchPic(ImgType.UserUpload);
            post.setImgName(userUploadImgName);
            UserUploadPic userUploadPic = UserUploadPic
                    .builder()
                    .imgName(userUploadImgName)
                    .serverPath(userUploadPicServerPath + userUploadImgName)
                    .build();
            userUploadPicService.saveUserUploadPic(userUploadPic);
            postDto.getFile().transferTo(new File(userUploadPicServerPath + userUploadImgName));
        } else {
            post.setSwitchPic(ImgType.DefaultBackground);
            post.setImgName(postDto.getBackgroundImgName() + ".jpg");
        }

        postService.savePost(post);

        //HashTag 테이블 생성
        if (postDto.getHashTagName().isEmpty()) {
            Optional<Post> hashTagPost = postService.findSinglePost(post.getPostId());
            for (String hashTag : postDto.getHashTagName()) {

                log.info("해시태그 존재");

                HashTag buildHashTag = HashTag.builder()
                        .hashTagName(hashTag)
                        .hashTagCount(1L)
                        .build();
                if (hashTagService.duplicateCheckHashTag(hashTag)) {
                    hashTagService.saveHashTag(buildHashTag);
                    savePostHash(hashTagPost, hashTag);
                    log.info("해시태그 {}", hashTag + " 생성");
                } else {
                    Optional<HashTag> byHashTagName = hashTagService.findByHashTagName(hashTag);
                    if (byHashTagName.isPresent()) {
                        Long hashTagCount = byHashTagName.get().getHashTagCount();
                        byHashTagName.get().setHashTagCount(hashTagCount + 1);
                        hashTagService.saveHashTag(byHashTagName.get());
                        savePostHash(hashTagPost, hashTag);
                        log.info("해시태그 {}", hashTag + "의 카운트 1증가");
                    }
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void savePostHash(Optional<Post> hashTagPost, String hashTag) {
        Optional<HashTag> byHashTagName = hashTagService.findByHashTagName(hashTag);
        if (byHashTagName.isPresent()) {
            PostHash postHash = PostHash.builder()
                    .postId(hashTagPost.get())
                    .hashTagId(byHashTagName.get())
                    .build();
            postHashService.savePostHash(postHash);
        }
    }


    /**
     * @param postLikeDto String email, Long postId
     *                    {"email" : "사용자 ID", "postId" : 글ID}
     * @return
     */
    @PostMapping("/post-like")
    ResponseEntity<?> postLike(@RequestBody PostLikeDto postLikeDto) {
        Optional<Post> post = postService.findSinglePost(postLikeDto.getPostId());
        Optional<Member> byEmail = memberService.findByEmail(postLikeDto.getEmail());
        Optional<List<PostLike>> byMemberId = postLikeService.findByMemberId(postLikeDto.getEmail());
        if (byMemberId.isPresent()) {
            for (PostLike postLike : byMemberId.get()) {
                if ((postLike.getPostId().getPostId()).equals(postLikeDto.getPostId())) {
                    postLikeService.deletePostLike(postLike);
                    log.info("{} 글의 공감 취소", postLikeDto.getPostId());
                    return new ResponseEntity<>(HttpStatus.OK);
                }
            }
        } else {
            PostLike postLike = PostLike.builder()
                    .postId(post.get())
                    .memberId(byEmail.get())
                    .build();
            postLikeService.savePostLike(postLike);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //글 내용, 작성자, 공감수, 위치, 댓글수, 작성 시간, 댓글
    @GetMapping("/post/detail")
    ResponseEntity<?> postDetails(@RequestParam Long postId, @RequestParam String userEmail, @RequestParam String location) {
        Optional<Post> post = postService.findSinglePost(postId);

        //리턴할 postDetail builder
        PostDetailDto postDetailDto = PostDetailDto.builder()
                .content(post.get().getContent())
                .postTime(post.get().getPostTime())
                .postLikeCnt(postLikeService.findLIkeCnt(post.get()))
                .postLikeResult(postLikeService.findLikeResult(userEmail, post.get()))
                .commentCnt(commentService.findCommentAndReplyCntByPostId(post.get()))
                .build();

        //글의 위치 데이터와 현재 내 위치 거리 계산
        if (Objects.equals(post.get().getLocation(), "")) {
            postDetailDto.setLocation(null);
        } else {
            String[] userLocation = location.split(",");
            String[] postLocation = post.get().getLocation().split(",");
            Double distance = distanceService.getDistance(Double.parseDouble(userLocation[0]),
                    Double.parseDouble(userLocation[1]),
                    Double.parseDouble(postLocation[0]),
                    Double.parseDouble(postLocation[1]));
            postDetailDto.setLocation((Long.valueOf(Math.round(distance)).intValue()));
        }

        //댓글 builder
        List<Comment> commentList = commentService.findCommentByPost(post.get());
        List<CommentDetailDto> commentDetailDtoList = new ArrayList<>();
        if (commentList.size() != 0) {
            for (Comment comment : commentList) {
                CommentDetailDto build = CommentDetailDto.builder()
                        .commentId(comment.getCommentId())
                        .comment(comment.getComment())
                        .commentLike(commentLikeService.findCommentLikeCnt(comment.getCommentId()))
                        .replies(replyService.findReplyList(comment))
                        .commentLikeResult(Boolean.FALSE)
                        .build();
                for (CommentLike commentLike : commentLikeService.findCommentLikeListByCommentId(comment.getCommentId())) {
                    if (commentLike.getMemberId().getEmail().equals(userEmail))
                        build.setCommentLikeResult(Boolean.TRUE);
                }
                commentDetailDtoList.add(build);
            }
        }
        postDetailDto.setCommentDetailDto(commentDetailDtoList);

        //해시태그 builder
        List<String> postHashListDto = new ArrayList<>();
        Optional<List<PostHash>> postHashList = postHashService.findPostHash(postId);
        if (postHashList.isPresent()) {
            for (PostHash postHash : postHashList.get()) {
                String hashTagName = postHash.getHashTagId().getHashTagName();
                postHashListDto.add(hashTagName);
            }
        }
        postDetailDto.setPostHashes(postHashListDto);

        //배경사진 builder
        if (post.get().getSwitchPic().equals(ImgType.DefaultBackground)) {
            postDetailDto.setBackgroundPicUri(backgroundPicServerPath + post.get().getImgName());
        } else {
            postDetailDto.setBackgroundPicUri(userUploadPicServerPath + post.get().getImgName());
        }

        return ResponseEntity.ok().body(postDetailDto);
    }

    @PostMapping("/comment")
    ResponseEntity<?> comment(@RequestBody CommentDto commentDto) {
        Optional<Member> member = memberService.findByEmail(commentDto.getEmail());
        Optional<Post> post = postService.findSinglePost(commentDto.getPostId());
        log.info("사용자 {}의 댓글 작성", commentDto.getEmail());
        Comment comment = Comment.builder()
                .comment(commentDto.getComment())
                .commentTime(LocalDateTime.now())
                .postId(post.get())
                .memberId(member.get())
                .build();
        commentService.saveComment(comment);
        Optional<Post> singlePost = postService.findSinglePost(commentDto.getPostId());
        if (singlePost.isPresent()) {
            List<Comment> commentByPost = commentService.findCommentByPost(singlePost.get());
            if (commentByPost.size() >= 10) {
                PopularPost build = PopularPost.builder().postId(singlePost.get()).build();
                popularPostService.savePopularPost(build);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/comment-like")
    ResponseEntity<?> commentLike(@RequestBody CommentLikeDto commentLikeDto) {
        Optional<Comment> comment = commentService.findCommentByCommentId(commentLikeDto.getCommentId());
        Optional<Member> member = memberService.findByEmail(commentLikeDto.getUserEmail());
        CommentLike commentLikeBuild = CommentLike.builder()
                .commentId(comment.get())
                .memberId(member.get())
                .build();

        List<CommentLike> commentLikeList = commentLikeService.findByCommentId(comment.get());
        if (commentLikeList.size() != 0) {
            for (CommentLike commentLike : commentLikeList) {
                if (commentLike.getMemberId().getEmail().equals(commentLikeDto.getUserEmail())) {
                    commentLikeService.deleteCommentLike(commentLike);
                    log.info("{} 글의 {} 댓글 공감 취소", commentLikeDto.getPostId(), commentLike.getCommentId().getCommentId());
                    return new ResponseEntity<>(HttpStatus.OK);
                }
            }
        }
        commentLikeService.saveCommentLike(commentLikeBuild);
        log.info("{} 글의 {} 댓글 공감", commentLikeDto.getPostId(), comment.get().getCommentId());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/post")
//이메일 . 들어가서 수정 필요
    ResponseEntity<?> inquirePost(@RequestParam String userEmail, @RequestParam Long postCnt, @RequestParam String location) {
        ArrayList<PostResponseDto> postResponseDtoList = new ArrayList<>();
        PostResponseDto.PostResponseDtoBuilder builder = PostResponseDto.builder();

        if (postCnt == 0) { //first find
            List<Post> firstPost = postService.findFirstPost();
            return makePostResponseList(userEmail, postResponseDtoList, builder, firstPost, location);
        } else {
            List<Post> postList = postService.findPost(postCnt);
            return makePostResponseList(userEmail, postResponseDtoList, builder, postList, location);
        }
    }

    private ResponseEntity<?> makePostResponseList(String userEmail,
                                                   ArrayList<PostResponseDto> postResponseDtoList,
                                                   PostResponseDto.PostResponseDtoBuilder builder,
                                                   List<Post> postList,
                                                   String location) {
        for (Post post : postList) {
            Integer lIkeCnt = postLikeService.findLIkeCnt(post);
            Integer commentCntByPostId = commentService.findCommentAndReplyCntByPostId(post);
            if (location.isBlank() || post.getLocation().isBlank()) { //사용자 위치가 "" 거리 계산 안해서 리턴
                builder.location(null)
                        .postId(post.getPostId())
                        .contents(post.getContent())
                        .postTime(post.getPostTime())
                        .ReplyCnt(commentCntByPostId)
                        .likeCnt(lIkeCnt);
            } else {
                String[] userLocation = location.split(",");
                String[] postLocation = post.getLocation().split(",");

                Double distance = distanceService.getDistance(Double.parseDouble(userLocation[0]),
                        Double.parseDouble(userLocation[1]),
                        Double.parseDouble(postLocation[0]),
                        Double.parseDouble(postLocation[1]));

                builder.postId(post.getPostId())
                        .contents(post.getContent())
                        .postTime(post.getPostTime())
                        .location(Long.valueOf(Math.round(distance)).intValue())
                        .ReplyCnt(commentCntByPostId)
                        .likeCnt(lIkeCnt);
            }

            if (post.getSwitchPic() == ImgType.UserUpload) {
                String[] split = post.getImgName().split("[.]");
                builder.backgroundPicUri(ipAddress + ":8080/api/userpic/" + split[0]);
            } else {
                String[] split = post.getImgName().split("[.]");
                builder.backgroundPicUri(ipAddress + ":8080/api/background/" + split[0]);
            }

            builder.likeResult(postLikeService.findLikeResult(userEmail, post));
            postResponseDtoList.add(builder.build());
        }
        return ResponseEntity.ok().body(postResponseDtoList);
    }

    @PostMapping("/check/registered")
    ResponseEntity<?> checkRegisteredMember(@RequestBody CheckRegisteredMemberDto memberDto) {
        Optional<Member> byEmail = memberService.findByEmail(memberDto.getEmail());
        if (byEmail.isPresent()) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/post/popular")
    ResponseEntity<?> inquirePopularPost(@RequestParam String userEmail, @RequestParam Long postCnt, @RequestParam String location){
        log.info("{}",userEmail);
        log.info("{}",postCnt);
        log.info("{}",location);
        ArrayList<PostResponseDto> postResponseDtoList = new ArrayList<>();
        PostResponseDto.PostResponseDtoBuilder builder = PostResponseDto.builder();

        if (postCnt == 0) { //first find
            log.info("조건문0");
            List<PopularPost> firstPost = popularPostService.findFirstPopularPost();
            log.info("{}",firstPost.size());
            return makePopularPostResponseList(userEmail, postResponseDtoList, builder, firstPost, location);
        } else {
            log.info("조건문 else");
            List<PopularPost> postList = popularPostService.findPopularPost(postCnt);
            log.info("{}",postList.size());
            return makePopularPostResponseList(userEmail, postResponseDtoList, builder, postList, location);
        }
    }

    private ResponseEntity<?> makePopularPostResponseList(String userEmail,
                                                   ArrayList<PostResponseDto> postResponseDtoList,
                                                   PostResponseDto.PostResponseDtoBuilder builder,
                                                   List<PopularPost> postList,
                                                   String location) {
        for (PopularPost popularPost : postList) {
            Integer lIkeCnt = postLikeService.findLIkeCnt(popularPost.getPostId());
            Integer commentCntByPostId = commentService.findCommentAndReplyCntByPostId(popularPost.getPostId());
            if (location.isBlank() || popularPost.getPostId().getLocation().isBlank()) { //사용자 위치가 "" 거리 계산 안해서 리턴
                builder.location(null)
                        .postId(popularPost.getPostId().getPostId())
                        .contents(popularPost.getPostId().getContent())
                        .postTime(popularPost.getPostId().getPostTime())
                        .ReplyCnt(commentCntByPostId)
                        .likeCnt(lIkeCnt);
            } else {
                String[] userLocation = location.split(",");
                String[] postLocation = popularPost.getPostId().getLocation().split(",");

                Double distance = distanceService.getDistance(Double.parseDouble(userLocation[0]),
                        Double.parseDouble(userLocation[1]),
                        Double.parseDouble(postLocation[0]),
                        Double.parseDouble(postLocation[1]));

                builder.postId(popularPost.getPostId().getPostId())
                        .contents(popularPost.getPostId().getContent())
                        .postTime(popularPost.getPostId().getPostTime())
                        .location(Long.valueOf(Math.round(distance)).intValue())
                        .ReplyCnt(commentCntByPostId)
                        .likeCnt(lIkeCnt);
            }

            if (popularPost.getPostId().getSwitchPic() == ImgType.UserUpload) {
                String[] split = popularPost.getPostId().getImgName().split("[.]");
                builder.backgroundPicUri(ipAddress + ":8080/api/userpic/" + split[0]);
            } else {
                String[] split = popularPost.getPostId().getImgName().split("[.]");
                builder.backgroundPicUri(ipAddress + ":8080/api/background/" + split[0]);
            }

            builder.likeResult(postLikeService.findLikeResult(userEmail, popularPost.getPostId()));
            postResponseDtoList.add(builder.build());
            log.info("size: {}",postResponseDtoList.size());
        }
        return ResponseEntity.ok().body(postResponseDtoList);
    }
}