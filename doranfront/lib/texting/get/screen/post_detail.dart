import 'package:dorandoran/common/css.dart';
import 'package:dorandoran/texting/get/quest/get_post_detail.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import '../component/post_detail_card.dart';
import '../component/post_detaili_commentcard.dart';

class PostDetail extends StatefulWidget {

  const PostDetail({
    Key? key}) : super(key: key);

  @override
  State<PostDetail> createState() => _PostDetailState();
}

class _PostDetailState extends State<PostDetail> {
  int number=1;
  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: FutureBuilder(
            future: getpostDetail(2,"7@gmail.com", ""),
            builder:(context, snapshot) {
              if(snapshot.hasData){
                dynamic e = snapshot.data!;
                print(e.commentDetailDto);
                return Container(
                    alignment: Alignment.topCenter,
                    decoration: gradient,
                    child:ListView(
                      padding: EdgeInsets.zero,
                      children: [
                              Detail_Card(
                                postNickname: e.postNickname,
                                postId: 4,
                                content: e.content,
                                postTime: e.postTime,
                                location: e.location,
                                postLikeCnt: e.postLikeCnt,
                                postLikeResult: e.postLikeResult,
                                commentCnt: e.commentCnt,
                                backgroundPicUri: e.backgroundPicUri,
                                postHashes: e.postHashes,
                                font: e.font,
                                fontBold: e.fontBold,
                                fontColor: e.fontColor,
                                fontSize: e.fontSize,
                              ),
                        SizedBox(height: 10.h),
                        ListBody(
                          children:
                          e.commentDetailDto!=null ?
                          e.commentDetailDto.map<CommentCard>((a) =>
                                  CommentCard(
                                      number: number,
                                      upnumber: upnumber,
                                      commentId: a['commentId'],
                                      comment: a['comment'],
                                      commentLike: a['commentLike'],
                                      commentLikeResult: a['commentLikeResult'],
                                      replies: a['replies'],
                                      commentNickname:a['commentNickname'],
                                      commentTime:a['commentTime']
                                  )).toList():[
                                    Center(
                                     child: Card(
                                       child: SizedBox(
                                         height: 300.h,
                                         width: 350.w,
                                         child:  Center(
                                           child:Text("작성된 댓글이 없습니다"),
                                         )
                                       )
                                     )
                                    )
                          ]
                        )
                      ],
                    )
                );
              }
              else{
                return Container(
                    decoration: gradient,
                    child: Center(child: CircularProgressIndicator()));
              }
            }
        )
    );
  }
  upnumber(){
    setState(() {
      number=number+1;
    });
  }
}