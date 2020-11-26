package io.alcatraz.audiohq.activities;

import android.content.pm.PackageInfo;
import android.graphics.Color;
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
import android.widget.TextView;

import com.alcatraz.support.v4.appcompat.StatusBarUtil;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import io.alcatraz.audiohq.AudioHQApplication;
import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.adapters.PresetGeneralRecyclerAdapter;
import io.alcatraz.audiohq.adapters.SimplePagerAdapter;
import io.alcatraz.audiohq.beans.AudioHQNativeInterface;
import io.alcatraz.audiohq.beans.OverallStatus;
import io.alcatraz.audiohq.beans.preset.ControlElement;
import io.alcatraz.audiohq.beans.preset.NormalViewPage;
import io.alcatraz.audiohq.beans.preset.PackageProfileQuery;
import io.alcatraz.audiohq.beans.preset.PresetSystem;
import io.alcatraz.audiohq.core.utils.AudioHQApis;
import io.alcatraz.audiohq.extended.CompatWithPipeActivity;
import io.alcatraz.audiohq.utils.PackageCtlUtils;
import io.alcatraz.audiohq.utils.Panels;
import io.alcatraz.audiohq.utils.Utils;

public class PresetGeneralActivity extends CompatWithPipeActivity {
    public static final String KEY_PROFILE_COUNT = "profile_count";
    SearchView searchView;
    ProgressBar progressBar;

    AppBarLayout appBarLayout;
    TextView shared_counter;
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    FloatingActionButton preset_add;

    RecyclerView recycler_for_all;
    List<ControlElement> all_data = new LinkedList<>();
    PresetGeneralRecyclerAdapter all_adapter;

    RecyclerView recycler_for_set;
    List<ControlElement> set_data = new LinkedList<>();
    PresetGeneralRecyclerAdapter set_adapter;

    LayoutAnimationController controller;

    PresetSystem presetSystem;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preset_general);
        AudioHQApplication application = (AudioHQApplication) getApplication();
        presetSystem = application.getPresetSystem();
        findViews();
        initViews();
        shared_counter.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateData();
                doneFirstInitialize = true;
            }
        }, 1000);
    }

    private void findViews() {
        appBarLayout = findViewById(R.id.preset_general_app_bar);
        shared_counter = findViewById(R.id.preset_general_counter);
        toolbar = findViewById(R.id.preset_general_toolbar);
        tabLayout = findViewById(R.id.preset_general_tabs);
        viewPager = findViewById(R.id.preset_general_pager);
        preset_add = findViewById(R.id.preset_general_precise_add);
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setImmersive(true);
        shared_counter.setText(getIntent().getStringExtra(KEY_PROFILE_COUNT));
        tabLayout.setSelectedTabIndicatorColor(color);

        preset_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Panels.getManualAddPanel(PresetGeneralActivity.this).show();
            }
        });

        initPager();
    }

    private void initPager() {
        View root1 = getLayoutInflater().inflate(R.layout.preset_recycler_view, null);
        View root2 = getLayoutInflater().inflate(R.layout.preset_recycler_view, null);
        recycler_for_all = root1.findViewById(R.id.preset_general_recycler);
        recycler_for_set = root2.findViewById(R.id.preset_general_recycler);
        all_adapter = new PresetGeneralRecyclerAdapter(this, all_data, preset_add);
        set_adapter = new PresetGeneralRecyclerAdapter(this, set_data, preset_add);
        recycler_for_all.setAdapter(all_adapter);
        recycler_for_all.setLayoutManager(new LinearLayoutManager(this));
        recycler_for_set.setAdapter(set_adapter);
        recycler_for_set.setLayoutManager(new LinearLayoutManager(this));

        controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fall_down);
        recycler_for_all.setLayoutAnimation(controller);
        recycler_for_set.setLayoutAnimation(controller);
        recycler_for_all.setItemViewCacheSize(20);

        NormalViewPage page1 = new NormalViewPage();
        page1.setTitle(getString(R.string.preset_tab_all_apps));
        page1.setView(root1);
        NormalViewPage page2 = new NormalViewPage();
        page2.setTitle(getString(R.string.preset_tab_has_set));
        page2.setView(root2);
        List<NormalViewPage> pages = new LinkedList<>();
        pages.add(page1);
        pages.add(page2);
        SimplePagerAdapter adapter = new SimplePagerAdapter(this, pages);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        updateData();
                        break;
                    case 1:
                        updateHasSet();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void updateData() {
        showProcessing();
        all_data.clear();
        all_adapter.notifyDataSetChanged();
        recycler_for_all.scheduleLayoutAnimation();
        new Thread(() -> {
            presetSystem.update(new AudioHQNativeInterface<PresetSystem>() {
                @Override
                public void onSuccess(PresetSystem result) {
                    List<PackageInfo> infos = getPackageManager().getInstalledPackages(0);
                    for (PackageInfo i : infos) {
                        PackageProfileQuery query = presetSystem.getPresetStatus(PresetGeneralActivity.this, i.packageName);
                        ControlElement element =
                                new ControlElement(PackageCtlUtils.getLabel(PresetGeneralActivity.this, i.packageName),
                                        query.getText(), i.packageName, true,
                                        PackageCtlUtils.getIcon(PresetGeneralActivity.this, i.packageName), query.getColor());
                        all_data.add(element);
                    }
                    runOnUiThread(() -> {
                        all_adapter.notifyDataSetChanged();
                        recycler_for_all.scheduleLayoutAnimation();
                        hideProcessing();
                    });
                }

                @Override
                public void onFailure(String reason) {
                    runOnUiThread(() -> {
                        toast(reason);
                    });
                }
            });
        }).start();
    }

    public void updateHasSet() {
        showProcessing();
        set_data.clear();
        set_adapter.notifyDataSetChanged();
        recycler_for_set.scheduleLayoutAnimation();
        new Thread(new Runnable() {
            @Override
            public void run() {
                presetSystem.update(new AudioHQNativeInterface<PresetSystem>() {
                    @Override
                    public void onSuccess(PresetSystem result) {
                        set_data.addAll(presetSystem.getSetData(PresetGeneralActivity.this));
                        runOnUiThread(() -> {
                            set_adapter.notifyDataSetChanged();
                            recycler_for_set.scheduleLayoutAnimation();
                            hideProcessing();
                        });
                    }

                    @Override
                    public void onFailure(String reason) {
                        runOnUiThread(() -> {
                            toast(reason);
                        });
                    }
                });
            }
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
                    // Clear the text filter.
                    switch (viewPager.getCurrentItem()) {
                        case 0:
                            all_adapter.onTextChanged("");
                            break;
                        case 1:
                            set_adapter.onTextChanged("");
                            break;
                    }

                } else {
                    // Sets the initial value for the text filter.
                    switch (viewPager.getCurrentItem()) {
                        case 0:
                            all_adapter.onTextChanged(s);
                            break;
                        case 1:
                            set_adapter.onTextChanged(s);
                            break;
                    }
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
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

    public void updateIndicator() {
        AudioHQApis.getOverallStatus(this, new AudioHQNativeInterface<OverallStatus>() {
            @Override
            public void onSuccess(OverallStatus result) {
                shared_counter.setText(String.format(getString(R.string.main_card_preset_indicator), result.getProfileCount()));
            }

            @Override
            public void onFailure(String reason) {
                toast(reason);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        preset_add.setTransitionName(getString(R.string.transition_preset_fab));
        if (doneFirstInitialize) {
            updateIndicator();
            switch (viewPager.getCurrentItem()) {
                case 0:
                    updateData();
                    break;
                case 1:
                    updateHasSet();
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishAfterTransition();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
