package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.config.FirebaseConfig;
import com.dorandoran.doranserver.entity.*;
import com.dorandoran.doranserver.entity.osType.OsType;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FirebaseServiceImpl implements FirebaseService {
    private final FirebaseConfig firebaseConfig;
    private static final String title = "도란도란";

    @Override
    public void notifyComment(Member member, Comment comment){
        List<String> tokenList = Collections.singletonList(member.getFirebaseToken());
        String content = "새로운 댓글이 달렸습니다 : " + comment;
        MulticastMessage message = MulticastMessage.builder()
                .setNotification(Notification.builder()
                                .setTitle(title)
                                .setBody(content)
                                .build())
                .putData("commentId", String.valueOf(comment.getCommentId()))
                .addAllTokens(tokenList)
                .build();

        notifyOneMemberByOsType(member, message);
    }

    @Override
    public void notifyReply(List<Member> memberList, Comment comment, Reply reply) {
        String commentMemberFirebaseToken = comment.getMemberId().getFirebaseToken();
        List<String> aosTokenList = makeMemberListByOs(memberList, commentMemberFirebaseToken, OsType.Aos);
        List<String> iosTokenList = makeMemberListByOs(memberList, commentMemberFirebaseToken, OsType.Ios);
        List<String> commentMemberTokenList = Collections.singletonList(commentMemberFirebaseToken);

        List<List<String>> combineTokenList = new ArrayList<>();
        combineTokenList.add(aosTokenList);
        combineTokenList.add(iosTokenList);
        combineTokenList.add(commentMemberTokenList);

        for (List<String> tokenList : combineTokenList) {
            String content = "새로운 대댓글이 달렸습니다 : " + reply.getReply();
            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(content)
                            .build())
                    .putData("replyId", String.valueOf(reply.getReplyId()))
                    .addAllTokens(tokenList)
                    .build();

            if(tokenList.equals(aosTokenList)){
                sendToAos(message);
            } else if (tokenList.equals(iosTokenList)) {
                sendToIos(message);
            }else {
                notifyOneMemberByOsType(comment.getMemberId(),message);
            }
        }




        String content = "새로운 대댓글이 달렸습니다 : " + reply.getReply();
        MulticastMessage message = MulticastMessage.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(content)
                        .build())
                .putData("replyId", String.valueOf(reply.getReplyId()))
                .addAllTokens(iosTokenList)
                .build();

    }

    private static List<String> makeMemberListByOs(List<Member> memberList, String commentFirebaseToken, OsType osType) {
        return memberList.stream()
                .distinct()
                .filter((member) -> member.getOsType().equals(osType)
                        && !member.getFirebaseToken().equals(commentFirebaseToken))
                .map(Member::getFirebaseToken)
                .collect(Collectors.toList());
    }

    @Override
    public void notifyPostLike(Member member, Post post) {
        List<String> tokenList = Collections.singletonList(member.getFirebaseToken());
        String content = "글에 좋아요가 눌렸습니다." ;
        MulticastMessage message = MulticastMessage.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(content)
                        .build())
                .putData("postId", String.valueOf(post.getPostId()))
                .addAllTokens(tokenList)
                .build();

        notifyOneMemberByOsType(member, message);
    }

    @Override
    public void notifyCommentLike(Member member, Comment comment) {
        List<String> tokenList = Collections.singletonList(member.getFirebaseToken());
        String content = "댓글에 좋아요가 눌렸습니다." ;
        MulticastMessage message = MulticastMessage.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(content)
                        .build())
                .putData("commentId", String.valueOf(comment.getCommentId()))
                .addAllTokens(tokenList)
                .build();

        notifyOneMemberByOsType(member, message);
    }

    @Override
    public void notifyBlockedMember(LockMember lockMember) {
        List<String> tokenList = Collections.singletonList(lockMember.getMemberId().getFirebaseToken());
        String content = "지속적인 신고로 귀하의 계정이 " + lockMember.getLockEndTime() + "까지 정지되었습니다.";
        MulticastMessage message = MulticastMessage.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(content)
                        .build())
                .addAllTokens(tokenList)
                .build();

        notifyOneMemberByOsType(lockMember.getMemberId(), message);

    }

    private void notifyOneMemberByOsType(Member member, MulticastMessage message) {
        switch (member.getOsType()){
            case Aos -> sendToAos(message);
            case Ios -> sendToIos(message);
        }
    }

    public void sendToAos(MulticastMessage message){
        try{
            FirebaseMessaging.getInstance(firebaseConfig.aosFirebaseApp()).sendMulticastAsync(message);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendToIos(MulticastMessage message) {
        try{
            FirebaseMessaging.getInstance(firebaseConfig.iosFirebaseApp()).sendMulticastAsync(message);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
