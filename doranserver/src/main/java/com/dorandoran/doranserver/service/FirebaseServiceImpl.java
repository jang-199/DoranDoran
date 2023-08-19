package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.config.FirebaseConfig;
import com.dorandoran.doranserver.entity.*;
import com.dorandoran.doranserver.entity.notificationType.NotificationType;
import com.dorandoran.doranserver.entity.osType.OsType;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FirebaseServiceImpl implements FirebaseService {
    private final FirebaseConfig firebaseConfig;
    private static final String title = "도란도란";
    private final NotificationHistoryService notificationHistoryService;

    @Override
    public void notifyComment(Member member, Comment comment){
        List<String> tokenList = Collections.singletonList(member.getFirebaseToken());
        String content = "새로운 댓글이 달렸습니다 : " + comment.getComment();
        MulticastMessage message = MulticastMessage.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(content)
                        .build())
                .putData("postId", String.valueOf(comment.getPostId().getPostId()))
                .putData("commentId", String.valueOf(comment.getCommentId()))
                .addAllTokens(tokenList)
                .build();
        notifyOneMemberByOsType(member, message);

        NotificationHistory notificationHistory = NotificationHistory.builder()
                .message(content)
                .notificationType(NotificationType.Comment)
                .objectId(comment.getCommentId())
                .memberId(member)
                .build();
        notificationHistoryService.saveNotification(notificationHistory);
    }

    @Override
    public void notifyReply(List<Member> memberList, Reply reply) {
        List<String> aosTokenList = makeMemberListByOs(memberList, OsType.Aos);
        List<String> iosTokenList = makeMemberListByOs(memberList, OsType.Ios);
        
        String content = "새로운 대댓글이 달렸습니다 : " + reply.getReply();
        notifyMessageByOs(reply, aosTokenList, content, OsType.Aos);
        notifyMessageByOs(reply, iosTokenList, content, OsType.Ios);

        for (Member member : memberList) {
            NotificationHistory notificationHistory = NotificationHistory.builder()
                    .message(content)
                    .notificationType(NotificationType.Reply)
                    .objectId(reply.getReplyId())
                    .memberId(member)
                    .build();
            notificationHistoryService.saveNotification(notificationHistory);
        }
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

        NotificationHistory notificationHistory = NotificationHistory.builder()
                .message(content)
                .notificationType(NotificationType.PostLike)
                .objectId(post.getPostId())
                .memberId(member)
                .build();
        notificationHistoryService.saveNotification(notificationHistory);
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
                .putData("postId", String.valueOf(comment.getPostId().getPostId()))
                .putData("commentId", String.valueOf(comment.getCommentId()))
                .addAllTokens(tokenList)
                .build();
        notifyOneMemberByOsType(member, message);

        NotificationHistory notificationHistory = NotificationHistory.builder()
                .message(content)
                .notificationType(NotificationType.CommentLike)
                .objectId(comment.getCommentId())
                .memberId(member)
                .build();
        notificationHistoryService.saveNotification(notificationHistory);
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

    private static List<String> makeMemberListByOs(List<Member> memberList, OsType osType) {
        return memberList.stream()
                .distinct()
                .filter((member) -> member.getOsType().equals(osType)
                        )
                .map(Member::getFirebaseToken)
                .collect(Collectors.toList());
    }

    private void notifyMessageByOs(Reply reply, List<String> tokenList, String content, OsType osType) {
        if (!tokenList.isEmpty()) {
            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(content)
                            .build())
                    .putData("postId", String.valueOf(reply.getCommentId().getPostId().getPostId()))
                    .putData("commentId", String.valueOf(reply.getCommentId().getCommentId()))
                    .putData("replyId", String.valueOf(reply.getReplyId()))
                    .addAllTokens(tokenList)
                    .build();

            if (osType.equals(OsType.Aos)) {
                sendToAos(message);
            }

            if (osType.equals(OsType.Ios)) {
                sendToIos(message);
            }
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
