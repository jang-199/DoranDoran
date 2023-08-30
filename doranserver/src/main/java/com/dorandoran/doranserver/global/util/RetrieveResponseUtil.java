package com.dorandoran.doranserver.global.util;

import com.dorandoran.doranserver.domain.api.background.domain.imgtype.ImgType;
import com.dorandoran.doranserver.domain.api.post.domain.Post;
import com.dorandoran.doranserver.domain.api.post.dto.RetrieveCloseDto;
import com.dorandoran.doranserver.domain.api.post.dto.RetrieveHashtagDto;
import com.dorandoran.doranserver.domain.api.post.dto.RetrievePopularDto;
import com.dorandoran.doranserver.domain.api.post.dto.RetrievePostDto;
import lombok.Builder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RetrieveResponseUtil {
    String ipAddress;
    Boolean isLocationPresent;
    String[] splitLocation;
    String userEmail;

    public List<RetrieveCloseDto.ReadCloseResponse> makeRetrieveCloseResponseList(Point clientPoint,
                                                                                  List<Post> postList,
                                                                                  List<Integer> likeCntList,
                                                                                  List<Boolean> likeResultList,
                                                                                  List<Integer> commentAndReplyCnt){
        List<RetrieveCloseDto.ReadCloseResponse> postResponseList = new ArrayList<>();
        Iterator<Integer> likeCntListIter = likeCntList.iterator();
        Iterator<Boolean> likeResultByPostListIter = likeResultList.iterator();
        Iterator<Integer> commentAndReplyCntListIter = commentAndReplyCnt.iterator();
        for (Post post : postList) {

            Integer distance = (int) Math.round(clientPoint.distance(post.getLocation()) * 100);

            String[] splitImgName = post.getImgName().split("[.]");
            String imgName = splitImgName[0];
            RetrieveCloseDto.ReadCloseResponse postResponse = RetrieveCloseDto.ReadCloseResponse.builder()
                    .postId(post.getPostId())
                    .contents(post.getContent())
                    .postTime(post.getCreatedTime())
                    .location(distance)
                    .likeCnt(likeCntListIter.hasNext()?likeCntListIter.next():0)
                    .likeResult(likeResultByPostListIter.hasNext()?likeResultByPostListIter.next():false)
                    .replyCnt(commentAndReplyCntListIter.hasNext()?commentAndReplyCntListIter.next():0)
                    .backgroundPicUri(ipAddress + (post.getSwitchPic().equals(ImgType.DefaultBackground) ? ":8080/api/pic/default/" : ":8080/api/pic/member/") + imgName)
                    .font(post.getFont())
                    .fontColor(post.getFontColor())
                    .fontSize(post.getFontSize())
                    .fontBold(post.getFontBold())
                    .isWrittenByMember(post.getMemberId().getEmail().equals(userEmail) ? Boolean.TRUE : Boolean.FALSE)
                    .build();
            postResponseList.add(postResponse);
        }
        return postResponseList;
    }

    public List<RetrieveHashtagDto.ReadHashtagResponse> makeRetrieveHashtagResponseList(List<Post> postList,
                                                                                        List<Integer> likeCntList,
                                                                                        List<Boolean> likeResultList,
                                                                                        List<Integer> commentAndReplyCnt){
        List<RetrieveHashtagDto.ReadHashtagResponse> postResponseList = new ArrayList<>();
        Iterator<Integer> likeCntListIter = likeCntList.iterator();
        Iterator<Boolean> likeResultByPostListIter = likeResultList.iterator();
        Iterator<Integer> commentAndReplyCntListIter = commentAndReplyCnt.iterator();
        for (Post post : postList) {
            Integer distance;
            if (isLocationPresent && post.getLocation() != null) {
                GeometryFactory geometryFactory = new GeometryFactory();
                String latitude = splitLocation[0];
                String longitude = splitLocation[1];
                Coordinate coordinate = new Coordinate(Double.parseDouble(latitude), Double.parseDouble(longitude));
                Point point = geometryFactory.createPoint(coordinate);

                distance = (int) Math.round(point.distance(post.getLocation()) * 100);
            } else {
                distance = null;
            }

            String[] splitImgName = post.getImgName().split("[.]");
            String imgName = splitImgName[0];
            RetrieveHashtagDto.ReadHashtagResponse postResponse = RetrieveHashtagDto.ReadHashtagResponse.builder()
                    .postId(post.getPostId())
                    .contents(post.getContent())
                    .postTime(post.getCreatedTime())
                    .location(distance)
                    .likeCnt(likeCntListIter.hasNext()?likeCntListIter.next():0)
                    .likeResult(likeResultByPostListIter.hasNext()?likeResultByPostListIter.next():false)
                    .replyCnt(commentAndReplyCntListIter.hasNext()?commentAndReplyCntListIter.next():0)
                    .backgroundPicUri(ipAddress + (post.getSwitchPic().equals(ImgType.DefaultBackground) ? ":8080/api/pic/default/" : ":8080/api/pic/member/") + imgName)
                    .font(post.getFont())
                    .fontColor(post.getFontColor())
                    .fontSize(post.getFontSize())
                    .fontBold(post.getFontBold())
                    .isWrittenByMember(post.getMemberId().getEmail().equals(userEmail) ? Boolean.TRUE : Boolean.FALSE)
                    .build();
            postResponseList.add(postResponse);
        }
        return postResponseList;
    }

    public List<RetrievePopularDto.ReadPopularResponse> makeRetrievePopularResponseList(List<Post> postList,
                                                                                        List<Integer> likeCntList,
                                                                                        List<Boolean> likeResultList,
                                                                                        List<Integer> commentAndReplyCnt) {
        List<RetrievePopularDto.ReadPopularResponse> responseList = new ArrayList<>();
        Iterator<Integer> likeCntListIter = likeCntList.iterator();
        Iterator<Boolean> likeResultByPostListIter = likeResultList.iterator();
        Iterator<Integer> commentAndReplyCntListIter = commentAndReplyCnt.iterator();
        for (Post post : postList) {
            Integer distance;
            if (isLocationPresent && post.getLocation() != null) {
                GeometryFactory geometryFactory = new GeometryFactory();
                String latitude = splitLocation[0];
                String longitude = splitLocation[1];
                Coordinate coordinate = new Coordinate(Double.parseDouble(latitude), Double.parseDouble(longitude));
                Point point = geometryFactory.createPoint(coordinate);

                distance = (int) Math.round(point.distance(post.getLocation()) * 100);
            } else {
                distance = null;
            }

            String[] splitImgName = post.getImgName().split("[.]");
            String imgName = splitImgName[0];
            RetrievePopularDto.ReadPopularResponse postResponse = RetrievePopularDto.ReadPopularResponse.builder()
                    .postId(post.getPostId())
                    .contents(post.getContent())
                    .postTime(post.getCreatedTime())
                    .location(distance)
                    .likeCnt(likeCntListIter.hasNext()?likeCntListIter.next():0)
                    .likeResult(likeResultByPostListIter.hasNext()?likeResultByPostListIter.next():false)
                    .replyCnt(commentAndReplyCntListIter.hasNext()?commentAndReplyCntListIter.next():0)
                    .backgroundPicUri(ipAddress + (post.getSwitchPic().equals(ImgType.DefaultBackground) ? ":8080/api/pic/default/" : ":8080/api/pic/member/") + imgName)
                    .font(post.getFont())
                    .fontColor(post.getFontColor())
                    .fontSize(post.getFontSize())
                    .fontBold(post.getFontBold())
                    .isWrittenByMember(post.getMemberId().getEmail().equals(userEmail) ? Boolean.TRUE : Boolean.FALSE)
                    .build();
            responseList.add(postResponse);
        }
        return responseList;
    }

    public List<RetrievePostDto.ReadPostResponse> makeRetrievePostResponseList(List<Post> postList,
                                                                               List<Integer> likeCntList,
                                                                               List<Boolean> likeResultList,
                                                                               List<Integer> commentAndReplyCnt) {
        List<RetrievePostDto.ReadPostResponse> responseList = new ArrayList<>();
        Iterator<Integer> likeCntListIter = likeCntList.iterator();
        Iterator<Boolean> likeResultByPostListIter = likeResultList.iterator();
        Iterator<Integer> commentAndReplyCntListIter = commentAndReplyCnt.iterator();


        for (Post post : postList) {
            Integer distance;
            if (isLocationPresent && post.getLocation() != null) {
                GeometryFactory geometryFactory = new GeometryFactory();
                String latitude = splitLocation[0];
                String longitude = splitLocation[1];
                Coordinate coordinate = new Coordinate(Double.parseDouble(latitude), Double.parseDouble(longitude));
                Point point = geometryFactory.createPoint(coordinate);

                distance = (int) Math.round(point.distance(post.getLocation()) * 100);
            } else {
                distance = null;
            }

            String[] splitImgName = post.getImgName().split("[.]");
            String imgName = splitImgName[0];
            RetrievePostDto.ReadPostResponse postResponse = RetrievePostDto.ReadPostResponse.builder()
                    .postId(post.getPostId())
                    .contents(post.getContent())
                    .postTime(post.getCreatedTime())
                    .location(distance)
                    .likeCnt(likeCntListIter.hasNext()?likeCntListIter.next():0)
                    .likeResult(likeResultByPostListIter.hasNext()?likeResultByPostListIter.next():false)
                    .replyCnt(commentAndReplyCntListIter.hasNext()?commentAndReplyCntListIter.next():0)
                    .backgroundPicUri(ipAddress + (post.getSwitchPic().equals(ImgType.DefaultBackground) ? ":8080/api/pic/default/" : ":8080/api/pic/member/") + imgName)
                    .font(post.getFont())
                    .fontColor(post.getFontColor())
                    .fontSize(post.getFontSize())
                    .fontBold(post.getFontBold())
                    .isWrittenByMember(post.getMemberId().getEmail().equals(userEmail) ? Boolean.TRUE : Boolean.FALSE)
                    .build();
            responseList.add(postResponse);
        }
        return responseList;
    }

    @Builder
    public RetrieveResponseUtil(String ipAddress, Boolean isLocationPresent, String[] splitLocation, String userEmail) {
        this.ipAddress = ipAddress;
        this.isLocationPresent = isLocationPresent;
        this.splitLocation = splitLocation;
        this.userEmail = userEmail;
    }
}

