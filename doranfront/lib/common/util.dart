import 'dart:ui';

import 'package:dorandoran/common/storage.dart';
import 'package:flutter/src/painting/text_style.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:geolocator/geolocator.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:shared_preferences/shared_preferences.dart';

//00 시간 출력설정
String getTimeFormat(int number) {
  return number.toString().padLeft(2, '0');
}

//년/월/초 출력
String timecount(String time) {
  DateTime today = DateTime.now();
  String timer = time.replaceAll(RegExp('\\D'), "");
  int year = int.parse(timer.substring(0, 4));
  int month = int.parse(timer.substring(4, 6));
  int day = int.parse(timer.substring(6, 8));
  int hour = int.parse(timer.substring(8, 10));
  int min = int.parse(timer.substring(10, 12));
  int second = int.parse(timer.substring(12, 14));

  int daycheck;
  daycheck =
      today.difference(DateTime(year, month, day, hour, min, second)).inDays;
  if (daycheck > 365) {
    return "${(daycheck / 365).toInt()}년 전ㅤ";
  } else if (daycheck > 31) {
    return "${(daycheck / 30).toInt()}달 전ㅤ";
  } else if (daycheck > 0) {
    return "${daycheck.toInt()}일 전ㅤ";
  } else {
    daycheck = today
        .difference(DateTime(year, month, day, hour, min, second))
        .inSeconds;
    if (daycheck > (60 * 60)) {
      return "${(daycheck / (60 * 60)).toInt()}시간 전";
    } else if (daycheck > 60) {
      return "${(daycheck / 60).toInt()}분 전ㅤ";
    } else {
      return "${daycheck.toInt()}초 전ㅤ";
    }
  }
}


//권한요청
void permissionquest() async {
  Map<Permission, PermissionStatus> statuses = await [
    Permission.locationWhenInUse,
    Permission.photos,
  ].request();
}

//이름체크
String checkname(String name) {
  if (name == '') {
    //null상태
    return "닉네임을 입력해주세요.";
  } else if (name.length <= 1) {
    return "닉네임의 길이는 최소 2글자 이상이어야 합니다.";
  } else if (!RegExp(r"^[가-힣0-9a-zA-Z]*$").hasMatch(name)) {
    //제대로된 문자인지 확인.(특수문자.이모티콘 체크)
    return "올바르지 않은 닉네임입니다.";
  } else {
    //형식은 통과
    return '';
  }
}

void getlocation() async {
  //현재위치 가져오기
  Position position = await Geolocator.getCurrentPosition();
  final prefs = await SharedPreferences.getInstance();
  prefs.setString('latitude', position.latitude.toString());
    latitude = position.latitude.toString();
  prefs.setString('longitude', position.longitude.toString());
    longtitude = position.longitude.toString();
}

//스타일가져오기
TextStyle selectfont(String font, String fontColor, int fontSize, int fontBold){
  Color color=fontColor=="black" ? Color(0xFF000000):Color(0xFFFFFFFF);
  TextStyle style = GoogleFonts.getFont(font, textStyle: TextStyle(fontSize: fontSize.sp, color:color, fontWeight: FontWeight.bold));
  return style;

}