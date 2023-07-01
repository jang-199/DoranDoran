package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.*;
import com.dorandoran.doranserver.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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

    @Override
    public Optional<AccountClosureMember> findClosureMemberByEmail(String email) {
        return accountClosureMemberRepository.findAccountClosureMemberByEmail(email);
    }

    @Override
    public void saveClosureMember(AccountClosureMember accountClosureMember) {
        accountClosureMemberRepository.save(accountClosureMember);
    }

    @Scheduled(cron = "0 0 4 * *",zone = "Asia/Seoul")
    @Override
    public void deleteScheduledClosureMember() {
        //약관0, 즐겨찾기 태그0, 해시태그0 , 글공감, 글 , 만약 사용자 사진 있음 삭제,
        //댓글, 대댓 , 댓글에 공감한 회원, 인기글
        LocalDate now = LocalDate.now();

        accountClosureMemberRepository.findAll().stream()
                .filter(ac->ac.getClosureMemberId().getClosureDate().plusDays(7).isBefore(now))
                .forEach(ac-> {

                    Member member = ac.getClosureMemberId(); //ToDo delete

                    PolicyTerms policyTermsId = member.getPolicyTermsId();
                    policyTermsRepository.delete(policyTermsId);

                    List<MemberHash> memberHashList = memberHashService.findHashByMember(member);
                    memberHashRepository.deleteAll(memberHashList);

                    List<PostLike> postLikeList = postLikeService.findByMemberId(member.getEmail());
                    postLikeRepository.deleteAll(postLikeList);

                    List<Comment> commentList = commentRepository.findAllByMember(member);
                    commentRepository.deleteAll(commentList);

                    List<Reply> replyList = replyRepository.findAllByMember(member);
                    replyRepository.deleteAll(replyList);

                    List<CommentLike> commentLikeList = commentLikeRepository.findAllByMember(member);
                    commentLikeRepository.deleteAll(commentLikeList);

                    List<Post> postList = postRepository.findAllByMember(member);

                    postList.forEach(post -> {
                        List<PostHash> postHashList = postHashRepository.findPostHashByPostId(post);
                        postHashList.stream().filter(postHash -> postHash.getHashTagId().getHashTagCount() > 1)
                                .forEach(postHash -> {
                                    postHash.getHashTagId().setHashTagCount(postHash.getHashTagId().getHashTagCount() - 1);
                                    hashTagService.saveHashTag(postHash.getHashTagId());
                                });

                        postHashList.stream().filter(postHash -> postHash.getHashTagId().getHashTagCount() <= 1)
                                .forEach(postHash -> hashTagRepository.delete(postHash.getHashTagId()));

                        postHashRepository.deleteAll(postHashList);

                        List<PopularPost> popularPostList = popularPostRepository.findByPostId(post);
                        popularPostRepository.deleteAll(popularPostList);

                        File userUploadPIc = new File(userUploadPicServerPath + post.getImgName());
                        if(userUploadPIc.exists()){
                            if(userUploadPIc.delete()){
                                log.info(post.getImgName() + " 삭제 성공");
                            }else{
                                log.info(post.getImgName() + " 삭제 실패");
                            }
                        }else{
                            log.info(post.getImgName() + "이 존재하지 않습니다.");
                        }

                        List<Comment> commentListByPost = commentRepository.findAllByPost(post);
                        commentListByPost.forEach(comment -> {
                            List<Reply> replyListByComment = replyRepository.findAllByComment(comment);
                            replyRepository.deleteAll(replyListByComment);
                            List<CommentLike> commentLikeListByComment = commentLikeRepository.findAllByComment(comment);
                            commentLikeRepository.deleteAll(commentLikeListByComment);
                        });
                        commentRepository.deleteAll(commentListByPost);

                        List<PostLike> postLIkeListByPost = postLikeRepository.findByPostId(post);
                        postLikeRepository.deleteAll(postLIkeListByPost);

                        List<AnonymityMember> anonymityMemberList = anonymityMemberRepository.findAllByPost(post);
                        anonymityMemberRepository.deleteAll(anonymityMemberList);

                        //Todo 신고된 글 삭제 구현 예정
                    });
                    postRepository.deleteAll(postList);

                    memberRepository.delete(member);

                    accountClosureMemberRepository.delete(ac);
                });
    }
}
