import 'package:dorandoran/texting/get/component/post_detail_inputcomment.dart';
import 'package:dorandoran/texting/get/screen/post_detail.dart';
import 'package:dorandoran/texting/write/screen/write.dart';
import 'package:dorandoran/user/login/screen/kakao_login.dart';
import 'package:dorandoran/user/login/screen/login_check.dart';
import 'package:dorandoran/user/sign_up/screen/sign_up.dart';
import 'package:http/http.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'texting/get/screen/home.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:kakao_flutter_sdk/kakao_flutter_sdk.dart';
import 'package:firebase_core/firebase_core.dart';
import 'firebase_options.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'common/storage.dart';

void main() async {
  KakaoSdk.init(nativeAppKey: kakaonativekey);
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );
  WidgetsFlutterBinding.ensureInitialized();
  SystemChrome.setPreferredOrientations([DeviceOrientation.portraitUp]); //회전방지
  firebasetoken = (await FirebaseMessaging.instance.getToken())!;
  final prefs = await SharedPreferences.getInstance();
  prefs.setString('firebasetoken', firebasetoken!);
  runApp(ScreenUtilInit(
    designSize: Size(360, 690),
    builder: (context, child) {
      //실행(with 폰트)
      return MaterialApp(
        theme: ThemeData(
          fontFamily: GoogleFonts.ibmPlexSansKr().fontFamily,
          primarySwatch: Colors.blue,
          canvasColor: Colors.transparent,
        ),
        builder: (context, child) {
          //폰트크기고정
          return MediaQuery(
              data: MediaQuery.of(context).copyWith(textScaleFactor: 1.0),
              child: child!);
        },
        home: Home(),
        //번영(영어.한국어)
        localizationsDelegates: [
          GlobalMaterialLocalizations.delegate,
          GlobalWidgetsLocalizations.delegate,
          GlobalCupertinoLocalizations.delegate
        ],
        supportedLocales: [
          Locale('ko', ''),
          Locale('en', ''),
        ],
      );
    },
  ));
}
