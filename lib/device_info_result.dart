/// dataVersion : "V1.00"
/// posVersion : "MP63-READER-V1.99.T7.EN"
/// model : "MP60"
/// battery : 2
/// serialNo : "63201125995111"

class DeviceInfoResult {
  String dataVersion;
  String posVersion;
  String model;
  int battery;
  String serialNo;

  DeviceInfoResult({
      this.dataVersion, 
      this.posVersion, 
      this.model, 
      this.battery, 
      this.serialNo});

  DeviceInfoResult.fromJson(dynamic json) {
    dataVersion = json["dataVersion"];
    posVersion = json["posVersion"];
    model = json["model"];
    battery = json["battery"];
    serialNo = json["serialNo"];
  }

  Map<String, dynamic> toJson() {
    var map = <String, dynamic>{};
    map["dataVersion"] = dataVersion;
    map["posVersion"] = posVersion;
    map["model"] = model;
    map["battery"] = battery;
    map["serialNo"] = serialNo;
    return map;
  }

}