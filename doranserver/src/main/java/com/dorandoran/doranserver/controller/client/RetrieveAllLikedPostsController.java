package com.dorandoran.doranserver.controller.client;

import com.dorandoran.doranserver.dto.PostResponseDto;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.PostLike;
import com.dorandoran.doranserver.entity.imgtype.ImgType;
import com.dorandoran.doranserver.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
@Slf4j
public class RetrieveAllLikedPostsController {

    @Value("${userUpload.Store.path}")
    String userUploadPicServerPath;
    @Value("${background.Store.path}")
    String backgroundPicServerPath;
    @Value("${doran.ip.address}")
    String ipAddress;

    private final PostLikeService postLikeService;

    @GetMapping("liked-posts/{position}")
    public ResponseEntity<LinkedList<PostResponseDto>> getAllLikedPosts(@PathVariable("position") Long position,
                                                                        @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();

        List<PostLike> myPost;
        if (position == 0) {
            myPost = postLikeService.findFirstMyLikedPosts(username);
        } else {
            myPost = postLikeService.findMyLikedPosts(username, position);
        }

        LinkedList<PostResponseDto> postDtoList = new LinkedList<>();
        for (PostLike post : myPost) {
            String[] split = post.getPostId().getImgName().split("[.]");

            PostResponseDto postResponseDto = PostResponseDto.builder().postId(post.getPostId().getPostId())
                    .contents(post.getPostId().getContent())
                    .postTime(post.getPostId().getPostTime())
                    .location(null)
                    .likeCnt(postLikeService.findLIkeCnt(post.getPostId()))
                    .likeResult(null)
                    .ReplyCnt(null)
                    .backgroundPicUri(
                            post.getPostId().getSwitchPic() == ImgType.DefaultBackground
                                    ?
                                    ipAddress + ":8080/api/userpic/" + split[0]
                                    :
                                    ipAddress + ":8080/api/background/" + split[0])
                    .font(post.getPostId().getFont())
                    .fontColor(post.getPostId().getFontColor())
                    .fontSize(post.getPostId().getFontSize())
                    .fontBold(post.getPostId().getFontBold())
                    .build();
            postDtoList.add(postResponseDto);
        }

        return ResponseEntity.ok().body(postDtoList);
    }
}
