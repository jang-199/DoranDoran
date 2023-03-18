import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

class MainLogo extends StatelessWidget {
  final String text;
  final TextStyle style;

  const MainLogo({required this.text, required this.style, Key? key})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(crossAxisAlignment: CrossAxisAlignment.center, children: [
      SizedBox(height: 100),
      Image.asset(
        'asset/image/logo.png',
        width: 135.w,
        height: 135.h,
        alignment: Alignment.centerLeft,
      ),
      SizedBox(height: 20),
      Text(text,
          style: style.copyWith(
            fontSize: 30,
            fontWeight: FontWeight.w800,
          )),
      Text("익명 커뮤니티 서비스 어쩌고저쩌고.",
          style: style.copyWith(
            fontSize: 20,
          )),
    ]);
  }
}
