import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:dorandoran/common/uri.dart';

//대댓글달기만드는중
Future<int>  postreply(int commentId, String userEmail, String reply, bool anonymity) async {
  http.Response response=  await http.post(
    Uri.parse('$url/api/reply'),
    headers: <String, String>{
      'Content-Type': 'application/json',
    },
    body: jsonEncode({
      "commentId":commentId,
      "userEmail":userEmail,
      "reply": reply,
      "anonymity":anonymity
    }),
  );
  return response.statusCode;
}