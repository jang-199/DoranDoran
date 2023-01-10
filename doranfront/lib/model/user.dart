import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:dorandoran/const/util.dart';

//회원가입: 데이터 전송
postRequest(
    String dateOfBirth, String nickName, String identificationNumber) async {
  var response = await http.post(
    url,
    headers: <String, String>{
      'Content-Type': 'application/json',
    },
    body: jsonEncode({
      "dateOfBirth": dateOfBirth,
      "nickName": nickName,
      "identificationNumber": identificationNumber,
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
