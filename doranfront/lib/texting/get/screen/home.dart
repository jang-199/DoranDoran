import 'package:dorandoran/common/css.dart';
import 'package:dorandoran/common/storage.dart';
import 'package:dorandoran/texting/get/component/home_message_card.dart';
import 'package:dorandoran/texting/get/quest/post.dart';
import 'package:dorandoran/texting/get/screen/post_detail.dart';
import 'package:dorandoran/texting/write/screen/write.dart';
import 'package:dropdown_button2/dropdown_button2.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:dorandoran/common/util.dart';
import 'package:pull_to_refresh/pull_to_refresh.dart';

import '../component/home_tag.dart';
import '../component/home_top.dart';

class Home extends StatefulWidget {
  const Home({Key? key}) : super(key: key);

  @override
  State<Home> createState() => _HomeState();
}

class _HomeState extends State<Home> {
  RefreshController _refreshController = RefreshController(initialRefresh: false);
  ScrollController scrollController = ScrollController();
  late Future myfuture;
  List<Message_Card>? item;
  int? checknumber;

  @override
  void initState() {
    super.initState();
    setState(() {
      _refreshController = RefreshController(initialRefresh: false);
      scrollController = ScrollController();
    });
    getlocation(); //임시
    myfuture = getPostContent(useremail, 0, latitude == '' ? '' : '$latitude,$longtitude');
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        backgroundColor: backgroundcolor,
        body: FutureBuilder(
            future: myfuture,
            builder: (context, snapshot) {
              if (snapshot.hasData) {
                int lastnumber = snapshot.data!.last.postId;
                if (snapshot.connectionState == ConnectionState.done) {
                  if (item?.length == 0 || item == null) {
                    item = snapshot.data!.map<Message_Card>((e) => Message_Card(
                              time: e.postTime,
                              heart: e.likeCnt,
                              chat: e.replyCnt,
                              map: e.location,
                              message: e.contents,
                              backimg: e.backgroundPicUri,
                              postId: e.postId,
                              likeresult: e.likeResult,
                      font: e.font,
                      fontColor: e.fontColor,
                      fontSize: e.fontSize,
                      fontBold: e.fontBold,
                            )).toList();
                  } else {
                    if (checknumber != lastnumber) {
                      item!.addAll(snapshot.data!.map<Message_Card>((e) => Message_Card(
                                time: e.postTime,
                                heart: e.likeCnt,
                                chat: e.replyCnt,
                                map: e.location,
                                message: e.contents,
                                backimg: e.backgroundPicUri,
                                postId: e.postId,
                                likeresult: e.likeResult,
                        font: e.font,
                        fontColor: e.fontColor,
                        fontSize: e.fontSize,
                        fontBold: e.fontBold,

                      )).toList());
                    }
                  }
                }
                return Container(
                  decoration: gradient,
                  child: SafeArea(
                    child: Padding(
                      padding: const EdgeInsets.all(20),
                      child: SafeArea(
                        child: Stack(children: [
                          Column(
                            children: [
                              Top(),
                              SizedBox(height: 10.h),
                              Tag(),
                              Expanded(
                                child: SmartRefresher(
                                  enablePullDown: true,
                                  enablePullUp: true,
                                  header: CustomHeader(
                                    builder: (BuildContext context, RefreshStatus? mode) {
                                      Widget body;
                                      if (mode == RefreshStatus.refreshing) body = CupertinoActivityIndicator();
                                      else body = Text('');

                                      return Container(
                                        height: 55.0,
                                        child: Center(child: body),
                                      );
                                    },
                                  ),
                                  footer: CustomFooter(
                                    builder: (BuildContext context, LoadStatus) {
                                      return Container(
                                        height: 55.0,
                                        child: Center(child: Text("")),
                                      );
                                    },
                                  ),
                                  onRefresh: () async {
                                    setState(() {
                                      item!.clear();
                                      myfuture = getPostContent(useremail, 0, latitude == null ? '' : '$latitude,$longtitude');
                                    });
                                    _refreshController.refreshCompleted();
                                  },
                                  // 새로고침
                                  onLoading: () async {
                                    if (lastnumber - 1 > 0) {
                                      setState(() {
                                        myfuture = getPostContent(useremail, lastnumber - 1, latitude == null ? '' : '$latitude,$longtitude');
                                        checknumber = lastnumber;
                                      });
                                      _refreshController.loadComplete();
                                    }
                                  },
                                  controller: _refreshController,
                                  child: ListView(
                                    controller: scrollController,
                                    children:
                                        item!.map<Widget>((e) => e).toList(),
                                  ),
                                ),
                              )
                            ],
                          ),
                          Container(
                            alignment: Alignment.bottomRight,
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.end,
                              children: [
                                RawMaterialButton(
                                  onPressed: () async {
                                    _refreshController.position!.animateTo(
                                      0.0,
                                      duration: const Duration(milliseconds: 300),
                                      curve: Curves.linear,
                                    );
                                    setState(() {
                                      item!.clear();
                                      myfuture = getPostContent(
                                          useremail,
                                          0,
                                          latitude == null ? '' : '$latitude,$longtitude');
                                    });
                                    _refreshController.refreshCompleted();
                                  },
                                  elevation: 5.0,
                                  fillColor: Colors.white,
                                  child: Icon(Icons.restart_alt, size: 20.0.r,),
                                  padding: EdgeInsets.all(15.0),
                                  shape: CircleBorder(),
                                ),
                                SizedBox(height: 5.h),
                                RawMaterialButton(
                                  onPressed: () {
                                    Navigator.push(context, MaterialPageRoute(builder: (context) => Write()));
                                  },
                                  elevation: 5.0,
                                  fillColor: Colors.white,
                                  child: Icon(Icons.edit, size: 50.0,),
                                  padding: EdgeInsets.all(15.0),
                                  shape: CircleBorder(),
                                )
                              ],
                            ),
                          )
                        ]),
                      ),
                    ),
                  ),
                );
              } else {
                return Container(
                    decoration: gradient,
                    child: Center(child: CircularProgressIndicator()));
              }
            }));
  }
}