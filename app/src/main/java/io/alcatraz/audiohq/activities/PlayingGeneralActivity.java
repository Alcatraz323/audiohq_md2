package io.alcatraz.audiohq.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.alcatraz.support.v4.appcompat.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.alcatraz.audiohq.AudioHQApplication;
import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.adapters.PlayingRecyclerAdapter;
import io.alcatraz.audiohq.beans.AudioHQNativeInterface;
import io.alcatraz.audiohq.beans.playing.Pkgs;
import io.alcatraz.audiohq.beans.playing.PlayingSystem;
import io.alcatraz.audiohq.extended.CompatWithPipeActivity;
import io.alcatraz.audiohq.utils.Utils;

public class PlayingGeneralActivity extends CompatWithPipeActivity {
    ProgressBar progressBar;

    Toolbar toolbar;
    LinearLayout filter;
    Switch filter_switch;

    ImageButton why_no_expand;
    TextView why_no_info;

    RecyclerView recyclerView;
    PlayingRecyclerAdapter playingRecyclerAdapter;
    LayoutAnimationController controller;

    PlayingSystem playingSystem;
    List<Pkgs> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);
        initViews();
        toolbar.post(() -> initialize());
    }

    private void findViews(){
        toolbar = findViewById(R.id.playing_general_toolbar);
        filter = findViewById(R.id.playing_general_filter);
        filter_switch = findViewById(R.id.playing_general_filter_switch);
        recyclerView = findViewById(R.id.playing_general_recycler);
        why_no_expand = findViewById(R.id.playing_why_no_expand);
        why_no_info = findViewById(R.id.playing_why_no_info);
    }

    private void initViews(){
        findViews();
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        playingRecyclerAdapter = new PlayingRecyclerAdapter(this,data);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        controller= AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fall_down);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.setAdapter(playingRecyclerAdapter);

        filter.setOnClickListener(view -> {
            if (filter_switch.isChecked()) {
                playingRecyclerAdapter.filterActive(false);
            }else {
                playingRecyclerAdapter.filterActive(true);
            }
            filter_switch.setChecked(!filter_switch.isChecked());
        });

        why_no_expand.setOnClickListener(view -> {
            if (why_no_info.getMaxLines() == 1) {
                why_no_expand.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                why_no_info.setMaxLines(Integer.MAX_VALUE);
            } else {
                why_no_expand.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                why_no_info.setMaxLines(1);
            }
        });
    }

    private void initialize(){
        AudioHQApplication application = (AudioHQApplication) getApplication();
        playingSystem = application.getPlayingSystem();
        initData();
        doneFirstInitialize = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = new MenuInflater(this);
        mi.inflate(R.menu.activity_playing_menu,menu);
        progressBar = (ProgressBar) menu.findItem(R.id.action_progress_bar).getActionView();
        int dp24 = Utils.Dp2Px(this,24);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(dp24+Utils.Dp2Px(this,16),dp24);
        progressBar.setLayoutParams(params);
        progressBar.setPadding(0,0,Utils.Dp2Px(this,16),0);

        return super.onCreateOptionsMenu(menu);
    }

    public void showProessing(){
        progressBar.post(() -> progressBar.setVisibility(View.VISIBLE));

    }

    public void hideProcessing(){
        progressBar.post(() -> progressBar.setVisibility(View.GONE));
    }

    private void initData(){
        showProessing();
        data.clear();
        playingRecyclerAdapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
        playingSystem.update(false, new AudioHQNativeInterface<PlayingSystem>() {
            @Override
            public void onSuccess(PlayingSystem result) {
                data.addAll(result.getData());
                playingRecyclerAdapter.notifyDataSetChanged();
                recyclerView.scheduleLayoutAnimation();
                hideProcessing();
                playingRecyclerAdapter.filterActive(filter_switch.isChecked());
            }

            @Override
            public void onFailure(String reason) {
                toast(reason);
            }
        });
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

    @Override
    protected void onResume() {
        super.onResume();
        if(doneFirstInitialize) {
            initData();
        }
    }
}
