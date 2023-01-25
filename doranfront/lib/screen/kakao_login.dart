import 'package:dorandoran/const/util.dart';
import 'package:dorandoran/const/storage.dart';
import 'package:dorandoran/screen/using_agree.dart';
import 'package:flutter/material.dart';
import 'package:kakao_flutter_sdk/kakao_flutter_sdk.dart';

class KaKaoLogin extends StatelessWidget {
  const KaKaoLogin({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    OAuthToken token;

    return Scaffold(
      body: Container(
        decoration: gradient,
        child: SafeArea(
          child: Padding(
              padding: const EdgeInsets.only(left: 30.0, right: 30),
              child: Column(
                children: [
                  SizedBox(height: 100),
                  MainLogo(text: "도란도란", style: whitestyle),
                  SizedBox(height: 200),
                  Center(
                      child: TextButton(
                          child: Image.asset(
                            'asset/image/kakao_login.png',
                            alignment: Alignment.center,
                          ),
                          onPressed: () async {
                            if (await isKakaoTalkInstalled()) {
                              try {
                                token = await UserApi.instance.loginWithKakaoTalk();
                                kakaotoken = token.accessToken.toString();
                                Navigator.push(
                                    context,
                                    MaterialPageRoute(
                                        builder: (context) => UsingAgree()));
                              } catch (error) {
                                print('카카오톡으로 로그인 실패 $error');
                                // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인
                                try {
                                  token = await UserApi.instance.loginWithKakaoAccount();
                                  kakaotoken = token.accessToken.toString();
                                  Navigator.push(
                                      context,
                                      MaterialPageRoute(
                                          builder: (context) => UsingAgree()));
                                } catch (error) {
                                  print('카카오계정으로 로그인 실패 $error');
                                }
                              }
                            } else {
                              try {
                                print("어3");
                                token = await UserApi.instance
                                    .loginWithKakaoAccount();
                                print("미");
                                kakaotoken = token.accessToken.toString();
                                Navigator.push(
                                    context,
                                    MaterialPageRoute(
                                        builder: (context) => UsingAgree()));
                              } catch (error) {
                                print('카카오계정으로 로그인 실패 $error');
                              }
                            }
                          })),
                ],
              )),
        ),
      ),
    );
  }
}

class MainLogo extends StatelessWidget {
  final String text;
  final TextStyle style;

  const MainLogo({required this.text, required this.style, Key? key})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(crossAxisAlignment: CrossAxisAlignment.center, children: [
      SizedBox(height: 100),
      Image.asset(
        'asset/image/logo.png',
        width: 200,
        height: 200,
        alignment: Alignment.centerLeft,
      ),
      SizedBox(height: 20),
      Text(text,
          style: style.copyWith(
            fontSize: 30,
            fontWeight: FontWeight.w800,
          )),
      Text("익명 커뮤니티 서비스 어쩌고저쩌고.",
          style: style.copyWith(
            fontSize: 20,
          )),
    ]);
  }
}
