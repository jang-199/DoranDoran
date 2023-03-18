import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:dorandoran/common/uri.dart';
import 'package:shared_preferences/shared_preferences.dart';
//회원가입: 데이터 전송 500
Future<String> postUserRequest(String dateOfBirth, String nickName, String firebasetoken,
    String kakaoAccessToken) async {
  var response = await http.post(
    Uri.parse('$url/api/signup'),
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

  final prefs = await SharedPreferences.getInstance();
  prefs.setString('email', response.body);
  return response.body;
}
