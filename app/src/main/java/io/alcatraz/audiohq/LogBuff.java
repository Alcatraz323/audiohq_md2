package io.alcatraz.audiohq;

import android.annotation.SuppressLint;
import android.text.Html;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogBuff {
    public static int MESSAGE_DEFAULT_MAX_AMOUNT = 256;

    public static String COLOR_LEVEL_INFO = "#4caf50";
    public static String COLOR_LEVEL_WARN = "#ff9800";
    public static String COLOR_LEVEL_ERROR = "#f44336";
    public static String COLOR_LEVEL_DEBUG = "#1565C0";
    public static String HTML_BRLINE = "<br/>";
    private static String whole_message = "";
    private static int num = 0;

    public static void I(String infoMsg) {
        String classname = new Exception().getStackTrace()[1].getClassName().replace(Constants.MY_PACKAGE_NAME,"");
        String method_name = new Exception().getStackTrace()[1].getMethodName();
        commitMessageChange(wrapFontString(getTime() + " [INFO][" + classname + "::" + method_name + "]" +
                infoMsg, COLOR_LEVEL_INFO));
    }

    public static void W(String warnMsg) {
        String classname = new Exception().getStackTrace()[1].getClassName().replace(Constants.MY_PACKAGE_NAME,"");
        String method_name = new Exception().getStackTrace()[1].getMethodName();
        commitMessageChange(wrapFontString(getTime() + " [WARNING][" + classname + "::" + method_name + "]" +
                warnMsg, COLOR_LEVEL_WARN));
    }

    public static void E(String errMsg) {
        String classname = new Exception().getStackTrace()[1].getClassName().replace(Constants.MY_PACKAGE_NAME,"");
        String method_name = new Exception().getStackTrace()[1].getMethodName();
        commitMessageChange(wrapFontString(getTime() + " [ERROR][" + classname + "::" + method_name + "]" +
                errMsg, COLOR_LEVEL_ERROR));
    }

    public static void D(String dbgMsg) {
        String classname = new Exception().getStackTrace()[1].getClassName().replace(Constants.MY_PACKAGE_NAME,"");
        String method_name = new Exception().getStackTrace()[1].getMethodName();
        commitMessageChange(wrapFontString(getTime() + " [DEBUG][" + classname + "::" + method_name + "]" +
                dbgMsg, COLOR_LEVEL_DEBUG));
    }

    public static String wrapFontString(String raw, String rgb_color) {
        return wrapFontString(raw, rgb_color, false, false);
    }

    public static String wrapFontString(String raw, String rgb_color, boolean isBold, boolean isItalic) {
        if (isBold) {
            raw = "<b>" + raw + "</b>";
        }
        if (isItalic) {
            raw = "<i>" + raw + "</i>";
        }

        return "<font color=\"" + rgb_color + "\">" + raw + "</font>";
    }

    public static void log(String content) {
        commitMessageChange(content);
    }

    public static String getTime() {
        long currentTime = System.currentTimeMillis();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm:ss");
        Date date = new Date(currentTime);
        return formatter.format(date);
    }

    public static void addDivider() {
        whole_message += "<br/>============================";
    }

    public static void clearLog() {
        whole_message = "";
        num = 0;
        W("Cleared operation log");
    }

    public static CharSequence getFinalLog() {
        return Html.fromHtml(whole_message);
    }

    private static void checkAndClearup() {
        if (num >= MESSAGE_DEFAULT_MAX_AMOUNT) {
            whole_message = "";
            num = 0;
            W("Automatically cleared log ( reaching max :" + MESSAGE_DEFAULT_MAX_AMOUNT + ")");
        }
    }

    private static void commitMessageChange(String content) {
        checkAndClearup();
        whole_message += HTML_BRLINE + content;
        num++;
    }
}
