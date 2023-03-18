import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

class Logo extends StatelessWidget {
  final TextStyle style;
  final String text;

  const Logo({required this.text, required this.style, Key? key})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(crossAxisAlignment: CrossAxisAlignment.stretch, children: [
      SizedBox(height: 80.h),
      Image.asset(
        'asset/image/logo.png',
        width: 100.w,
        height: 100.h,
        alignment: Alignment.centerLeft,
      ),
      SizedBox(height: 20.h),
      Text(text,
          style: style.copyWith(
            fontSize: 30.sp,
            fontWeight: FontWeight.w500,
          )),
    ]);
  }
}
