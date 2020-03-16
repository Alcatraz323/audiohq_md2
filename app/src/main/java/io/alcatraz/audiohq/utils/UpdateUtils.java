package io.alcatraz.audiohq.utils;

import java.io.File;

public class UpdateUtils {
    public static final int FILE_DEFAULT_EXPIRATION_MILLS = 24 * 3600 * 1000;
    public static final int WEATHER_FILE_DEFAULT_EXPIRATION_MILLS = 5 * 60 * 1000;

    public static boolean checkFileNeedsUpdate(String dir) {
        return checkFileNeedsUpdate(dir, FILE_DEFAULT_EXPIRATION_MILLS);
    }

    public static boolean checkFileNeedsUpdate(String dir, long mills_to_expire) {
        File toCheck = new File(dir);
        double expi_min = mills_to_expire / 60000;
        boolean result = toCheck.lastModified() + mills_to_expire < System.currentTimeMillis();
        return result;
    }

    public static boolean checkUpdate() {
        //TODO : Implement this method
        return false;
    }
}
