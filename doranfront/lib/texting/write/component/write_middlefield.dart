import 'package:dropdown_button2/dropdown_button2.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import '../../../common/css.dart';
import 'package:google_fonts/google_fonts.dart';
import '../screen/write.dart';
import 'package:custom_pop_up_menu/custom_pop_up_menu.dart';
class MiddleTextField extends StatefulWidget {
  final Image backimg;

  const MiddleTextField({required this.backimg, Key? key}) : super(key: key);

  @override
  State<MiddleTextField> createState() => _MiddleTextFieldState();
}


List<int> menuitem = [15,32,48];
List<String> menufontitem = [
  'cuteFont',
  'nanumGothic',
  'Jua',
];

TextStyle style = TextStyle(color: Colors.black);
bool colors=false, weight=false;
CustomPopupMenuController _controller = CustomPopupMenuController();
CustomPopupMenuController _controllerr = CustomPopupMenuController();
class _MiddleTextFieldState extends State<MiddleTextField> {
  @override
  Widget build(BuildContext context) {

    return Column(children: [
      Container(
        height: 300.h,
        decoration: BoxDecoration(
          border: Border.all(),
          image: DecorationImage(
              image: widget.backimg.image,
              fit: BoxFit.cover,
              colorFilter: ColorFilter.mode(
                  Colors.black.withOpacity(0.5), BlendMode.dstATop)),
        ),
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          child: TextFormField(
            textAlignVertical: TextAlignVertical.center,
            textAlign: TextAlign.center,
            style: style,
            maxLines: null,
            expands: true,
            decoration: InputDecoration(
              border: InputBorder.none,
              enabledBorder: UnderlineInputBorder(borderSide: BorderSide.none),
              hintText: "내용을 작성해주세요",
              hintStyle: whitestyle.copyWith(color: Colors.black12),
            ),
            controller: contextcontroller,
          ),
        ),
      ),
      Row(
        children: [
          CustomPopupMenu(
            child: Icon(Icons.font_download),
            menuBuilder:()=>
                ClipRRect(
                  borderRadius: BorderRadius.circular(5),
                  child: Container(
                    width: 220,
                    color: const Color(0xFF4C4C4C),
                    child: GridView.count(
                      padding: EdgeInsets.symmetric(horizontal: 5, vertical: 10),
                      crossAxisCount: 3,
                      crossAxisSpacing: 0,
                      mainAxisSpacing: 10,
                      shrinkWrap: true,
                      physics: NeverScrollableScrollPhysics(),
                      children:
                      menufontitem.map((item) => TextButton(
                        onPressed:(){
                          setState(() {
                            setState(() {
                              [
                                'cuteFont',
                                'nanumGothic',
                                'Jua',
                              ];
                              if (item == 'cuteFont')
                                style = GoogleFonts.getFont('Cute Font', textStyle: style);
                              else if (item == 'nanumGothic')
                                style = GoogleFonts.getFont('Nanum Gothic', textStyle: style);
                              else if (item == 'Jua')
                                style = GoogleFonts.getFont('Jua', textStyle: style);
                            });
                            _controllerr.hideMenu();
                          });
                        },
                        child: item=='cuteFont' ?
                        Text("큐트", style: GoogleFonts.cuteFont(fontSize: 20.sp,color: Colors.white),):
                        item=='nanumGothic' ?
                        Text("나눔",style:  GoogleFonts.nanumGothic(fontSize: 13.sp,color: Colors.white,)):
                        Text("주아",style:  GoogleFonts.jua(fontSize: 14.sp,color: Colors.white)),
                      ))
                          .toList(),
                    ),
                  ),
                ),
            pressType: PressType.singleClick,
            controller: _controllerr,
          ),
          IconButton(
              onPressed: () {
                setState(() {
                if(style.color==Colors.black){
                    colors=true;
                    style=style.copyWith(color: Colors.white);
                }else{
                    colors=false;
                    style=style.copyWith(color: Colors.black);
                }
                });
              },
              icon:Icon(Icons.format_color_text,color: colors? Colors.white:Colors.black)), //색상
        CustomPopupMenu(
            child: Icon(Icons.format_size),
            menuBuilder:()=>
                ClipRRect(
                  borderRadius: BorderRadius.circular(5),
                  child: Container(
                    width: 220,
                    color: const Color(0xFF4C4C4C),
                    child: GridView.count(
                      padding: EdgeInsets.symmetric(horizontal: 5, vertical: 10),
                      crossAxisCount: 3,
                      crossAxisSpacing: 0,
                      mainAxisSpacing: 10,
                      shrinkWrap: true,
                      physics: NeverScrollableScrollPhysics(),
                      children:
                      menuitem.map((item) => TextButton(
                    onPressed:(){
                      setState(() {
                        style= style.copyWith(fontSize: item.sp);
                        _controller.hideMenu();
                      });
                    },
                     child: item==15 ? Text("작게", style: buttontextstyle,): item==32 ? Text("중간",style: buttontextstyle):Text("크게",style: buttontextstyle),
                      ))
                          .toList(),
                    ),
                  ),
                ),
            pressType: PressType.singleClick,
          controller: _controller,
        ),
          IconButton(
              onPressed: () {
                setState(() {
                  if (style.fontWeight == FontWeight.w900) {
                    style = style.copyWith(fontWeight: FontWeight.w400);
                    weight=false;
                  } else {
                    style = style.copyWith(fontWeight: FontWeight.w900);
                    weight=true;
                  }
                });
              },
              icon: Icon(Icons.title, size: weight?25.r:20.r,)), //굵기굵게얇게
        ],
      ),
    ]);
  }
}
