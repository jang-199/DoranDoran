import 'package:flutter/material.dart';
import '../../../common/css.dart';

Widget inputfield(TextEditingController name){
  return TextField(
    style: whitestyle,
    decoration: InputDecoration(
      enabledBorder: UnderlineInputBorder(
          borderSide:
          BorderSide(color: Colors.white)),
      hintText: "닉네임을 입력해주세요",
      hintStyle:
      whitestyle.copyWith(color: Colors.indigo),
    ),
    controller: name,
    maxLength: 8,
  );
}