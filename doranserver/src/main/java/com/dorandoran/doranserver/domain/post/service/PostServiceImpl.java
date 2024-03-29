package com.dorandoran.doranserver.domain.post.service;

import com.dorandoran.doranserver.domain.background.domain.UserUploadPic;
import com.dorandoran.doranserver.domain.background.domain.imgtype.ImgType;
import com.dorandoran.doranserver.domain.background.service.UserUploadPicService;
import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.comment.domain.Reply;
import com.dorandoran.doranserver.domain.post.dto.PostDto;
import com.dorandoran.doranserver.domain.post.exception.UnsupportedImageExtensionException;
import com.dorandoran.doranserver.global.util.exception.customexception.post.NotFoundPostException;
import com.dorandoran.doranserver.global.util.fileextension.FileExtensionsFilter;
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
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    @Value("${userUpload.Store.path}")
    String userUploadPicServerPath;
    @Value("${cloud.aws.s3.bucket}")
    String bucket;

    private final PostRepository postRepository;
    private final UserUploadPicService userUploadPicService;
    private final S3Client s3Client;

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
        return postRepository.findById(postId).orElseThrow(() -> new NotFoundPostException("해당 글이 존재하지 않습니다."));
    }

    @Override
    public void deletePost(Post post) {
        postRepository.delete(post);
    }

    @Override
    public List<Post> findFirstClosePost(Point point, double distance, List<Member> memberBlockListByBlockingMember, Member member) {
        PageRequest of = PageRequest.of(0, 20);
        if (memberBlockListByBlockingMember.isEmpty()) {
            return postRepository.findFirstClosePost(point, distance, member, of);
        }
        return postRepository.findFirstClosePostWithoutBlockLists(point, distance, memberBlockListByBlockingMember, member, of);
    }

    @Override
    public List<Post> findClosePost(Point point,double distance, Long startPost, List<Member> memberBlockListByBlockingMember, Member member) {
        PageRequest of = PageRequest.of(0, 20);
        if (memberBlockListByBlockingMember.isEmpty()) {
            return postRepository.findClosePost(startPost, point, distance, member, of);
        }
        return postRepository.findClosePostWithoutBlockLists(startPost, point, distance,memberBlockListByBlockingMember, member, of);
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
                .orElseThrow(() -> new NotFoundPostException("해당 글이 없습니다."));
    }

    @Override
    @Transactional
    public void setUnLocked(Post post) {
        post.setUnLocked();
    }

    @Override
    @Transactional
    public void setPostPic(PostDto.CreatePost postDto, Post post) throws IOException {
        FileExtensionsFilter fileExtensionsFilter = new FileExtensionsFilter();
        if (postDto.getBackgroundImgName().isBlank()) {
            String fileName = postDto.getFile().getOriginalFilename();
            String imageExtension = fileName.substring(fileName.lastIndexOf(".") + 1);

            fileExtensionsFilter.isAvailableFileExtension(imageExtension);

            MultipartFile imageFile = postDto.getFile();
            String userUploadImgName = UUID.randomUUID() + "." + imageExtension;

            transferImageToS3(imageFile, userUploadImgName);

            post.setSwitchPic(ImgType.UserUpload);
            post.setImgName(userUploadImgName);

            UserUploadPic userUploadPic = UserUploadPic
                    .builder()
                    .imgName(userUploadImgName)
                    .serverPath(userUploadPicServerPath + userUploadImgName)
                    .build();
            userUploadPicService.saveUserUploadPic(userUploadPic);
        } else {
            post.setSwitchPic(ImgType.DefaultBackground);
            post.setImgName(postDto.getBackgroundImgName() + ".jpg");
        }
    }

    private void transferImageToS3(MultipartFile imageFile, String userUploadImgName) throws IOException {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key("UserUploadPic/" + userUploadImgName)
                        .build(),
                RequestBody.fromInputStream(
                        imageFile.getInputStream(),
                        imageFile.getSize()
                )
        );
    }

    @Override
    public Boolean isCommentReplyAuthor(List<Comment> commentList, List<Reply> replyList, Member member) {
        boolean checkCommentList = commentList.stream()
                .anyMatch(comment -> comment.getMemberId().equals(member));

        boolean checkReplyList = replyList.stream()
                .anyMatch(reply -> reply.getMemberId().equals(member));

        return checkCommentList || checkReplyList ? Boolean.TRUE : Boolean.FALSE;
    }

    private void setPostLocation(PostDto.CreatePost postDto, Post post) {
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
