class postcardDetail {
  final String content;
  final String postTime;
  final String? location;
  final int postLikeCnt;
  final bool? postLikeResult;
  final int commentCnt;
  final String backgroundPicUri;
  final String? postNickname;
  final List<dynamic>? commentDetailDto;
  final List<dynamic>? postHashes;
  final String font;
  final String fontColor;
  final int fontSize;
  final int fontBold;

  postcardDetail({
    required this.content,
    required this.postTime,
    required this.location,
    required this.postLikeCnt,
    required this.postLikeResult,
    required this.commentCnt,
    required this.backgroundPicUri,
    required this.postNickname,
    required this.commentDetailDto,
    required this.postHashes,
    required this.font,
    required this.fontColor,
    required this.fontSize,
    required this.fontBold,
  });

  factory postcardDetail.fromJson(Map<String, dynamic> json) {
    return postcardDetail(
        content: json["content"],
        postTime: json["postTime"],
        location: json["location"],
        postLikeCnt: json["postLikeCnt"],
        postLikeResult: json["postLikeResult"] ?? false,
        commentCnt: json["commentCnt"],
        backgroundPicUri: json["backgroundPicUri"],
        postNickname: json["postNickname"],
        commentDetailDto: json["commentDetailDto"],
        postHashes: json["postHashes"],
        font: json["font"],
        fontColor: json["fontColor"],
        fontSize: json["fontSize"],
        fontBold: json["fontBold"]);
  }
}
