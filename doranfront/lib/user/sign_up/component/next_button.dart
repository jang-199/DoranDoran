import 'package:dorandoran/common/storage.dart';
import 'package:dorandoran/texting/get/screen/home.dart';
import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';
import 'package:dorandoran/common/css.dart';
import 'package:dorandoran/user/sign_up/quest/user.dart';
import 'package:dorandoran/common/util.dart';


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
          onPressed: () async {
            // if(namecheck[name]==true) {
            //포스트
            useremail  = await postUserRequest('${selectedDate.year}-${getTimeFormat(selectedDate.month)}-${getTimeFormat(selectedDate.day)}', name.text.toString(),firebasetoken!,kakaotoken!);
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
