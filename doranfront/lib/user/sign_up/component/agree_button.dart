import 'package:dorandoran/user/sign_up/screen/sign_up.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

class AgreeButton extends StatefulWidget {
  final TextStyle style;

  const AgreeButton({required this.style, Key? key}) : super(key: key);

  @override
  State<AgreeButton> createState() => _AgreeButtonState();
}

List<bool> agree = [false, false, false, false];

class _AgreeButtonState extends State<AgreeButton> {
  Widget checkbutton(String text, int index) {
    bool? isagree = false;
    isagree = agree[index];
    return Builder(builder: (context) {
      return Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Row(
            children: [
              Checkbox(
                  value: isagree,
                  onChanged: (value) {
                    setState(() {
                      isagree = value;
                      agree[index] = value!;
                    });
                    if (agree[0] == false && agree[1] && agree[2] && agree[3]) agree[0] = true;
                    if (value == false && agree[0]) agree[0] = false;
                  }),
              Text(
                text,
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 14.sp,
                ),
              ),
            ],
          ),
          IconButton(
            alignment: Alignment.centerRight,
            icon: Icon(
              Icons.chevron_right,
              color: Colors.white,
            ),
            onPressed: () {
              showDialog(
                  context: context,
                  barrierDismissible: false, // 바깥 영역 터치시 닫을지 여부
                  builder: (BuildContext context) {
                    return AlertDialog(
                      backgroundColor: Colors.black,
                      content: const Text("..."),
                      actions: [
                        TextButton(
                          child: const Text('확인',
                              style: TextStyle(
                                  color: Colors.white,
                                  fontSize: 16,
                                  fontWeight: FontWeight.w700)),
                          onPressed: () {
                            Navigator.of(context).pop();
                          },
                        ),
                      ],
                    );
                  });
            },
          )
        ],
      );
    });
  }

  Widget allbutton(int index) {
    bool? isagree = false;
    isagree = agree[index];
    return Builder(builder: (context) {
      return Row(
        children: [
          Checkbox(
              value: isagree,
              onChanged: (value) {
                setState(() {
                  isagree = value;
                  for (int i = 0; i < agree.length; i++) agree[i] = value!;
                });
              }),
          Text("전체동의",
            style: TextStyle(color: Colors.white, fontSize: 18),
          ),
        ],
      );
    });
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        allbutton(0),
        SizedBox(height: 5.h),
        Container(
          height: 1.0.h,
          width: 450.0.w,
          color: Colors.white,
        ),
        SizedBox(height: 8.h),
        checkbutton("이용약관에 동의합니다.", 1),
        checkbutton("개인정보 수집 및 이용에 동의합니다.", 2),
        checkbutton("위치기반 서비스 이용에 동의합니다.", 3),
        SizedBox(height: 20.h),
        SizedBox(
          width: double.infinity,
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 19.0),
            child: ElevatedButton(
              style: ElevatedButton.styleFrom(
                  primary: Colors.blueAccent,
                  //side: BorderSide(width:3, color:Colors.brown),
                  elevation: 30,
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(30)),
                  padding: EdgeInsets.all(15)),
              onPressed: () {
                if (agree[0] == true) {
                  Navigator.push(context,
                      MaterialPageRoute(builder: (context) => SignUp()));
                }
              },
              child: Text('다음', style: widget.style),
            ),
          ),
        ),
      ],
    );
  }
}
