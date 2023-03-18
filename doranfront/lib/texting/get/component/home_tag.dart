import 'package:flutter/material.dart';

class Tag extends StatelessWidget {
  const Tag({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    Widget tagname(String name) {
      return TextButton(
        onPressed: () {},
        child: Text(
          name,
          style: TextStyle(fontSize: 18, color: Colors.black),
        ),
      );
    }

    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        tagname("#근처에"),
        tagname("#인기있는"),
        tagname("#새로운"),
        tagname("#관심있는"),
      ],
    );
  }
}
