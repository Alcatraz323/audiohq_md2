package io.alcatraz.audiohq.core.utils;

import android.content.Context;

import io.alcatraz.audiohq.beans.AudioHQNativeInterface;
import io.alcatraz.audiohq.beans.playing.PlayingData;
import io.alcatraz.audiohq.beans.preset.ListMuted;
import io.alcatraz.audiohq.beans.preset.ListProfiles;
import io.alcatraz.audiohq.beans.OverallStatus;
import io.alcatraz.audiohq.beans.StringProfile;
import io.alcatraz.audiohq.beans.SwitchConfigurations;
import io.alcatraz.audiohq.utils.Panels;
import io.alcatraz.audiohq.utils.Utils;

public class AudioHQApis {
    public static void setProfile(Context context, String process_name, float prog_general,
                                  float prog_left,
                                  float prog_right,
                                  boolean split_control,
                                  boolean isweakkey) {
        new Thread(() -> AudioHQRaw.setProfile(process_name, prog_general, prog_left, prog_right, split_control, isweakkey)).start();
    }

    public static void unsetProfile(Context context, String process_name,
                                    boolean isweakkey) {
        AudioHQRaw.unsetProfile(process_name, isweakkey);
    }

    public static void getSwitches(Context context, AudioHQNativeInterface<SwitchConfigurations> nativeInterface) {
        if (CheckUtils.checkAndWarnMismatch()) {
            Panels.getNotInstalledPanel(context).show();
            nativeInterface.onFailure("Installation failure!");
        } else {
            ShellUtils.CommandResult result = AudioHQRaw.getSwitches();
            String[] process_0 = result.responseMsg.split(";");
            SwitchConfigurations configurations = new SwitchConfigurations();
            configurations.setRaw(result.responseMsg);
            configurations.setDefaultsilent(process_0[1].equals("true"));
            configurations.setNativecode(Integer.parseInt(process_0[0]));
            nativeInterface.onSuccess(configurations);
        }
    }

    public static void getAllPlayingClient(Context context, AudioHQNativeInterface<PlayingData> nativeInterface) {
            ShellUtils.CommandResult result = AudioHQRaw.getAllPlayingClients();
            try {
                nativeInterface.onSuccess(Utils.json2Object(result.responseMsg, PlayingData.class));
            }catch (Exception e){
                nativeInterface.onFailure("Installation failure!");
            }
    }

    public static void clearAllSetting(Context context) {
        AudioHQRaw.clearAllNativeSettings();
    }

    public static void setDefaultSilentState(Context context, boolean defs) {
        AudioHQRaw.setDefaultSilentState(defs);
    }

    @Deprecated
    public static void setWeakkeyAdjust(Context context, boolean weakkey) {
        if (CheckUtils.checkAndWarnMismatch()) {
            Panels.getNotInstalledPanel(context).show();
        } else {
            AudioHQRaw.setWeakKeyAdjust(weakkey);
        }
    }

    public static void muteProcess(Context context, String process, boolean isweakkey) {
        AudioHQRaw.muteProcess(process, isweakkey);
    }

    public static void unmuteProcess(Context context, String process, boolean isweakkey) {
        AudioHQRaw.unmuteProcess(process, isweakkey);
    }

    public static void startNativeService(Context context) {
        if (CheckUtils.checkAndWarnMismatch()) {
            Panels.getNotInstalledPanel(context).show();
        } else {
            AudioHQRaw.startNativeService();
        }
    }

    public static void getOverallStatus(Context context, AudioHQNativeInterface<OverallStatus> nativeInterface) {
        if (CheckUtils.checkAndWarnMismatch()) {
            Panels.getNotInstalledPanel(context).show();
            nativeInterface.onFailure("Installation failure!");
        } else {
            ShellUtils.CommandResult result = AudioHQRaw.getOverallStatus();
            nativeInterface.onSuccess(Utils.json2Object(result.responseMsg, OverallStatus.class));
        }
    }

    public static void getProfile(Context context, String process, boolean isweakkey, AudioHQNativeInterface<StringProfile> nativeInterface) {
        if (CheckUtils.checkAndWarnMismatch()) {
            Panels.getNotInstalledPanel(context).show();
            nativeInterface.onFailure("Installation failure!");
        } else {
            ShellUtils.CommandResult result = AudioHQRaw.getProfile(process, isweakkey);
            String[] process_0 = result.responseMsg.split(",");
            StringProfile profile = new StringProfile();
            if (!result.responseMsg.equals("__NO_PROFILE")) {
                profile.setRaw(result.responseMsg);
                profile.setLeft(Float.parseFloat(process_0[0]));
                profile.setRight(Float.parseFloat(process_0[1]));
                profile.setGeneral(Float.parseFloat(process_0[2]));
                profile.setControl_lr(process_0[3].equals("1"));
            }
            nativeInterface.onSuccess(profile);
        }
    }

    public static void getDefaultProfile(Context context, AudioHQNativeInterface<StringProfile> nativeInterface) {
        if (CheckUtils.checkAndWarnMismatch()) {
            Panels.getNotInstalledPanel(context).show();
            nativeInterface.onFailure("Installation failure!");
        } else {
            ShellUtils.CommandResult result = AudioHQRaw.getDefaultProfile();
            String[] process_0 = result.responseMsg.split(",");
            StringProfile profile = new StringProfile();
            profile.setRaw(result.responseMsg);
            profile.setLeft(Float.parseFloat(process_0[0]));
            profile.setRight(Float.parseFloat(process_0[1]));
            profile.setGeneral(Float.parseFloat(process_0[2]));
            profile.setControl_lr(process_0[3].equals("1"));
            nativeInterface.onSuccess(profile);
        }
    }

    public static void setDefaultProfile(Context context, float prog_general,
                                         float prog_left,
                                         float prog_right,
                                         boolean split_control) {
        AudioHQRaw.setDefaultProfile(prog_general, prog_left, prog_right, split_control);
    }

    public static void listProfiles(Context context, AudioHQNativeInterface<ListProfiles> nativeInterface) {
        if (CheckUtils.checkAndWarnMismatch()) {
            Panels.getNotInstalledPanel(context).show();
            nativeInterface.onFailure("Installation failure!");
        } else {
            ShellUtils.CommandResult result = AudioHQRaw.listProfiles();
            nativeInterface.onSuccess(Utils.json2Object(result.responseMsg, ListProfiles.class));
        }
    }

    public static void listMuted(Context context, AudioHQNativeInterface<ListMuted> nativeInterface) {
        if (CheckUtils.checkAndWarnMismatch()) {
            //Panels.getNotInstalledPanel(context).show();
            nativeInterface.onFailure("Installation failure!");
        } else {
            ShellUtils.CommandResult result = AudioHQRaw.listMuted();
            nativeInterface.onSuccess(Utils.json2Object(result.responseMsg, ListMuted.class));
        }
    }

    public static void getAudioHQNativeInfo(Context context, AudioHQNativeInterface<String[]> nativeInterface) {
        if (CheckUtils.checkAndWarnMismatch()) {
            Panels.getNotInstalledPanel(context).show();
            nativeInterface.onFailure("Installation failure!");
        } else {
            ShellUtils.CommandResult result = AudioHQRaw.getAudioHqNativeInfo();
            nativeInterface.onSuccess(result.responseMsg.split(";"));
        }
    }
}
