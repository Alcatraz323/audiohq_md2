package io.alcatraz.audiohq.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.alcatraz.audiohq.AudioHQApplication;
import io.alcatraz.audiohq.Constants;
import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.adapters.PackPickerAdapter;
import io.alcatraz.audiohq.beans.PickerPack;
import io.alcatraz.audiohq.extended.CompatWithPipeActivity;
import io.alcatraz.audiohq.utils.PackageCtlUtils;
import io.alcatraz.audiohq.utils.Panels;
import io.alcatraz.audiohq.utils.SharedPreferenceUtil;
import io.alcatraz.audiohq.utils.Utils;

public class AppPickActivity extends CompatWithPipeActivity {
    public static final String KEY_PICKER_TYPE = "key_picker_type";
    public static final int PICKER_WHITE_LIST = 0;
    public static final int PICKER_TYPE_STICKY_APPS = 1;

    public static final int PICKER_TYPE_NULL = -1;

    Toolbar toolbar;
    RecyclerView recyclerView;

    List<PickerPack> data = new ArrayList<>();
    List<String> picked = new ArrayList<>();
    List<String> notInstalled = new ArrayList<>();
    PackPickerAdapter adapter;

    SearchView searchView;
    ProgressBar progressBar;

    Handler mainAsyncHandler;
    int pickerType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pickerType = getIntent().getIntExtra(KEY_PICKER_TYPE, PICKER_TYPE_NULL);
        mainAsyncHandler = new Handler();
        setContentView(R.layout.activity_profile_pick_app);
        initViews();
        toolbar.post(() -> {
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                    runOnUiThread(this::initData);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            doneFirstInitialize = true;
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.pick_app_toolbar);
        recyclerView = findViewById(R.id.pick_app_recycler);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        adapter = new PackPickerAdapter(this, data);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fall_down);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setAdapter(adapter);
    }

    private void initData() {
        showProcessing();
        new Thread(() -> {
            data.clear();
            picked.clear();
            notInstalled.clear();

            SharedPreferenceUtil sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
            String raw = "";
            switch (pickerType) {
                case PICKER_WHITE_LIST:
                    raw = (String) sharedPreferenceUtil.get(AppPickActivity.this,
                            Constants.PREF_FLOAT_WINDOW_FILTER,
                            Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_FILTER);
                    break;
                case PICKER_TYPE_STICKY_APPS:
                    raw = (String) sharedPreferenceUtil.get(AppPickActivity.this,
                            Constants.PREF_FLOAT_STICKY_APPS,
                            Constants.DEFAULT_VALUE_PREF_FLOAT_STICKY_APPS);
                    break;
            }

            if (raw != null && !raw.equals("")) {
                String[] filter_cooking = raw.split(",");
                Collections.addAll(picked, filter_cooking);
                Collections.addAll(notInstalled, filter_cooking);
            }

            //Add 'system_server' manually
            PickerPack system_server = new PickerPack("system_server", "system_server",
                    null, picked.contains("system_server"));
            data.add(system_server);

            List<PackageInfo> packageInfo = getPackageManager().getInstalledPackages(0);
            for (PackageInfo i : packageInfo) {
                String label = PackageCtlUtils.getLabel(AppPickActivity.this, i.applicationInfo);
                Drawable icon = PackageCtlUtils.getIcon(AppPickActivity.this, i.applicationInfo);
                boolean currentPicked = picked.contains(i.packageName);
                PickerPack current = new PickerPack(i.packageName, label, icon, currentPicked);
                notInstalled.remove(i.packageName);
                if (currentPicked) {
                    data.add(0, current);
                } else {
                    data.add(current);
                }
            }

            addNotInstalledButSelectedPacks();

            runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
                recyclerView.scheduleLayoutAnimation();
                hideProcessing();
            });
        }).start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = new MenuInflater(this);
        mi.inflate(R.menu.activity_preset_general_menu, menu);
        menu.add(R.string.preset_manual_add);
        setupProcessingProgress(menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void pickerAdd(String target) {
        if (!picked.contains(target)) {
            picked.add(target);
            savePicked();
        }
    }

    public void pickerRemove(String target) {
        if (picked.contains(target)) {
            picked.remove(target);
            savePicked();
        }
    }

    private void savePicked() {
        switch (pickerType) {
            case PICKER_WHITE_LIST:
                SharedPreferenceUtil.getInstance().put(this, Constants.PREF_FLOAT_WINDOW_FILTER, pickedPackToStr());
                mainAsyncHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendBroadcast(new Intent().setAction(Constants.BROADCAST_ACTION_UPDATE_PREFERENCES));
                        notifyPlayingStickyUpdate();
                    }
                },30);
                break;
            case PICKER_TYPE_STICKY_APPS:
                SharedPreferenceUtil.getInstance().put(this, Constants.PREF_FLOAT_STICKY_APPS, pickedPackToStr());
                mainAsyncHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendBroadcast(new Intent().setAction(Constants.BROADCAST_ACTION_UPDATE_PREFERENCES));
                        notifyPlayingStickyUpdate();
                    }
                },30);
                break;
        }
    }

    private void notifyPlayingStickyUpdate() {
        AudioHQApplication application = (AudioHQApplication) getApplication();
        application.getPlayingSystem().updateSpecialAppLists();
    }

    private String pickedPackToStr() {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < picked.size(); i++) {
            out.append(picked.get(i));
            if (i != picked.size() - 1) {
                out.append(",");
            }
        }
        return out.toString();
    }

    private void addNotInstalledButSelectedPacks() {
        for (String i : notInstalled) {
            PickerPack target = new PickerPack(i, i, null, true);
            data.add(0, target);
        }
    }

    private void setupProcessingProgress(Menu menu) {
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        View underline = searchView.findViewById(R.id.search_plate);
        underline.setBackgroundColor(Color.TRANSPARENT);
        progressBar = (ProgressBar) menu.findItem(R.id.action_progress_bar).getActionView();
        int dp24 = Utils.Dp2Px(this, 24);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(dp24, dp24);
        progressBar.setLayoutParams(params);
        hideProcessing();

        searchView.setQueryHint(getString(R.string.preset_search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (TextUtils.isEmpty(s)) {
                    adapter.onTextChanged("");
                } else {
                    adapter.onTextChanged(s);
                }
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sendBroadcast(new Intent().setAction(Constants.BROADCAST_ACTION_UPDATE_PREFERENCES));
    }

    public void showProcessing() {
        progressBar.post(() -> progressBar.setVisibility(View.VISIBLE));
    }

    public void hideProcessing() {
        progressBar.post(() -> progressBar.setVisibility(View.GONE));
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        hideProcessing();
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finishAfterTransition();
        } else if(item.getTitle().equals(getString(R.string.preset_manual_add))){
            Panels.showPickerManualPanel(this, new Panels.ManualInterface() {
                @Override
                public void onResult(String input) {
                    pickerAdd(input);
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }
}
