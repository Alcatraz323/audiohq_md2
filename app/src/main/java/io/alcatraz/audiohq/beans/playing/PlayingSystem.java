package io.alcatraz.audiohq.beans.playing;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.alcatraz.audiohq.AudioHQApplication;
import io.alcatraz.audiohq.Constants;
import io.alcatraz.audiohq.beans.AudioHQNativeInterface;
import io.alcatraz.audiohq.beans.StringProfile;
import io.alcatraz.audiohq.core.utils.AudioHQApis;
import io.alcatraz.audiohq.utils.SharedPreferenceUtil;

public class PlayingSystem {
    PlayingData data;
    Context context;
    AudioHQApplication application;

    String stickyApps;
    List<String> stickyAppList = new ArrayList<>();
    List<String> notPlayingStickyApps = new ArrayList<>();

    String ignoredApps;
    List<String> ignoredAppList = new ArrayList<>();

    public PlayingSystem(Context context, AudioHQApplication application) {
        this.context = context;
        this.application = application;
        updateSpecialAppLists();
    }

    @SuppressWarnings("ConstantConditions")
    public synchronized void updateSpecialAppLists() {
        stickyAppList.clear();
        ignoredAppList.clear();
        SharedPreferenceUtil spfu = SharedPreferenceUtil.getInstance();
        stickyApps = (String) spfu.get(context, Constants.PREF_FLOAT_STICKY_APPS, Constants.DEFAULT_VALUE_PREF_FLOAT_STICKY_APPS);
        String[] arraySticky = stickyApps.split(",");
        stickyAppList.addAll(Arrays.asList(arraySticky));
        stickyAppList.remove("");
        ignoredApps = (String) spfu.get(context, Constants.PREF_FLOAT_WINDOW_FILTER, Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_FILTER);
        String[] arrayIgnored = ignoredApps.split(",");
        ignoredAppList.addAll(Arrays.asList(arrayIgnored));
        ignoredAppList.remove("");
    }

    public synchronized void update(boolean callFromService, AudioHQNativeInterface<PlayingSystem> nativeInterface) {
        notPlayingStickyApps.clear();
        notPlayingStickyApps.addAll(stickyAppList);
        AudioHQApis.getAllPlayingClient(context, new AudioHQNativeInterface<PlayingData>() {
            @Override
            public void onSuccess(PlayingData result) {
                List<Pkgs> toEnum = result.getPkgs();
                List<Pkgs> packages = new ArrayList<>();
                for (Pkgs i : toEnum) {
                    if (ignoredAppList.contains(i.getPkg())) {
                        if(callFromService){
                            continue;
                        }
                    }

                    packages.add(i);
                    if (stickyAppList.contains(i.getPkg())) {
                        i.setSticky(true);
                        notPlayingStickyApps.remove(i.getPkg());
                    }
                }
                for (String i : notPlayingStickyApps) {
                    if (ignoredAppList.contains(i)) {
                            continue;
                    }
                    createStickyAppBean(i, packages);
                }
                result.setPkgs(packages);
                data = result;
                nativeInterface.onSuccess(PlayingSystem.this);
            }

            @Override
            public void onFailure(String reason) {
                nativeInterface.onFailure(reason);
            }
        });
    }

    public List<Pkgs> getData() {
        return data.getPkgs();
    }

    public synchronized void addStickyApp(String packageName) {
        if (!stickyAppList.contains(packageName)) {
            stickyAppList.add(packageName);
            saveSticky();
        }
    }

    public synchronized void removeStickyApp(String packageName) {
        if (stickyAppList.contains(packageName)) {
            stickyAppList.remove(packageName);
            saveSticky();
        }
    }

    private String stickyListToStr() {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < stickyAppList.size(); i++) {
            out.append(stickyAppList.get(i));
            if (i != stickyAppList.size() - 1) {
                out.append(",");
            }
        }
        return out.toString();
    }

    private void saveSticky() {
        SharedPreferenceUtil.getInstance().put(context, Constants.PREF_FLOAT_STICKY_APPS, stickyListToStr());
    }

    public synchronized void addIgnoredApp(String packageName) {
        if (!ignoredAppList.contains(packageName)) {
            ignoredAppList.add(packageName);
            saveIgnored();
        }
    }

    public synchronized void removeIgnoredApp(String packageName) {
        if (ignoredAppList.contains(packageName)) {
            ignoredAppList.remove(packageName);
            saveIgnored();
        }
    }

    private String ignoreListToStr() {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < ignoredAppList.size(); i++) {
            out.append(ignoredAppList.get(i));
            if (i != ignoredAppList.size() - 1) {
                out.append(",");
            }
        }
        return out.toString();
    }

    private void saveIgnored() {
        SharedPreferenceUtil.getInstance().put(context, Constants.PREF_FLOAT_WINDOW_FILTER, ignoreListToStr());
    }

    private void createStickyAppBean(String packageName, List<Pkgs> target) {
        AudioHQApis.getProfile(context, packageName, true, new AudioHQNativeInterface<StringProfile>() {
            @Override
            public void onSuccess(StringProfile result) {
                boolean muted = application.getPresetSystem().getMuted(packageName, true);
                Pkgs current = new Pkgs();
                current.setPkg(packageName);
                current.setSticky(true);
                current.setGeneral(result.getGeneral());
                current.setLeft(result.getLeft());
                current.setRight(result.getRight());
                current.setHas_active_track(false);
                current.setHas_profile(true);
                current.setIsweakkey(true);
                current.setMuted(muted);
                current.setProcesses(new ArrayList<>());
                target.add(current);
            }

            @Override
            public void onFailure(String reason) {

            }
        });
    }
}
