import 'package:dorandoran/common/css.dart';
import 'package:dorandoran/common/util.dart';
import 'package:dorandoran/texting/get/screen/home.dart';
import 'package:dorandoran/user/login/quest/kakao_login.dart';
import 'package:dorandoran/user/sign_up/screen/using_agree.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import '../component/mainlogo.dart';

class KaKaoLogin extends StatefulWidget {
  const KaKaoLogin({Key? key}) : super(key: key);

  @override
  State<KaKaoLogin> createState() => _KaKaoLoginState();
}

class _KaKaoLoginState extends State<KaKaoLogin> {
  @override
  void initState() {
    getlocation();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: gradient,
        child: SafeArea(
          child: Padding(
              padding: const EdgeInsets.only(left: 30, right: 30),
              child: Column(
                children: [
                  SizedBox(height: 70.h),
                  MainLogo(text: "도란도란", style: whitestyle),
                  SizedBox(height: 180.h),
                  Center(
                    child: TextButton(
                        child: Image.asset(
                          'asset/image/kakao_login.png',
                          alignment: Alignment.center,
                        ),
                        onPressed: () async {
                          if (await questkakaologin() == 200)
                            Navigator.push(context, MaterialPageRoute(builder: (context) => Home()));
                          else
                            Navigator.push(context, MaterialPageRoute(builder: (context) => UsingAgree()));
                        }),
                  ),
                ],
              )),
        ),
      ),
    );
  }
}
