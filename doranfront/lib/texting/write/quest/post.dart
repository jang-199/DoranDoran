
//글써서 보내기
import 'dart:convert';
import 'dart:io';
import 'package:dorandoran/common/uri.dart';
import 'package:dio/dio.dart';

//formdata형식
Future<int> writing(String email,String content, bool forme, String? locations, String? backgroundImgName, List<String>? hashTag, MultipartFile? file) async {
  var dio=Dio();
  var response = await dio.post(
    posturl.toString(),
    data: FormData.fromMap({
        'email': email,
        'content':content,
        'forMe':forme,
        'location':locations,
        'backgroundImgName':backgroundImgName,
        'hashTagName':hashTag,
        'file':file,
    })
  );
  if (response.statusCode == 200) {
    print(200);
    return 200;
  } else {
    print(400);
    return 400;
  }
}
