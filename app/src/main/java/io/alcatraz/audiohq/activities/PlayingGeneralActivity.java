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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.alcatraz.support.v4.appcompat.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

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
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                initialize();
            }
        });
    }

    private void findViews(){
        toolbar = findViewById(R.id.playing_general_toolbar);
        filter = findViewById(R.id.playing_general_filter);
        filter_switch = findViewById(R.id.playing_general_filter_switch);
        recyclerView = findViewById(R.id.playing_general_recycler);
    }

    private void initViews(){
        findViews();
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        StatusBarUtil.setColor(this, Color.parseColor("#212121"),0);

        playingRecyclerAdapter = new PlayingRecyclerAdapter(this,data);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        controller= AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fall_down);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.setAdapter(playingRecyclerAdapter);

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filter_switch.isChecked()) {
                    playingRecyclerAdapter.filterActive(false);
                }else {
                    playingRecyclerAdapter.filterActive(true);
                }
                filter_switch.setChecked(!filter_switch.isChecked());
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
        progressBar.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });

    }

    public void hideProcessing(){
        progressBar.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void initData(){
        showProessing();
        data.clear();
        playingRecyclerAdapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
        playingSystem.update(new AudioHQNativeInterface<PlayingSystem>() {
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
