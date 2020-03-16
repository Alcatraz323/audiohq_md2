package io.alcatraz.audiohq.beans.playing;

import android.content.Context;

import java.util.List;

import io.alcatraz.audiohq.beans.AudioHQNativeInterface;
import io.alcatraz.audiohq.core.utils.AudioHQApis;

public class PlayingSystem {
    PlayingData data;
    Context context;

    public PlayingSystem(Context context) {
        this.context = context;
    }

    public void update(AudioHQNativeInterface<PlayingSystem> nativeInterface) {
        AudioHQApis.getAllPlayingClient(context, new AudioHQNativeInterface<PlayingData>() {
            @Override
            public void onSuccess(PlayingData result) {
                data = result;
                nativeInterface.onSuccess(PlayingSystem.this);
            }

            @Override
            public void onFailure(String reason) {
                nativeInterface.onFailure(reason);
            }
        });
    }

    public List<Pkgs> getData(){
        return data.getPkgs();
    }
}
