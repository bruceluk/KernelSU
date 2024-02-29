package com.vdroid.core;

import android.content.Context;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.topjohnwu.superuser.Shell;
import com.topjohnwu.superuser.ipc.RootService;
import com.vdroid.core.IHttpInterface;

public class HttpServiceConnection implements ServiceConnection {
    public static final String TAG = "HttpServiceConnection";
    static private HttpServiceConnection daemonConn;
    public HttpServiceConnection() {
        Log.i(TAG, "new" );
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "onServiceConnected");
        daemonConn = this;

        IHttpInterface ipc = IHttpInterface.Stub.asInterface(service);
        try {
            Utils.log("serialno", ipc.getSystemProperty("ro.serialno", ""));
        } catch (RemoteException e) {
            Log.e(TAG, "Remote error", e);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "onServiceDisconnected");
        daemonConn = null;
    }

    public static void start(Context ctx) {
        Intent intent = new Intent(ctx, HttpService.class);
        intent.addCategory(RootService.CATEGORY_DAEMON_MODE);
        //RootService.bind(intent, new HttpServiceConnection());
        Shell.Task task = RootService.bindOrTask(intent, Shell.EXECUTOR, new HttpServiceConnection());
        task.
    }

    public static void stop(Context ctx) {
        Intent intent = new Intent(ctx, HttpService.class);
        intent.addCategory(RootService.CATEGORY_DAEMON_MODE);
        RootService.stop(intent);
    }

}
