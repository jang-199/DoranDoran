import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

//폰트스타일
final TextStyle whitestyle = TextStyle(
  color: Colors.white,
  fontSize: 17.0,
  fontWeight: FontWeight.w500,
);

//배경색상
Color backgroundcolor=Color(0xFF000054);

//배경색상2(그라데이션)
Decoration gradient= BoxDecoration(
borderRadius: BorderRadius.circular(80),
gradient: LinearGradient(
begin: Alignment.topLeft,
end: Alignment.bottomRight,
colors: [Color(0xff3F0099),Color(0xFF000054)],
));

//서버 url
final url=Uri.parse(
  'http://124.60.219.83:8080/api/signup'
);

//00 시간 설절
String getTimeFormat(int number) {
return number.toString().padLeft(2, '0');
}