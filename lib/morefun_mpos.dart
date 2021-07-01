import 'dart:async';

import 'package:flutter/services.dart';
import 'package:morefun_mpos/card_param_result_dto.dart';
import 'package:morefun_mpos/device_info_result.dart';


class MorefunMpos {
  static const MethodChannel _channel = const MethodChannel('morefun_mpos');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<bool> connect(String mac) async {
    return  await _channel.invokeMethod('connect', {"macAddress": mac});
  }

  static Future<bool> isConnected()async {
    return await _channel.invokeMethod('isConnected');
  }

  static Future<DeviceInfoResult> getDeviceInfo()async {
    var result =  await _channel.invokeMethod('getDeviceInfo');
    // print(result.toString());
    return DeviceInfoResult.fromJson(result);
  }

  static Future<bool> loadKek(String CTMKey) async{
    return await _channel.invokeMethod('loadKek', {"CTMKey": CTMKey});
  }

  static Future<bool> loadMasterKey(String masterKey, String masterKCV) async{
    return await _channel.invokeMethod('loadMainKey', {"masterKey": masterKey, "masterKCV": masterKCV});
  }

  static Future<bool> loadWorkingKey(String pinKey, String pinKCV, String sessionKey, String sessionKCV) async{
    return await _channel.invokeMethod('downloadWorkKey', {"pinKey": pinKey, "pinKCV": pinKCV, "sessionKey": sessionKey, "sessionKCV": sessionKCV});
  }
  static Future<CardParamResultDto> executeCardReading(String amount) async{
    var result = await _channel.invokeMethod('executeCardReading', {"amount": amount});
    return CardParamResultDto.fromJson(result);
  }
  static Future<dynamic> calcMac() async{
    return await _channel.invokeMethod('calcMac');
  }
  static Future<String> onlineAuth(String tlv) async{
    return await _channel.invokeMethod('onlineAuth', {"tlv": tlv});
  }

  static Future<String> getRandomNumber() async{
    return await _channel.invokeMethod('getRandomNumbers');
  }

  static Future<void> downloadAID() async {
    return await _channel.invokeMethod('downloadAID');
  }

  static Future<void> downloadPUK() async {
    return await _channel.invokeMethod('downloadPUK');
  }

  static Future<String> dukptInit() async{
    return await _channel.invokeMethod('dukptInit');
  }

   static Future<bool> setTlv(String emvParam) async {
    return await _channel.invokeMethod('setEmvParam', {"emvParam": emvParam});
  }

   static Future<bool> setSleepTime(int sleepTime, int shutDownTime) async{
    return await _channel.invokeMethod('setSleepTime', {"sleepTime": sleepTime, "shutDownTime" : shutDownTime});
  }


}
