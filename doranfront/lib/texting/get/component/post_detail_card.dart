import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

import '../../../common/storage.dart';
import '../../../common/util.dart';
import '../quest/like.dart';


class Detail_Card extends StatefulWidget {
  final int postId;
  final String content;
  final String postTime;
  final String? location;
  final int postLikeCnt;
  final bool? postLikeResult;
  final int commentCnt;
  final String backgroundPicUri;
  final List<dynamic>? postHashes;
  final String? postNickname;
  final String font;
  final String fontColor;
  final int fontSize;
  final int fontBold;

  const Detail_Card({
    required this.postNickname,
    required this.postId,
    required this.content,
    required this.postTime,
    required this.location,
    required this.postLikeCnt,
    required this.postLikeResult,
    required this.commentCnt,
    required this.backgroundPicUri,
    required this.postHashes,
    required this.font,
    required this.fontColor,
    required this.fontSize,
    required this.fontBold,
    Key? key}) : super(key: key);

  @override
  State<Detail_Card> createState() => _Detail_CardState();
}

bool like=false;
int likecnt=0;
class _Detail_CardState extends State<Detail_Card> {
  @override
  void initState() {
    super.initState();
    setState(() {
      like=widget.postLikeResult!;
      likecnt=widget.postLikeCnt;
    });
  }
  @override
  Widget build(BuildContext context) {
    return Column(
        children: [InkWell(
          child: Container(
            decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(16.r),
                image: DecorationImage(
                  image: NetworkImage('http://' + widget.backgroundPicUri),
                  fit: BoxFit.cover,
                  colorFilter: ColorFilter.mode(
                      Colors.black.withOpacity(0.7), BlendMode.dstATop),
                )),
            //BoxDecoration(image: DecorationImage(image:NetworkImage('http://'+backimg))),
            child: Padding(
                padding: const EdgeInsets.only(left: 20, right: 20),
                child: Column(
                  children: [
                    SizedBox(height: 20.h),
                    Container(
                      alignment: Alignment.bottomLeft,
                      child: BackButton(),
                    ),
                    SizedBox(
                      height: 450.h,
                      child: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children:[ Text(widget.content,
                              maxLines: 4,
                              overflow: TextOverflow.ellipsis,
                              style:  selectfont(widget.font,widget.fontColor,widget.fontSize,widget.fontBold)),
                            SizedBox(height:20.h),
                            Text("by ${widget.postNickname?? "익명"}",
                                maxLines: 4,
                                overflow: TextOverflow.ellipsis,
                                style: selectfont(widget.font,widget.fontColor,widget.fontSize,widget.fontBold).copyWith(fontSize: 12.sp)),
                          ]
                      ),
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Row(
                          children: [
                            Icon(Icons.access_time_filled_rounded),
                            SizedBox(width: 3.w),
                            Text(timecount(widget.postTime)),
                            SizedBox(width: 7.w),
                            if (widget.location != null) Icon(Icons.place),
                            Text(widget.location == null ? '' : '${widget.location}km'),
                          ],
                        ),
                        Row( //하트버튼
                          children: [
                            IconButton(
                              onPressed: () {
                                setState(() {
                                  like= !like;
                                  if(likecnt!=widget.postLikeResult && like==false) { //화면에서 취소누르면,,
                                    likecnt=likecnt-1;
                                  }
                                  else if(likecnt!=widget.postLikeResult && like==true){ //화면에서 좋아요
                                    likecnt=widget.postLikeCnt+1;
                                  }
                                  else{
                                    likecnt=widget.postLikeCnt;
                                  }
                                });
                                postLike(widget.postId, useremail!);
                              },
                              icon: like!
                                  ? Icon(Icons.favorite)
                                  : Icon(Icons.favorite_border),
                              constraints: BoxConstraints(),
                              padding: EdgeInsets.zero,
                            ),
                            SizedBox(width: 3.w),
                            Text('${likecnt}'),
                            SizedBox(width: 7.w),
                            Icon(Icons.forum),
                            SizedBox(width: 3.w),
                            Text('${widget.commentCnt}'),
                          ],
                        ),
                      ],
                    ),
                    SizedBox(height: 10.h),
                  ],
                )),
          ),
        ),
        ]
    );
  }
}
