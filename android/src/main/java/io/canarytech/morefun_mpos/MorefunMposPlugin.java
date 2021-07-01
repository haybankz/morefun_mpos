package io.canarytech.morefun_mpos;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Callable;


import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;


import com.mf.mpos.pub.Controler;
import com.mf.mpos.pub.result.ConnectPosResult;
import com.mf.mpos.pub.CommEnum;
import com.mf.mpos.pub.Controler;
import com.mf.mpos.pub.result.CalMacResult;
import com.mf.mpos.util.Misc;
import java.util.concurrent.Callable;
import com.mf.mpos.pub.result.ReadPosInfoResult;
import com.mf.mpos.pub.result.GetRandomResult;
import com.mf.mpos.pub.result.LoadDukptResult;
import com.mf.mpos.pub.result.LoadKekResult;
import com.mf.mpos.pub.result.EmvDealOnlineRspResult;
import com.mf.mpos.pub.result.GetEmvDataResult;
import com.mf.mpos.pub.EmvTagDef;
import com.mf.mpos.pub.param.ReadCardParam;
import com.mf.mpos.pub.result.ReadCardResult;
import com.mf.mpos.pub.IUpdatePosProc;
import com.mf.mpos.pub.result.UpdatePosResult;
import com.mf.mpos.pub.result.LoadMainKeyResult;
import com.mf.mpos.pub.result.LoadWorkKeyResult;
import com.mf.mpos.pub.result.ICAidResult;


import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/** MorefunMposPlugin */
public class MorefunMposPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware  {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private String TAG = "morefun_mpos";
  private Context mContext;
  private Activity mActivity;

  Result mResult;

//  private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
//  private BluetoothReceiver br;



  //This is the permission to apply
  private static String[] premission = {
          Manifest.permission.READ_EXTERNAL_STORAGE,
          Manifest.permission.WRITE_EXTERNAL_STORAGE,
          Manifest.permission.ACCESS_COARSE_LOCATION,
  };


  //here is the implementation of that new method
  private void onAttachedToEngine(Context applicationContext, BinaryMessenger messenger) {
    this.mContext = applicationContext;


    channel = new MethodChannel(messenger, TAG);
    channel.setMethodCallHandler(this);

    Controler.Init(mContext, CommEnum.CONNECTMODE.BLUETOOTH, 0);



  }
  
//  @Override
//  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
//    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), TAG);
//    channel.setMethodCallHandler(this);
//
//  }

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    onAttachedToEngine(flutterPluginBinding.getApplicationContext(),flutterPluginBinding.getBinaryMessenger());  // <- this is the line we need here, a new method call
//    final MethodChannel channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), TAG);
//    channel.setMethodCallHandler(new MorefunMposPlugin());
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
//    if (call.method.equals("getPlatformVersion")) {
//      result.success("Android " + android.os.Build.VERSION.RELEASE);
//    } else if(call.method.equals("connect")){
//      String mac = call.argument("macAddress");
//      result.success(connectDevice(mac));
//    }else {
//      result.notImplemented();
//    }

    mResult = result;
//    mResult = new MethodResultWrapper(result);
    try {

      switch (call.method) {
        case "getPlatformVersion":
          result.success("Android " + Build.VERSION.RELEASE);
        case "connect": {
          String mac = call.argument("macAddress");
          connectDevice(mac);
          break;
        }

        case "getDeviceInfo": {
          result.success(getDeviceInfo());
          break;
        }
        case "isConnected": {
          isConnected();
          break;
        }
        case "loadKek":
          String key = call.argument("CTMKey");
          result.success(loadKek(key));
          break;

        case "loadMainKey":
          String masterKey = call.argument("masterKey");
          String masterKCV = call.argument("masterKCV");
          result.success(loadMainKey(masterKey, masterKCV));
          break;
        case "downloadWorkKey":
          String pinKey = call.argument("pinKey");
          String pinKCV = call.argument("pinKCV");
          String sessionKey = call.argument("sessionKey");
          String sessionKCV = call.argument("sessionKCV");
          result.success(updateWorkingKey(pinKey, pinKCV, sessionKey, sessionKCV));
          break;
        case "executeCardReading": {
          String amt = call.argument("amount");
          result.success(startSwiper(amt));
          break;
        }
        case "calcMac":
          String mac = call.argument("mac");
          result.success(calcMacCallable(mac));
          break;
        case "onlineAuth": {
          String tlv = call.argument("tlv");
          result.success(onlineAuth(tlv));
          break;
        }
        case "getRandomNumbers":
          result.success(getRandomNumber());
          break;

        case "downloadAID":
          downloadAID();
          break;
        case "downloadPUK":
          downloadPUK();
          break;
        case "dukptInit":
          result.success(loadDukpt());
          break;
        case "setEmvParam": {
          String emvParam = call.argument("emvParam");
          result.success(setTlv(emvParam));
          break;
        }
        case "setSleepTime": {
          int sleepTime = (int) call.argument("sleepTime");
          int shutDownTime = (int) call.argument("shutDownTime");
          result.success(setSleepTime(sleepTime, shutDownTime));
          break;
        }
        default:
          result.notImplemented();
      }
    }catch(Exception e){
      Log.d(TAG, "onMethodCall: "+e.getMessage());
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
  @Override
  public void onDetachedFromActivity() {
//    TODO("Not yet implemented");
    mActivity = null;
  }

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
//    TODO("Not yet implemented");
  }

  @Override
  public void onAttachedToActivity(ActivityPluginBinding binding ) {
    this.mActivity = binding.getActivity();



  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
   // TODO("Not yet implemented");
  }

//  public static boolean permissionRequest(Activity activity, int requestCode) {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//      int storagePermission = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//      int access_location = activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
//      //Check the permission, if don't, you need to apply
//      if (storagePermission != PackageManager.PERMISSION_GRANTED || access_location != PackageManager.PERMISSION_GRANTED) {
//        //apply the permission
//        activity.requestPermissions(premission, requestCode);
//        //return false。There's no permission
//        return false;
//      }
//    }
//    //Already apply the permission
//    return true;
//  }
//
//  private void registerReceiver() {
//    try {
//      br = new BluetoothReceiver();
//      IntentFilter filter = new IntentFilter();
//      filter.addAction(BluetoothDevice.ACTION_FOUND);
//      mContext.registerReceiver(br, filter);
//    } catch (Exception e) {
//      //e.printStackTrace();
//    }
//  }
//
//  private void unregisterReceiver() {
//    if (br != null) {
//      try {
//        mContext.unregisterReceiver(br);
//      } catch (Exception e) {
//        //e.printStackTrace();
//      }
//    }
//    br = null;
//  }
//
//  private void startDiscovery() {
//    Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
//    for (BluetoothDevice device : pairedDevices) {
//      Log.d(TAG, "startDiscovery: btName: " + device.getName() +" mac: "+ device.getAddress());
//    }
//
//  }
//
//  private class BluetoothReceiver extends BroadcastReceiver {
//    @Override
//    public void onReceive(Context context, Intent intent) {
//      // TODO Auto-generated method stub
//      try {
//        if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
//          BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//          Log.d(TAG, "onReceive: btName: "+btDevice.getName() +" mac: "+ btDevice.getAddress() );
//        }
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//    }
//  }

  private void connectDevice(final String mac) {
    Log.d(TAG, "connectDevice: connecting to "+ mac);
//    Log.d(TAG, "onAttachedToActivity: here");
//    if(permissionRequest(mActivity, 1)){

//      btAdapter.enable();
//
//      registerReceiver();
//
//      btAdapter.cancelDiscovery();
//      startDiscovery();
//    }
//    BluetoothManager bluetoothManager = (BluetoothManager) mActivity.getSystemService(Context.BLUETOOTH_SERVICE);
//    btAdapter.cancelDiscovery();
//    if (Controler.posConnected()) {
//      Controler.disconnectPos();
//    }



//    Log.d(TAG, "connectDevice: "+ret.result);

//    calcMacCallable().subscribeOn(Schedulers.newThread())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(new Consumer<String>() {
//              @Override
//              public void accept(String s) throws Exception {
//                Log.d(TAG, "accept: "+s);
//              }
//            });

//      calcMacCallable(mac);
//    return Controler.posConnected();

//    Thread one = new Thread() {
//      public void run() {
//        try {
//          System.out.println("Does it work?");

          ConnectPosResult ret = Controler.connectPos(mac);
          mResult.success(ret.bConnected);


  }

  private void isConnected(){
    mResult.success(Controler.posConnected());
  }

  private void disConnectDevice(String mac) {
    Controler.posConnected();
  }

  private Map<String, Object> calcMacCallable(String macs) {
//    return Observable.fromCallable(new Callable<String>() {
//      @Override
//      public String call() {
//        Controler.connectPos(macd);

    Map<String, Object> map = new HashMap<>();

        String macData = "1234567890123000";
        byte[] mac = Misc.asc2hex(macData, macData.length(), 0);
        CommEnum.MACALG macalg = CommEnum.MACALG.ENCRYPTION_MAC_X919_DUCKPT;
        CalMacResult result = Controler.CalMac(macalg, mac, mac.length);
        if (result.commResult.equals(CommEnum.COMMRET.NOERROR)) {
          StringBuilder builder = new StringBuilder();
          Log.d(TAG, "calcMac mac:" + Misc.hex2asc(result.macvalue));
          Log.d(TAG, "calcMac mMacRandom:" + Misc.hex2asc(result.macrandom));
          builder.append("\nmacValue:" + Misc.hex2asc(result.macvalue));
          builder.append("\nmacrandom:" + Misc.hex2asc(result.macrandom));
          map.put("success", true);
          map.put("message", "Mac Calculated successfully");
          map.put("macValue", Misc.hex2asc(result.macvalue));
          map.put("macRandom", Misc.hex2asc(result.macrandom));
          return map;
//          return builder.toString();
        }

      map.put("success", false);
      map.put("message", "Mac Calculation failed");

        return map;
//        return "Error";
//      }
//    });
  }

  private final String[] aids = new String[]{
          //Union Pay
          "9F0608A000000333010100DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000200000DF1906000000200000DF2006000002000000DF2106000000100000",
          "9F0608A000000333010101DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000200000DF1906000000200000DF2006000002000000DF2106000000100000",
          "9F0608A000000333010102DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000200000DF1906000000200000DF2006000002000000DF2106000000100000",
          "9F0608A000000333010103DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000200000DF1906000000200000DF2006000002000000DF2106000000100000",
          //Visa
          "9F0607A0000000031010DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000200000DF1906000000200000DF2006000002000000DF2106000000100000",
          //Master
          "9F0607A0000000041010DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000200000DF1906000000200000DF2006000002000000DF2106000000100000",
          //Local Master
          "9F0607D4100000012010DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000200000DF1906000000200000DF2006000002000000DF2106000000100000",
          //Local Visa
          "9F0607D4100000011010DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000200000DF1906000000200000DF2006000002000000DF2106000000100000",
          //AMERICAN EXPRESS
          "9F0608A000000025010402DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000200000DF1906000000200000DF2006000002000000DF2106000000100000",
          //AMERICAN EXPRESS
          "9F0608A000000025010501DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000200000DF1906000000200000DF2006000002000000DF2106000000100000",
          //JCB
          "9F0607A0000000651010DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000200000DF1906000000200000DF2006000002000000DF2106000000100000",
          //D-PAS
          "9F090200649F0607A0000001523010DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000200000DF1906000000200000DF2006000002000000DF2106000000100000",
          //Rupay
          "9F090200649F0607A0000005241010DF010100DF11050000000000DF12050000000000DF130500000000009F1B04000186A0DF14039F3704DF150400000000DF160105DF170100DF1801319F7B06000000200000DF1906000000200000DF2006000002000000DF2106000000100000",
          "9F090200649F0608A000000524010101DF010100DF11050000000000DF12050000000000DF130500000000009F1B04000186A0DF150400000000DF160100DF170100DF14039F3704DF1801319F7B06000000010000DF1906000000010000DF2006000000050000DF2106000000004000",
          "9F090200649F0607A0000005241011DF010100DF11050000000000DF12050000000000DF130500000000009F1B04000186A0DF14039F3704DF150400000000DF160105DF170100DF1801319F7B06000000200000DF1906000000200000DF2006000002000000DF2106000000100000",
  };

  private void downloadAID() {
    for (String aid : aids) {
      Controler.ICAidManage(CommEnum.ICAIDACTION.ADD, Misc.asc2hex(aid));
    }
  }

  private String readAIDList() {
    ICAidResult result = Controler.ICAidManage(CommEnum.ICAIDACTION.READLIST, null);
    if (result.commResult.equals(CommEnum.COMMRET.NOERROR)) {
      StringBuilder builder = new StringBuilder();
      builder.append("aid len:" + result.aidLen);
      builder.append("\r\naid list:" + Misc.hex2asc(result.aid));
      return builder.toString();
    }
    return null;
  }

  private final String[] rids = new String[]{
          "9F0605A0000003339F220102DF050420211231DF060101DF070101DF028190A3767ABD1B6AA69D7F3FBF28C092DE9ED1E658BA5F0909AF7A1CCD907373B7210FDEB16287BA8E78E1529F443976FD27F991EC67D95E5F4E96B127CAB2396A94D6E45CDA44CA4C4867570D6B07542F8D4BF9FF97975DB9891515E66F525D2B3CBEB6D662BFB6C3F338E93B02142BFC44173A3764C56AADD202075B26DC2F9F7D7AE74BD7D00FD05EE430032663D27A57DF040103DF031403BB335A8549A03B87AB089D006F60852E4B8060",
          "9F0605A0000003339F220103DF050420221231DF060101DF070101DF0281B0B0627DEE87864F9C18C13B9A1F025448BF13C58380C91F4CEBA9F9BCB214FF8414E9B59D6ABA10F941C7331768F47B2127907D857FA39AAF8CE02045DD01619D689EE731C551159BE7EB2D51A372FF56B556E5CB2FDE36E23073A44CA215D6C26CA68847B388E39520E0026E62294B557D6470440CA0AEFC9438C923AEC9B2098D6D3A1AF5E8B1DE36F4B53040109D89B77CAFAF70C26C601ABDF59EEC0FDC8A99089140CD2E817E335175B03B7AA33DDF040103DF031487F0CD7C0E86F38F89A66F8C47071A8B88586F26",
          "9F0605A0000003339F220104DF050420221231DF060101DF070101DF0281F8BC853E6B5365E89E7EE9317C94B02D0ABB0DBD91C05A224A2554AA29ED9FCB9D86EB9CCBB322A57811F86188AAC7351C72BD9EF196C5A01ACEF7A4EB0D2AD63D9E6AC2E7836547CB1595C68BCBAFD0F6728760F3A7CA7B97301B7E0220184EFC4F653008D93CE098C0D93B45201096D1ADFF4CF1F9FC02AF759DA27CD6DFD6D789B099F16F378B6100334E63F3D35F3251A5EC78693731F5233519CDB380F5AB8C0F02728E91D469ABD0EAE0D93B1CC66CE127B29C7D77441A49D09FCA5D6D9762FC74C31BB506C8BAE3C79AD6C2578775B95956B5370D1D0519E37906B384736233251E8F09AD79DFBE2C6ABFADAC8E4D8624318C27DAF1DF040103DF0314F527081CF371DD7E1FD4FA414A665036E0F5E6E5"
  };

  private void downloadPUK() {
    for (String rid : rids) {
      Controler.ICPublicKeyManage(CommEnum.ICPUBLICKEYACTION.ADD, Misc.asc2hex(rid));
    }
  }

  public Map<String, Object> getDeviceInfo() {
    ReadPosInfoResult readPosInfoResult = Controler.ReadPosInfo2();

    Map<String, Object> map = new HashMap<>();
    StringBuilder builder = new StringBuilder();
    Log.d(TAG, "getDeviceInfo: "+readPosInfoResult.sn);

    builder.append("\nPosVer:" + readPosInfoResult.posVer);
    builder.append("\nDataVer:" + readPosInfoResult.dataVer);
    builder.append("\nModel:" + readPosInfoResult.model);
    builder.append("\nSn:" + readPosInfoResult.sn);
//    builder.append("\nStatus:" + readPosInfoResult.initStatus);



    map.put("posVersion", readPosInfoResult.posVer);
    map.put("dataVersion", readPosInfoResult.dataVer);
    map.put("model", readPosInfoResult.model);
    map.put("serialNo", readPosInfoResult.sn);
//    map.put("initStatus", readPosInfoResult.initStatus);
    /**
     * Battery status
     * 0    No power off
     * 1    Low battery (tension is low)
     * 2    On battery (normal)
     * 3    High battery (full battery)
     * 4    Full
     * 5    Charging
     */
    map.put("battery", readPosInfoResult.btype);

    return map;
  }

  private String getRandomNumber() {
    GetRandomResult r = Controler.GetRandomNum();

    if (r.commResult.equals(CommEnum.COMMRET.NOERROR)) {
      return Misc.hex2asc(r.randomNum);

    }
    return null;
  }

  private String loadDukpt() {
    byte[] bdk = Misc.asc2hex("C1D0F8FB4958670DBA40AB1F3752EF0D");
    byte[] ksn = Misc.asc2hex("FFFF9876543210000000");

    LoadDukptResult bret = Controler.LoadDukpt((byte) 0x01, CommEnum.KEYINDEX.INDEX0, bdk, ksn);

    StringBuilder sb = new StringBuilder();
    sb.append("LoadDukpt result:" + bret.loadResult);
    sb.append("LoadDukpt checkvalue:" + Misc.hex2asc(bret.checkvalue));

    return sb.toString();
  }

  private boolean loadKek(String key) {
//    String key = "11111111111111112222222222222222D2B91CC5";

    byte[] kek1 = new byte[8];
    byte[] kek2 = new byte[8];
    byte[] kvc = new byte[4];

    kek1 = Misc.asc2hex(key, 0, 16, 0);
    kek2 = Misc.asc2hex(key, 16, 16, 0);
    kvc = Misc.asc2hex(key, 32, 8, 0);

    LoadKekResult result = Controler.LoadKek(CommEnum.KEKTYPE.DOUBLE, kek1, kek2, kvc);
    return result.loadResult;
  }

  private String onlineAuth(String text) {

    try {
      byte[] b = Misc.asc2hex(text);
      List<byte[]> tags = new ArrayList<byte[]>();

      EmvDealOnlineRspResult r = Controler.EmvDealOnlineRsp(true, b, b.length);

      if (r.commResult.equals(CommEnum.COMMRET.NOERROR)) {
        if (r.authResult.equals(CommEnum.EMVDEALONLINERSP.SUCC)) {
          tags.add(new byte[]{(byte) 0x9F, (byte) 0x26});
          tags.add(new byte[]{(byte) 0x95});
          tags.add(new byte[]{(byte) 0x4F});
          tags.add(new byte[]{(byte) 0x5F, (byte) 0x34});
          tags.add(new byte[]{(byte) 0x9B});
          tags.add(new byte[]{(byte) 0x9F, (byte) 0x36});
          tags.add(new byte[]{(byte) 0x82});
          tags.add(new byte[]{(byte) 0x9F, (byte) 0x37});
          tags.add(new byte[]{(byte) 0x50});

          GetEmvDataResult rdata = Controler.GetEmvData(tags, false);
          if (rdata.commResult.equals(CommEnum.COMMRET.NOERROR)) {
            return Misc.hex2asc(rdata.tlvData);
          } else {
            return r.commResult.toDisplayName();
          }
        }
        return null;
      } else {
        return r.commResult.toDisplayName();
      }
    } catch (NullPointerException e) {
      return e.getMessage();
    } catch (Exception e) {
      return e.getMessage();
    }
  }

  private boolean setTlv(String emvParam) {
    return Controler.SetEmvParamTlv(emvParam);
  }

  private ReadCardParam.onStepListener stepListener = new ReadCardParam.onStepListener() {
    @Override
    public void onStep(byte step) {
      switch (step) {
        case 1://waiting read card
//          showResult(getString(R.string.wait_read_card));
          Log.d(TAG, "onStep: wait read card");
          break;
        case 2://Reading card
//          showResult(getString(R.string.reading_card));
          Log.d(TAG, "onStep: reading card");

          break;
        case 3://Waiting enter the password
//          showResult(getString(R.string.waiting_input_pin));
          Log.d(TAG, "onStep: input pin");

          break;
        case 4://Waiting enter the amount
          break;
        case 5:
          break;
        case 6:
          break;
        case 7:
          break;
      }
    }
  };

  private ReadCardParam getReadCardParam(String amount,
                                         ReadCardParam.onStepListener listener) {
    ReadCardParam param = new ReadCardParam();
//    String amount = "000000000001";
    byte timeOut = 40;

    param.setAllowfallback(true);
    param.setAmount(Long.parseLong(amount));
    param.setPinInput((byte) 1);
    param.setPinMaxLen((byte) 4);
    param.setCardTimeout((byte) timeOut);
    param.setRequireReturnCardNo((byte) 0x1);
    param.setEmvTransactionType((byte) 0x00);
    param.setForceonline(true);
    param.setTransName("ParaPay Agent");
    param.setTags(getTags());
    //0x01 for swipe card mode, 0x02 for chip and 0x04 TF (Tap), don't set if you want to use any of them
    param.setCardmode((byte) 0x02);

    param.setOnSteplistener(listener);
    return param;
  }

  private List<byte[]> getTags() {
    List<byte[]> tags = new ArrayList<byte[]>();

    tags.add(EmvTagDef.EMV_TAG_9F02_TM_AUTHAMNTN);
    tags.add(EmvTagDef.EMV_TAG_9F26_IC_AC);
    tags.add(EmvTagDef.EMV_TAG_9F27_IC_CID);
    tags.add(EmvTagDef.EMV_TAG_9F10_IC_ISSAPPDATA);
    tags.add(EmvTagDef.EMV_TAG_9F37_TM_UNPNUM);
    tags.add(EmvTagDef.EMV_TAG_9F36_IC_ATC);
    tags.add(EmvTagDef.EMV_TAG_95_TM_TVR);
    tags.add(EmvTagDef.EMV_TAG_9A_TM_TRANSDATE);
    tags.add(EmvTagDef.EMV_TAG_9C_TM_TRANSTYPE);
    tags.add(EmvTagDef.EMV_TAG_5F2A_TM_CURCODE);
    tags.add(EmvTagDef.EMV_TAG_82_IC_AIP);
    tags.add(EmvTagDef.EMV_TAG_9F1A_TM_CNTRYCODE);
    tags.add(EmvTagDef.EMV_TAG_9F03_TM_OTHERAMNTN);
    tags.add(EmvTagDef.EMV_TAG_9F33_TM_CAP);
    tags.add(EmvTagDef.EMV_TAG_9F34_TM_CVMRESULT);
    tags.add(EmvTagDef.EMV_TAG_9F35_TM_TERMTYPE);
    tags.add(EmvTagDef.EMV_TAG_9F1E_TM_IFDSN);
    tags.add(EmvTagDef.EMV_TAG_84_IC_DFNAME);
    tags.add(EmvTagDef.EMV_TAG_9F09_TM_APPVERNO);
    tags.add(EmvTagDef.EMV_TAG_9F63_TM_BIN);
    tags.add(EmvTagDef.EMV_TAG_9F41_TM_TRSEQCNTR);
    return tags;
  }

  public synchronized Map<String, Object> startSwiper(String amt) {
    ReadCardResult result = Controler.ReadCard(getReadCardParam(amt, stepListener));

    Map<String, Object> map = new HashMap<>();

    if (!result.commResult.equals(CommEnum.COMMRET.NOERROR)) {
//      Log.d(TAG, "ERROR_READ_ERROR");
      Log.d(TAG, "startSwiper: read card error");
      map = new HashMap<>();
     map.put("success", false);
     map.put("message", "Read card error");

      return map;
    } else {
      switch (result.cardType) {
        case 0:
//          showResult(getString(R.string.user_cancel));
          Log.d(TAG, "startSwiper: user cancel");
          //cancel
          map = new HashMap<>();
          map.put("success", false);
          map.put("message", "User cancelled");

          break;
        case 1:
        case 2:
        case 3:
          map = new HashMap<>();
          map.put("success", true);
          map.put("message", "Card Read successful");
          Map<String, Object> data = new HashMap<>();
//          StringBuilder builder = new StringBuilder();
          if (result.cardType == 1) {
//            builder.append("\ncardType:" + "Mag Card");
            data.put("cardType", "Mag Card");
            //Mag Card
          } else if (result.cardType == 2) {
            //IC Card
//            builder.append("\ncardType:" + "IC Card");
            data.put("cardType", "IC Card");
          } else if (result.cardType == 3) {
            //RF Card
//            builder.append("\ncardType:" + "RF Card");
            data.put("cardType", "RF Card");
          }

//          builder.append("{");
//          builder.append("\npan:" + result.pan);
//          builder.append("\npansn:" + result.pansn);
//          builder.append("\npinBlock:" + result.pinblock);
//          builder.append("\ntrack2:" + result.track2);
//          builder.append("\ntrack3:" + result.track3);
//          builder.append("\nicData:" + result.icData);
//          builder.append("\nexpData:" + result.expData);
//          builder.append("\nksn:" + result.ksn);
//          builder.append("\nmac_ksn:" + result.mac_ksn);
//          builder.append("\nmag_ksn:" + result.mag_ksn);
//          builder.append("\npin_ksn:" + result.pin_ksn);
//          builder.append("\n}");

          data.put("pan", result.pan);
          data.put("pansn", result.pansn);
          data.put("pinBlock", result.pinblock);
          data.put("track2", result.track2);
          data.put("track3", result.track3);
          data.put("icData", result.icData);
          data.put("expData", result.expData);
          data.put("ksn", result.ksn);
          data.put("mac_ksn", result.mac_ksn);
          data.put("mag_ksn", result.mag_ksn);
          data.put("pin_ksn", result.pin_ksn);



//          Log.d(TAG, "startSwiper: "+builder.toString());

          map.put("data", data);

          break;
        case 4:
//          showResult(getString(R.string.need_insert_iccard));
          Log.d(TAG, "startSwiper: need insert iccard");
          //Need Insert ICCard
          map = new HashMap<>();
          map.put("success", false);
          map.put("message", "Need Insert ICCard");

          break;
        case 5:
          //TimeOut
//          showResult(getString(R.string.timeout));
          Log.d(TAG, "startSwiper: timeout");
          map = new HashMap<>();
          map.put("success", false);
          map.put("message", "Time out");

          break;
        case 6:
//          showResult(getString(R.string.read_card_error));
          Log.d(TAG, "startSwiper: read card error");
          map = new HashMap<>();
          map.put("success", false);
          map.put("message", "Read card error");
          //read error

          break;
        default:
          break;
      }
      return map;
    }
  }

  public class UpdatePosProc implements IUpdatePosProc {
    InputStream fs;

    public UpdatePosProc(InputStream fs) {
      // TODO Auto-generated constructor stub
      this.fs = fs;
    }

    @Override
    public void UpdateProcess(final int totalSize, final int alreadySize) {
//      setProgress("Upgrading..." +  alreadySize  + "/" + totalSize);
      Log.d(TAG, "UpdateProcess: Upgrading..." + alreadySize + "/" + totalSize);
    }

    @Override
    public int totalsize() throws IOException {
      // TODO Auto-generated method stub
      return this.fs.available();
    }

    @Override
    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
      // TODO Auto-generated method stub
      return this.fs.read(buffer, byteOffset, byteCount);
    }
  }

  public Boolean updateFirmware() {
//    AssetManager manager = getResources().getAssets();
//    try {
//      InputStream inputStream;
//      File file = new File(Environment.getExternalStorageDirectory(), "mpos_reader.bin");
//      if (file.exists()) {
//        Log.d(TAG, "updateFirmware from storage");
//        inputStream  = new FileInputStream(file);
//      } else {
//        Log.d(TAG, "updateFirmware from assets");
//        inputStream = manager.open("mpos_60l.bin");
//      }
//
//      UpdatePosProc updateProc = new UpdatePosProc(inputStream);
//      UpdatePosResult result = Controler.UpdatePos(updateProc);
//      if (result.isComplete()) {
//        return true;
//      }
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
    return false;
  }

  private boolean loadMainKey(String key, String kcv) {
//    String key = "1111111111111111111111111111111182E13665";

    Log.d(TAG, "loadMainKey: "+key + " --- "+ kcv);
    byte[] keyBuf = BytesUtil.hexString2ByteArray(key);

//    byte[] kekD1 = BytesUtil.subBytes(keyBuf, 0, 8);
//    byte[] kekD2 = BytesUtil.subBytes(keyBuf, 8, 8);
    byte[] kekD1 = Misc.asc2hex(key, 0, 16, 0);
    byte[] kekD2 = Misc.asc2hex(key, 16, 16, 0);
    byte[] kvcBuf = BytesUtil.hexString2ByteArray(kcv);

    Misc.traceHex(TAG, "updateMainKey kekD1", kekD1);
    Misc.traceHex(TAG, "updateMainKey kekD2", kekD2);
    Misc.traceHex(TAG, "updateMainKey kvc", kvcBuf);

    LoadMainKeyResult result = Controler.LoadMainKey(CommEnum.MAINKEYENCRYPT.PLAINTEXT,
            CommEnum.KEYINDEX.INDEX0,
            CommEnum.MAINKEYTYPE.DOUBLE,
            kekD1, kekD2, kvcBuf);
    return result.loadResult;
  }

  private boolean updateWorkingKey(String pinKey, String pinKCV,
                                   String sessionKey, String sessionKCV) {
//    String pinKey = "B7C60530D82A361516E938B5343D2F774C82B0AF";
//    String macKey = "B7C60530D82A361516E938B5343D2F774C82B0AF";
//    String tdkKey = "B7C60530D82A361516E938B5343D2F774C82B0AF";
    Log.d(TAG, "updateWorkingKey: "+pinKey + " --- "+ pinKCV  +" --- "+ sessionKey +  " --- " +sessionKCV);

    String key = pinKey + pinKCV  + sessionKey + sessionKCV + sessionKey + sessionKCV;
//    String key = pinKey + pinKCV;

    CommEnum.KEYINDEX mainKeyIndex = CommEnum.KEYINDEX.INDEX0;

    byte[] keyArrays = Misc.asc2hex(key);
    Log.d(TAG, "updateWorkingKey key:" + key);

    LoadWorkKeyResult result = Controler.LoadWorkKey(mainKeyIndex, CommEnum.WORKKEYTYPE.DOUBLEMAG,
            keyArrays, keyArrays.length);

    return result.loadResult;
  }

  private boolean setSleepTime(int sleepTime, int shutDownTime){
    return Controler.setSleepTime(sleepTime, shutDownTime);

  }

  private static class MethodResultWrapper implements Result {
    private Result methodResult;
    private Handler handler;

    MethodResultWrapper(Result result) {
      methodResult = result;
      handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void success(final Object result) {
      handler.post(
              new Runnable() {
                @Override
                public void run() {
                  methodResult.success(result);
                }
              });
    }

    @Override
    public void error(
            final String errorCode, final String errorMessage, final Object errorDetails) {
      handler.post(
              new Runnable() {
                @Override
                public void run() {
                  methodResult.error(errorCode, errorMessage, errorDetails);
                }
              });
    }

    @Override
    public void notImplemented() {
      handler.post(
              new Runnable() {
                @Override
                public void run() {
                  methodResult.notImplemented();
                }
              });
    }
  }
}
