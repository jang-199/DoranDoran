import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:dorandoran/common/uri.dart';

//좋아요
void commentLike(int postId,int commentId, String email) async {
  await http.post(
    Uri.parse('$url/api/comment-like'),
    headers: <String, String>{
      'Content-Type': 'application/json',
    },
    body: jsonEncode({
      "postId":postId,
      "commentId":commentId,
      "userEmail": email,
    }),
  );
}