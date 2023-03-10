package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.dto.*;
import com.dorandoran.doranserver.dto.postDetail.CommentDetailDto;
import com.dorandoran.doranserver.dto.postDetail.PostDetailDto;
import com.dorandoran.doranserver.dto.postDetail.ReplyDetailDto;
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
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import javax.naming.SizeLimitExceededException;
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

    @PostMapping("/post")
    ResponseEntity<?> Post(PostDto postDto) {
        Optional<Member> memberEmail = memberService.findByEmail(postDto.getEmail());
        Post post = Post.builder()
                .content(postDto.getContent())
                .forMe(postDto.getForMe())
                .postTime(LocalDateTime.now())
                .memberId(memberEmail.get())
                .font(postDto.getFont())
                .fontColor(postDto.getFontColor())
                .fontSize(postDto.getFontSize())
                .fontBold(postDto.getFondBold())
                .build();

        //location null ??????
        if (postDto.getLocation().isBlank()) {
            post.setLatitude("");
            post.setLongitude("");
        } else {
            String[] userLocation = postDto.getLocation().split(",");
            post.setLatitude(userLocation[0]);
            post.setLongitude(userLocation[1]);
        }

        //?????? ??????
        if (postDto.getFile() != null) {
            String fileName = postDto.getFile().getOriginalFilename();
            String fileNameSubstring = fileName.substring(fileName.lastIndexOf(".") + 1);
            String userUploadImgName = UUID.randomUUID() + "." + fileNameSubstring;
            try {
                postDto.getFile().transferTo(new File(userUploadPicServerPath + userUploadImgName));
            }catch (Exception e){
                log.info("????????? ?????? ?????? exception ??????",e);
            }
            post.setSwitchPic(ImgType.UserUpload);
            post.setImgName(userUploadImgName);
            UserUploadPic userUploadPic = UserUploadPic
                    .builder()
                    .imgName(userUploadImgName)
                    .serverPath(userUploadPicServerPath + userUploadImgName)
                    .build();
            userUploadPicService.saveUserUploadPic(userUploadPic);
            log.info("????????? ?????? ????????? ?????? : {}",fileNameSubstring);

        } else {
            post.setSwitchPic(ImgType.DefaultBackground);
            post.setImgName(postDto.getBackgroundImgName() + ".jpg");
        }

        log.info("{}??? ??? ??????", memberEmail.get().getNickname());
        postService.savePost(post);

        //HashTag ????????? ??????
        if (postDto.getHashTagName() != null) {
            Optional<Post> hashTagPost = postService.findSinglePost(post.getPostId());
            for (String hashTag : postDto.getHashTagName()) {

                log.info("???????????? ??????");

                HashTag buildHashTag = HashTag.builder()
                        .hashTagName(hashTag)
                        .hashTagCount(1L)
                        .build();
                if (hashTagService.duplicateCheckHashTag(hashTag)) {
                    hashTagService.saveHashTag(buildHashTag);
                    savePostHash(hashTagPost, hashTag);
                    log.info("???????????? {}", hashTag + " ??????");
                } else {
                    Optional<HashTag> byHashTagName = hashTagService.findByHashTagName(hashTag);
                    if (byHashTagName.isPresent()) {
                        Long hashTagCount = byHashTagName.get().getHashTagCount();
                        byHashTagName.get().setHashTagCount(hashTagCount + 1);
                        hashTagService.saveHashTag(byHashTagName.get());
                        savePostHash(hashTagPost, hashTag);
                        log.info("???????????? {}", hashTag + "??? ????????? 1??????");
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
     * ?????? ?????? -> ??? ?????? ?????? -> ??? ?????? ?????? ?????? -> ???????????? ??? ?????? ->??? ??????
     * ??????????????? ???????????? ?????? ?????? ?????? ?????? bad request
     * @param postDeleteDto Long postId, String userEmail
     * @return Ok
     */
    @PostMapping("/post-delete")
    public ResponseEntity<?> postDelete(@RequestBody PostDeleteDto postDeleteDto) throws IOException {
        Optional<Post> post = postService.findSinglePost(postDeleteDto.getPostId());
        List<Comment> commentList = commentService.findCommentByPost(post.get());

        if (post.get().getMemberId().getEmail().equals(postDeleteDto.getUserEmail())) {
            //?????? ??????
            if (commentList.size() != 0) {
                log.info("??? ?????? ??? ?????? ??????");
                for (Comment comments : commentList) {
                    Optional<Comment> comment = commentService.findCommentByCommentId(comments.getCommentId());
                    List<CommentLike> commentLikeList = commentLikeService.findByCommentId(comment.get());
                    List<Reply> replyList = replyService.findReplyList(comment.get());
                    commentService.deleteAllCommentByPost(comment, commentLikeList, replyList);
                    //?????? ??????
                    commentService.deleteComment(comment.get());
                }
            }

            //??? ?????? ??????
            List<PostLike> postLikeList = postLikeService.findByPost(post.get());
            if (postLikeList.size() != 0) {
                log.info("??? ?????? ??? ??? ?????? ?????? ?????? ??????");
                for (PostLike postLike : postLikeList) {
                    postLikeService.deletePostLike(postLike);
                }
            }

            //???????????? ??????
            List<PostHash> postHashList = postHashService.findPostHash(post.get());
            if (postHashList.size() != 0) {
                log.info("??? ?????? ??? ???????????? ?????? ?????? ??????");
                for (PostHash postHash : postHashList) {
                    postHashService.deletePostHash(postHash);
                }
            }

            //???????????? ??? ??????
            List<PopularPost> popularPostList = popularPostService.findPopularPostByPost(post.get());
            if (popularPostList.size() != 0){
                log.info("??? ?????? ??? ???????????? ??? ?????? ?????? ??????");
                for (PopularPost popularPost : popularPostList) {
                    popularPostService.deletePopularPost(popularPost);
                }
            }

            //????????? ????????? ?????? (imageName??? ????????? ??????)
            if (post.get().getSwitchPic().equals(ImgType.UserUpload)) {
                //window??????
                Path path = Paths.get("C:\\Users\\thrus\\Downloads\\DoranPic\\" + post.get().getImgName());

                /*????????? ??????..?
                Path path = Paths.get("home\\jw1010110\\DoranDoranPic\\UserUploadPic\\" + post.get().getImgName());*/

                log.info("path : {}",path);
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    log.info("????????? ??????????????????.");
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
        for (PostLike postLike : byMemberId) {
            if ((postLike.getPostId().getPostId()).equals(postLikeDto.getPostId())) {
                postLikeService.deletePostLike(postLike);
                log.info("{} ?????? ?????? ??????", postLikeDto.getPostId());
                return new ResponseEntity<>(HttpStatus.OK);
            }
        }
        log.info("{}??? ??? ?????????", postLikeDto.getPostId());
        PostLike postLike = PostLike.builder()
                .postId(post.get())
                .memberId(byEmail.get())
                .build();
        postLikeService.savePostLike(postLike);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    //??? ??????, ?????????, ?????????, ??????, ?????????, ?????? ??????, ??????
    @PostMapping("/post/detail")
    ResponseEntity<?> postDetails(@RequestBody PostRequestDetailDto postRequestDetailDto) {
        Optional<Post> post = postService.findSinglePost(postRequestDetailDto.getPostId());

        //????????? postDetail builder
        PostDetailDto postDetailDto = PostDetailDto.builder()
                .content(post.get().getContent())
                .postTime(post.get().getPostTime())
                .postLikeCnt(postLikeService.findLIkeCnt(post.get()))
                .postLikeResult(postLikeService.findLikeResult(postRequestDetailDto.getUserEmail(), post.get()))
                .commentCnt(commentService.findCommentAndReplyCntByPostId(post.get()))
                .font(post.get().getFont())
                .fontColor(post.get().getFontColor())
                .fontSize(post.get().getFontSize())
                .fondBold(post.get().getFontBold())
                .postNickname(post.get().getMemberId().getNickname())
                .build();

        //?????? ?????? ???????????? ?????? ??? ?????? ?????? ??????
        if (postRequestDetailDto.getLocation().isBlank() || post.get().getLatitude().isBlank() || post.get().getLongitude().isBlank()) {
            postDetailDto.setLocation(null);
        } else {
            String[] userLocation = postRequestDetailDto.getLocation().split(",");
            Double distance = distanceService.getDistance(Double.parseDouble(userLocation[0]),
                    Double.parseDouble(userLocation[1]),
                    Double.parseDouble(post.get().getLatitude()),
                    Double.parseDouble(post.get().getLongitude()));
            postDetailDto.setLocation((Long.valueOf(Math.round(distance)).intValue()));
        }

        //?????? builder
        List<Comment> commentList = commentService.findCommentByPost(post.get());

        List<CommentDetailDto> commentDetailDtoList = new ArrayList<>();
        if (commentList.size() != 0) {
            for (Comment comment : commentList) {
                CommentDetailDto build = CommentDetailDto.builder()
                        .commentId(comment.getCommentId())
                        .comment(comment.getComment())
                        .commentLike(commentLikeService.findCommentLikeCnt(comment))
                        .commentLikeResult(Boolean.FALSE)
                        .commentNickname(comment.getMemberId().getNickname())
                        .commentTime(comment.getCommentTime())
                        .build();
                for (CommentLike commentLike : commentLikeService.findCommentLikeListByCommentId(comment)) {
                    if (commentLike.getMemberId().getEmail().equals(postRequestDetailDto.getUserEmail()))
                        build.setCommentLikeResult(Boolean.TRUE);
                }
                List<ReplyDetailDto> replyDetailDtoList = new ArrayList<>();
                List<Reply> replyList = replyService.findReplyList(comment);
                for (Reply reply : replyList) {
                    ReplyDetailDto replyDetailDtoBuild = ReplyDetailDto.builder()
                            .replyId(reply.getReplyId())
                            .replyNickname(reply.getMemberId().getEmail())
                            .reply(reply.getReply())
                            .replyTime(reply.getReplyTime())
                            .build();
                    replyDetailDtoList.add(replyDetailDtoBuild);
                }
                build.setReplies(replyDetailDtoList);
                commentDetailDtoList.add(build);
            }
        }
        postDetailDto.setCommentDetailDto(commentDetailDtoList);

        //???????????? builder
        List<String> postHashListDto = new ArrayList<>();
        List<PostHash> postHashList = postHashService.findPostHash(post.get());
        if (postHashList.size() != 0){
            for (PostHash postHash : postHashList) {
                String hashTagName = postHash.getHashTagId().getHashTagName();
                postHashListDto.add(hashTagName);
            }
        }
        postDetailDto.setPostHashes(postHashListDto);

        //???????????? builder
        String[] split = post.get().getImgName().split("[.]");
        if (post.get().getSwitchPic().equals(ImgType.DefaultBackground)) {
            postDetailDto.setBackgroundPicUri(ipAddress + ":8080/api/background/" + split[0]);
        } else {
            postDetailDto.setBackgroundPicUri(ipAddress + ":8080/api/userpic/" + split[0]);
        }

        return ResponseEntity.ok().body(postDetailDto);
    }
}