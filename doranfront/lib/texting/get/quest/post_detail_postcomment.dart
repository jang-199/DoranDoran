import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:dorandoran/common/uri.dart';

//대댓글달기만드는중
Future<int> postcomment(int postId, String email, String comment, bool anonymity) async {
  http.Response response= await http.post(
    Uri.parse('$url/api/comment'),
    headers: <String, String>{
      'Content-Type': 'application/json',
    },
    body: jsonEncode({
      "postId":postId,
      "email":email,
      "comment": comment,
      "anonymity":anonymity
    }),
  );
  return response.statusCode;
}