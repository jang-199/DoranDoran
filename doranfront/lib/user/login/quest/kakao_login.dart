import 'package:dorandoran/common/storage.dart';
import 'package:dorandoran/user/login/quest/logincheck.dart';
import 'package:dorandoran/user/sign_up/quest/namecheck.dart';
import 'package:kakao_flutter_sdk/kakao_flutter_sdk.dart';
import 'package:shared_preferences/shared_preferences.dart';

questkakaologin() async {
  final prefs = await SharedPreferences.getInstance();
  OAuthToken token;
  if (await isKakaoTalkInstalled()) {
    try {
      token = await UserApi.instance.loginWithKakaoTalk();
      kakaotoken  =token.accessToken.toString();
      prefs.setString('kakaotoken', kakaotoken!);
      User user = await UserApi.instance.me();
      int ok= await postNameCheckRequest(user.kakaoAccount!.email.toString());
      if(ok==200) return 200;
      else return 100;
    } catch (error) {
      try {
        token = await UserApi.instance.loginWithKakaoAccount();
        kakaotoken = token.accessToken.toString();
        prefs.setString('kakaotoken', kakaotoken!);
        return 100;
      } catch (error) {
        print('카카오계정으로 로그인 실패 $error');
      }
    }
  } else {
    try {
      token = await UserApi.instance.loginWithKakaoAccount();
      kakaotoken = token.accessToken.toString();
      prefs.setString('kakaotoken', kakaotoken!);
      User user = await UserApi.instance.me();
      prefs.setString('email',user.kakaoAccount!.email.toString());
      int ok= await postUserCheckRequest(user.kakaoAccount!.email.toString());
      if(ok==200) return 200;
      else return 100;
    } catch (error) {
      print('카카오계정으로 로그인 실패 $error');
    }
  }
}