import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:google_fonts/google_fonts.dart';

import '../../../common/util.dart';

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

  const CommentCard({
    required this.commentId,
    required this.comment,
    required this.commentLike,
    required this.commentLikeResult,
    required this.replies,
    required this.commentNickname,
    required this.commentTime,
    required this.number,
    required this.upnumber,
    Key? key}) : super(key: key);

  @override
  State<CommentCard> createState() => _CommentCardState();
}
Map<int, bool> commentlike = {0: false};
Map<int, int> commentlikecnt = {0: 0};
class _CommentCardState extends State<CommentCard> {
  @override
  void initState() {
    if(widget.commentNickname==null) widget.upnumber();
    super.initState();
    setState(() {
      commentlike.addAll({widget.commentId: widget.commentLikeResult});
      commentlikecnt.addAll({widget.commentId: widget.commentLike});
    });
  }

  @override
  Widget build(BuildContext context) {
    return Container(
        child:Card(
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16.r)),
            elevation: 2, //그림자
            child: Padding(padding: EdgeInsets.all(15),
                child:
                Row(
                    children:[
                      Icon(Icons.person, size: 50.r,),
                      Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(widget.commentNickname??"익명${widget.number}",style: GoogleFonts.jua(fontSize: 17.sp),),
                          Text(widget.comment, style: GoogleFonts.jua(),),
                          Row(
                              children: [
                                Row(
                                  children: [
                                    Text(timecount(widget.commentTime),style: TextStyle(fontSize: 12.sp),),
                                    SizedBox(width: 165.w),
                                    Row( //하트버튼
                                      children: [
                                        IconButton(
                                          onPressed: () {
                                            setState(() {
                                              commentlike[widget.commentId] = !commentlike[widget.commentId]!;
                                              if (widget.commentLikeResult == true &&
                                                  commentlike[widget.commentId] == false) {
                                                //눌린상태에서 취소
                                                commentlikecnt[widget.commentId] =
                                                    commentlikecnt[widget.commentId]! -1;
                                              } else if (widget.commentLikeResult == false &&
                                                  commentlike[widget.commentId] == true) {
                                                //누르기
                                                commentlikecnt[widget.commentId] =
                                                    commentlikecnt[widget.commentId]! + 1;
                                              } else {
                                                //해당화면에서 상태변경취소
                                                commentlikecnt[widget.commentId] = widget.commentLike;
                                              }
                                              print(commentlikecnt[widget.commentId]);
                                              print(commentlike[widget.commentId]);
                                            });
                                            // 댓글좋아요
                                            //postLike(widget.postId, useremail!);
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
                                        Icon(Icons.forum),
                                        SizedBox(width: 2.w),
                                        Text('${widget.replies!.length}'),
                                      ],
                                    ),
                                  ],
                                ),
                              ]
                          )
                        ],
                      ),
                    ]
                )
            )
        ));
  }
}
