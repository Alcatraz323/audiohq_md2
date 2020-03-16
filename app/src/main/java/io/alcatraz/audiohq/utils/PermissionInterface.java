package io.alcatraz.audiohq.utils;

public interface PermissionInterface {
    void onResult(int requestCode, String[] permissions, int[] granted);
}
