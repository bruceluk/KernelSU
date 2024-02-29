/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vdroid.core;

//import java.rmi.RemoteException;

import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import androidx.annotation.NonNull;

import com.topjohnwu.superuser.ipc.RootService;

import java.lang.reflect.Method;
import java.util.List;

import com.vdroid.core.IHttpInterface;

public class HttpService extends RootService {
    private static final String TAG = "HttpService";
    private static HttpService mService;
    public static HttpService getService(){
        return mService;
    }

    class Stub extends IHttpInterface.Stub {
        @Override
        public String getSystemProperty(String key, String def) {
            try {
                Class<?> c = Class.forName("android.os.SystemProperties");
                Method get = c.getMethod("get", String.class);

                return (String) get.invoke(c, key);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return def;
        }
    }

    public HttpService(){
        mService = this;
        Utils.log("HttpService constructor");
    }

    @Override
    public IBinder onBind(@NonNull Intent intent) {
        Utils.log("onBind", Httpd.VERSION, Httpd.RUNTYPE);
        return new HttpService.Stub();
    }

    @Override
    public void onRebind(@NonNull Intent intent) {
        Utils.log("onRebind, daemon process reused");
    }

    @Override
    public void onCreate() {
        Utils.log("HttpService onCreate");
//
//        final PackageManager packageManager = getPackageManager();
//        if (packageManager != null) {
//            try {
//                Httpd.VERSION = String.valueOf(packageManager.getPackageInfo(getPackageName(), 0).getLongVersionCode());
//                Httpd.RUNTYPE = packageManager.getPackageInfo(getPackageName(), 0).versionName;
//            } catch (PackageManager.NameNotFoundException e) {
//                Httpd.VERSION = e.toString();
//            }
//        }
//
//        Utils.log("HttpService onCreate", Httpd.VERSION, Httpd.RUNTYPE);
//        super.onCreate();
//        mService = this;
//        //RootShell.exec("ip rule add fwmark 1 lookup 100");
//        Httpd.start();
        //setup();
        //SetServerIps.reload();
        //requestPermission();
    }

    @Override
    public void onDestroy() {
        Utils.log();
//        Httpd.stop();
    }
/*
    private void setup() {
        try {
            boolean needReboot = false;
            ContentResolver contentResolver = getContentResolver();
            final IInstalld installd = IInstalld.Stub.asInterface(ServiceManager.getService("installd"));
            if(Settings.Secure.getInt(contentResolver, 
                Settings.Secure.USER_SETUP_COMPLETE, 0) == 0) {
                Settings.Secure.putInt(contentResolver, 
                    Settings.Secure.USER_SETUP_COMPLETE, 1);
                Settings.Global.putInt(contentResolver, 
                    Settings.Global.DEVICE_PROVISIONED, 1);
                Settings.Global.putInt(contentResolver, 
                    Settings.Global.INSTALL_NON_MARKET_APPS, 1);
                Settings.Global.putInt(contentResolver, 
                    Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 1);

                needReboot = true;
            }
            Settings.Global.putInt(contentResolver, 
                Settings.Global.ADB_ENABLED, 1);
            Settings.Global.putInt(getContentResolver(), 
                    Settings.Global.WIFI_SLEEP_POLICY, 
                    Settings.Global.WIFI_SLEEP_POLICY_NEVER);
            Settings.Global.putInt(getContentResolver(), 
                    Settings.Global.WIFI_SLEEP_POLICY, 
                    Settings.Global.WIFI_SLEEP_POLICY_NEVER);
            Settings.System.putInt(getContentResolver(), 
                    Settings.System.SCREEN_BRIGHTNESS_MODE, 
                    Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            Settings.System.putInt(getContentResolver(), 
                    Settings.System.SCREEN_BRIGHTNESS, 
                    10 + (int)(Math.random() * 30));
            Settings.Global.putString(HttpService.getService().getContentResolver(), 
                    Settings.Global.PRIVATE_DNS_MODE, 
                    "off");
            Settings.Global.putString(HttpService.getService().getContentResolver(), 
                    Settings.Global.NTP_SERVER, 
                    "ntp1.aliyun.com");

            WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            Utils.log("device width", String.valueOf(dm.widthPixels));
            // if(dm.widthPixels == 480 || dm.widthPixels == 640) {
            //     Root root = new Root();
            //     String device = root.obj.getVdroid("ro.product.vendor.device", "");
            //     if(device.equals("sailfish")) {
            //         SetProp.set("vdroid.display.x_pixels", "1080");
            //         SetProp.set("vdroid.display.y_pixels", "1920");
            //         // SetProp.set("vdroid.display.x_dpi", "326");
            //         // SetProp.set("vdroid.display.y_dpi", "326");
            //         // SetProp.set("vdroid.display.fps", "60");
            //         needReboot = true;
            //     }else if(device.equals("marlin")) {
            //         SetProp.set("vdroid.display.x_pixels", "1440");
            //         SetProp.set("vdroid.display.y_pixels", "2560");
            //         // SetProp.set("vdroid.display.x_dpi", "326");
            //         // SetProp.set("vdroid.display.y_dpi", "326");
            //         // SetProp.set("vdroid.display.fps", "60");
            //         needReboot = true;
            //     }else if(device.equals("walleye")) {
            //         SetProp.set("vdroid.display.x_pixels", "1080");
            //         SetProp.set("vdroid.display.y_pixels", "1920");
            //         // SetProp.set("vdroid.display.x_dpi", "420");
            //         // SetProp.set("vdroid.display.y_dpi", "420");
            //         // SetProp.set("vdroid.display.fps", "60");
            //         needReboot = true;
            //     }else{
            //         SetProp.set("vdroid.display.x_pixels", "1080");
            //         SetProp.set("vdroid.display.y_pixels", "1920");
            //         Utils.log("unknow device", device);
            //     }
            // }

            if(needReboot) {
                Thread.sleep(1000);
                Reboot.reboot();
            }else{
                Unlock.makeSureUnlock();
            }
            Shell.exec("svc data enable");
            Shell.exec("svc power stayon ac");
        }catch(Exception e) {
            Utils.log(e.toString());
        }
    }

    public void requestPermission() {
        if( ContextCompat.checkSelfPermission(this, 
            Manifest.permission.WRITE_EXTERNAL_STORAGE) != 
            PackageManager.PERMISSION_GRANTED) {
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            Utils.log("WRITE_EXTERNAL_STORAG", "granted");
        }else{
            try {
                Utils.log("WRITE_EXTERNAL_STORAGE", "not granted", getPackageName(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                IPackageManager pm = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
                pm.grantRuntimePermission(getPackageName(), Manifest.permission.WRITE_EXTERNAL_STORAGE, UserHandle.USER_OWNER);
                pm.updatePermissionFlags(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, 
                    getPackageName(), 
                    PackageManager.FLAG_PERMISSION_GRANTED_BY_DEFAULT,
                    PackageManager.FLAG_PERMISSION_GRANTED_BY_DEFAULT,
                    true,
                    UserHandle.USER_OWNER);    
            } catch(RemoteException e) {
                Utils.log("WRITE_EXTERNAL_STORAGE", "grant fail", e.toString());
            }
        }
    }*/
}

/*
 * Copyright 2023 John "topjohnwu" Wu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.topjohnwu.libsuexample;

        import static com.topjohnwu.libsuexample.MainActivity.TAG;

        import android.content.Intent;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.IBinder;
        import android.os.Looper;
        import android.os.Message;
        import android.os.Messenger;
        import android.os.Process;
        import android.os.RemoteException;
        import android.util.Log;

        import androidx.annotation.NonNull;

        import com.topjohnwu.superuser.ipc.RootService;

        import java.util.UUID;

// Demonstrate root service using Messengers
class MSGService extends RootService implements Handler.Callback {

    static final int MSG_GETINFO = 1;
    static final int MSG_STOP = 2;
    static final String UUID_KEY = "uuid";

    private String uuid;

    @Override
    public void onCreate() {
        uuid = UUID.randomUUID().toString();
        Log.d(TAG, "MSGService: onCreate, " + uuid);
    }

    @Override
    public IBinder onBind(@NonNull Intent intent) {
        Log.d(TAG, "MSGService: onBind");
        Handler h = new Handler(Looper.getMainLooper(), this);
        Messenger m = new Messenger(h);
        return m.getBinder();
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        if (msg.what == MSG_STOP) {
            stopSelf();
            return false;
        }
        if (msg.what != MSG_GETINFO)
            return false;
        Message reply = Message.obtain();
        reply.what = msg.what;
        reply.arg1 = Process.myPid();
        reply.arg2 = Process.myUid();
        Bundle data = new Bundle();
        data.putString(UUID_KEY, uuid);
        reply.setData(data);
        try {
            msg.replyTo.send(reply);
        } catch (RemoteException e) {
            Log.e(TAG, "Remote error", e);
        }
        return false;
    }

    @Override
    public boolean onUnbind(@NonNull Intent intent) {
        Log.d(TAG, "MSGService: onUnbind, client process unbound");
        // Default returns false, which means onRebind will not be called
        return false;
    }
}