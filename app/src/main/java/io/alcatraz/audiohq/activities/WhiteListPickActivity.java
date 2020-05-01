package io.alcatraz.audiohq.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import io.alcatraz.audiohq.Constants;
import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.adapters.PackPickerAdapter;
import io.alcatraz.audiohq.beans.PickerPack;
import io.alcatraz.audiohq.extended.CompatWithPipeActivity;
import io.alcatraz.audiohq.utils.PackageCtlUtils;
import io.alcatraz.audiohq.utils.SharedPreferenceUtil;
import io.alcatraz.audiohq.utils.Utils;

public class WhiteListPickActivity extends CompatWithPipeActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;

    List<PickerPack> data = new ArrayList<>();
    List<String> picked = new ArrayList<>();
    PackPickerAdapter adapter;

    SearchView searchView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_pick_app);
        initViews();
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                initData();
                doneFirstInitialize = true;
            }
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
            SharedPreferenceUtil sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
            String raw = (String) sharedPreferenceUtil.get(WhiteListPickActivity.this,
                    Constants.PREF_FLOAT_WINDOW_FILTER,
                    Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_FILTER);

            if(raw != null && !raw.equals("")){
                String[] filter_cooking = raw.split(",");
                Collections.addAll(picked, filter_cooking);
            }

            //Add 'system_server' manually
            PickerPack system_server = new PickerPack("system_server","system_server",
                    null, picked.contains("system_server"));
            data.add(system_server);

            List<PackageInfo> packageInfo = getPackageManager().getInstalledPackages(0);
            for(PackageInfo i: packageInfo){
                String label = PackageCtlUtils.getLabel(WhiteListPickActivity.this,i.applicationInfo);
                Drawable icon = PackageCtlUtils.getIcon(WhiteListPickActivity.this,i.applicationInfo);
                PickerPack current = new PickerPack(i.packageName,label,icon,picked.contains(i.packageName));
                data.add(current);
            }

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
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        View underline = searchView.findViewById(R.id.search_plate);
        underline.setBackgroundColor(Color.TRANSPARENT);
        progressBar = (ProgressBar) menu.findItem(R.id.action_progress_bar).getActionView();
        int dp24 = Utils.Dp2Px(this, 24);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(dp24, dp24);
        progressBar.setLayoutParams(params);
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

        return super.onCreateOptionsMenu(menu);
    }

    public void ignoreAdd(String target){
        picked.add(target);
        saveIgnored();
    }

    public void ignoreRemove(String target) {
        for(int i = 0; i< picked.size();i++){
            if(picked.get(i).equals(target)){
                picked.remove(i);
                saveIgnored();
                return;
            }
        }
    }
    private void saveIgnored(){
        SharedPreferenceUtil.getInstance().put(this,Constants.PREF_FLOAT_WINDOW_FILTER,ignoredPackToStr());
    }

    private String ignoredPackToStr() {
        StringBuilder out = new StringBuilder();
        for(int i = 0; i< picked.size();i++){
            out.append(picked.get(i));
            if(i != picked.size() -1){
                out.append(",");
            }
        }
        return out.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sendBroadcast(new Intent().setAction(Constants.BROADCAST_ACTION_UPDATE_PREFERENCES));
    }

    public void showProcessing() {
        progressBar.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });

    }

    public void hideProcessing() {
        progressBar.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finishAfterTransition();
        }
        return super.onOptionsItemSelected(item);
    }
}
