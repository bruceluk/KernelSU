package com.vdroid.core;

import android.os.RemoteException;
import android.util.ArrayMap;

import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;
import java.util.Enumeration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import android.content.pm.PackageManager;

import org.java_websocket.enums.ReadyState;

import com.vdroid.core.command.Command;
import com.vdroid.core.command.HttpRequest;

/** {@hide} */
public class Httpd {

    public static String VERSION = "unknow";
    public static String RUNTYPE = "LINEAGE";

    public static ServerSocket serverSocket;
    private static boolean isStart = false;
    private static long chargeCheckLastTime = System.currentTimeMillis();

    public static final String SERVER_IP = "0.0.0.0";
    public static final int SERVER_PORT = 8080;

    public static String SERVER_DOMAIN = "api.vdroid.xyz";

    public static String VDROID_PATH;
    public static String TMP_PATH;
    public static String UPLOADS_PATH;

    public static final String CONTENT_LENGTH_REGEX = "Content-Length:";
    public static final Pattern CONTENT_LENGTH_PATTERN = Pattern.compile(CONTENT_LENGTH_REGEX, Pattern.CASE_INSENSITIVE);
    public static final String CONTENT_TYPE_REGEX = "([ |\t]*content-type[ |\t]*:)(.*)";
    public static final Pattern CONTENT_TYPE_PATTERN = Pattern.compile(CONTENT_TYPE_REGEX, Pattern.CASE_INSENSITIVE);

    public static String[] mAddtionalApps = new String[]{};
    public static String[] mAddtionalBackupPaths = new String[]{};
    public static String[] mAddtionalRemovePaths = new String[]{};

    public static String[] mClientIps = new String[]{};

    public static Map<String, String> mProps = new ArrayMap<String, String>();

    private static Map<String, Command> mCommands = new LinkedHashMap<String, Command>();

    private static MainThread mMainThread;
    private static UploadThread mUploadThread;
//    public static Websocket mWebsocket;
    private static String mUpdatedRomName = "";
    public static String mWifiSSID;
    public static String mWifiPass;
    public static String mStartupCommands;
    public static boolean mNeedFactoryReset = false;
    public static long mLastLogined;
    public static boolean mLocalStorage = false;
    
    public static boolean start(){
        VDROID_PATH = HttpService.getService().getFilesDir().getAbsolutePath();
        TMP_PATH = VDROID_PATH + "/tmp/";
        UPLOADS_PATH = VDROID_PATH + "/uploads/";

        Utils.log("Httpd start");

        if(isStart){
            Utils.log();
            return true;
        }

        mCommands.put("version", new com.vdroid.core.command.Version());

//        mCommands.put("install", new com.vdroid.core.command.Install());
//        mCommands.put("uninstall", new com.vdroid.core.command.Uninstall());
//        mCommands.put("upgradeOTA", new com.vdroid.core.command.UpgradeOTA());
//        mCommands.put("setServerIps", new com.vdroid.core.command.SetServerIps());
//
//        mCommands.put("ping", new com.vdroid.core.command.Ping());
//        mCommands.put("shell", new com.vdroid.core.command.Shell());
//        mCommands.put("rootShell", new com.vdroid.core.command.RootShell());
//        mCommands.put("executeSh", new com.vdroid.core.command.ExecuteSh());
//        mCommands.put("screenshot", new com.vdroid.core.command.Screenshot());
//        mCommands.put("screenshotPost", new com.vdroid.core.command.ScreenshotPost());
//        mCommands.put("download", new com.vdroid.core.command.Download());
//        mCommands.put("ftpUpload", new com.vdroid.core.command.FtpUpload());
//        mCommands.put("ftpListFiles", new com.vdroid.core.command.FtpListFiles());
//        mCommands.put("ftpListDirectories", new com.vdroid.core.command.FtpListDirectories());
//        mCommands.put("ftpGetFile", new com.vdroid.core.command.FtpGetFile());
//        mCommands.put("lock", new com.vdroid.core.command.Lock());
//        mCommands.put("unlock", new com.vdroid.core.command.Unlock());
//        mCommands.put("reboot", new com.vdroid.core.command.Reboot());
//        mCommands.put("setupFtp", new com.vdroid.core.command.SetupFtp());
//        mCommands.put("testFtp", new com.vdroid.core.command.TestFtp());
//
//        mCommands.put("loadDeviceJson", new com.vdroid.core.command.LoadDeviceJson());
//        mCommands.put("loadDeviceStatus", new com.vdroid.core.command.LoadDeviceStatus());
//        mCommands.put("releaseDevice", new com.vdroid.core.command.ReleaseDevice());
//
//        mCommands.put("readFile", new com.vdroid.core.command.ReadFile());
//        mCommands.put("writeFile", new com.vdroid.core.command.WriteFile());
//        mCommands.put("listDir", new com.vdroid.core.command.ListDir());
//        mCommands.put("mkDir", new com.vdroid.core.command.MkDir());
//        mCommands.put("rm", new com.vdroid.core.command.Rm());
//
//        mCommands.put("getProp", new com.vdroid.core.command.GetProp());
//        mCommands.put("setProp", new com.vdroid.core.command.SetProp());
//
//        mCommands.put("input", new com.vdroid.core.command.Input());
//        mCommands.put("inputBase64", new com.vdroid.core.command.InputBase64());
//        mCommands.put("inputCode", new com.vdroid.core.command.InputCode());
//        mCommands.put("hideInput", new com.vdroid.core.command.HideInput());
//
//        mCommands.put("listBackups", new com.vdroid.core.command.ListBackups());
//        mCommands.put("destroyBackup", new com.vdroid.core.command.DestroyBackup());
//
//        mCommands.put("rpc", new com.vdroid.core.command.Rpc());
//        mCommands.put("setPrivateDNS", new com.vdroid.core.command.SetPrivateDNS());
//        mCommands.put("setTimeZone", new com.vdroid.core.command.SetTimeZone());
//        mCommands.put("setLocale", new com.vdroid.core.command.SetLocale());
//
//        mCommands.put("importContacts", new com.vdroid.core.command.ImportContacts());
//        mCommands.put("exportContacts", new com.vdroid.core.command.ExportContacts());
//        mCommands.put("clearContacts", new com.vdroid.core.command.ClearContacts());
//
//        mCommands.put("setAddtionalApps", new com.vdroid.core.command.SetAddtionalApps());
//        mCommands.put("setAddtionalBackupPaths", new com.vdroid.core.command.SetAddtionalBackupPaths());
//        mCommands.put("setAddtionalRemovePaths", new com.vdroid.core.command.SetAddtionalRemovePaths());
//        mCommands.put("startProxy", new com.vdroid.core.command.StartProxy());
//        mCommands.put("stopProxy", new com.vdroid.core.command.StopProxy());
//        mCommands.put("httpGet", new com.vdroid.core.command.HttpGet());
//
//        mCommands.put("startScrcpy", new com.vdroid.core.command.StartScrcpy());
//        mCommands.put("stopScrcpy", new com.vdroid.core.command.StopScrcpy());
//
//        mCommands.put("setAppNotification", new com.vdroid.core.command.SetAppNotification());
//
//        mCommands.put("factoryReset", new com.vdroid.core.command.FactoryReset());
//        mCommands.put("sleep", new com.vdroid.core.command.Sleep());

        mCommands.put("test", new com.vdroid.core.command.Test());
//        mCommands.put("genSSAID", new com.vdroid.core.command.GenSSAID());
//
//        mCommands.put("setSharedPreferences", new com.vdroid.core.command.SharedPreferencesSet());
//        mCommands.put("listSharedPreferences", new com.vdroid.core.command.SharedPreferencesList());
//        mCommands.put("clearSharedPreferences", new com.vdroid.core.command.SharedPreferencesClear());
//        mCommands.put("getSharedPreferences", new com.vdroid.core.command.SharedPreferencesGet());
//
//        mCommands.put("initHookEnv", new com.vdroid.core.command.InitHookEnv());
//
//        mCommands.put("cameraSetEnable", new com.vdroid.core.command.CameraSetEnable());
//        mCommands.put("cameraPrepared", new com.vdroid.core.command.CameraPrepared());
//        mCommands.put("cameraPlay", new com.vdroid.core.command.CameraPlay());
//        mCommands.put("cameraPause", new com.vdroid.core.command.CameraPause());
//        mCommands.put("cameraStop", new com.vdroid.core.command.CameraStop());
//        mCommands.put("cameraSeekTo", new com.vdroid.core.command.CameraSeekTo());
//        mCommands.put("cameraSetSource", new com.vdroid.core.command.CameraSetSource());
//        mCommands.put("cameraSetVolume", new com.vdroid.core.command.CameraSetVolume());
//        mCommands.put("cameraSetAutoplay", new com.vdroid.core.command.CameraSetAutoplay());
//        mCommands.put("cameraSetPlayback", new com.vdroid.core.command.CameraSetPlayback());
//
//        mCommands.put("setPlatformServer", new com.vdroid.core.command.SetPlatformServer());
        
//        SERVER_DOMAIN = GetProp.getPreference("platformSever", "api.vdroid.xyz");
//        Utils.log("startup do start proxy blocked");
//        V2ray.start(HttpService.getService(), "blocked", "", "", "", "");
        mMainThread = new MainThread();
        mMainThread.start();
        return true;
    }

    public static void stop(){
        try {
            Utils.log();
            isStart = false;
            if(serverSocket != null){
                serverSocket.close();
            }
        } catch (Exception e) {
            Utils.log(e.toString());
        }
    }

    public static boolean isRunning(){
        return isStart;
    }

    public static String[] getClientIps() {
        return mClientIps;
    }

    public static void setClientIps(String[] ips) {
        mClientIps = ips;
    }

    public static Command getCommand(String cmd) {
        return mCommands.get(cmd);
    }

    private Httpd() {
    }

    private static class MainThread extends Thread {
        public MainThread(){
        }
        public void run(){
            Utils.log("MainThread run");
            Utils.log();
//            init();

//            try {
//                int entityId = 0;
//                long startTime = System.currentTimeMillis();
//                while(entityId == 0){
//                    entityId = Websocket.Login();
//                    Utils.log("websocket login", String.valueOf(entityId));
//                    Thread.sleep(5000);
//
//                    ConnectivityHelper.tryConnectWifi();
//                    if(System.currentTimeMillis() - startTime > 360*1000) {
//                        com.vdroid.core.command.Reboot.reboot();
//                    }
//                    if(ConnectivityHelper.tryConnectWifi(mWifiSSID, mWifiPass) &&
//                        (entityId > 0 || entityId < -10000)) {
//                        Utils.log("wifi connected and entityId ok");
//                        break;
//                    }
//                }
//                Utils.log("websocket login", String.valueOf(entityId));
//                mLastLogined = System.currentTimeMillis() + (int)Math.random()*600*1000;
//                initProcValue(entityId);
//
//                mWebsocket = new Websocket("wss://"+SERVER_DOMAIN+"/websocket/entity");
//                mWebsocket.connect();
//            }catch(Exception e){
//                Utils.log("try connect exception, reboot", e.toString());
//                com.vdroid.core.command.Reboot.reboot();
//                return;
//            }

            isStart = true;

//            mUploadThread = new UploadThread();
//            mUploadThread.start();

//            Websocket.executeStartupCommands(Httpd.mStartupCommands);

            try{

                InetAddress addr = InetAddress.getByName(SERVER_IP);
                serverSocket = new ServerSocket(SERVER_PORT, 100, addr);
                serverSocket.setSoTimeout(10000);  //set timeout for listner

                while (isStart) {
                    try {
                        Socket newSocket = serverSocket.accept();
                        Thread newClient = new SocketThread(newSocket);
                        newClient.start();
                    } catch (SocketTimeoutException e) {
                        continue;
                    } catch (Exception e) {
                        Utils.log(e.toString());
                    }
                }
            } catch (Exception e) {
                Utils.log("httpd exception", e.toString());
            }finally{
                serverSocket = null;
                isStart = false;
                //Utils.log("httpd final, reboot");
                //com.vdroid.core.command.Reboot.reboot();
            }
        }

//        void initProcValue(int entityId) {
//            SystemProperties.set("vdroid.entity_id", String.valueOf(entityId));
//            WriteFile.writeFileSystem("/proc/self/vdroid/entity_id", String.valueOf(entityId).getBytes());
//            String deviceId = GetProp.get("vdroid.id");
//            if(deviceId != null && deviceId.length() > 0) {
//                WriteFile.writeFileSystem("/proc/self/vdroid/device_id", deviceId.getBytes());
//            }
//            SetProp.set("vdroid.businessApp", GetProp.get("vdroid.businessApp"));
//            SetProp.set("vdroid.autoApp", GetProp.get("vdroid.autoApp"));
//        }
        
    }

    private static class SocketThread extends Thread {

        protected Socket socket;

        public SocketThread(Socket clientSocket) {
            this.socket = clientSocket;
        }

        @Override
        public void run() {
            DataInputStream in = null;
            DataOutputStream out = null;
            int bytesReceived = 0;
            int bodyReceived = 0;
            try {
                if (socket.isConnected()) {
                    in = new DataInputStream(socket.getInputStream());
                    out = new DataOutputStream(socket.getOutputStream());
                }

                String request = new String();
                byte[] data = new byte[1500];
                //socket.setSoTimeout(60 * 1000 * 5);

                while (true) {
                    int readed = in.read(data);
                    if(readed == -1){
                        HttpRequest.sendError(400, "Bad Request read", out);
                        return;
                    }
                    bytesReceived += readed;

                    request += new String(data, 0, readed);
                    Utils.log(request);
                    int bodyStart = request.indexOf("\r\n\r\n");
                    if(bodyStart == -1) {
                        Utils.log("continue read");
                        continue;
                    }
                    bodyReceived = bytesReceived - bodyStart - 4;
                    String postData = request.substring(bodyStart+4);

                    String[] headers = request.split("\\r?\\n");
                    String method = "GET";
                    String uri;
                    int contentLength = 0;
                    String contentType = "text/html";

                    for (int h = 0; h < headers.length; h++) {
                        String value = headers[h].trim();

                        if (CONTENT_LENGTH_PATTERN.matcher(value).find()) {
                            String val = value.split(":")[1].trim();
                            contentLength = Integer.parseInt(val);
                        } else if (CONTENT_TYPE_PATTERN.matcher(value).find()) {
                            contentType = value.split(":")[1].trim();
                        }
                    }

                    if(headers.length > 1){
                        String firstLine = headers[0];
                        int firstSpacePos = firstLine.indexOf(" ");
                        int lastSpacePos = firstLine.lastIndexOf(" ");
                        if(firstSpacePos < 0 || lastSpacePos < 0 || lastSpacePos == firstSpacePos) {
                            HttpRequest.sendError(400, "Bad Request first line", out);
                        }
                        method = firstLine.substring(0, firstSpacePos);
                        uri = firstLine.substring(firstSpacePos+1, lastSpacePos);
                        Utils.log(firstLine, uri, postData, String.valueOf(bodyReceived));
                        if (method.equalsIgnoreCase("POST")) {
                            while(bodyReceived < contentLength) {
                                readed = in.read(data);
                                if(readed == -1){
                                    HttpRequest.sendError(400, "Bad Request read", out);
                                    return;
                                }
                                bodyReceived += readed;
                                postData = postData + new String(data, 0, readed);
                                Utils.log("postData", postData);
                                Utils.log("postDataLen", 
                                    String.valueOf(bodyReceived),  
                                    String.valueOf(contentLength));
                            }                            
                        }
                        try {
                            processCommand(uri, postData, out);
                        } catch (Exception e) {
                            Utils.log("Exception", e.toString());
                            HttpRequest.sendError(400, e.toString(), out);
                        }
                    }
                }
            } catch (Exception e) {
                Utils.log("Exception", e.toString());
            }finally{
                try {
                    if(out != null){
                        out.flush();
                        out.close();
                    }
                    socket.close();
                } catch (Exception e) {
                    Utils.log(e.toString());
                }
                if(mLastLogined < System.currentTimeMillis()) {
                    //Websocket.Login();
                    mLastLogined = System.currentTimeMillis() + (int)Math.random()*600*1000;
                }
            }
        }
    }

    private static class UploadThread extends Thread {
        public UploadThread(){
            //Utils.log("UploadThread");
        }
        public void run(){
            //Utils.log("UploadThread run");
//            while (isStart) {
//                Utils.log("UploadThread loop");
//                try{
//                    if(!checkAndUpload()){
//                        controlCharger();
//                        try {
//                            if(mWifiSSID != null && mWifiSSID.length() > 0) {
//                                ConnectivityHelper.tryConnectWifi(mWifiSSID, mWifiPass);
//                            }
//
//                            try{
//                                mWebsocket.checkTimeoutReconnect();
//                                Utils.log("loop", String.valueOf(mWebsocket.getReadyState()));
//                            }catch(Exception e){
//                                mWebsocket.removeHeader("Lan");
//                                mWebsocket.addHeader("Lan", Httpd.getLocalIpAddress());
//                                mWebsocket.reconnect();
//                                Utils.log("websocket reconnect exception", e.toString());
//                            }
//                        } catch (Exception e) {
//                            Utils.log("timeout", String.valueOf(mWebsocket.getReadyState()));
//                            if(mWebsocket.getReadyState() == ReadyState.OPEN) {
//                                mWebsocket.sendPing();
//                            }
//                            continue;
//                        }
//                        Thread.sleep(10000);
//                    }
//                } catch (InterruptedException e) {
//                    Utils.log("UploadThread exit", e.toString());
//                    //com.vdroid.core.command.Reboot.reboot();
//                } catch (Exception e) {
//                    Utils.log("Upload Exception", e.toString());
//                }
//                if(mLastLogined < System.currentTimeMillis()) {
//                    Websocket.Login();
//                    mLastLogined = System.currentTimeMillis() + (int)Math.random()*600*1000;
//                }
//            }
        }
    }

    private static void processCommand(String location, String postData, DataOutputStream out) {
        HttpRequest param = new HttpRequest(location, postData, out);
        if(!param.getNext().equals("command")){
            HttpRequest.sendError(400, "Params Error: " + location, out);
            return;
        }

        int entityID = 1;// GetProp.getInt("vdroid.entity_id", 0);
        if(entityID == 0){
            HttpRequest.sendError(400, "Forbidden", out);
            return;
        }

        String command = param.getNext();
        Utils.log(command, param.remain());
        Utils.log(command, postData);
        if(command.equals("usage")) {
            usage(out);
        }else{
            Command exe = mCommands.get(command);
            if(exe != null) {
                exe.execute(param);
            }else{
                Utils.log("Command Not Found", location);
                usage(out);
            }    
        }
    }

    private static void usage(DataOutputStream out) {
        StringBuffer sb = new StringBuffer();
        sb.append("Vdroid " + VERSION + "\n\n");
        for (Map.Entry<String, Command> entry : mCommands.entrySet()) {
            Command cmd = entry.getValue();
            if(cmd.getUsage() != null) {
                sb.append(entry.getKey());
                sb.append("\t");
                sb.append(cmd.getUsage());
                sb.append("\n\n");    
            }
        }
        HttpRequest.sendResponseBinary(sb.toString(), "text/html; charset=UTF-8", out);
    }
   
    public static void setReadable(String fileName, boolean readable, boolean ownerOnly){
        final File f = new File(fileName);
        f.setReadable(readable, ownerOnly);
    }

    public static boolean mkdirs(File file, boolean readable, boolean ownerOnly){
        if (file.exists()) {
            return true;
        }
        if (file.mkdir()) {
            file.setReadable(readable, ownerOnly);
            file.setExecutable(true, false);
            return true;
        }
        File canonFile = null;
        try {
            canonFile = file.getCanonicalFile();
        } catch (IOException e) {
            return false;
        }

        File parent = canonFile.getParentFile();
        if(parent != null){
            if(mkdirs(parent, readable, ownerOnly)){
                canonFile.mkdir();
                canonFile.setExecutable(true, false);
                return canonFile.setReadable(readable, ownerOnly);
            }
        }
        return false;
    }
    
    private static class FileUserMode{
        public String path;
        public int uid;
        public int gid;
        public int mode;
        FileUserMode(){}
    };

    public static boolean updateFiles(String project){
        return true;
    }

    public static void prepareData(String deviceId, String business){
//        try{
//            final IPackageManager pm = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
//            int installFlags = PackageManager.INSTALL_ALL_WHITELIST_RESTRICTED_PERMISSIONS;
//            int installReason = PackageManager.INSTALL_REASON_UNKNOWN;
//
//            pm.installExistingPackageAsUser(
//                business,//packageName,
//                HttpService.getService().getUserId(),//userId,
//                installFlags,
//                installReason,
//                null);
//            Utils.log("prepareData", deviceId, business);
//        }catch(RemoteException e){
//            Utils.log("prepareData", deviceId, business, e.toString());
//        }
    }

    public static void deleteSdcardPath(String business){
//        final IInstalld installd = IInstalld.Stub.asInterface(ServiceManager.getService("installd"));
//
//        deletePathRoot("/sdcard/Android/data/" + business);
//        deletePathRoot("/storage/emulated/0/Android/data/" + business);
//        deletePathRoot("/storage/emulated/0/tencent");
//        deletePathRoot("/storage/emulated/0/tencentmapsdk");
//        deletePathRoot("/sdcard/wechat");
//        deletePathRoot("/sdcard/baidu");
//        deletePathRoot("/sdcard/WhatsApp");
//        deletePathRoot("/sdcard/ByteDownload");
//        deletePathRoot("/sdcard/Catfish");
//        deletePathRoot("/sdcard/autonavi");
//        deletePathRoot("/sdcard/TurboNet");
//        deletePathRoot("/sdcard/alipay");
//        deletePathRoot("/sdcard/soul");
//        deletePathRoot("/sdcard/log.txt");
//        deletePathRoot("/sdcard/qqlog.txt");
//        deletePathRoot("/sdcard/soul");
//
//        try{
//            Root root = new Root();
//            String[] sdcardDirLists = root.obj.ListDir("/sdcard");
//            for (String fileName : sdcardDirLists) {
//                if(fileName.startsWith("file:.")){
//                    fileName = fileName.substring(5);
//                    String filePathName = "/sdcard/" + fileName;
//                    deletePathRoot(filePathName);
//                }else if(fileName.startsWith("dir:.")){
//                    fileName = fileName.substring(4);
//                    String filePathName = "/sdcard/" + fileName;
//                    deletePathRoot(filePathName);
//                }
//            }
//        }catch(Exception e){
//            Utils.log("List Dir Exception", e.toString());
//        }
    }

    public static void deletePathRoot(String path){
//        Root root = new Root();
//        boolean deleted = root.obj.DeletePath(path);
//        if(deleted){
//            Utils.log("delete sdcard data success", path);
//        }else{
//            Utils.log("delete sdcard data fail", path);
//            rootExec("/system/bin/rm -rf " + path);
//        }
    }

    public static boolean syncRomPublic(String romName) {
        return true;
    }

    public static boolean rootExec(String cmd) {
//        Root root = new Root();
//        if(0 == root.obj.Execvp(cmd)) {
//            return true;
//        }
        return false;
    }

    public static boolean checkAndUpload(){
//        try{
//            if(mLocalStorage) {
//                return false;
//            }
//            //Utils.log("checkAndUpload loop");
//            File upload = null;
//            synchronized (mUploadThread) {
//                File uploadPath = new File(UPLOADS_PATH);
//                File[] allFiles = uploadPath.listFiles();
//                for (File file : allFiles) {
//                    if(file.getName().endsWith(".tar.gz")){
//                        if(upload == null || file.lastModified() < upload.lastModified()){
//                            upload = file;
//                        }
//                    }
//                }
//                if(upload != null){
//                    FtpHelper ftp = FtpHelper.getInstance();
//                    if(ftp.login() && ftp.createDir("/uploads")){
//                        String uploadDeviceId = ftp.uploadBackupFile(upload.getAbsolutePath(), true);
//                        ftp.close();
//                        upload.getAbsolutePath();
//                        if("".equals(uploadDeviceId)) {
//                            return false;
//                        }else{
//                            return true;
//                        }
//                    }else{
//                        ftp.close();
//                        //return Rpc.uploadFile(upload.getAbsolutePath(), true);
//                        return false;
//                    }
//                }else if(mNeedFactoryReset) {
//                    com.vdroid.core.command.FactoryReset.reset();
//                }
//            }
//        }catch(Exception e){
//            Utils.log("Exception", e.toString());
//        }
        return false;
    }

    public static boolean uploadDevice(String deviceId){
//        if(mLocalStorage) {
//            return true;
//        }
//        try{
//            File uploadPath = new File(Httpd.UPLOADS_PATH);
//            synchronized (mUploadThread) {
//                File[] allFiles = uploadPath.listFiles();
//                for (File upload : allFiles) {
//                    String name = upload.getName();
//                    if(name.equals(deviceId+".tar.gz")){
//                        FtpHelper ftp = FtpHelper.getInstance();
//                        if(ftp.login() && ftp.createDir("/uploads")){
//                            String uploadDeviceId = ftp.uploadBackupFile(upload.getAbsolutePath(), true);
//                            ftp.close();
//                            if("".equals(uploadDeviceId)) {
//                                return false;
//                            }else{
//                                return true;
//                            }
//                        }else{
//                            ftp.close();
//                            //return Rpc.uploadFile(upload.getAbsolutePath(), true);
//                            return false;
//                        }
//                    }
//                }
//            }
//        }catch(Exception e){
//            Utils.log("Exception", e.toString());
//        }
        return true;
    }

    private static void init() {
        rootExec("/system/bin/rm -rf " + TMP_PATH);
        File dir = new File(VDROID_PATH);
        mkdirs(dir, true, false);
        dir = new File(TMP_PATH);
        mkdirs(dir, true, false);
        dir = new File(UPLOADS_PATH);
        mkdirs(dir, true, false);
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void controlCharger() {
//        if (System.currentTimeMillis() - chargeCheckLastTime < 60*1000) {
//            return;
//        }
//
//        Root root = new Root();
//
//        chargeCheckLastTime = System.currentTimeMillis();
//
//        byte[] bytes = root.obj.ReadFile("/sys/class/power_supply/battery/capacity");
//        String capacityStr = new String(bytes).trim();
//        Utils.log("charge capacity readed", capacityStr);
//        int capacity = Integer.parseInt(capacityStr);
//        int chargerMax = GetProp.getInt("vdroid.charger.max", 75);
//        int chargerMin = GetProp.getInt("vdroid.charger.min", 45);
//        boolean disCharging = false;
//        boolean doCharging = false;
//
//        if(capacityStr.isEmpty() || capacity <= 0 || capacity > 100) {
//            Utils.log("charge capacity read fail", String.valueOf(capacity));
//            return;
//        }
//
//        if(capacity > chargerMax){
//            disCharging = true;
//        }else if(capacity < chargerMin){
//            doCharging = true;
//        }
//
//        Utils.log("charge capacity", String.valueOf(capacity),
//            String.valueOf(disCharging), String.valueOf(doCharging),
//            String.valueOf(chargerMin), String.valueOf(chargerMax));
//
//        String device = root.obj.getVdroid("ro.product.vendor.device", "");
//        if(device.equals("sailfish") || device.equals("marlin")) {
//            String initFile = "/sys/class/power_supply/battery/charger_control";
//            String ctrlFile = "/sys/class/power_supply/battery/charging_enabled";
//            if(disCharging){
//                root.obj.WriteFile(initFile, "0".getBytes());
//                root.obj.WriteFile(ctrlFile, "0".getBytes());
//                Utils.log("charge disable");
//            }else if(doCharging){
//                root.obj.WriteFile(initFile, "1".getBytes());
//                root.obj.WriteFile(ctrlFile, "1".getBytes());
//                Utils.log("charge enable");
//            }
//        }else if(device.equals("walleye") || device.equals("taimen") ||
//                device.equals("blueline") || device.equals("crosshatch")) {
//            String initFile = "/sys/class/power_supply/battery/input_suspend";
//            String ctrlFile = "/sys/class/power_supply/battery/charge_disable";
//            if(disCharging){
//                root.obj.WriteFile(initFile, "1".getBytes());
//                root.obj.WriteFile(ctrlFile, "1".getBytes());
//                Utils.log("charge disable");
//            }else if(doCharging){
//                root.obj.WriteFile(initFile, "0".getBytes());
//                root.obj.WriteFile(ctrlFile, "0".getBytes());
//                Utils.log("charge enable");
//            }
//        }
    }
}