import 'dart:io';
import 'dart:math';
import 'package:dorandoran/common/util.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:image_picker/image_picker.dart';
import '../../../common/css.dart';
import 'package:dorandoran/common/uri.dart';
import 'package:dorandoran/texting/write/component/write_top.dart';
import '../component/write_middlefield.dart';

class Write extends StatefulWidget {
  const Write({Key? key}) : super(key: key);

  @override
  State<Write> createState() => _WriteState();
}

TextEditingController contextcontroller = TextEditingController();
bool forme = false;
bool usinglocation = false;
File? dummyFille;
List<String> hashtag = [];
String? backgroundimgname = (Random().nextInt(99) + 1).toString();
Set<int> imagenumber = {int.parse(backgroundimgname!)};

class _WriteState extends State<Write> {
  setimagenumber() {
    while (imagenumber.length < 10) {
      imagenumber.add(Random().nextInt(99) + 1);
    }
  }

  @override
  void initState() {
    setState(() {
      contextcontroller = TextEditingController();
      forme = false;
      usinglocation = false;
      hashtag = [];
      dummyFille = null;
      backgroundimgname = (Random().nextInt(99) + 1).toString();
      if (backgroundimgname != null) {
        backimg = Image.network('$url/api/background/' + backgroundimgname!);
        imagenumber = {int.parse(backgroundimgname!)};
      }
    });
    getlocation();
    permissionquest();
    setimagenumber();
  }

  Image backimg = Image.network('$url/api/background/' + '1');

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
          alignment: Alignment.center,
          decoration: gradient,
          child: SafeArea(
            child: Padding(
                padding: const EdgeInsets.all(20.0),
                child: Column(
                  children: [
                    Text(
                      "글 작성하기",
                      style: TextStyle(fontSize: 30.sp),
                    ),
                    Expanded(
                      child: Column(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Top(),
                            Column(children: [
                              MiddleTextField(backimg: backimg),
                              Wrap(
                                  children: hashtag != [] ?
                                  hashtag.map((e) => Chip(label: Text(e))).toList() : [Text("")])
                            ]),
                            Row(
                              //하단메뉴
                              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                              children: [
                                IconButton(
                                    onPressed: () {
                                      setState(() {
                                        forme = !forme;
                                      });
                                    },
                                    icon: forme ? Icon(Icons.lock) : Icon(Icons.lock_open)),
                                IconButton(
                                    onPressed: () {
                                      setState(() {
                                        usinglocation = !usinglocation;
                                      });
                                      print(usinglocation);
                                    },
                                    icon: usinglocation ? Icon(Icons.location_on) : Icon(Icons.location_off_outlined)),
                                IconButton(
                                    onPressed: () {
                                      GetImageFile();
                                    },
                                    icon: Icon(Icons.image)),
                                IconButton(
                                  icon: Icon(Icons.grid_view),
                                  onPressed: () {
                                    showModalBottomSheet<void>(
                                      context: context,
                                      builder: (BuildContext context) {
                                        imagenumber.clear();
                                        if (backgroundimgname != null) {
                                          imagenumber.add(int.parse(backgroundimgname!));
                                        }
                                        while (imagenumber.length < 10) {
                                          imagenumber.add(Random().nextInt(99) + 1);
                                        }
                                        return StatefulBuilder(
                                            builder: (context, StateSetter setState) {
                                          return Container(
                                            height: 200.h,
                                            color: Colors.white70,
                                            child: Column(
                                              children: <Widget>[
                                                Column(
                                                  children: [
                                                    IconButton(
                                                        onPressed: () {
                                                          setState(() {
                                                            imagenumber.clear();
                                                            if (backgroundimgname != null) {
                                                              imagenumber.add(int.parse(backgroundimgname!));
                                                            }
                                                            while (imagenumber.length < 10) {
                                                              imagenumber.add(Random().nextInt(99) + 1);
                                                            }
                                                          });
                                                        },
                                                        icon: Icon(Icons.restart_alt)),
                                                    Wrap(
                                                      children: imagenumber.map((e) => TextButton(
                                                              child: Image.network('$url/api/background/' + e.toString(),
                                                                width: 72.w,
                                                                height: 72.h,
                                                                fit: BoxFit.cover,
                                                                opacity: e.toString() == backgroundimgname ? AlwaysStoppedAnimation<double>(0.3)
                                                                    : AlwaysStoppedAnimation<double>(1),
                                                              ),
                                                              onPressed: () => pickdefaultimg(e),
                                                              style: ButtonStyle(padding: MaterialStateProperty.all(EdgeInsets.zero),
                                                              ),
                                                            ),
                                                          ).toList(),
                                                    ),
                                                  ],
                                                ),
                                              ],
                                            ),
                                          );
                                        });
                                      },
                                    );
                                  },
                                ),
                                IconButton(
                                  //태그
                                  icon: Icon(Icons.tag),
                                  onPressed: () {
                                    showModalBottomSheet<void>(
                                        isDismissible: false,
                                        context: context,
                                        builder: (BuildContext context) {
                                          TextEditingController tagcontroller =
                                              TextEditingController();
                                          return StatefulBuilder(builder:
                                              (BuildContext context,
                                                  StateSetter setState) {
                                            return Container(
                                                height: 200.h,
                                                color: Colors.transparent,
                                                child: Container(
                                                    decoration:
                                                        const BoxDecoration(
                                                      color: Color(0xBBFFFFFF),
                                                      borderRadius: BorderRadius.only(
                                                        topLeft: Radius.circular(30),
                                                        topRight: Radius.circular(30),
                                                      ),
                                                    ),
                                                    child: Center(
                                                      child: Column(
                                                        mainAxisAlignment: MainAxisAlignment.center,
                                                        mainAxisSize: MainAxisSize.min,
                                                        children: <Widget>[
                                                          Container(
                                                            width: 300.w,
                                                            child: TextFormField(
                                                              textInputAction: TextInputAction.go,
                                                              onFieldSubmitted: (value) {
                                                                setState(() {
                                                                  tagcontroller.clear();
                                                                  hashtag.add(value);
                                                                });
                                                                print(hashtag);
                                                              },
                                                              style: TextStyle(fontSize: 20.sp),
                                                              decoration:
                                                                  InputDecoration(
                                                                hintText: "태그명을 입력해주세요",
                                                                hintStyle: whitestyle.copyWith(
                                                                    fontSize: 15.sp,
                                                                    color: Colors.black12
                                                                ),
                                                              ),
                                                              controller: tagcontroller,
                                                            ),
                                                          ),
                                                          Wrap(
                                                            children: hashtag == null ? [Text('')] : hashtag
                                                                    .map((e) => Chip(
                                                                          label: Text(e),
                                                                          onDeleted: () {
                                                                            setState(() {
                                                                              hashtag.removeAt(hashtag.indexOf(e));
                                                                            });
                                                                            hashtag.remove(e);
                                                                          },
                                                                        )).toList(),
                                                          ),
                                                          ElevatedButton(
                                                            child: const Text('완료'),
                                                            onPressed: resetting,
                                                          )
                                                        ],
                                                      ),
                                                    )));
                                          });
                                        });
                                  },
                                )
                              ],
                            ),
                          ]),
                    ),
                  ],
                )),
          )),
    );
  }

  resetting() {
    setState(() {
      backimg = backimg;
    });
    Navigator.pop(context);
  }

  pickdefaultimg(int e) {
    //기본이미지
    dummyFille = null;
    Navigator.pop(context);
    setState(() {
      backgroundimgname = e.toString();
      if (backgroundimgname != null) {
        backimg = Image.network('$url/api/background/' + backgroundimgname!);
      }
    });
  }

  GetImageFile() async {
    //사용자이미지
    XFile? f = await ImagePicker().pickImage(source: ImageSource.gallery);
    if (f != null) {
      dummyFille = File(f.path);
    }
    print(dummyFille);
    setState(() {
      backgroundimgname = null;
      backimg = Image.file(dummyFille!);
    });
  }
}