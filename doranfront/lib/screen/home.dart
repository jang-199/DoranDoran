import 'package:dorandoran/const/util.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:flutter_downloader/flutter_downloader.dart';
import 'package:path_provider/path_provider.dart';



class Home extends StatefulWidget {
  const Home({Key? key}) : super(key: key);

  @override
  State<Home> createState() => _HomeState();
}

class _HomeState extends State<Home> {
  // void initState() {
  //   // TODO: implement initState
  //   super.initState();
  //   FlutterDownloader.registerCallback(downloadCallback);
  //
  // }
  @override
  Widget build(BuildContext context) {
    Image img=Image.network('http://116.44.231.162:8080/api/background/1');
    return Scaffold(
      backgroundColor:backgroundcolor,
      body: Center(
      child: SafeArea(
          child: Column(
            children: [
              SizedBox(height: 200,),
              Container(
                width: 200,
                height: 300,
                decoration: BoxDecoration(
                  image:  DecorationImage(image: img.image, fit: BoxFit.none),
                ),
              )
              // GestureDetector(
              //     onTap: () async{
              //       String dir = (await getApplicationDocumentsDirectory()).path; 	//path provider로 저장할 경로 가져오기
              //       try{
              //         await FlutterDownloader.enqueue(
              //           url: "http://116.44.231.162:8080/api/background/1", 	// file url
              //           savedDir: 'asset/img/',	// 저장할 dir
              //           fileName: '${}',	// 파일명
              //           saveInPublicStorage: true ,	// 동일한 파일 있을 경우 덮어쓰기 없으면 오류발생함!
              //         );
              //       }catch(e){
              //         print("eerror :::: $e");
              //       }
              //     }
              // ),
            ],
          ),
        ),
      )
    );
  }
}
