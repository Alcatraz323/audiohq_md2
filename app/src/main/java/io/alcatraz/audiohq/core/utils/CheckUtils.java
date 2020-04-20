package io.alcatraz.audiohq.core.utils;

import android.os.Build;

import io.alcatraz.audiohq.Constants;
import io.alcatraz.audiohq.utils.Utils;

public class CheckUtils {
    public static String getSeLinuxEnforce() {
        return ShellUtils.execCommand("getenforce", true).
                responseMsg.replace("\n","");
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
}
