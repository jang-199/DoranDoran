import 'package:dorandoran/screen/home.dart';
import 'package:dorandoran/screen/kakao_login.dart';
import 'package:dorandoran/screen/sign_up.dart';
import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:kakao_flutter_sdk/kakao_flutter_sdk.dart';
import 'package:firebase_core/firebase_core.dart';
import 'firebase_options.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'const/storage.dart';
import 'package:flutter_downloader/flutter_downloader.dart';

void main() async {
  KakaoSdk.init(nativeAppKey: kakaonativekey);
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );
  firebasetoken = await FirebaseMessaging.instance.getToken();
  runApp(MaterialApp(
    home: KaKaoLogin(),
    localizationsDelegates: [
      GlobalMaterialLocalizations.delegate,
      GlobalWidgetsLocalizations.delegate,
      GlobalCupertinoLocalizations.delegate
    ],
    supportedLocales: [
      Locale('ko',''),
      Locale('en',''),
    ],
  ));
}
