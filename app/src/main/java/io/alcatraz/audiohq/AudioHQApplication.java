package io.alcatraz.audiohq;

import android.app.Application;
import android.content.Context;

import java.util.concurrent.atomic.AtomicReference;

import io.alcatraz.audiohq.beans.playing.PlayingSystem;
import io.alcatraz.audiohq.beans.preset.PresetSystem;


public class AudioHQApplication extends Application {
    private Context mContext;
    private AtomicReference<PresetSystem> mPresetSystem;
    private AtomicReference<PlayingSystem> mPlayingSystem;
    //TODO : Check string.xml/Setup versionCode/build.gradle when release update
    //TODO : Set Empty View for all adapter views
    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        mPresetSystem = new AtomicReference<>(new PresetSystem(mContext));
        mPlayingSystem = new AtomicReference<>(new PlayingSystem(mContext));
        super.onCreate();
    }

    public PresetSystem getPresetSystem(){
        return mPresetSystem.get();
    }

    public PlayingSystem getPlayingSystem() {
        return mPlayingSystem.get();
    }

    public Context getOverallContext() {
        return mContext;
    }

}
