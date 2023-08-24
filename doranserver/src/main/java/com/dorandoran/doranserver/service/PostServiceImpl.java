package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.dto.PostDto;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.MemberBlockList;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.UserUploadPic;
import com.dorandoran.doranserver.entity.imgtype.ImgType;
import com.dorandoran.doranserver.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    public List<Post> findFirstPost(List<MemberBlockList> memberBlockLists) {
        PageRequest of = PageRequest.of(0, 20);
        if (memberBlockLists.isEmpty()) {
            return postRepository.findFirstPost(of);
        }
        List<Member> list = memberBlockLists.stream().map(MemberBlockList::getBlockedMember).toList();
        log.info(list.toString());
        return postRepository.findFirstPostWithoutBlockLists(of,list);
    }

    @Override
    public List<Post> findPost(Long startPost,List<MemberBlockList> memberBlockLists) {
        PageRequest of = PageRequest.of(0, 20);
        if (memberBlockLists.isEmpty()) {
            return postRepository.findPost(startPost, of);
        }
        List<Member> list = memberBlockLists.stream().map(MemberBlockList::getBlockedMember).toList();
        return postRepository.findPostWithoutBlockLists(startPost, of,list);
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
    public List<Post> findFirstClosePost(Double Slatitude,Double Llatitude, Double Slongitude, Double Llongitude,List<MemberBlockList> memberBlockListByBlockingMember) {
        PageRequest of = PageRequest.of(0, 20);
        List<Member> list = memberBlockListByBlockingMember.stream().map(MemberBlockList::getBlockedMember).toList();
        if (list.isEmpty()) {
            return postRepository.findFirstClosePost(Slatitude,Llatitude, Slongitude, Llongitude, of);
        }
        return postRepository.findFirstClosePostWithoutBlockLists(Slatitude, Llatitude, Slongitude, Llongitude, list, of);
    }

    @Override
    public List<Post> findClosePost(Double Slatitude, Double Llatitude, Double Slongitude, Double Llongitude,Long startPost,List<MemberBlockList> memberBlockListByBlockingMember) {
        PageRequest of = PageRequest.of(0, 20);
        List<Member> list = memberBlockListByBlockingMember.stream().map(MemberBlockList::getBlockedMember).toList();
        if (list.isEmpty()) {
            return postRepository.findClosePost(startPost, Slatitude, Llatitude, Slongitude, Llongitude, of);
        }
        return postRepository.findClosePostWithoutBlockLists(startPost, Slatitude, Llatitude, Slongitude, Llongitude, list, of);
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
        if (postDto.getLocation().isBlank()) {
            post.setLatitude(null);
            post.setLongitude(null);
        } else {
            String[] userLocation = postDto.getLocation().split(",");
            post.setLatitude(Double.valueOf(userLocation[0]));
            post.setLongitude(Double.valueOf(userLocation[1]));
        }
    }
}
