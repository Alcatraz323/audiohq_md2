package io.alcatraz.audiohq;

import java.util.LinkedList;
import java.util.List;

import io.alcatraz.audiohq.beans.QueryElement;

public class Constants {
    public static String[] SUPPORT_ABIS = {"armeabi", "armeabi-v7a", "arm64-v8a"};
    public static int[] SUPPORT_APIS = {26, 27, 28, 29, 30, 31};    //Android 8.0 - Android 12
    public static String MATCHED_MODULE_VERSION = "0.91";
    public static boolean ANNIVERSARY_1ST = true;
    public static boolean ANNIVERSARY_2ND = false;

    public static boolean ANNIVERSARY_INDICATOR = false;

    public static String MY_PACKAGE_NAME = "io.alcatraz.audiohq";

    //===============================================
    public static final String PREF_BOOT = "boot";
    public static final boolean DEFAULT_VALUE_PREF_BOOT = true;
    public static final String PREF_DEFAULT_SILENT = "default_silent";
    public static final boolean DEFAULT_VALUE_PREF_DEFAULT_SILENT = false;
    public static final String PREF_FLOAT_SERVICE = "float_service";
    public static final boolean DEFAULT_VALUE_PREF_FLOAT_SERVICE = false;
    public static final String PREF_LANGUAGE = "language";
    public static final String DEFAULT_VALUE_PREF_LANGUAGE = "Auto";
    public static final String PREF_THEME = "theme";
    public static final String DEFAULT_VALUE_PREF_THEME = "Pixel";
    public static final String PREF_DARK_MODE = "dark_mode";
    public static final String DEFAULT_VALUE_PREF_DARK_MODE = "Auto";
    public static final String PREF_RERUN_SETUP = "rerun_setup";


    public static final String PREF_SHOW_THEME_SETTINGS = "show_theme_settings";
    public static final boolean DEFAULT_VALUE_PREF_SHOW_THEME_SETTINGS = false;
    public static final String PREF_SHOW_ANNIVERSARY_2020_QUIZ_CONTENT = "anniversary_2020_quiz";
    public static final boolean DEFAULT_VALUE_PREF_SHOW_ANNIVERSARY_2020_QUIZ_CONTENT = false;
    public static final String PREF_SHOW_ANNIVERSARY_2020_FOREGROUND = "anniversary_2020_foreground";
    public static final boolean DEFAULT_VALUE_PREF_SHOW_ANNIVERSARY_2020_FOREGROUND = false;
    public static final String PREF_SHOW_ANNIVERSARY_2020_INTRO = "show_anniversary_2020_intro";
    public static final boolean DEFAULT_VALUE_PREF_SHOW_ANNIVERSARY_2020_INTRO = true;


    public static final String PREF_FLOAT_WINDOW_GRAVITY = "float_gravity";
    public static final String DEFAULT_VALUE_PREF_FLOAT_WINDOW_GRAVITY = "start_top";
    public static final String PREF_FLOAT_FOREGROUND_SERVICE = "float_foreground_notification";
    public static final boolean DEFAULT_VALUE_PREF_FLOAT_FOREGROUND_SERVICE = false;
    public static final String PREF_FLOAT_WINDOW_BACKGROUND = "float_background";
    public static final String DEFAULT_VALUE_PREF_FLOAT_WINDOW_BACKGROUND = "#ffffffff";
    public static final String PREF_FLOAT_WINDOW_BACKGROUND_DARK = "float_background_dark";
    public static final String DEFAULT_VALUE_PREF_FLOAT_WINDOW_BACKGROUND_DARK = "#ff000000";
    public static final String PREF_FLOAT_WINDOW_DISMISS_DELAY = "float_dismiss_delay";
    public static final String DEFAULT_VALUE_PREF_FLOAT_WINDOW_DISMISS_DELAY = "3000";
    public static final String PREF_FLOAT_WINDOW_MARGIN_TOP = "float_margin_top";
    public static final String DEFAULT_VALUE_PREF_FLOAT_WINDOW_MARGIN_TOP = "0";
    public static final String PREF_FLOAT_WINDOW_MARGIN_TOP_LANDSCAPE = "float_margin_top_landscape";
    public static final String DEFAULT_VALUE_PREF_FLOAT_WINDOW_MARGIN_TOP_LANDSCAPE = "0";
    public static final String PREF_FLOAT_WINDOW_ICON_TINT = "float_icon_tint";
    public static final String DEFAULT_VALUE_PREF_FLOAT_WINDOW_ICON_TINT = "#ff009688";
    public static final String PREF_FLOAT_WINDOW_ICON_TINT_DARK = "float_icon_tint_dark";
    public static final String DEFAULT_VALUE_PREF_FLOAT_WINDOW_ICON_TINT_DARK = "#ff009688";
    public static final String PREF_FLOAT_WINDOW_TOGGLE_SIZE = "float_toggle_size";
    public static final String DEFAULT_VALUE_PREF_FLOAT_WINDOW_TOGGLE_SIZE = "52";
    public static final String PREF_FLOAT_WINDOW_FONT_COLOR = "float_font_color";
    public static final String DEFAULT_VALUE_PREF_FLOAT_WINDOW_FONT_COLOR = "#ff009688";
    public static final String PREF_FLOAT_WINDOW_FONT_COLOR_DARK = "float_font_color_dark";
    public static final String DEFAULT_VALUE_PREF_FLOAT_WINDOW_FONT_COLOR_DARK = "#ff009688";
    public static final String PREF_FLOAT_WINDOW_SIDE_MARGIN = "float_side_margin";
    public static final String DEFAULT_VALUE_PREF_FLOAT_WINDOW_SIDE_MARGIN = "8";
    public static final String PREF_FLOAT_WINDOW_TOGGLE_CORNER_RADIUS = "float_toggle_corner_radius";
    public static final String DEFAULT_VALUE_PREF_FLOAT_WINDOW_TOGGLE_CORNER_RADIUS = "2";
    public static final String PREF_FLOAT_WINDOW_CARD_RADIUS = "float_card_radius";
    public static final String DEFAULT_VALUE_PREF_FLOAT_WINDOW_CARD_RADIUS = "2";
    public static final String PREF_FLOAT_WINDOW_SEEK_COLOR = "float_seek_bar_color";
    public static final String DEFAULT_VALUE_PREF_FLOAT_WINDOW_SEEK_COLOR = "#ff4285f4";
    public static final String PREF_FLOAT_DIRECT_REACT = "float_direct_react";
    public static final boolean DEFAULT_VALUE_PREF_FLOAT_DIRECT_REACT = false;
    public static final String PREF_FLOAT_WINDOW_SIDE_MARGIN_LANDSCAPE = "float_side_margin_landscape";
    public static final String DEFAULT_VALUE_PREF_FLOAT_WINDOW_SIDE_MARGIN_LANDSCAPE = "8";
    public static final String PREF_FLOAT_WINDOW_FILTER = "float_filter";
    public static final String DEFAULT_VALUE_PREF_FLOAT_WINDOW_FILTER = "";
    public static final String PREF_FLOAT_NO_EMPTY_WINDOW = "float_no_empty_window";
    public static final boolean DEFAULT_VALUE_PREF_NO_EMPTY_WINDOW = false;
    public static final String PREF_FLOAT_DEFAULT_EXPANDED_PANEL = "float_default_expanded_panel";
    public static final boolean DEFAULT_VALUE_PREF_FLOAT_DEFAULT_EXPANDED_PANEL = false;
    public static final String PREF_FLOAT_ACCESSIBILITY_TRIGGER = "float_accessibility_trigger";
    public static final String PREF_FLOAT_STICKY_APPS = "float_sticky_apps";
    public static final String DEFAULT_VALUE_PREF_FLOAT_STICKY_APPS = "";

    public static final String PREF_EXCLUDE_FROM_RECENT = "exclude_from_recent";
    public static final boolean DEFAULT_VALUE_PREF_EXCLUDE_FROM_RECENT = false;
    public static final String PREF_ROOT_SHELL = "root_shell";
    public static final boolean DEFAULT_VALUE_PREF_ROOT_SHELL = false;

    public static final String PREF_DEFAULT_PROFILE = "default_profile";
    public static final String PREF_CHECK_UPDATE = "check_update";
    public static final String PREF_CLEAR_PROFILES = "clear_profiles";
    public static final String PREF_UNINSTALL_NATIVE = "uninstall_native";

    //==============================================
    public static String BROADCAST_ACTION_UPDATE_PREFERENCES = "update_preferences";


    public static List<QueryElement> getOpenSourceProjects() {
        List<QueryElement> out = new LinkedList<>();

        //gson
        QueryElement o1 = new QueryElement();
        o1.setAuthor("google");
        o1.setUrl("https://github.com/google/gson");
        o1.setintro("A Java serialization/deserialization library to convert Java Objects into JSON and back");
        o1.setLicense("Apache 2.0");
        o1.setName("gson");
        //aosp
        QueryElement o2 = new QueryElement();
        o2.setAuthor("google");
        o2.setUrl("https://source.android.com/");
        o2.setintro("Android is an open source operating system for mobile devices and a corresponding open source project led by Google. This site and the Android Open Source Project (AOSP) repository offer the information and source code needed to create custom variants of the Android OS, port devices and accessories to the Android platform, and ensure devices meet the compatibility requirements that keep the Android ecosystem a healthy and stable environment for millions of users.");
        o2.setLicense("Apache 2.0");
        o2.setName("AOSP");
//        //Okio
//        QueryElement o3 = new QueryElement();
//        o3.setAuthor("square");
//        o3.setUrl("https://github.com/square/okio");
//        o3.setintro("A modern I/O API for Java ");
//        o3.setLicense("Apache 2.0");
//        o3.setName("Okio");
//        //okhttputils
//        QueryElement o4 = new QueryElement();
//        o4.setAuthor("hongyangAndroid");
//        o4.setUrl("https://github.com/hongyangAndroid/okhttputils");
//        o4.setintro("okhttp的辅助类");
//        o4.setLicense("Apache 2.0");
//        o4.setName("okhttputils");

        //Adding Process
        out.add(o1);
        out.add(o2);
//        out.add(o3);
//        out.add(o4);

        return out;
    }
}
