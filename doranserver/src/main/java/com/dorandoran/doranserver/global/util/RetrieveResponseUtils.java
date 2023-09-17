package com.dorandoran.doranserver.global.util;

import com.dorandoran.doranserver.domain.background.domain.imgtype.ImgType;
import com.dorandoran.doranserver.domain.hashtag.domain.HashTag;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.post.dto.*;
import com.dorandoran.doranserver.global.util.distance.DistanceUtil;
import lombok.Builder;
import org.locationtech.jts.geom.Point;
import org.springframework.util.LinkedMultiValueMap;

import java.util.*;

public class RetrieveResponseUtils {
    public static class AllLikedPostsResponse{
        String ipAddress;
        public List<RetrievePostDto.ReadPostResponse> makeAllPostsResponseList(List<Post> myPost, List<Integer> likeCntList) {
            List<RetrievePostDto.ReadPostResponse> postDtoList = new ArrayList<>();
            Iterator<Integer> likeCntListIter = likeCntList.iterator();
            for (Post post : myPost) {
                String[] split = post.getImgName().split("[.]");

                RetrievePostDto.ReadPostResponse postResponseDto = RetrievePostDto.ReadPostResponse.builder()
                        .postId(post.getPostId())
                        .contents(post.getContent())
                        .postTime(post.getCreatedTime())
                        .location(null)
                        .likeCnt(likeCntListIter.hasNext() ? likeCntListIter.next() : 0)
                        .likeResult(null)
                        .replyCnt(null)
                        .backgroundPicUri(
                                post.getSwitchPic() == ImgType.DefaultBackground
                                        ? ipAddress + ":8080/api/pic/default/" + split[0]
                                        : ipAddress + ":8080/api/pic/member/" + split[0])
                        .font(post.getFont())
                        .fontColor(post.getFontColor())
                        .fontSize(post.getFontSize())
                        .fontBold(post.getFontBold())
                        .build();
                postDtoList.add(postResponseDto);
            }
            return postDtoList;
        }
        @Builder
        public AllLikedPostsResponse(String ipAddress) {
            this.ipAddress = ipAddress;
        }
    }
    public static class AllPostsResponse{
        String ipAddress;
        public List<RetrievePostDto.ReadPostResponse> makeAllPostsResponseList(List<Post> myPost, List<Integer> likeCntList) {
            List<RetrievePostDto.ReadPostResponse> responseList = new ArrayList<>();
            Iterator<Integer> likeCntListIter = likeCntList.iterator();
            for (Post post : myPost) {
                String[] split = post.getImgName().split("[.]");

                RetrievePostDto.ReadPostResponse postResponseDto = RetrievePostDto.ReadPostResponse.builder().postId(post.getPostId())
                        .contents(post.getContent())
                        .postTime(post.getCreatedTime())
                        .location(null)
                        .likeCnt(likeCntListIter.hasNext() ? likeCntListIter.next() : 0)
                        .likeResult(null)
                        .replyCnt(null)
                        .backgroundPicUri(
                                post.getSwitchPic() == ImgType.DefaultBackground
                                        ? ipAddress + ":8080/api/pic/default/" + split[0]
                                        : ipAddress + ":8080/api/pic/member/" + split[0])
                        .font(post.getFont())
                        .fontColor(post.getFontColor())
                        .fontSize(post.getFontSize())
                        .fontBold(post.getFontBold())
                        .build();
                responseList.add(postResponseDto);
            }
            return responseList;
        }
        @Builder
        public AllPostsResponse(String ipAddress) {
            this.ipAddress = ipAddress;
        }
    }
    public static class InterestedPostResponse{
        String userEmail;
        String ipAddress;

        public LinkedList<HashMap<String,RetrieveInterestedDto.ReadInterestedResponse>> makeRetrieveInterestedResponseList(List<Post> postList,
                                                                                                                           List<Integer> likeCntList,
                                                                                                                           List<Boolean> likeResultList,
                                                                                                                           List<Integer> commentAndReplyCnt,
                                                                                                                           LinkedMultiValueMap<Long, String> stringPostHashMap) {
            HashMap<String, RetrieveInterestedDto.ReadInterestedResponse> stringPostResponseDtoHashMap = new LinkedHashMap<>();
            LinkedList<HashMap<String,RetrieveInterestedDto.ReadInterestedResponse>> mapLinkedList = new LinkedList<>();
            Iterator<Integer> likeCntListIter = likeCntList.iterator();
            Iterator<Boolean> likeResultByPostListIter = likeResultList.iterator();
            Iterator<Integer> commentAndReplyCntListIter = commentAndReplyCnt.iterator();
            for (Post post : postList) {
                String[] splitImgName = post.getImgName().split("[.]");
                String imgName = splitImgName[0];
                RetrieveInterestedDto.ReadInterestedResponse postResponse = RetrieveInterestedDto.ReadInterestedResponse.builder()
                        .postId(post.getPostId())
                        .contents(post.getContent())
                        .postTime(post.getCreatedTime())
                        .location(null)
                        .likeCnt(likeCntListIter.hasNext()?likeCntListIter.next():0)
                        .likeResult(likeResultByPostListIter.hasNext() ? likeResultByPostListIter.next() : false)
                        .replyCnt(commentAndReplyCntListIter.hasNext() ? commentAndReplyCntListIter.next() : 0)
                        .backgroundPicUri(ipAddress + (post.getSwitchPic().equals(ImgType.DefaultBackground) ? ":8080/api/pic/default/" : ":8080/api/pic/member/") + imgName)
                        .font(post.getFont())
                        .fontColor(post.getFontColor())
                        .fontSize(post.getFontSize())
                        .fontBold(post.getFontBold())
                        .isWrittenByMember(post.getMemberId().getEmail().equals(userEmail) ? Boolean.TRUE : Boolean.FALSE)
                        .build();
                if (Objects.requireNonNull(stringPostHashMap.get(post.getPostId())).size() > 1) {
                    List<String> strings = stringPostHashMap.get(post.getPostId());
                    for (String string : Objects.requireNonNull(strings)) {
                        stringPostResponseDtoHashMap.put(string,postResponse);
                    }
                }else {
                    stringPostResponseDtoHashMap.put(Objects.requireNonNull(stringPostHashMap.get(post.getPostId())).get(0),postResponse);
                }

            }
            mapLinkedList.add(stringPostResponseDtoHashMap);
            return mapLinkedList;
        }

        @Builder
        public InterestedPostResponse(String userEmail, String ipAddress) {
            this.userEmail = userEmail;
            this.ipAddress = ipAddress;
        }
    }


    public static class PostResponse{
        String ipAddress;
        Boolean isLocationPresent;
        String[] splitLocation;
        String userEmail;

        public List<RetrievePostDto.ReadPostResponse> makeRetrievePostResponseList(List<Post> postList,
                                                                                   List<Integer> likeCntList,
                                                                                   List<Boolean> likeResultList,
                                                                                   List<Integer> commentAndReplyCnt) {
            List<RetrievePostDto.ReadPostResponse> responseList = new ArrayList<>();
            Iterator<Integer> likeCntListIter = likeCntList.iterator();
            Iterator<Boolean> likeResultByPostListIter = likeResultList.iterator();
            Iterator<Integer> commentAndReplyCntListIter = commentAndReplyCnt.iterator();
            DistanceUtil distanceUtil = new DistanceUtil();

            for (Post post : postList) {
                Integer distance;
                if (isLocationPresent && post.getLocation() != null) {
                    distance = distanceUtil.getDistance(splitLocation, post.getLocation());
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
                        .likeResult(likeResultByPostListIter.hasNext() ? likeResultByPostListIter.next() : false)
                        .replyCnt(commentAndReplyCntListIter.hasNext() ? commentAndReplyCntListIter.next() : 0)
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
        public PostResponse(String ipAddress, Boolean isLocationPresent, String[] splitLocation, String userEmail) {
            this.ipAddress = ipAddress;
            this.isLocationPresent = isLocationPresent;
            this.splitLocation = splitLocation;
            this.userEmail = userEmail;
        }
    }

    public static class PopularPostResponse{
        String ipAddress;
        Boolean isLocationPresent;
        String[] splitLocation;
        String userEmail;

        public List<RetrievePopularDto.ReadPopularResponse> makeRetrievePopularResponseList(List<Post> postList,
                                                                                            List<Integer> likeCntList,
                                                                                            List<Boolean> likeResultList,
                                                                                            List<Integer> commentAndReplyCnt) {
            List<RetrievePopularDto.ReadPopularResponse> responseList = new ArrayList<>();
            Iterator<Integer> likeCntListIter = likeCntList.iterator();
            Iterator<Boolean> likeResultByPostListIter = likeResultList.iterator();
            Iterator<Integer> commentAndReplyCntListIter = commentAndReplyCnt.iterator();
            DistanceUtil distanceUtil = new DistanceUtil();
            for (Post post : postList) {
                Integer distance;
                if (isLocationPresent && post.getLocation() != null) {
                    distance = distanceUtil.getDistance(splitLocation, post.getLocation());

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
                        .likeResult(likeResultByPostListIter.hasNext() ? likeResultByPostListIter.next() : false)
                        .replyCnt(commentAndReplyCntListIter.hasNext() ? commentAndReplyCntListIter.next() : 0)
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
        public PopularPostResponse(String ipAddress, Boolean isLocationPresent, String[] splitLocation, String userEmail) {
            this.ipAddress = ipAddress;
            this.isLocationPresent = isLocationPresent;
            this.splitLocation = splitLocation;
            this.userEmail = userEmail;
        }
    }

    public static class HashtagPostResponse{
        String ipAddress;
        Boolean isLocationPresent;
        String[] splitLocation;
        String userEmail;

        public HashMap<String, Object> makeRetrieveHashtagResponseList(Boolean isBookmarked,
                                                                       List<Post> postList,
                                                                       List<Integer> likeCntList,
                                                                       List<Boolean> likeResultList,
                                                                       List<Integer> commentAndReplyCnt){
            HashMap<String, Object> responseMap = new HashMap<>();
            responseMap.put("isBookmarked", isBookmarked);

            List<RetrieveHashtagDto.ReadHashtagResponse> postResponseList = new ArrayList<>();
            Iterator<Integer> likeCntListIter = likeCntList.iterator();
            Iterator<Boolean> likeResultByPostListIter = likeResultList.iterator();
            Iterator<Integer> commentAndReplyCntListIter = commentAndReplyCnt.iterator();
            DistanceUtil distanceUtil = new DistanceUtil();
            for (Post post : postList) {
                Integer distance;
                if (isLocationPresent && post.getLocation() != null) {
                    distance = distanceUtil.getDistance(splitLocation, post.getLocation());
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
                        .likeResult(likeResultByPostListIter.hasNext() ? likeResultByPostListIter.next() : false)
                        .replyCnt(commentAndReplyCntListIter.hasNext() ? commentAndReplyCntListIter.next() : 0)
                        .backgroundPicUri(ipAddress + (post.getSwitchPic().equals(ImgType.DefaultBackground) ? ":8080/api/pic/default/" : ":8080/api/pic/member/") + imgName)
                        .font(post.getFont())
                        .fontColor(post.getFontColor())
                        .fontSize(post.getFontSize())
                        .fontBold(post.getFontBold())
                        .isWrittenByMember(post.getMemberId().getEmail().equals(userEmail) ? Boolean.TRUE : Boolean.FALSE)
                        .build();
                postResponseList.add(postResponse);
            }
            responseMap.put("Data", postResponseList);
            return responseMap;
        }

        @Builder
        public HashtagPostResponse(String ipAddress, Boolean isLocationPresent, String[] splitLocation, String userEmail) {
            this.ipAddress = ipAddress;
            this.isLocationPresent = isLocationPresent;
            this.splitLocation = splitLocation;
            this.userEmail = userEmail;
        }
    }

    public static class ClosePostResponse{
        String ipAddress;
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
                        .likeResult(likeResultByPostListIter.hasNext() ? likeResultByPostListIter.next() : false)
                        .replyCnt(commentAndReplyCntListIter.hasNext() ? commentAndReplyCntListIter.next() : 0)
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

        @Builder
        public ClosePostResponse(String ipAddress, String[] splitLocation, String userEmail) {
            this.ipAddress = ipAddress;
            this.splitLocation = splitLocation;
            this.userEmail = userEmail;
        }
    }
}

