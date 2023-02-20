package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.dto.PostDeleteDto;
import com.dorandoran.doranserver.dto.PostDetailDto;
import com.dorandoran.doranserver.dto.PostDto;
import com.dorandoran.doranserver.dto.PostLikeDto;
import com.dorandoran.doranserver.dto.commentdetail.CommentDetailDto;
import com.dorandoran.doranserver.entity.*;
import com.dorandoran.doranserver.entity.imgtype.ImgType;
import com.dorandoran.doranserver.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("api")
@RestController
@Slf4j
public class PostController {
    @Value("${userUpload.Store.path}")
    String userUploadPicServerPath;
    @Value("${background.Store.path}")
    String backgroundPicServerPath;

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
            String fileName = postDto.getFile().getOriginalFilename();
            String fileNameSubstring = fileName.substring(fileName.lastIndexOf(".") + 1);
            log.info("이미지 이름 : {}",fileNameSubstring);
            String userUploadImgName = UUID.randomUUID() + "." + fileNameSubstring;
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
    @PostMapping("/post-delete")
    public ResponseEntity<?> postDelete(@RequestBody PostDeleteDto postDeleteDto) throws IOException {
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
                Path path = Paths.get("C:\\Users\\thrus\\Downloads\\DoranPic\\" + post.get().getImgName());

                /*리눅스 되나..?
                Path path = Paths.get("home\\jw1010110\\DoranDoranPic\\UserUploadPic\\" + post.get().getImgName());*/

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

    @PostMapping("/post-like")
    ResponseEntity<?> postLike(@RequestBody PostLikeDto postLikeDto) {
        Optional<Post> post = postService.findSinglePost(postLikeDto.getPostId());
        Optional<Member> byEmail = memberService.findByEmail(postLikeDto.getEmail());
        List<PostLike> byMemberId = postLikeService.findByMemberId(postLikeDto.getEmail());
        if (byMemberId.size() != 0) {
            for (PostLike postLike : byMemberId) {
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
        List<PostHash> postHashList = postHashService.findPostHash(post.get());
        if (postHashList.size() != 0){
            for (PostHash postHash : postHashList) {
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

}
