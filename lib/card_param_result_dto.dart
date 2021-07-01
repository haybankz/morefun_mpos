/// data : {"pansn":"01","track2":"5399834474338100D22122210016552476","expData":"2212","pinBlock":"null",
/// "icData":"9F02060000000000019F2608C79EE19CE9CC879B9F2701809F10120110A5000304000000000000000000
/// 0000FF9F3704C28791DC9F36020063950504800080009A032103259C01005F2A020408820239009F1A0204089F030
/// 60000000000009F330360D0C89F34034103029F3501229F1E086D665F36306220208407A00000000410109F0902002
/// 09F410400000002","ksn":"null","cardType":"IC Card","mac_ksn":"null","pan":"5399834474338100",
/// "pin_ksn":"null","track3":"null","mag_ksn":"null"}
/// success : true
/// message : "Card Read successful"

class CardParamResultDto {
  Data data;
  bool success;
  String message;

  CardParamResultDto({
      this.data, 
      this.success, 
      this.message});

  CardParamResultDto.fromJson(dynamic json) {
    data = json["data"] != null ? Data.fromJson(json["data"]) : null;
    success = json["success"];
    message = json["message"];
  }

  Map<String, dynamic> toJson() {
    var map = <String, dynamic>{};
    if (data != null) {
      map["data"] = data.toJson();
    }
    map["success"] = success;
    map["message"] = message;
    return map;
  }

}

/// pansn : "01"
/// track2 : "5399834474338100D22122210016552476"
/// expData : "2212"
/// pinBlock : "null"
/// icData : "9F02060000000000019F2608C79EE19CE9CC879B9F2701809F10120110A50003040000000000000000000000FF9F3704C28791DC9F36020063950504800080009A032103259C01005F2A020408820239009F1A0204089F03060000000000009F330360D0C89F34034103029F3501229F1E086D665F36306220208407A00000000410109F090200209F410400000002"
/// ksn : "null"
/// cardType : "IC Card"
/// mac_ksn : "null"
/// pan : "5399834474338100"
/// pin_ksn : "null"
/// track3 : "null"
/// mag_ksn : "null"

class Data {
  String pansn;
  String track2;
  String expData;
  String pinBlock;
  String icData;
  String ksn;
  String cardType;
  String macKsn;
  String pan;
  String pinKsn;
  String track3;
  String magKsn;

  Data({
      this.pansn, 
      this.track2, 
      this.expData, 
      this.pinBlock, 
      this.icData, 
      this.ksn, 
      this.cardType, 
      this.macKsn, 
      this.pan, 
      this.pinKsn, 
      this.track3, 
      this.magKsn});

  Data.fromJson(dynamic json) {
    pansn = json["pansn"];
    track2 = json["track2"];
    expData = json["expData"];
    pinBlock = json["pinBlock"];
    icData = json["icData"];
    ksn = json["ksn"];
    cardType = json["cardType"];
    macKsn = json["mac_ksn"];
    pan = json["pan"];
    pinKsn = json["pin_ksn"];
    track3 = json["track3"];
    magKsn = json["mag_ksn"];
  }

  Map<String, dynamic> toJson() {
    var map = <String, dynamic>{};
    map["pansn"] = pansn;
    map["track2"] = track2;
    map["expData"] = expData;
    map["pinBlock"] = pinBlock;
    map["icData"] = icData;
    map["ksn"] = ksn;
    map["cardType"] = cardType;
    map["mac_ksn"] = macKsn;
    map["pan"] = pan;
    map["pin_ksn"] = pinKsn;
    map["track3"] = track3;
    map["mag_ksn"] = magKsn;
    return map;
  }

}