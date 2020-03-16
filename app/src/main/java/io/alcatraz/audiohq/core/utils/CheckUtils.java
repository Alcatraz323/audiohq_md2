package io.alcatraz.audiohq.core.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import io.alcatraz.audiohq.Constants;
import io.alcatraz.audiohq.utils.Utils;

public class CheckUtils {
    public static final String SELINUX_DISABLED = "Disabled";
    public static final String SELINUX_ENFORCING = "Enforcing";
    public static final String SELINUX_PERMISSIVE = "Permissive";

    public static String getSeLinuxEnforce() {
        return ShellUtils.execCommand("getenforce", true).responseMsg.replace("\n","");
    }

    public static String getAudioServerInfo() {
        return ShellUtils.execCommand("file /system/bin/audioserver", true).responseMsg;
    }

    public static boolean getRootStatus() {
        return ShellUtils.hasRootPermission();
    }

    public static int getSDK() {
        return Build.VERSION.SDK_INT;
    }

    public static String[] getSupportArch() {
        return Build.SUPPORTED_ABIS;
    }

    public static boolean getIfSupported() {
        int[] Apis = Constants.SUPPORT_APIS;
        String[] audiohq_abis = Constants.SUPPORT_ABIS;
        String[] device_abis = getSupportArch();
        for (int i : Apis) {
            if (i == getSDK()) {
                for (String j : audiohq_abis) {
                    for (String k : device_abis)
                        if (k.equals(j)) return true;
                }
            }
        }
        return false;
    }

    public static boolean checkAndWarnMismatch(){
        boolean mismatch = false;
        ShellUtils.CommandResult result = AudioHQRaw.getAudioHqNativeInfo();
        if(result.responseMsg ==null){
            mismatch = true;
        }else {
            if(Utils.isStringNotEmpty(result.responseMsg)){
                if(result.responseMsg.contains("found")||result.responseMsg.toLowerCase().contains("denied")){
                    mismatch = true;
                }else {
                    String[] process_0 = result.responseMsg.split("]\\[");
                    String[] process_1 = process_0[1].split("]");
                    if (!process_1[0].equals(Constants.MATCHED_MODULE_VERSION)) {
                        mismatch = true;
                    }
                }
            }else {
                mismatch = true;
            }
        }
        return mismatch;
    }

    public static boolean hasModifiedRC(){
        ShellUtils.CommandResult original = ShellUtils.execCommand("cat /system/etc/init/audioserver.rc", false);
        String modify = original.responseMsg;
        return modify.contains("readproc");
    }

//    public static String getLibVersion() {
//        String raw = AudioHqApis.getAudioFlingerInfo().responseMsg;
//
//        if (Utils.isStringNotEmpty(raw)) {
//            String[] process_1 = raw.split("\\[");
//            String[] process_2 = process_1[1].split("]");
//            return process_2[0];
//        }
//        return null;
//    }

    public static boolean getMagiskInstalled(Context context) {
        final PackageManager packageManager = context.getPackageManager();//获取packagemanager
        List<PackageInfo> pinfo;//获取所有已安装程序的包信息
        pinfo = packageManager.getInstalledPackages(0);
        List<String> pName = new ArrayList<String>();//用于存储所有已安装程序的包名
        //从pinfo中将包名字逐一取出，压入pName list中
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return true;//pName.contains("com.topjohnwu.magisk");//判断pName中是否有目标程序的包名，有TRUE，没有FALSE
    }


}
