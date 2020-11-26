package io.alcatraz.audiohq.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import io.alcatraz.audiohq.Constants;
import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.beans.AudioHQNativeInterface;
import io.alcatraz.audiohq.beans.OverallStatus;
import io.alcatraz.audiohq.core.utils.AudioHQApis;
import io.alcatraz.audiohq.extended.CompatWithPipeActivity;
import io.alcatraz.audiohq.services.FloatPanelService;
import io.alcatraz.audiohq.utils.AnimateUtils;
import io.alcatraz.audiohq.utils.Panels;
import io.alcatraz.audiohq.utils.SharedPreferenceUtil;

public class MainActivity extends CompatWithPipeActivity implements View.OnClickListener {
    Toolbar toolbar;

    CardView status;
    ImageView status_indicator_image;
    TextView status_indicator;
    TextView status_vinfo;
    LinearLayout status_overlay;

    CardView playing;
    ImageView playing_indicator_image;
    TextView playing_indicator;

    CardView preset;
    ImageView preset_indicator_image;
    TextView preset_indicator;
    FloatingActionButton preset_add;

    CardView settings;
    CardView help;

    boolean isFunctionValid = false;
    OverallStatus current_status;

    boolean showAnniversary2020Intro = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        initViews();
        initData();
        startFloatingService();
        if(showAnniversary2020Intro){
            Panels.getAnniversary2020Intro(this).show();
        }
    }

    public void startFloatingService() {
        if (float_service) {
            if (!Settings.canDrawOverlays(this)) {
                toast(R.string.toast_cant_overlay);
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
            } else {
                startService(new Intent(MainActivity.this, FloatPanelService.class));
            }
        }
    }

    private void findViews() {
        toolbar = findViewById(R.id.main_toolbar);
        status = findViewById(R.id.main_card_status);
        status_indicator_image = findViewById(R.id.main_card_status_indicator_image);
        status_indicator = findViewById(R.id.main_card_status_indicator);
        status_vinfo = findViewById(R.id.main_card_status_vinfo);
        status_overlay = findViewById(R.id.main_card_status_overlay);
        playing = findViewById(R.id.main_card_playing);
        playing_indicator = findViewById(R.id.main_card_playing_indicator);
        playing_indicator_image = findViewById(R.id.main_card_playing_indicator_image);
        preset = findViewById(R.id.main_card_preset);
        preset_indicator = findViewById(R.id.main_card_preset_indicator);
        preset_indicator_image = findViewById(R.id.main_card_preset_indicator_image);
        preset_add = findViewById(R.id.main_preset_precise_add);
        settings = findViewById(R.id.main_card_setting);
        help = findViewById(R.id.main_card_help);

        SharedPreferenceUtil sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
        showAnniversary2020Intro = (boolean) sharedPreferenceUtil.get(this, Constants.PREF_SHOW_ANNIVERSARY_2020_INTRO,Constants.DEFAULT_VALUE_PREF_SHOW_ANNIVERSARY_2020_INTRO);
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        status.setOnClickListener(this);
        playing.setOnClickListener(this);
        preset.setOnClickListener(this);
        preset_add.setOnClickListener(this);
        settings.setOnClickListener(this);
        help.setOnClickListener(this);

    }

    private void initData() {
        AudioHQApis.getOverallStatus(this, new AudioHQNativeInterface<OverallStatus>() {
            @Override
            public void onSuccess(OverallStatus result) {
                current_status = result;
                playing_indicator.setText(String.format(getString(R.string.main_card_playing_indicator), result.getPlayingcount()));
                preset_indicator.setText(String.format(getString(R.string.main_card_preset_indicator), result.getProfileCount()));
                status_indicator_image.setImageResource(R.drawable.ic_check_circle_black_24dp);
                status_indicator.setText(R.string.check_daemon_status_alive);
                status_overlay.setBackgroundColor(color);
                isFunctionValid = true;
                showStatusLayerAnim();
            }

            @Override
            public void onFailure(String reason) {
                current_status = null;
                status_indicator_image.setImageResource(R.drawable.ic_alert);
                status_indicator.setText(R.string.check_daemon_status_dead);
                status_overlay.setBackgroundColor(getColor(R.color.base_gray_tint));
                playing_indicator.setText(R.string.main_card_invalid);
                preset_indicator.setText(R.string.main_card_invalid);
                isFunctionValid = false;
                showStatusLayerAnim();
            }
        });
        AudioHQApis.getAudioHQNativeInfo(this, new AudioHQNativeInterface<String[]>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(String[] result) {
                String[] process_1 = result[1].split("\n");
                String[] ids = process_1[0].split(":");
                String[] times = process_1[1].split(":");
                status_vinfo.setText(ids[1] + "\n" + times[1]);
            }

            @Override
            public void onFailure(String reason) {
                status_vinfo.setText(reason);
            }
        });
    }

    private void showStatusLayerAnim() {
        if (status_overlay.getVisibility() == View.GONE) {
            status_overlay.setVisibility(View.VISIBLE);
            status_overlay.post(() -> AnimateUtils.playstart(status_overlay, () -> {
            }));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = new MenuInflater(this);
        mi.inflate(R.menu.activity_main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item2:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.item3:
                initData();
                break;
            case R.id.item4:
                startActivity(new Intent(this, LogActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_card_status:
                Intent check = new Intent(MainActivity.this, CheckActivity.class);
                check.putExtra(CheckActivity.KEY_PRE_KNOWN_STATUS, current_status != null);
                startTransition(check,
                        status_indicator, status_indicator_image);
                break;
            case R.id.main_card_playing:
                if (isFunctionValid) {
                    startActivity(new Intent(MainActivity.this, PlayingGeneralActivity.class));
                } else {
                    toast(R.string.main_card_invalid);
                }
                break;
            case R.id.main_card_preset:
            case R.id.main_preset_precise_add:
                if (isFunctionValid) {
                    Intent intent = new Intent(MainActivity.this, PresetGeneralActivity.class);
                    intent.putExtra(PresetGeneralActivity.KEY_PROFILE_COUNT, preset_indicator.getText().toString());
                    startTransition(intent, preset_add, preset_indicator, preset_indicator_image);
                } else {
                    toast(R.string.main_card_invalid);
                }
                break;
            case R.id.main_card_setting:
                if (isFunctionValid) {
                    startActivity(new Intent(MainActivity.this, PreferenceActivity.class));
                } else {
                    toast(R.string.main_card_invalid);
                }
                break;
            case R.id.main_card_help:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://alcatraz323.github.io/audiohq")));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

}
