import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:morefun_mpos/morefun_mpos.dart';

void main() {
  const MethodChannel channel = MethodChannel('morefun_mpos');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await MorefunMpos.platformVersion, '42');
  });
}
