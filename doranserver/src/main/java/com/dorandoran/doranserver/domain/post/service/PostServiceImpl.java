package com.dorandoran.doranserver.domain.post.service;

import com.dorandoran.doranserver.domain.background.domain.UserUploadPic;
import com.dorandoran.doranserver.domain.background.domain.imgtype.ImgType;
import com.dorandoran.doranserver.domain.background.service.UserUploadPicService;
import com.dorandoran.doranserver.domain.post.dto.PostDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Value;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.post.repository.PostRepository;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    @Value("${userUpload.Store.path}")
    String userUploadPicServerPath;
    private final PostRepository postRepository;
    private final UserUploadPicService userUploadPicService;

    @Override
    public void savePost(Post post) {
        postRepository.save(post);
    }

    @Override
    @Transactional
    public void saveMemberPost(Post post, PostDto.CreatePost postDto) throws IOException {
        setPostLocation(postDto, post);
        setPostPic(postDto, post);
        savePost(post);
    }

    @Override
    public List<Post> findFirstPost(Member member, List<Member> memberBlockLists) {
        PageRequest of = PageRequest.of(0, 20);
        if (memberBlockLists.isEmpty()) {
            return postRepository.findFirstPost(member, of);
        }
        return postRepository.findFirstPostWithoutBlockLists(member, memberBlockLists,of);
    }

    @Override
    public List<Post> findPost(Long startPost, Member member, List<Member> memberBlockLists) {
        PageRequest of = PageRequest.of(0, 20);
        if (memberBlockLists.isEmpty()) {
            return postRepository.findPost(startPost,member, of);
        }
        return postRepository.findPostWithoutBlockLists(startPost,  member , memberBlockLists, of);
    }

    @Override
    public Post findSinglePost(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당 글이 존재하지 않습니다."));
    }

    @Override
    public void deletePost(Post post) {
        postRepository.delete(post);
    }

    @Override
    public List<Post> findFirstClosePost(Point point,double distance,List<Member> memberBlockListByBlockingMember) {
        PageRequest of = PageRequest.of(0, 20);
        if (memberBlockListByBlockingMember.isEmpty()) {
            return postRepository.findFirstClosePostV2(point, distance, of);
        }
        return postRepository.findFirstClosePostWithoutBlockListsV2(point, distance, memberBlockListByBlockingMember, of);
    }

    @Override
    public List<Post> findClosePost(Point point,double distance, Long startPost, List<Member> memberBlockListByBlockingMember) {
        PageRequest of = PageRequest.of(0, 20);
//        List<Member> list = memberBlockListByBlockingMember.stream().map(MemberBlockList::getBlockedMember).toList();
        if (memberBlockListByBlockingMember.isEmpty()) {
            return postRepository.findClosePostV2(startPost, point, distance, of);
        }
        return postRepository.findClosePostWithoutBlockListsV2(startPost, point, distance,memberBlockListByBlockingMember, of);
    }

    @Override
    public List<Post> findFirstMyPost(Member member) {
        PageRequest of = PageRequest.of(0, 20);
        return postRepository.findMyFirstPost(member, of);
    }

    @Override
    public List<Post> findMyPost(Member member, Long startPost) {
        PageRequest of = PageRequest.of(0, 20);
        return postRepository.findMyPost(member, startPost, of);
    }

    @Override
    public List<Post> findBlockedPost(Integer page) {
        PageRequest of = PageRequest.of(page, 20);
        return postRepository.findBlockedPost(of);
    }

    @Override
    public Post findFetchMember(Long postId){
        return postRepository.findFetchMember(postId)
                .orElseThrow(() -> new NoSuchElementException("해당 글이 없습니다."));
    }

    @Override
    @Transactional
    public void setUnLocked(Post post) {
        post.setUnLocked();
    }

    @Override
    @Transactional
    public void setPostPic(PostDto.CreatePost postDto, Post post) throws IOException {
        if (postDto.getBackgroundImgName().isBlank()) {
            log.info("사진 생성 중");
            String fileName = postDto.getFile().getOriginalFilename();
            String fileNameSubstring = fileName.substring(fileName.lastIndexOf(".") + 1);
            String userUploadImgName = UUID.randomUUID() + "." + fileNameSubstring;
            postDto.getFile().transferTo(new File(userUploadPicServerPath + userUploadImgName));
            //todo 사진 확장자 체크 로직 추가

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
    }
    private static void setPostLocation(PostDto.CreatePost postDto, Post post) {
        Point distance;
        if (!postDto.getLocation().isBlank()) {
            String[] splitLocation = postDto.getLocation().split(",");
            String latitude = splitLocation[0];
            String longitude = splitLocation[1];
            GeometryFactory geometryFactory = new GeometryFactory();
            Coordinate coordinate = new Coordinate(Double.parseDouble(latitude), Double.parseDouble(longitude));
            distance = geometryFactory.createPoint(coordinate);

        } else {
            distance = null;
        }
        post.setLocation(distance);
    }
}
