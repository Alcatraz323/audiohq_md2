package io.alcatraz.audiohq.core.utils;


import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class OSUtils {
    //MIUI标识
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

    //EMUI标识
    private static final String KEY_EMUI_VERSION_CODE = "ro.build.version.emui";
    private static final String KEY_EMUI_API_LEVEL = "ro.build.hw_emui_api_level";
    private static final String KEY_EMUI_CONFIG_HW_SYS_VERSION = "ro.confg.hw_systemversion";

    //Flyme标识
    private static final String KEY_FLYME_ID_FALG_KEY = "ro.build.display.id";
    private static final String KEY_FLYME_ID_FALG_VALUE_KEYWORD = "Flyme";
    private static final String KEY_FLYME_ICON_FALG = "persist.sys.use.flyme.icon";
    private static final String KEY_FLYME_SETUP_FALG = "ro.meizu.setupwizard.flyme";
    private static final String KEY_FLYME_PUBLISH_FALG = "ro.flyme.published";

    /**
     * @param
     * @return ROM_TYPE ROM类型的枚举
     * @description获取ROM类型: MIUI_ROM, FLYME_ROM, EMUI_ROM, OTHER_ROM
     */
    public static ROM_TYPE getRomType() {
        ROM_TYPE rom_type = ROM_TYPE.OTHER;
        try {
            BuildProperties buildProperties = BuildProperties.getInstance();

            if (buildProperties.containsKey(KEY_EMUI_VERSION_CODE) || buildProperties.containsKey(KEY_EMUI_API_LEVEL) || buildProperties.containsKey(KEY_MIUI_INTERNAL_STORAGE)) {
                return ROM_TYPE.EMUI;
            }
            if (buildProperties.containsKey(KEY_MIUI_VERSION_CODE) || buildProperties.containsKey(KEY_MIUI_VERSION_NAME) || buildProperties.containsKey(KEY_MIUI_VERSION_NAME)) {
                return ROM_TYPE.MIUI;
            }
            if (buildProperties.containsKey(KEY_FLYME_ICON_FALG) || buildProperties.containsKey(KEY_FLYME_SETUP_FALG) || buildProperties.containsKey(KEY_FLYME_PUBLISH_FALG)) {
                return ROM_TYPE.FLYME;
            }
            if (buildProperties.containsKey(KEY_FLYME_ID_FALG_KEY)) {
                String romName = buildProperties.getProperty(KEY_FLYME_ID_FALG_KEY);
                if (!TextUtils.isEmpty(romName) && romName.contains(KEY_FLYME_ID_FALG_VALUE_KEYWORD)) {
                    return ROM_TYPE.FLYME;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rom_type;
    }

    public enum ROM_TYPE {
        MIUI,
        FLYME,
        EMUI,
        OTHER
    }

}

class BuildProperties {

    private static BuildProperties ourInstance;
    private Properties properties;

    public static BuildProperties getInstance() throws IOException {
        if (ourInstance == null) {
            ourInstance = new BuildProperties();
        }
        return ourInstance;

    }

    private BuildProperties() throws IOException {
        properties = new Properties();
        properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
    }

    public boolean containsKey(final Object key) {
        return properties.containsKey(key);
    }

    public boolean containsValue(final Object value) {
        return properties.containsValue(value);
    }

    public String getProperty(final String name) {
        return properties.getProperty(name);
    }

    public String getProperty(final String name, final String defaultValue) {
        return properties.getProperty(name, defaultValue);
    }

    public Set<Map.Entry<Object, Object>> entrySet() {
        return properties.entrySet();
    }

    public boolean isEmpty() {
        return properties.isEmpty();
    }

    public Enumeration keys() {
        return properties.keys();
    }

    public Set keySet() {
        return properties.keySet();
    }

    public int size() {
        return properties.size();
    }

    public Collection values() {
        return properties.values();
    }

}