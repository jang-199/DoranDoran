import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:dorandoran/common/uri.dart';

//좋아요
void postLike(int postId, String email) async {
  await http.post(
    Uri.parse('$url/api/post-like'),
    headers: <String, String>{
      'Content-Type': 'application/json',
    },
    body: jsonEncode({
      "email": email,
      "postId": postId,
    }),
  );
}