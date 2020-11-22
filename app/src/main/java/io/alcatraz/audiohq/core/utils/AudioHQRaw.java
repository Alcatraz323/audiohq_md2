package io.alcatraz.audiohq.core.utils;

public class AudioHQRaw {
    static synchronized void setProfile(String process_name,
                                        float prog_general,
                                        float prog_left,
                                        float prog_right,
                                        boolean split_control,
                                        boolean isweakkey) {
        runAudioHqCmd(AudioHqCmds.SET_PROFILE, process_name,
                prog_left + "", prog_right + "", prog_general + "",
                split_control ? "true" : "false", isweakkey ? "true" : "false");
    }

    static void unsetProfile(String process_name, boolean weakkey) {
        runAudioHqCmd(AudioHqCmds.UNSET_PROFILE, process_name, weakkey ? "true" : "false");
    }

    static ShellUtils.CommandResult getSwitches() {
        ShellUtils.CommandResult command = runAudioHqCmd(AudioHqCmds.GET_SWITCHES);
        if (command.responseMsg != null) {
            command.responseMsg = command.responseMsg.replaceAll("[\n]", "");
        }
        return command;
    }

    static ShellUtils.CommandResult getAllPlayingClients() {
        return runAudioHqCmd(AudioHqCmds.LIST_ALL_BUFFER, 1 + "");
    }

    static void clearAllNativeSettings() {
        runAudioHqCmd(AudioHqCmds.CLEAR_ALL_SETTINGS);
    }

    static ShellUtils.CommandResult getAudioHqNativeInfo() {
        return runAudioHqCmd(AudioHqCmds.GET_NATIVE_ELF_INFO);
    }

    static void setDefaultSilentState(boolean state) {
        runAudioHqCmd(AudioHqCmds.SET_DEFAULT_SILENT_STATE, state ? "true" : "false");
    }

    static void setWeakKeyAdjust(boolean state) {
        runAudioHqCmd(AudioHqCmds.SET_WEAK_KEY_ADJUST, state ? "true" : "false");
    }

    static void muteProcess(String process_name, boolean isweakkey) {
        runAudioHqCmd(AudioHqCmds.MUTE_PROCESS, process_name, isweakkey ? "true" : "false");
    }

    static void unmuteProcess(String process_name, boolean isweakkey) {
        runAudioHqCmd(AudioHqCmds.UNMUTE_PROCESS, process_name, isweakkey ? "true" : "false");
    }

    static ShellUtils.CommandResult getOverallStatus() {
        return runAudioHqCmd(AudioHqCmds.GET_OVR_STATUS);
    }

    static ShellUtils.CommandResult getProfile(String process, boolean isweakKey) {
        return runAudioHqCmd(AudioHqCmds.GET_PROFILE, process, isweakKey ? "true" : "false");
    }

    static ShellUtils.CommandResult getDefaultProfile() {
        ShellUtils.CommandResult command = runAudioHqCmd(AudioHqCmds.GET_DEFAULT_PROFILE);
        if (command.responseMsg != null) {
            command.responseMsg = command.responseMsg.replaceAll("[\n\t]", "");
        }
        return command;
    }

    static void setDefaultProfile(float prog_general,
                                  float prog_left,
                                  float prog_right,
                                  boolean split_control) {
        runAudioHqCmd(AudioHqCmds.SET_DEFAULT_PROFILE,
                prog_left + "", prog_right + "", prog_general + "", split_control ? "true" : "false");
    }

    static void startNativeService() {
        runAudioHqCmd(AudioHqCmds.START_NATIVE_SERVICE);
    }

    static ShellUtils.CommandResult listProfiles() {
        return runAudioHqCmd(AudioHqCmds.LIST_PROFILE);
    }

    static ShellUtils.CommandResult listMuted() {
        return runAudioHqCmd(AudioHqCmds.LIST_MUTED);
    }

    private static ShellUtils.CommandResult runAudioHqCmd(AudioHqCmds audioHqCmds, String... params) {
        String cmd;
        if (audioHqCmds.hasParams())
            cmd = audioHqCmds.createCmd(params);
        else
            cmd = audioHqCmds.getCmd_raw();
        ShellUtils.CommandResult result = ShellUtils.execCommand(cmd, AudioHqCmds.FORCE_ROOT_SHELL || audioHqCmds.requiresRoot());
        if (result.responseMsg != null && result.responseMsg.length() != 0) {
            result.responseMsg = result.responseMsg.substring(0, result.responseMsg.length() - 1);
        }
        return result;
    }

    public enum AudioHqCmds {
        SET_PROFILE("audiohq --set-profile \"%s\" %s %s %s %s %s", false, true),
        UNSET_PROFILE("audiohq --unset-profile \"%s\" %s", false, true),
        GET_SWITCHES("audiohq --switches", false, false),
        SET_DEFAULT_SILENT_STATE("audiohq --def-silent %s", false, true),
        GET_DEFAULT_PROFILE("audiohq --def-profile", false, false),
        SET_DEFAULT_PROFILE("audiohq --def-profile %s %s %s %s", false, true),
        MUTE_PROCESS("audiohq --mute \"%s\" %s", false, true),
        UNMUTE_PROCESS("audiohq --unmute \"%s\" %s", false, true),
        CLEAR_ALL_SETTINGS("audiohq --clear", false, false),
        LIST_ALL_BUFFER("audiohq --list-buffers %s", false, true),
        GET_NATIVE_ELF_INFO("audiohq --elf-info", false, false),
        SET_WEAK_KEY_ADJUST("audiohq --weak-key %s", false, true),
        START_NATIVE_SERVICE("audiohq --service", true, false),
        GET_OVR_STATUS("audiohq --ovr-status", false, false),
        GET_PROFILE("audiohq --get-profile \"%s\" %s", false, true),
        LIST_PROFILE("audiohq --list-profile", false, false),
        LIST_MUTED("audiohq --list-muted", false, false);

        public static boolean FORCE_ROOT_SHELL = false;

        private String cmd_raw;
        private boolean require_root;
        private boolean has_params;

        AudioHqCmds(String cmd_raw, boolean require_root, boolean has_params) {
            this.cmd_raw = cmd_raw;
            this.require_root = require_root;
            this.has_params = has_params;
        }

        protected boolean requiresRoot() {
            return require_root;
        }

        protected boolean hasParams() {
            return has_params;
        }

        protected String getCmd_raw() {
            return cmd_raw;
        }

        @SuppressWarnings("ConfusingArgumentToVarargsMethod")
        protected String createCmd(String... params) {
            return String.format(cmd_raw, params);
        }
    }
}
