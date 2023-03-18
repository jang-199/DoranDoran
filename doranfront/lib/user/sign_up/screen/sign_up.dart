import 'package:dorandoran/user/sign_up/component/InputField.dart';
import 'package:dorandoran/user/sign_up/component/logo.dart';
import 'package:dorandoran/user/sign_up/component/next_button.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:dorandoran/common/css.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:dorandoran/common/util.dart';
import '../quest/namecheck.dart';

class SignUp extends StatefulWidget {
  const SignUp({Key? key}) : super(key: key);

  @override
  State<SignUp> createState() => _SignUpState();
}

class _SignUpState extends State<SignUp> {
  DateTime selectedDate = DateTime.now();
  TextEditingController name = TextEditingController();
  Map<String, bool> namecheck = {'': false};
  String text = "";

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: backgroundcolor,
      body: Container(
        decoration: gradient,
        child: SafeArea(
          child: Padding(
            padding: const EdgeInsets.only(left: 30, right: 30),
            child: Column(children: [
              Logo(text: '첫 방문을\n환영합니다!', style: whitestyle),
              SizedBox(height: 50.h),
              Column(
                children: [
                  Column(
                      crossAxisAlignment: CrossAxisAlignment.stretch,
                      children: [
                        Text('닉네임을 설정해주세요',
                            style: whitestyle.copyWith(fontSize: 20.sp)),
                        SizedBox(height: 5.h),
                        Row(
                          children: [
                            Container(
                              child: inputfield(name),
                              width: 220.w,
                            ),
                            SizedBox(width: 15.w),
                            TextButton(
                                onPressed: (){
                                  print("체크");
                                  textchange(checkname(name.text.toString()),name.text.toString());
                                },
                                child: Text("확인"),
                                style: TextButton.styleFrom(
                                    primary: Colors.white,
                                    side: BorderSide(
                                      color: Colors.white,
                                    ))),
                          ],
                        ),
                        Text(
                          text,
                          style: text == '사용가능한 이름입니다.' ? whitestyle.copyWith(color: Colors.blue, fontSize: 14.sp) : whitestyle.copyWith(color: Colors.red, fontSize: 14.sp),
                        ),
                      ]),
                  SizedBox(height: 12.h),
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      Text('생년월일을 선택해주세요',
                          style: whitestyle.copyWith(fontSize: 20.sp)),
                      SizedBox(height: 10.h),
                      Container(
                        width: 240.w,
                        height: 40.h,
                        child: TextButton(
                          child: Text(
                            '${selectedDate.year}-${getTimeFormat(selectedDate.month)}-${getTimeFormat(selectedDate.day)}',
                            style: whitestyle.copyWith(fontSize: 15.sp),
                          ),
                          style: TextButton.styleFrom(
                              primary: Colors.white,
                              side: BorderSide(
                                color: Colors.white,
                              )),
                          onPressed: dialog,
                        ),
                      ),
                    ],
                  ),
                  SizedBox(height: 30.h),
                  NextButton(
                    selectedDate: selectedDate,
                    name: name,
                    namecheck: namecheck,
                  )
                ],
              ),
            ]),
          ),
        ),
      ),
    );
  }

  void textchange(String context, String name) {
    if(context=='') {
      postNameCheckRequest(name).then((value) {
        if (value == 200) {
          context = '사용가능한 이름입니다.';
          namecheck[name] = true;
        } else {
          context = '이미 사용중인 이름입니다.';
          namecheck[name] = false;
        }
      });
    }
    setState(() {
      text = context;
    });
  }

  void dialog(){
    showCupertinoDialog(
      context: context,
      barrierDismissible: true,
      builder: (BuildContext context) {
        return Align(
          alignment: Alignment.bottomCenter,
          child: Container(
            color: Color(0xDDFFFFFF), //색상,,
            height: 250.0.h,
            child: CupertinoDatePicker(
              mode: CupertinoDatePickerMode.date,
              initialDateTime: selectedDate,
              maximumYear: DateTime.now().year,
              maximumDate: DateTime.now(),
              onDateTimeChanged: (DateTime date) {
                setState(() {
                  selectedDate = date; //사용자가 선택한 날짜 저장
                });
              },
            ),
          ),
        );
      },
    );
  }

}
