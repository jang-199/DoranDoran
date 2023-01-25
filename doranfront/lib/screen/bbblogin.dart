// import 'dart:convert';
// import 'package:dorandoran/const/login_platform.dart';
// import 'package:flutter/material.dart';
// import 'package:google_sign_in/google_sign_in.dart';
// import 'package:http/http.dart' as http;
//
// class Login extends StatefulWidget {
//   const Login({Key? key}) : super(key: key);
//
//   @override
//   State<Login> createState() => _LoginState();
// }
//
// class _LoginState extends State<Login> {
//   LoginPlatform _loginPlatform = LoginPlatform.google;
//
//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       body: Center(
//           child: Row(
//             mainAxisAlignment: MainAxisAlignment.center,
//             children: [
//               _loginButton(
//                 'google_logo',
//                 signInWithGoogle,
//               )
//             ],
//           )),
//     );
//   }
//   void signInWithGoogle() async {
//     final GoogleSignInAccount? googleUser = await GoogleSignIn().signIn();
//
//     if (googleUser != null) {
//       print('name = ${googleUser.displayName}');
//       print('email = ${googleUser.email}');
//       print('id = ${googleUser.}');
//
//       setState(() {
//         _loginPlatform = LoginPlatform.google;
//       });
//     }
//   }
//
//   Widget _loginButton(String path, VoidCallback onTap) {
//     return Card(
//       elevation: 5.0,
//       shape: const CircleBorder(),
//       clipBehavior: Clip.antiAlias,
//       child: Ink.image(
//         image: AssetImage('asset/image/$path.png'),
//         width: 60,
//         height: 60,
//         child: InkWell(
//           borderRadius: const BorderRadius.all(
//             Radius.circular(35.0),
//           ),
//           onTap: onTap,
//         ),
//       ),
//     );
//   }
//
// }