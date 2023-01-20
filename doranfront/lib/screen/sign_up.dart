import 'package:dorandoran/const/storage.dart';
import 'package:dorandoran/screen/home.dart';
import 'package:dorandoran/screen/using_agree.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:dorandoran/const/util.dart';
import 'package:dorandoran/model/user.dart';


class SignUp extends StatefulWidget {

  const SignUp({ Key? key}) : super(key: key);

  @override
  State<SignUp> createState() => _SignUpState();
}

class _SignUpState extends State<SignUp> {
  DateTime selectedDate = DateTime.now();
  TextEditingController name = TextEditingController();
  Map<String,bool> namecheck={'':false};
  String text ="";


  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: backgroundcolor,
      body: Container(
        decoration: gradient,
        child: SafeArea(
          child: Padding(
            padding: const EdgeInsets.only(left: 30, right: 30),
            child: Column(children: [
              Logo(text: '첫 방문을\n환영합니다!', style: whitestyle),
              SizedBox(height: 50),
              Column(
                children: [
                  Column(crossAxisAlignment: CrossAxisAlignment.stretch, children: [
                Text('별명을 설정해주세요', style: whitestyle.copyWith(fontSize: 24)),
                SizedBox(height: 10),
                Row(
                  children: [
                    Container(
                      child: TextField(
                        style: whitestyle,
                        decoration: InputDecoration(
                          enabledBorder: UnderlineInputBorder(
                              borderSide: BorderSide(color: Colors.white)),
                          hintText: "닉네임을 입력해주세요",
                          hintStyle: whitestyle.copyWith(color: Colors.indigo),
                        ),
                        controller: name,
                        maxLength: 8,
                      ),
                      width: 285,
                    ),
                    SizedBox(width: 20),
                    TextButton(
                        onPressed: () {
                          checkname(name.text.toString());
                        },
                        child: Text("확인"),
                        style: TextButton.styleFrom(
                            primary: Colors.white,
                            side: BorderSide(
                              color: Colors.white,
                            ))),
                  ],
                ),
                Text(text,style: text=='사용가능한 이름입니다.' ? whitestyle.copyWith(color: Colors.blue):whitestyle.copyWith(color: Colors.red),),
              ]),
                  SizedBox(height: 20),
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      Text('생년월일을 선택해주세요',
                          style: whitestyle.copyWith(fontSize: 24)),
                      SizedBox(height: 10),
                      Container(
                        width: 240,
                        height: 50,
                        child: TextButton(
                          child: Text(
                            '${selectedDate.year}-${getTimeFormat(selectedDate.month)}-${getTimeFormat(selectedDate.day)}',
                            style: whitestyle.copyWith(fontSize: 18),
                          ),
                          style: TextButton.styleFrom(
                              primary: Colors.white,
                              side: BorderSide(
                                color: Colors.white,
                              )),
                          onPressed: () {
                            showCupertinoDialog(
                              context: context,
                              barrierDismissible: true,
                              builder: (BuildContext context) {
                                return Align(
                                  alignment: Alignment.bottomCenter,
                                  child: Container(
                                    color: Color(0xDDFFFFFF), //색상,,
                                    height: 250.0,
                                    child: CupertinoDatePicker(
                                      mode: CupertinoDatePickerMode.date,
                                      initialDateTime: selectedDate,
                                      maximumYear: DateTime.now().year,
                                      maximumDate: DateTime.now(),
                                      onDateTimeChanged: (DateTime date) {
                                        setState(() {
                                          selectedDate = date; //사용자가 선택한 날짜 저장
                                        });
                                      },
                                    ),
                                  ),
                                );
                              },
                            );
                          },
                        ),
                      ),
                    ],
                  ),
                  SizedBox(height: 40),
                  NextButton(
                    selectedDate: selectedDate,
                    name: name,
                    namecheck: namecheck,
                  )
                ],
              ),
            ]),
          ),
        ),
      ),
    );
  }
  void textchange(String context){
    setState((){
      text=context;
    });
  }

  void checkname(String name) {
    if (name == '') { //null상태
      textchange("닉네임을 입력해주세요.");
      print('null');
    }
    else if (name.length <= 1) {
      textchange("닉네임의 길이는 최소 2글자 이상이어야 합니다.");
    }
    else if (!RegExp(r"^[가-힣0-9a-zA-Z]*$").hasMatch(
        name)) { //제대로된 문자인지 확인.(특수문자.이모티콘 체크)
      textchange("올바르지 않은 닉네임입니다.");
    }
    else { //형식은 통과
      textchange("");
      postNameCheckRequest(name).then((value) {
        if (value == 200) {
          textchange('사용가능한 이름입니다.');
          namecheck[name] = true;
        }
        else {
          textchange('이미 사용중인 이름입니다.');
          namecheck[name] = false;
        }
      });
    }
  }
}

class NextButton extends StatelessWidget {
  final DateTime selectedDate;
  final TextEditingController name;
  final Map<String,bool> namecheck;

  const NextButton(
      {
        required this.namecheck,
      required this.selectedDate,
      required this.name,
      Key? key})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: double.infinity,
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 19.0),
        child: ElevatedButton(
          style: ElevatedButton.styleFrom(
              primary: Colors.blueAccent,
              elevation: 3,
              shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(30)),
              padding: EdgeInsets.all(15)),
          onPressed: () {
          // if(namecheck[name]==true) {
            //포스트
           postUserRequest('${selectedDate.year}-${getTimeFormat(selectedDate.month)}-${getTimeFormat(selectedDate.day)}', name.text.toString(),firebasetoken!,kakaotoken!);
            print('${selectedDate.year}-${getTimeFormat(
                selectedDate.month)}-${getTimeFormat(selectedDate.day)}');
            print('${name.text.toString()}');
            print(firebasetoken!);
            print(kakaotoken!);

            Navigator.push(
                context, MaterialPageRoute(builder: (context) => Home()));
          },
          child: Text('다음', style: whitestyle),
        ),
      ),
    );
  }
}
