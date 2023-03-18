import 'package:dorandoran/common/css.dart';
import 'package:dorandoran/common/storage.dart';
import 'package:dorandoran/common/util.dart';
import 'package:dorandoran/texting/get/screen/home.dart';
import 'package:dorandoran/user/login/screen/kakao_login.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import '../component/mainlogo.dart';
import 'package:shared_preferences/shared_preferences.dart';

class Login_check extends StatefulWidget {
  const Login_check({Key? key}) : super(key: key);

  @override
  State<Login_check> createState() => _Login_checkState();
}

class _Login_checkState extends State<Login_check> {
  @override
  void initState() {
    getlocation();
  }

  @override
  Widget build(BuildContext context) {
    logincheck();
    return Scaffold(
      body: Container(
        decoration: gradient,
        child: SafeArea(
            child: Padding(
              padding: const EdgeInsets.only(left: 30, right: 30),
              child:
              Center(
                  child:
                  Column(
                    children: [
                      SizedBox(height: 70.h),
                      MainLogo(text: "도란도란", style: whitestyle),
                      SizedBox(height: 160.h),
                      SizedBox(height: 20.h,),
                      CircularProgressIndicator(),
                      SizedBox(height: 20.h,),
                      Text("데이터를 로딩 중입니다. 잠시만 기다려주세요.", style: TextStyle(
                          fontSize: 12.sp, color: Colors.white54)),
                    ],
                  )),)
        ),
      ),
    );
  }

   logincheck() async {
    final prefs = await SharedPreferences.getInstance();
    if(prefs.getString('email')!.isNotEmpty) {
      useremail!=prefs.getString('email');
      kakaotoken!=prefs.getString('kakaotoken');
      firebasetoken!=prefs.getString('firebasetoken');
      Navigator.push(
          context,
          MaterialPageRoute(
              builder: (context) => Home()));
    }
      else{
      Navigator.push(
          context,
          MaterialPageRoute(
              builder: (context) => KaKaoLogin()));
      }
    }
    }
