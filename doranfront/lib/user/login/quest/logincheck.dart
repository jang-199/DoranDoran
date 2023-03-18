import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:dorandoran/common/uri.dart';

//로그인: statusCode:200, statusCode:400
Future<int> postUserCheckRequest(
    String email) async {
  var response = await http.post(
    Uri.parse('$url/api/check/registered'),
    headers: <String, String>{
      'Content-Type': 'application/json',
    },
    body: jsonEncode({
      "email": email,
    }),
  );
  return response.statusCode.toInt();
}
