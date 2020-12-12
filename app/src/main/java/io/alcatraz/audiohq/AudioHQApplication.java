package io.alcatraz.audiohq;

import android.app.Application;
import android.content.Context;

import java.util.concurrent.atomic.AtomicReference;

import io.alcatraz.audiohq.beans.AudioHQNativeInterface;
import io.alcatraz.audiohq.beans.playing.PlayingSystem;
import io.alcatraz.audiohq.beans.preset.PresetSystem;
import io.alcatraz.audiohq.core.utils.AudioHQRaw;
import io.alcatraz.audiohq.utils.SharedPreferenceUtil;


public class AudioHQApplication extends Application {
    private Context mContext;
    private AtomicReference<PresetSystem> mPresetSystem;
    private PlayingSystem mPlayingSystem;

    public static String theme = "";
    public static int color = 0;

    //TODO : Check string.xml/Setup versionCode/build.gradle when release update
    //TODO : Set Empty View for all adapter views
    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        mPresetSystem = new AtomicReference<>(new PresetSystem(mContext));
        mPlayingSystem = new PlayingSystem(mContext, this);
        mPresetSystem.get().update(new AudioHQNativeInterface<PresetSystem>() {
            @Override
            public void onSuccess(PresetSystem result) {

            }

            @Override
            public void onFailure(String reason) {

            }
        });
        SharedPreferenceUtil spfu = SharedPreferenceUtil.getInstance();
        AudioHQRaw.AudioHqCmds.FORCE_ROOT_SHELL = (boolean) spfu.get(mContext, Constants.PREF_ROOT_SHELL, false);
        theme = (String) spfu.get(mContext, Constants.PREF_THEME, Constants.DEFAULT_VALUE_PREF_THEME);
        setColor();
        super.onCreate();
    }

    public PresetSystem getPresetSystem() {
        return mPresetSystem.get();
    }

    public PlayingSystem getPlayingSystem() {
        return mPlayingSystem;
    }

    public Context getOverallContext() {
        return mContext;
    }

    private void setColor() {
        switch (theme) {
            case "Pixel":
                color = getColor(R.color.colorAccent);
                break;
            case "Original":
                color = getColor(R.color.umr_colorPrimary);
                break;
            case "Sakura":
                color = getColor(R.color.sakura_colorPrimaryDark);
                break;
            case "Purple":
                color = getColor(R.color.purple_colorPrimary);
                break;
            case "LightBlue":
                color = getColor(R.color.addedblue_colorPrimary);
                break;
            case "Leaf":
                color = getColor(R.color.leaf_colorPrimaryDark);
                break;
        }
    }

}
