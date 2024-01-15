package com.dorandoran.doranserver.domain.member.service;

import com.dorandoran.doranserver.domain.background.domain.UserUploadPic;
import com.dorandoran.doranserver.domain.background.domain.imgtype.ImgType;
import com.dorandoran.doranserver.domain.background.service.repository.UserUploadPicRepository;
import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.comment.domain.CommentLike;
import com.dorandoran.doranserver.domain.comment.domain.Reply;
import com.dorandoran.doranserver.domain.comment.repository.CommentLikeRepository;
import com.dorandoran.doranserver.domain.comment.repository.CommentRepository;
import com.dorandoran.doranserver.domain.comment.repository.ReplyRepository;
import com.dorandoran.doranserver.domain.customerservice.domain.InquiryComment;
import com.dorandoran.doranserver.domain.customerservice.domain.InquiryPost;
import com.dorandoran.doranserver.domain.customerservice.service.repository.InquiryCommentRepository;
import com.dorandoran.doranserver.domain.customerservice.service.repository.InquiryPostRepository;
import com.dorandoran.doranserver.domain.hashtag.domain.HashTag;
import com.dorandoran.doranserver.domain.hashtag.domain.PostHash;
import com.dorandoran.doranserver.domain.hashtag.repository.HashTagRepository;
import com.dorandoran.doranserver.domain.member.domain.*;
import com.dorandoran.doranserver.domain.member.repository.*;
import com.dorandoran.doranserver.domain.notification.domain.NotificationHistory;
import com.dorandoran.doranserver.domain.notification.repository.NotificationHistoryRepository;
import com.dorandoran.doranserver.domain.post.domain.AnonymityMember;
import com.dorandoran.doranserver.domain.post.domain.PopularPost;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.post.domain.PostLike;
import com.dorandoran.doranserver.domain.post.repository.*;
import com.dorandoran.doranserver.domain.report.domain.ReportComment;
import com.dorandoran.doranserver.domain.report.domain.ReportPost;
import com.dorandoran.doranserver.domain.report.domain.ReportReply;
import com.dorandoran.doranserver.domain.report.repository.ReportCommentRepository;
import com.dorandoran.doranserver.domain.report.repository.ReportPostRepository;
import com.dorandoran.doranserver.domain.hashtag.service.HashTagService;
import com.dorandoran.doranserver.domain.post.service.PostLikeService;
import com.dorandoran.doranserver.domain.report.repository.ReportReplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountClosureMemberServiceImpl implements AccountClosureMemberService{

    @Value("${userUpload.Store.path}")
    String userUploadPicServerPath;

    @Value("${cloud.aws.s3.bucket}")
    String bucket;

    private final AccountClosureMemberRepository accountClosureMemberRepository;
    private final MemberHashService memberHashService;
    private final MemberHashRepository memberHashRepository;
    private final HashTagRepository hashTagRepository;
    private final HashTagService hashTagService;
    private final PostLikeService postLikeService;
    private final PostLikeRepository postLikeRepository;
    private final PolicyTermsRepository policyTermsRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository postRepository;
    private final PostHashRepository postHashRepository;
    private final PopularPostRepository popularPostRepository;
    private final AnonymityMemberRepository anonymityMemberRepository;
    private final MemberRepository memberRepository;
    private final ReportPostRepository reportPostRepository;
    private final ReportCommentRepository reportCommentRepository;
    private final ReportReplyRepository reportReplyRepository;
    private final MemberBlockListRepository memberBlockListRepository;
    private final NotificationHistoryRepository notificationHistoryRepository;
    private final InquiryCommentRepository inquiryCommentRepository;
    private final InquiryPostRepository inquiryPostRepository;
    private final UserUploadPicRepository userUploadPicRepository;
    private final S3Client s3Client;

    @Override
    public Optional<AccountClosureMember> findClosureMemberByEmail(String email) {
        return accountClosureMemberRepository.findAccountClosureMemberByEmail(email);
    }

    @Override
    public void saveClosureMember(AccountClosureMember accountClosureMember) {
        accountClosureMemberRepository.save(accountClosureMember);
    }

    @Scheduled(cron = "0 0 4 * * *",zone = "Asia/Seoul")
    @Transactional
    @Override
    public void deleteScheduledClosureMember() {
        //약관0, 즐겨찾기 태그0, 해시태그0 , 글공감, 글 , 만약 사용자 사진 있음 삭제,
        //댓글, 대댓 , 댓글에 공감한 회원, 인기글
        LocalDate now = LocalDate.now();

        accountClosureMemberRepository.findAll().stream()
                .filter(ac->ac.getClosureMemberId().getClosureDate().isBefore(now))//.plusDays(7)
                .forEach(ac-> {

                    Member member = ac.getClosureMemberId();

                    List<InquiryPost> allByMemberId3 = inquiryPostRepository.findAllByMemberId(member);
                    List<InquiryComment> allByInquiryPostId = inquiryCommentRepository.findAllByInquiryPostId(allByMemberId3);
                    inquiryCommentRepository.deleteAllInBatch(allByInquiryPostId);
                    inquiryPostRepository.deleteAllInBatch(allByMemberId3);


                    List<NotificationHistory> allByMemberId2 = notificationHistoryRepository.findAllByMemberId(member);
                    notificationHistoryRepository.deleteAllInBatch(allByMemberId2);

                    List<MemberBlockList> allByMemberId1 = memberBlockListRepository.findAllByBlockingMember(member);
                    memberBlockListRepository.deleteAllInBatch(allByMemberId1);

                    List<MemberHash> memberHashList = memberHashService.findHashByMember(member);
                    memberHashRepository.deleteAllInBatch(memberHashList);

                    List<PostLike> postLikeList = postLikeService.findByMemberId(member.getEmail());
                    postLikeRepository.deleteAllInBatch(postLikeList);



                    List<Reply> replyList = replyRepository.findAllByMember(member);
                    replyRepository.deleteAllInBatch(replyList);

                    List<Comment> commentList = commentRepository.findAllByMember(member);
                    commentRepository.deleteAllInBatch(commentList);



                    List<CommentLike> commentLikeList = commentLikeRepository.findAllByMember(member);
                    commentLikeRepository.deleteAllInBatch(commentLikeList);

                    List<Post> postList = postRepository.findAllByMember(member);

                    postList.forEach(post -> {
                        List<PostHash> postHashList = postHashRepository.findPostHashByPostId(post);

                        postHashRepository.deleteAllInBatch(postHashList);

                        postHashList.stream().filter(postHash -> postHash.getHashTagId().getHashTagCount() >= 1)
                                .forEach(postHash -> {
                                    postHash.getHashTagId().setHashTagCount(postHash.getHashTagId().getHashTagCount() - 1);
                                    hashTagService.saveHashTag(postHash.getHashTagId());
                                });

                        List<HashTag> hashTagList = postHashList.stream().map(PostHash::getHashTagId)
                                .filter(hashTagId -> hashTagId.getHashTagCount() <= 0).toList();
//                                .forEach(postHash -> hashTagRepository.delete(postHash.getHashTagId()));
                        hashTagRepository.deleteAllInBatch(hashTagList);


                        List<PopularPost> popularPostList = popularPostRepository.findByPostId(post);
                        popularPostRepository.deleteAllInBatch(popularPostList);

                        if (post.getSwitchPic().equals(ImgType.UserUpload)) {

                            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder().bucket(bucket).key("UserUploadPic/" + post.getImgName()).build();
                            s3Client.deleteObject(deleteObjectRequest);
                        }



                        List<Comment> commentListByPost = commentRepository.findAllByPost(post);

                        List<ReportComment> allByMember = reportCommentRepository.findAllByMemberId(member);
                        List<ReportReply> allByMemberId = reportReplyRepository.findAllByMemberId(member);
                        List<ReportPost> allByPostId = reportPostRepository.findAllByMemberId(member);
                        reportCommentRepository.deleteAllInBatch(allByMember);
                        reportReplyRepository.deleteAllInBatch(allByMemberId);
                        reportPostRepository.deleteAllInBatch(allByPostId);


                        commentListByPost.forEach(comment -> {

                            List<Reply> replyListByComment = replyRepository.findAllByComment(comment);
                            replyRepository.deleteAllInBatch(replyListByComment);
                            List<CommentLike> commentLikeListByComment = commentLikeRepository.findAllByComment(comment);
                            commentLikeRepository.deleteAllInBatch(commentLikeListByComment);
                        });

                        commentRepository.deleteAllInBatch(commentListByPost);


                        List<PostLike> postLIkeListByPost = postLikeRepository.findByPostId(post);
                        postLikeRepository.deleteAllInBatch(postLIkeListByPost);

                        List<AnonymityMember> anonymityMemberList = anonymityMemberRepository.findAllByPost(post);
                        anonymityMemberRepository.deleteAllInBatch(anonymityMemberList);
                    });
                    postRepository.deleteAllInBatch(postList);

                    accountClosureMemberRepository.delete(ac);

                    memberRepository.delete(member);

                    PolicyTerms policyTermsId = member.getPolicyTermsId(); //맴버 지우고
                    policyTermsRepository.delete(policyTermsId);


                });
    }
}
