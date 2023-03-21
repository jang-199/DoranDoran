import 'package:dorandoran/common/storage.dart';
import 'package:dorandoran/texting/get/component/post_detail_reply_card.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../../common/util.dart';
import '../quest/post_detail_commentlike.dart';
import '../screen/post_detail.dart';

class CommentCard extends StatefulWidget {
  final int commentId;
  final String comment;
  final int commentLike;
  final bool commentLikeResult;
  final List<dynamic>? replies;
  final String? commentNickname;
  final String commentTime;
  final int number;
  final VoidCallback upnumber;
  final VoidCallback changeinputtarget;
  final int postId;

  const CommentCard(
      {required this.commentId,
      required this.comment,
      required this.commentLike,
      required this.commentLikeResult,
      required this.replies,
      required this.commentNickname,
      required this.commentTime,
      required this.number,
      required this.upnumber,
        required this.postId,
        required this.changeinputtarget,
      Key? key})
      : super(key: key);

  @override
  State<CommentCard> createState() => _CommentCardState();
}

Map<int, bool> commentlike = {0: false};
Map<int, int> commentlikecnt = {0: 0};
class _CommentCardState extends State<CommentCard> {
  @override
  void initState() {
    if (widget.commentNickname == null) widget.upnumber;
    setState(() {
      commentlike.addAll({widget.commentId: widget.commentLikeResult});
      commentlikecnt.addAll({widget.commentId: widget.commentLike});
    });
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    Widget replycardd=Container();
    return FutureBuilder(
        future: getreply(widget.replies),
    builder:(context, snapshot)  {
          if(snapshot.hasData) replycardd= snapshot.data!;{
      return Column(children: [
      Container(
          child: Card(
              shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(16.r)),
              elevation: 2, //그림자
              child: Padding(
                  padding: EdgeInsets.all(15),
                  child: Row(children: [
                    Icon(
                      Icons.person,
                      size: 50.r,
                    ),
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          widget.commentNickname ?? "익명${widget.number}",
                          style: GoogleFonts.jua(fontSize: 17.sp),
                        ),
                        Text(
                          widget.comment,
                          style: GoogleFonts.jua(),
                        ),
                        Row(children: [
                          Text(
                            timecount(widget.commentTime),
                            style: TextStyle(fontSize: 12.sp),
                          ),
                          SizedBox(
                            width: 165.w,
                          ),
                          Row(
                            //하트버튼
                            children: [
                              IconButton(
                                onPressed: () {
                                  setState(() {
                                    commentlike[widget.commentId] =
                                        !commentlike[widget.commentId]!;
                                    if (widget.commentLikeResult == true &&
                                        commentlike[widget.commentId] ==
                                            false) {
                                      //눌린상태에서 취소
                                      commentlikecnt[widget.commentId] =
                                          commentlikecnt[widget.commentId]! - 1;
                                    } else if (widget.commentLikeResult ==
                                            false &&
                                        commentlike[widget.commentId] == true) {
                                      //누르기
                                      commentlikecnt[widget.commentId] =
                                          commentlikecnt[widget.commentId]! + 1;
                                    } else {
                                      //해당화면에서 상태변경취소
                                      commentlikecnt[widget.commentId] =
                                          widget.commentLike;
                                    }
                                  });
                                  // 댓글좋아요
                                  commentLike(widget.postId,widget.commentId, useremail);
                                },
                                icon: commentlike[widget.commentId]!
                                    ? Icon(Icons.favorite)
                                    : Icon(Icons.favorite_border),
                                constraints: BoxConstraints(),
                                padding: EdgeInsets.zero,
                              ),
                              SizedBox(width: 2.w),
                              Text('${commentlikecnt[widget.commentId]}'),
                              SizedBox(width: 5.w),
                              IconButton(
                                  padding: EdgeInsets.zero,
                                  constraints: BoxConstraints(),
                                  onPressed: (){
                                    select=widget.commentId;
                                    widget.changeinputtarget();
                                  },
                                  icon: Icon(Icons.chat_bubble_outline)),
                              SizedBox(width: 2.w),
                              Text('${widget.replies!.length}'),
                            ],
                          ),
                        ])
                      ],
                    ),
                  ])))),
      replycardd
    ]);
    }});
  }

  Future<Widget> getreply(dynamic replies) async {
    return replies!=null ?
    ListBody(
        children:
        await replies!.map<ReplyCard>((a) =>
            ReplyCard(
                replyId: a['replyId'],
                replyNickname:  a['replyNickname'],
                reply:  a['reply'],
                replyTime:  a['replyTime'],
                number:  1
            )).toList()
    ):Center();
  }
}
