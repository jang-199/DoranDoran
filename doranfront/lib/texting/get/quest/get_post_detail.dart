//글 공감
import 'package:dorandoran/texting/get/model/postcard_detaril.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:dorandoran/common/uri.dart';

//세부 글 가져오기
Future<postcardDetail> getpostDetail(
    int postId, String useremail, String location) async {
  var response = await http.post(
    Uri.parse('$url/api/post/detail'),
    headers: <String, String>{
      'Content-Type': 'application/json',
    },
    body: jsonEncode(
        {"postId": postId, "userEmail": useremail, "location": location}),
  );

  Map<String, dynamic> body = jsonDecode(utf8.decode(response.bodyBytes));
  postcardDetail card = postcardDetail.fromJson(body);
  return card;
}
