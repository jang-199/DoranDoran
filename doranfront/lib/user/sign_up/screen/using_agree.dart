import 'package:dorandoran/user/sign_up/component/agree_button.dart';
import 'package:dorandoran/user/sign_up/component/logo.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:dorandoran/common/css.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

class UsingAgree extends StatelessWidget {
  final TextStyle textStyle = whitestyle;

  UsingAgree({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: Container(
      decoration: gradient,
      child: SafeArea(
        child: Padding(
          padding: const EdgeInsets.only(left: 30.0, right: 30),
          child: Column(
            children: [
              Logo(text: '회원님,\n안녕하세요!', style: textStyle),
              SizedBox(height: 100.h),
              AgreeButton(style: textStyle),
              SizedBox(height: 20.h),
            ],
          ),
        ),
      ),
    ));
  }
}
