package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.dto.*;
import com.dorandoran.doranserver.dto.postDetail.CommentDetailDto;
import com.dorandoran.doranserver.dto.postDetail.PostDetailDto;
import com.dorandoran.doranserver.dto.postDetail.ReplyDetailDto;
import com.dorandoran.doranserver.entity.*;
import com.dorandoran.doranserver.entity.imgtype.ImgType;
import com.dorandoran.doranserver.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Tag(name = "글 관련 API",description = "PostController")
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
@Slf4j
public class PostController {
    @Value("${userUpload.Store.path}")
    String userUploadPicServerPath;
    @Value("${background.Store.path}")
    String backgroundPicServerPath;
    @Value("${doran.ip.address}")
    String ipAddress;

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
    private final AnonymityMemberServiceImpl anonymityMemberService;

    @Tag(name = "글 관련 API")
    @Operation(summary = "글 생성", description = "글을 생성하는 API입니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "글 생성, 요청한 사진이 있을 시 사진 저장, 해시 태그 저장 성공"),
                   @ApiResponse(responseCode = "400", description = "사진 저장 실패")
    })
    @PostMapping("/post")
    ResponseEntity<?> Post(@Parameter(description = "글 저장 시 필요한 데이터") PostDto postDto) {
        Optional<Member> memberEmail = memberService.findByEmail(postDto.getEmail());
        Post post = Post.builder()
                .content(postDto.getContent())
                .forMe(postDto.getForMe())
                .postTime(LocalDateTime.now())
                .memberId(memberEmail.get())
                .anonymity(postDto.getAnonymity())
                .font(postDto.getFont())
                .fontColor(postDto.getFontColor())
                .fontSize(postDto.getFontSize())
                .fontBold(postDto.getFontBold())
                .build();

        //location null 처리
        if (postDto.getLocation().isBlank()) {
            post.setLatitude(null);
            post.setLongitude(null);
        } else {
            String[] userLocation = postDto.getLocation().split(",");
            post.setLatitude(Double.valueOf(userLocation[0]));
            post.setLongitude(Double.valueOf(userLocation[1]));
        }

        //파일 처리
        if (postDto.getFile() != null) {
            String fileName = postDto.getFile().getOriginalFilename();
            String fileNameSubstring = fileName.substring(fileName.lastIndexOf(".") + 1);
            String userUploadImgName = UUID.randomUUID() + "." + fileNameSubstring;
            try {
                postDto.getFile().transferTo(new File(userUploadPicServerPath + userUploadImgName));
            }catch (IOException e){
                log.info("IO Exception 발생",e);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            catch (MaxUploadSizeExceededException e){
                log.info("파일 업로드 크기 제한 exception",e);
                return new ResponseEntity<>("파일 업로드 크기 제한", HttpStatus.BAD_REQUEST);
            }
            post.setSwitchPic(ImgType.UserUpload);
            post.setImgName(userUploadImgName);
            UserUploadPic userUploadPic = UserUploadPic
                    .builder()
                    .imgName(userUploadImgName)
                    .serverPath(userUploadPicServerPath + userUploadImgName)
                    .build();
            userUploadPicService.saveUserUploadPic(userUploadPic);
            log.info("사용자 지정 이미지 이름 : {}",fileNameSubstring);

        } else {
            post.setSwitchPic(ImgType.DefaultBackground);
            post.setImgName(postDto.getBackgroundImgName() + ".jpg");
        }

        log.info("{}의 글 생성", memberEmail.get().getNickname());
        postService.savePost(post);

        //HashTag 테이블 생성
        if (postDto.getHashTagName() != null) {
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
     * 댓글 삭제 -> 글 공감 삭제 -> 글 해시 태그 삭제 -> 인기있는 글 삭제 ->글 삭제
     * 삭제하려는 사용자가 본인 글이 아닐 경우 bad request
     * @param postDeleteDto Long postId, String userEmail
     * @return Ok
     */
    @Tag(name = "글 관련 API")
    @Operation(summary = "글 삭제", description = "요청한 글을 삭제하는 API입니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "글 삭제 성공"),
                   @ApiResponse(responseCode = "400", description = "글 작성한 사용자와 삭제 요청한 사용자가 다를 시 실패")
    })
    @Transactional
    @PostMapping("/post-delete")
    public ResponseEntity<?> postDelete(@Parameter(description = "글 삭제 시 필요한 데이터") @RequestBody PostDeleteDto postDeleteDto) throws IOException {
        Optional<Post> post = postService.findSinglePost(postDeleteDto.getPostId());
        List<Comment> commentList = commentService.findCommentByPost(post.get());

        if (post.get().getMemberId().getEmail().equals(postDeleteDto.getUserEmail())) {
            //댓글 삭제
            if (commentList.size() != 0) {
                log.info("글 삭제 전 댓글 삭제");
                for (Comment comments : commentList) {
                    Optional<Comment> comment = commentService.findCommentByCommentId(comments.getCommentId());
                    List<CommentLike> commentLikeList = commentLikeService.findByCommentId(comment.get());
                    List<Reply> replyList = replyService.findReplyList(comment.get());
                    commentService.deleteAllCommentByPost(comment, commentLikeList, replyList);
                    //댓글 삭제
                    commentService.deleteComment(comment.get());
                }
            }

            //글 공감 삭제
            List<PostLike> postLikeList = postLikeService.findByPost(post.get());
            if (postLikeList.size() != 0) {
                log.info("글 삭제 전 글 공감 삭제 로직 실행");
                for (PostLike postLike : postLikeList) {
                    postLikeService.deletePostLike(postLike);
                }
            }

            //해시태그 삭제
            List<PostHash> postHashList = postHashService.findPostHash(post.get());
            if (postHashList.size() != 0) {
                log.info("글 삭제 전 해시태그 삭제 로직 실행");
                for (PostHash postHash : postHashList) {
                    postHashService.deletePostHash(postHash);
                }
            }

            //인기있는 글 삭제
            List<PopularPost> popularPostList = popularPostService.findPopularPostByPost(post.get());
            if (popularPostList.size() != 0){
                log.info("글 삭제 전 인기있는 글 삭제 로직 실행");
                for (PopularPost popularPost : popularPostList) {
                    popularPostService.deletePopularPost(popularPost);
                }
            }

            //사용자 이미지 삭제 (imageName은 이미지 이름)
            if (post.get().getSwitchPic().equals(ImgType.UserUpload)) {
                //window전용
//                Path path = Paths.get("C:\\Users\\thrus\\Downloads\\DoranPic\\" + post.get().getImgName());

                //리눅스
                Path path = Paths.get("home\\jw1010110\\DoranDoranPic\\UserUploadPic\\" + post.get().getImgName());

                log.info("path : {}",path);
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    log.info("사진이 사용중입니다.");
                }
            }

            postService.deletePost(post.get());
        }
        else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Tag(name = "글 관련 API")
    @Operation(summary = "글 공감", description = "요청한 글에 공감을 추가하는 API로 기존에 공감이 돼 있으면 공감을 취소합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "글 공감이나 취소 성공")})
    @PostMapping("/post-like")
    ResponseEntity<?> postLike(@Parameter(description = "글 공감에 필요한 데이터")@RequestBody PostLikeDto postLikeDto) {
        Optional<Post> post = postService.findSinglePost(postLikeDto.getPostId());
        Optional<Member> byEmail = memberService.findByEmail(postLikeDto.getEmail());
        List<PostLike> byMemberId = postLikeService.findByMemberId(postLikeDto.getEmail());
        for (PostLike postLike : byMemberId) {
            if ((postLike.getPostId().getPostId()).equals(postLikeDto.getPostId())) {
                postLikeService.deletePostLike(postLike);
                log.info("{} 글의 공감 취소", postLikeDto.getPostId());
                return new ResponseEntity<>(HttpStatus.OK);
            }
        }
        log.info("{}번 글 좋아요", postLikeDto.getPostId());
        PostLike postLike = PostLike.builder()
                .postId(post.get())
                .memberId(byEmail.get())
                .build();
        postLikeService.savePostLike(postLike);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Tag(name = "글 관련 API")
    @Operation(summary = "글 상세보기", description = "글에 대한 상세정보를 반환하는 API입니다.")
    @ApiResponse(responseCode = "200", description = "글 상세보기 반환 성공",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,schema = @Schema(implementation = PostDetailDto.class)))
    //글 내용, 작성자, 공감수, 위치, 댓글수, 작성 시간, 댓글
    @PostMapping("/post/detail")
    ResponseEntity<?> postDetails(@Parameter(description = "글 상세보기에 필요한 데이터")
                                  @RequestBody PostRequestDetailDto postRequestDetailDto) {
        Optional<Post> post = postService.findSinglePost(postRequestDetailDto.getPostId());
        List<String> anonymityMemberList = anonymityMemberService.findAllUserEmail(post.get());

        //리턴할 postDetail builder
        PostDetailDto postDetailDto = PostDetailDto.builder()
                .content(post.get().getContent())
                .postTime(post.get().getPostTime())
                .postLikeCnt(postLikeService.findLIkeCnt(post.get()))
                .postLikeResult(postLikeService.findLikeResult(postRequestDetailDto.getUserEmail(), post.get()))
                .commentCnt(commentService.findCommentAndReplyCntByPostId(post.get()))
                .postAnonymity(post.get().getAnonymity())
                .postNickname(post.get().getMemberId().getNickname())
                .font(post.get().getFont())
                .fontColor(post.get().getFontColor())
                .fontSize(post.get().getFontSize())
                .fontBold(post.get().getFontBold())
                .build();

        //글의 위치 데이터와 현재 내 위치 거리 계산
        if (postRequestDetailDto.getLocation().isBlank() || post.get().getLatitude()==null || post.get().getLongitude()==null) {
            postDetailDto.setLocation(null);
        } else {
            String[] userLocation = postRequestDetailDto.getLocation().split(",");
            Double distance = distanceService.getDistance(Double.parseDouble(userLocation[0]),
                    Double.parseDouble(userLocation[1]),
                    post.get().getLatitude(),
                    post.get().getLongitude());
            postDetailDto.setLocation((Long.valueOf(Math.round(distance)).intValue()));
        }

        //댓글 builder
        List<Comment> comments = commentService.findFirstComments(post.get());

        List<CommentDetailDto> commentDetailDtoList = new ArrayList<>();
        if (comments.size() != 0) {
            for (Comment comment : comments) {
                Integer commentLikeCnt = commentLikeService.findCommentLikeCnt(comment);
                Boolean commentLikeResult = commentLikeService.findCommentLikeResult(postRequestDetailDto.getUserEmail(), comment);

                //대댓글 10개 저장 로직
                List<Reply> replies = replyService.findFirstReplies(comment);
                List<ReplyDetailDto> replyDetailDtoList = new ArrayList<>();
                for (Reply reply : replies) {
                    ReplyDetailDto replyDetailDto = null;
                    //비밀 대댓글에 따른 저장 로직
                    if (reply.getSecretMode() == Boolean.TRUE) {
                        log.info("{}는 비밀 댓글로직 실행", reply.getReplyId());
                        if (postRequestDetailDto.getUserEmail().equals(post.get().getMemberId().getEmail())) {
                            //글쓴이일 시 비밀댓글 상관없이 모두 조회 가능
                            replyDetailDto = new ReplyDetailDto(reply, reply.getReply());
                            log.info("글쓴이입니다.");
                        } else {
                            //글쓴이가 아닐 시 해당 댓글 작성 사용자만 비밀댓글 조회 가능
                            replyDetailDto =
                                    (reply.getMemberId().getEmail().equals(postRequestDetailDto.getUserEmail()))
                                            ? new ReplyDetailDto(reply, reply.getReply())
                                            : new ReplyDetailDto(reply, "비밀 댓글입니다.");
                            log.info("글쓴이가 아닙니다.");
                        }
                    }else {
                        log.info("{}는 비밀 댓글로직 실행 안함",reply.getReplyId());
                        replyDetailDto = new ReplyDetailDto(reply, reply.getReply());
                    }
                    if (anonymityMemberList.contains(reply.getMemberId().getEmail())) {
                        int replyAnonymityIndex = anonymityMemberList.indexOf(reply.getMemberId().getEmail()) + 1;
                        log.info("{}의 index값은 {}이다", reply.getMemberId().getEmail(), replyAnonymityIndex);
                        replyDetailDto.setReplyAnonymityNickname("익명" + replyAnonymityIndex);
                    }
                    replyDetailDtoList.add(replyDetailDto);
                }
                Collections.reverse(replyDetailDtoList);

                //비밀 댓글에 따른 저장 로직
                CommentDetailDto commentDetailDto = null;
                if (comment.getSecretMode() == Boolean.TRUE) {
                    if (postRequestDetailDto.getUserEmail().equals(post.get().getMemberId().getEmail())) {
                        //글쓴이일 시 비밀댓글 상관없이 모두 조회 가능
                        commentDetailDto = new CommentDetailDto(comment, comment.getComment(), commentLikeCnt, commentLikeResult, replyDetailDtoList);
                    } else {
                        //글쓴이가 아닐 시 해당 댓글 작성 사용자만 비밀댓글 조회 가능
                        commentDetailDto =
                                (comment.getMemberId().getEmail() == postRequestDetailDto.getUserEmail())
                                        ? new CommentDetailDto(comment, comment.getComment(), commentLikeCnt, commentLikeResult, replyDetailDtoList)
                                        : new CommentDetailDto(comment, "비밀 댓글입니다.", commentLikeCnt, commentLikeResult, replyDetailDtoList);
                    }
                }else {
                    commentDetailDto = new CommentDetailDto(comment, comment.getComment(), commentLikeCnt, commentLikeResult, replyDetailDtoList);
                }

                if (anonymityMemberList.contains(comment.getMemberId().getEmail())) {
                    int commentAnonymityIndex = anonymityMemberList.indexOf(comment.getMemberId().getEmail()) + 1;
                    log.info("{}의 index값은 {}이다", comment.getMemberId().getEmail(), commentAnonymityIndex);
                    commentDetailDto.setCommentAnonymityNickname("익명" + commentAnonymityIndex);
                }
                commentDetailDtoList.add(commentDetailDto);
            }
        }
        Collections.reverse(commentDetailDtoList);
        postDetailDto.setCommentDetailDto(commentDetailDtoList);

        //해시태그 builder
        List<String> postHashListDto = new ArrayList<>();
        List<PostHash> postHashList = postHashService.findPostHash(post.get());
        if (postHashList.size() != 0){
            for (PostHash postHash : postHashList) {
                String hashTagName = postHash.getHashTagId().getHashTagName();
                postHashListDto.add(hashTagName);
            }
        }
        postDetailDto.setPostHashes(postHashListDto);

        //배경사진 builder
        String[] split = post.get().getImgName().split("[.]");
        if (post.get().getSwitchPic().equals(ImgType.DefaultBackground)) {
            postDetailDto.setBackgroundPicUri(ipAddress + ":8080/api/background/" + split[0]);
        } else {
            postDetailDto.setBackgroundPicUri(ipAddress + ":8080/api/userpic/" + split[0]);
        }

        return ResponseEntity.ok().body(postDetailDto);
    }
}