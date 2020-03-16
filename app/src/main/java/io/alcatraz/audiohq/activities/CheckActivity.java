package io.alcatraz.audiohq.activities;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alcatraz.support.v4.appcompat.StatusBarUtil;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.appcompat.widget.Toolbar;
import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.beans.AudioHQNativeInterface;
import io.alcatraz.audiohq.core.utils.AudioHQApis;
import io.alcatraz.audiohq.core.utils.CheckUtils;
import io.alcatraz.audiohq.extended.CompatWithPipeActivity;
import io.alcatraz.audiohq.utils.Utils;

public class CheckActivity extends CompatWithPipeActivity {
    ProgressBar progressBar;
    AppBarLayout appBarLayout;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    ImageView indicator_image;
    TextView indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        findViews();
        initViews();
    }

    private void findViews(){
        appBarLayout = findViewById(R.id.check_app_bar);
        toolbar = findViewById(R.id.check_toolbar);
        indicator_image = findViewById(R.id.check_icon);
        indicator = findViewById(R.id.check_counter);
        collapsingToolbarLayout = findViewById(R.id.check_collapse);
    }

    private void initViews(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        StatusBarUtil.setColor(this, Color.parseColor("#212121"),0);

        toolbar.post(new Runnable() {
            @Override
            public void run() {
                loadCheckPanel();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void loadCheckPanel(){
        showProessing();
        TextView root = findViewById(R.id.check_root);
        TextView selinux = findViewById(R.id.check_selinux);
        TextView supported = findViewById(R.id.check_supported);
        TextView elf_info = findViewById(R.id.check_elf_info);
        TextView audioserver_info = findViewById(R.id.check_audioserver_info);
        ImageView support_indicator = findViewById(R.id.check_support_indicator);

        boolean support = CheckUtils.getIfSupported();

        root.setText(CheckUtils.getRootStatus() + "");
        selinux.setText(CheckUtils.getSeLinuxEnforce());
        supported.setText(CheckUtils.getIfSupported() + " (Api " + Build.VERSION.SDK_INT + " - "
                + Utils.extractStringArr(CheckUtils.getSupportArch()) + ")");

        AudioHQApis.getAudioHQNativeInfo(this, new AudioHQNativeInterface<String[]>() {
            @Override
            public void onSuccess(String[] result) {
                appBarLayout.setBackgroundColor(getColor(R.color.status_back));
                collapsingToolbarLayout.setContentScrimColor(getColor(R.color.status_back));
                StatusBarUtil.setColor(CheckActivity.this,getColor(R.color.status_back),0);
                elf_info.setText(result[0]+result[1]);
            }

            @Override
            public void onFailure(String reason) {
                appBarLayout.setBackgroundColor(getColor(R.color.base_gray_tint));
                collapsingToolbarLayout.setContentScrimColor(getColor(R.color.base_gray_tint));
                StatusBarUtil.setColor(CheckActivity.this,getColor(R.color.base_gray_tint),0);
                indicator.setText(R.string.check_daemon_status_dead);
                indicator_image.setImageResource(R.drawable.ic_close);
                elf_info.setText(reason);
            }
        });
        audioserver_info.setText(CheckUtils.getAudioServerInfo());

        if (support)
            Utils.setImageWithTint(support_indicator, R.drawable.ic_check_black_24dp, getResources().getColor(R.color.green_colorPrimary));
        else
            Utils.setImageWithTint(support_indicator, R.drawable.ic_close, getResources().getColor(android.R.color.holo_red_light));
        hideProcessing();
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

}
