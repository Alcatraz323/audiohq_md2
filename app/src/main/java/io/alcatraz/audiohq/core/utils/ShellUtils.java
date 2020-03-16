package io.alcatraz.audiohq.core.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import io.alcatraz.audiohq.LogBuff;
import io.alcatraz.audiohq.utils.Utils;

public class ShellUtils {
    public static final String ERR_EMPTY_COMMAND = "Command can't be null";

    /**
     * check whether has root permission
     */
    public static boolean hasRootPermission() {
        return execCommand("echo root", true, false, false).result == 0;
    }


    public static CommandResult execCommand(String command, boolean isRoot) {
        return execCommand(new String[]{command}, isRoot, true, true);
    }

    public static CommandResult execCommand(String command, boolean isRoot, boolean isNeedResultMsg, boolean needLogging) {

        return execCommand(new String[]{command}, isRoot, isNeedResultMsg, needLogging);
    }

    public static CommandResult execCommand(List<String> commands, boolean isRoot, boolean isNeedResultMsg, boolean needLogging) {
        return execCommand(commands == null ? null : commands.toArray(new String[]{}), isRoot, isNeedResultMsg, needLogging);
    }

    /**
     * execute shell commands
     * {@link CommandResult#result} is -1, there maybe some excepiton.
     *
     * @param commands     command array
     * @param isRoot       whether need to run with root
     * @param needResponse whether need result msg
     */
    public static CommandResult execCommand(String[] commands, boolean isRoot, boolean needResponse, boolean needLogging) {

        int result = -1;
        if (commands == null || commands.length == 0) {
            return new CommandResult(result, null, ERR_EMPTY_COMMAND, needLogging);
        }

        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;

        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) {
                    continue;
                }
                if (needLogging)
                    LogBuff.log((isRoot ? "# " : "$ ") + command);
                // donnot use os.writeBytes(commmand), avoid chinese charset error
                os.write(command.getBytes());
                os.writeBytes(COMMAND_LINE_END);
                os.flush();
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();

            result = process.waitFor();
            if (needResponse) {
                successMsg = new StringBuilder();
                errorMsg = new StringBuilder();
                successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
                errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String s;
                while ((s = successResult.readLine()) != null) {
                    successMsg.append(s).append("\n");
                }
                while ((s = errorResult.readLine()) != null) {
                    errorMsg.append(s);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (errorResult != null) {
                    errorResult.close();
                }
                if (successResult != null) {
                    successResult.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (process != null) {
                    process.destroy();
                }
            }

        }
        return new CommandResult(result, successMsg == null ? null : successMsg.toString(), errorMsg == null ? null
                : errorMsg.toString(), needLogging);
    }


    public static class CommandResult {

        public int result;
        public String responseMsg;
        public String errorMsg;

        public CommandResult(int result) {
            this.result = result;
        }

        public CommandResult(int result, String responseMsg, String errorMsg, boolean needLogging) {
            this.result = result;
            this.responseMsg = responseMsg;
            this.errorMsg = errorMsg;
            if (needLogging) {
                if (Utils.isStringNotEmpty(errorMsg))
                    LogBuff.log(errorMsg);
                if (Utils.isStringNotEmpty(responseMsg))
                    LogBuff.log(responseMsg);
            }
        }
    }

    public static final String COMMAND_SU = "su";
    public static final String COMMAND_SH = "sh";
    public static final String COMMAND_EXIT = "exit\n";
    public static final String COMMAND_LINE_END = "\n";

}
