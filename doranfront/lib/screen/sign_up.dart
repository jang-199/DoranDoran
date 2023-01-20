import 'package:dorandoran/screen/home.dart';
import 'package:dorandoran/screen/using_agree.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:dorandoran/const/util.dart';
import 'package:dorandoran/model/user.dart';
import 'package:flutter/services.dart';
import 'dart:async';
import 'package:unique_identifier/unique_identifier.dart';
//import 'package:device_information/device_information.dart';
import 'package:device_info_plus/device_info_plus.dart';

class SignUp extends StatefulWidget {
  const SignUp({Key? key}) : super(key: key);

  @override
  State<SignUp> createState() => _SignUpState();
}

class _SignUpState extends State<SignUp> {
  DateTime selectedDate = DateTime.now();
  TextEditingController name = TextEditingController();
  String _identifier='UnKnown';

  @override
  void initState() {
    super.initState();
    initUniqueIdentifierState();
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
                  NameInput(controller: name),
                  SizedBox(height: 40),
              Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  Text('생년월일을 선택해주세요', style: whitestyle.copyWith(fontSize: 24)),
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
                              child: Container(
                                color: Colors.white,
                                height: 300.0,
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
                  NextButton(selectedDate: selectedDate, name: name)
                ],
              ),
            ]),
          ),
        ),
      ),
    );
  }

  //유저 IMEI(ios:UDID) 가져오기
  Future<void> initUniqueIdentifierState() async {
    String? identifier;
    try {
      identifier = await UniqueIdentifier.serial;
    } on  Exception{
      identifier = 'Failed to get Unique Identifier';
    }
    if(identifier=='Failed to get Unique Identifier') {
      final DeviceInfoPlugin deviceInfoPlugin = new DeviceInfoPlugin();
      var data = await deviceInfoPlugin.iosInfo;
      identifier = data.identifierForVendor;
    }
    //identifier = await DeviceInformation.deviceIMEINumber;
    setState(() {
      _identifier = identifier!;
    });
  }
}

class NameInput extends StatelessWidget {
  final TextEditingController controller;

  const NameInput({required this.controller, Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(crossAxisAlignment: CrossAxisAlignment.stretch, children: [
      Text('별명을 설정해주세요', style: whitestyle.copyWith(fontSize: 24)),
      SizedBox(height: 10),
      Container(
        child: TextField(
          style: whitestyle,
          decoration: InputDecoration(
            enabledBorder: UnderlineInputBorder(
                borderSide: BorderSide(color: Colors.white)),
            hintText: "닉네임을 입력해주세요",
            hintStyle: whitestyle.copyWith(color: Colors.indigo),
          ),
          controller: controller,
        ),
        width: 250,
      )
    ]);
  }
}

// class BirthInput extends StatefulWidget {
//   final DateTime selectedDate;
//
//   const BirthInput({required this.selectedDate, Key? key}) : super(key: key);
//
//   @override
//   State<BirthInput> createState() => _BirthInputState();
// }
//
// class _BirthInputState extends State<BirthInput> {
//   @override
//   Widget build(BuildContext context) {
//     DateTime selectedDate = widget.selectedDate;
//
//     return Column(
//       crossAxisAlignment: CrossAxisAlignment.stretch,
//       children: [
//         Text('생년월일을 선택해 주세요', style: whitestyle.copyWith(fontSize: 24)),
//         SizedBox(height: 10),
//         Container(
//           width: 240,
//           height: 50,
//           child: TextButton(
//             child: Text(
//               '${selectedDate.year}-${getTimeFormat(selectedDate.month)}-${getTimeFormat(selectedDate.day)}',
//               style: whitestyle.copyWith(fontSize: 18),
//             ),
//             style: TextButton.styleFrom(
//                 primary: Colors.white,
//                 side: BorderSide(
//                   color: Colors.white,
//                 )),
//             onPressed: () {
//               showCupertinoDialog(
//                 context: context,
//                 barrierDismissible: true,
//                 builder: (BuildContext context) {
//                   return Align(
//                     child: Container(
//                       color: Colors.white,
//                       height: 300.0,
//                       child: CupertinoDatePicker(
//                         mode: CupertinoDatePickerMode.date,
//                         initialDateTime: selectedDate,
//                         maximumYear: DateTime.now().year,
//                         maximumDate: DateTime.now(),
//                         onDateTimeChanged: (DateTime date) {
//                           setState(() {
//                             selectedDate = date; //사용자가 선택한 날짜 저장
//                           });
//                         },
//                       ),
//                     ),
//                   );
//                 },
//               );
//             },
//           ),
//         ),
//       ],
//     );
//   }
// }

class NextButton extends StatelessWidget {
  final DateTime selectedDate;
  final TextEditingController name;

  const NextButton({required this.selectedDate, required this.name, Key? key})
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
            //postRequest('${selectedDate.year}-${getTimeFormat(selectedDate.month)}-${getTimeFormat(selectedDate.day)}', name.text.toString(),'dddd');
            Navigator.push(
                context, MaterialPageRoute(builder: (context) => Home()));
          },
          child: Text('다음', style: whitestyle),
        ),
      ),
    );
  }
}
