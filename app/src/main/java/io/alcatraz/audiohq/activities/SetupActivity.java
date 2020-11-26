package io.alcatraz.audiohq.activities;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;
import io.alcatraz.audiohq.BuildConfig;
import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.beans.AudioHQNativeInterface;
import io.alcatraz.audiohq.beans.SetupPage;
import io.alcatraz.audiohq.core.utils.AudioHQApis;
import io.alcatraz.audiohq.core.utils.CheckUtils;
import io.alcatraz.audiohq.extended.SetupWizardBaseActivity;
import io.alcatraz.audiohq.utils.AnimateUtils;
import io.alcatraz.audiohq.utils.Utils;

public class SetupActivity extends SetupWizardBaseActivity {
    @Override
    public void onSetupPageInit(List<SetupPage> pages) {
        String[] setup_titles = getResources().getStringArray(R.array.setup_page_titles);
        int[] page_layout_ids = {R.layout.setup_1, R.layout.setup_2,
                R.layout.setup_3, R.layout.setup_4, R.layout.setup_5};

        for (int i = 0; i < setup_titles.length; i++) {
            SetupPage page = new SetupPage(setup_titles[i], page_layout_ids[i]);
            pages.add(page);
        }

        getPager().addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 1:
                        onSelectSetup2();
                        break;
                    case 2:
                        onSelectSetup3();
                        break;
                    case 3:
                        onSelectSetup4_Apply();
                        break;
                    case 4:
                        banForwardStep();
                        break;
                    default:
                        restoreState();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public void onUpdate(List<SetupPage> pages) {
        SetupPage page = new SetupPage(getResources().getString(R.string.setup_current_update), R.layout.setup_6);
        pages.add(getPermissionPage());
        pages.add(page);
    }

    @Override
    public void onFinishSetup() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.AppTask> tasks = activityManager.getAppTasks();
            if (tasks != null && tasks.size() > 0) {
                tasks.get(0).setExcludeFromRecents(exclude_from_recent);
            }
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void setTheme() {
        setTheme(R.style.Setup);
    }

    @Override
    public void setupStatusBarAntiColor() {

    }

    @Override
    public int getVersionCode() { return BuildConfig.VERSION_CODE;
    }

    private void onSelectSetup4_Apply() {
        startPending();

        //Setup here

        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                endPending();
                getPager().setCurrentItem(getPager().getCurrentItem() + 1);
            });
        }).start();
    }

    private void onSelectSetup3() {
        startPending();

        View root_view = getPageList().get(2).getRootView();

        Button btn_go_website_github = root_view.findViewById(R.id.setup_3_go_website_github);
        Button btn_go_website_fastgit = root_view.findViewById(R.id.setup_3_go_website_fastgit);
        CheckBox installed = root_view.findViewById(R.id.setup_3_installed);
        TextView current_installed = root_view.findViewById(R.id.setup_3_installed_info);
        ImageButton refresh = root_view.findViewById(R.id.setup_3_installed_refresh);

        //Initial state setup
        installed.setChecked(false);

        endPending();
        banNextStep();

        btn_go_website_github.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.setup_module_download_url_github)))));
        btn_go_website_fastgit.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.setup_module_download_url_fastgit)))));
        installed.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                restoreState();
            } else {
                banNextStep();
            }
        });

        AudioHQApis.getAudioHQNativeInfo(this, new AudioHQNativeInterface<String[]>() {
            @Override
            public void onSuccess(String[] result) {
                installed.setChecked(true);
                current_installed.setText(String.format(getString(R.string.setup_3_current_installed),
                        result[0]));
            }

            @Override
            public void onFailure(String reason) {
                current_installed.setText(String.format(getString(R.string.setup_3_current_installed),
                        getString(R.string.setup_3_not_detected)));
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioHQApis.getAudioHQNativeInfo(SetupActivity.this, new AudioHQNativeInterface<String[]>() {
                    @Override
                    public void onSuccess(String[] result) {
                        installed.setChecked(true);
                        current_installed.setText(String.format(getString(R.string.setup_3_current_installed),
                                result[0]));
                    }

                    @Override
                    public void onFailure(String reason) {
                        current_installed.setText(String.format(getString(R.string.setup_3_current_installed),
                                getString(R.string.setup_3_not_detected)));
                    }
                });
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void onSelectSetup2() {
        boolean can_go_next = true;
        int color_red = getResources().getColor(android.R.color.holo_red_light, null);

        startPending();

        //Find views
        View root_view = getPageList().get(1).getRootView();

        CardView api_check = root_view.findViewById(R.id.setup_2_api_check);
        CardView requirements_not_meet = root_view.findViewById(R.id.setup_2_requirements_not_meet);

        Button requirement_unlock = root_view.findViewById(R.id.setup_2_requirements_not_meet_unlock);

        TextView api_check_title = root_view.findViewById(R.id.setup_2_api_check_title);
        TextView api_check_state = root_view.findViewById(R.id.setup_2_api_check_state);
        ImageView api_check_indicator = root_view.findViewById(R.id.setup_2_api_check_indicator);

        setupSelinuxCard(root_view);

        //Initial state setup
        api_check.setVisibility(View.GONE);
        requirements_not_meet.setVisibility(View.GONE);

        if (!CheckUtils.getIfSupported()) {
            api_check_title.setTextColor(color_red);
            Utils.setImageWithTint(api_check_indicator, R.drawable.ic_close, color_red);
            can_go_next = false;
        }
        api_check_state.setText("Api:" + Build.VERSION.SDK_INT +
                "(" + Utils.extractStringArr(CheckUtils.getSupportArch()) + ")");
        AnimateUtils.playstart(api_check, () -> {
        });

        endPending();

        if (!can_go_next) {
            AnimateUtils.playstart(requirements_not_meet, () -> {
            });
            restoreState();
            banNextStep();
        } else {
            banNextStep();
            toast(R.string.setup_2_warning_delay);
            new Thread(() -> {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(this::restoreState);
            }).start();
        }

        requirement_unlock.setOnClickListener(view -> {
            AnimateUtils.playEnd(requirements_not_meet/*, () -> {}*/);
            restoreState();
        });
    }

    public void setupSelinuxCard(View root) {
        TextView title = root.findViewById(R.id.setup_2_selinux_check_title);
        TextView status = root.findViewById(R.id.setup_2_selinux_check_state);
        ImageView indicator = root.findViewById(R.id.setup_2_selinux_check_indicator);

        String enforcing = CheckUtils.getSeLinuxEnforce();
        if (enforcing != null) {
            boolean isPermissive = enforcing.contains("Permissive");

            status.setText(enforcing);
            if (isPermissive) {
                title.setTextColor(getColor(R.color.green_colorPrimary));
                indicator.setImageResource(R.drawable.ic_check_green_24dp);
            }else {
                title.setTextColor(getColor(R.color.orange_colorPrimary));
                Utils.setImageWithTint(indicator, R.drawable.ic_alert, getColor(R.color.orange_colorPrimary));
            }

        }
    }

    public SetupPage getPermissionPage() {
        SetupPage page = new SetupPage(getString(R.string.setup_permissions), R.layout.setup_7);
        View root = getLayoutInflater().inflate(R.layout.setup_7, null);
        Button request_battery_ignore = root.findViewById(R.id.request_battery_ignore);
        Button request_overlay = root.findViewById(R.id.request_overlay);
        request_battery_ignore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.ignoreBatteryOptimization(SetupActivity.this);
            }
        });

        request_overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())));
            }
        });
        page.setRootView(root);
        return page;
    }
}
