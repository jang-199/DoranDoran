import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:dorandoran/common/storage.dart';
import 'package:dorandoran/common/util.dart';
import 'package:dorandoran/texting/get/quest/like.dart';

import '../screen/post_detail.dart'; //??

class Message_Card extends StatefulWidget {
  final VoidCallback movetocard;
  final String time;
  final int heart;
  final int? chat;
  final int? map;
  final String message;
  final String backimg;
  final int postId;
  final bool likeresult;
  final String font;
  final String fontColor;
  final int fontSize;
  final int fontBold;

  const Message_Card(
      {required this.postId,
      required this.movetocard,
      required this.time,
      required this.heart,
      required this.chat,
      required this.map,
      required this.message,
      required this.backimg,
      required this.likeresult,
      required this.font,
      required this.fontColor,
      required this.fontSize,
      required this.fontBold,
      Key? key})
      : super(key: key);

  @override
  State<Message_Card> createState() => _Message_CardState();
}

//bool like=false;
Map<int, bool> like = {0: false};
Map<int, int> click = {0: 0};

class _Message_CardState extends State<Message_Card> {
  @override
  void initState() {
    //print("응애는초기화에요");
    setState(() {
      //  like=widget.likeresult;
      like.addAll({widget.postId: widget.likeresult});
      click.addAll({widget.postId: widget.heart});
    });
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16.r)),
      elevation: 2, //그림자
      child: InkWell(
        onTap: widget.movetocard,
      //  widget.movetocard
        child: Container(
          decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(16.r),
              image: DecorationImage(
                image: NetworkImage('http://' + widget.backimg),
                fit: BoxFit.cover,
                colorFilter: ColorFilter.mode(
                    Colors.black.withOpacity(0.7), BlendMode.dstATop),
              )),
          //BoxDecoration(image: DecorationImage(image:NetworkImage('http://'+backimg))),
          child: Padding(
              padding: const EdgeInsets.only(left: 20, right: 20),
              child: Column(
                children: [
                  SizedBox(
                    height: 150.h,
                    child: Container(
                      alignment: Alignment.center,
                      child: Padding(
                        padding: const EdgeInsets.all(20.0),
                        child: Text(widget.message,
                            maxLines: 4,
                            overflow: TextOverflow.ellipsis,
                            style: selectfont(widget.font, widget.fontColor,
                                widget.fontSize, widget.fontBold)),
                      ),
                    ),
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Row(
                        children: [
                          Icon(Icons.access_time_filled_rounded),
                          SizedBox(width: 3.w),
                          Text(timecount(widget.time)),
                          SizedBox(width: 7.w),
                          if (widget.map != null) Icon(Icons.place),
                          Text(widget.map == null ? '' : '${widget.map}km'),
                        ],
                      ),
                      Row(
                        children: [
                          IconButton(
                            onPressed: () {
                              setState(() {
                                print(like);
                                like[widget.postId] = !like[widget.postId]!;
                                if (widget.likeresult == true &&
                                    like[widget.postId] == false) {
                                  //눌린상태에서 취소
                                  click[widget.postId] =
                                      click[widget.postId]! - 1;
                                } else if (widget.likeresult == false &&
                                    like[widget.postId] == true) {
                                  //누르기
                                  click[widget.postId] =
                                      click[widget.postId]! + 1;
                                } else {
                                  //해당화면에서 상태변경취소
                                  click[widget.postId] = widget.heart;
                                }
                              });
                              postLike(widget.postId, useremail!);
                            },
                            icon: like[widget.postId]!
                                ? Icon(Icons.favorite)
                                : Icon(Icons.favorite_border),
                            constraints: BoxConstraints(),
                            padding: EdgeInsets.zero,
                          ),
                          SizedBox(width: 3.w),
                          Text('${click[widget.postId]}'),
                          SizedBox(width: 7.w),
                          Icon(Icons.forum),
                          SizedBox(width: 3.w),
                          Text('${widget.chat}'),
                        ],
                      ),
                    ],
                  ),
                  SizedBox(height: 10.h),
                ],
              )),
        ),
      ),
    );
  }
}
