import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:dorandoran/const/storage.dart';

//회원가입: 데이터 전송 500
postUserRequest(String dateOfBirth, String nickName, String firebasetoken,
    String kakaoAccessToken) async {
  var response = await http.post(
    signupurl,
    headers: <String, String>{
      'Content-Type': 'application/json',
    },
    body: jsonEncode({
      "dateOfBirth": dateOfBirth,
      "nickName": nickName,
      "firebaseToken": firebasetoken,
      "kakaoAccessToken": kakaoAccessToken
    }),
  );
  print(response.headers);
  print(response.body);
  if (response.statusCode == 201 || response.statusCode == 200) {
    print("서버 통신양호");
  } else if (response.statusCode == 504) {
    print("서버와의 연결이 불안정 합니다.");
  } else {
    print("종목코드가 올바르지 않습니다.");
    throw Exception('Failed to contect Server.');
  }
}

//사용가능닉네임 statusCode:200, 불가능닉네임 statusCode:400
Future<int> postNameCheckRequest(
    // not-null해야됨 / 글자초과x(8자) / 이미 요청하고 안된다고 요청받은 닉네임은 따로 저장해서 막음 / 특수문자제한 / 이모티콘사용불가처리.
    String nickName) async {
  var response = await http.post(
    namecheckurl,
    headers: <String, String>{
      'Content-Type': 'application/json',
    },
    body: jsonEncode({
      "nickname": nickName,
    }),
  );
  if (response.statusCode == 200) {
    return 200;
  } else if (response.statusCode == 500) {
    return 400;
  } else {
    return 0;
  }
}
