package io.alcatraz.audiohq.extended;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import androidx.viewpager.widget.ViewPager;
import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.adapters.SetupPagerAdapter;
import io.alcatraz.audiohq.beans.SetupPage;
import io.alcatraz.audiohq.utils.AnimateUtils;
import io.alcatraz.audiohq.utils.SharedPreferenceUtil;
import io.alcatraz.audiohq.utils.Utils;

public abstract class SetupWizardBaseActivity extends CompatWithPipeActivity {
    public static final String PREF_ACTION_HAS_RUN_FULL_SETUP = "has_run_full_setup";
    public static final String PREF_ACTION_PREVIOUS_VERSIONCODE = "previous_version_code";

    public static final String KEY_SETUP_START_UP_TYPE = "key_setup_start_up_type";
    public static final String STARTUP_DEFAULT = "default";
    public static final String STARTUP_FORCE_SHOW_UPDATE = "force_show_update";
    public static final String STARTUP_FORCE_SHOW_FULL_SETUP = "force_show_full_setup";

    private String setup_pref_prefix = "alc_setup_";
    private SharedPreferenceUtil spf = SharedPreferenceUtil.getInstance();

    TextView setup_title;
    NoScrollViewPager setup_pager;
    ProgressBar setup_progress;
    ImageButton setup_forward;
    Button setup_next;
    LinearLayout setup_nav;
    FrameLayout setup_progress_bar_limit;

    SetupPagerAdapter adapter;

    //Data
    List<SetupPage> pages = new LinkedList<>();

    //Prefs
    int versionCode;
    boolean hasRunFullSetup;

    String startUpType;

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_Setup);
        setContentView(R.layout.activity_setup);
        initViews();
        initPrefs();
        initPages();
    }

    public abstract void onSetupPageInit(List<SetupPage> pages);

    public abstract void onUpdate(List<SetupPage> pages);

    public abstract void onFinishSetup();

    public abstract int getVersionCode();

    public ProgressBar getProgressBar() {
        return setup_progress;
    }

    public void setTitle(CharSequence title) {
        setTitle(title, true);
    }

    public void setTitle(CharSequence title, boolean animate) {
        if (animate)
            AnimateUtils.textChange(setup_title, title);
        else
            setup_title.setText(title);
    }

    public void removeAllPages() {
        pages.clear();
        adapter.notifyDataSetChanged();
    }

    public void addPage(SetupPage page) {
        pages.add(page);
        adapter.notifyDataSetChanged();
    }

    public void removePage(Object o) {
        if (o instanceof SetupPage)
            pages.remove(o);
        else
            pages.remove((int) o);
        adapter.notifyDataSetChanged();
    }

    public NoScrollViewPager getPager() {
        return setup_pager;
    }

    public List<SetupPage> getPageList() {
        return pages;
    }

    public void setShowProgress(boolean showProgress) {
        if (showProgress)
            setup_progress_bar_limit.setVisibility(View.VISIBLE);
        else
            setup_progress_bar_limit.setVisibility(View.GONE);
    }

    public ImageButton getBtnForward() {
        return setup_forward;
    }

    public Button getBtnNext() {
        return setup_next;
    }

    public void restoreState() {
        setShowProgress(false);
        getBtnForward().setEnabled(true);
        Utils.setImageWithTint(getBtnForward(), R.drawable.ic_chevron_left_black_24dp, Color.DKGRAY);
        getBtnNext().setEnabled(true);
        getBtnNext().setTextColor(Color.DKGRAY);
        getBtnNext().setCompoundDrawablesRelative(null, null,
                Utils.getTintedDrawable(this, R.drawable.ic_chevron_right_black_24dp, Color.DKGRAY), null);
    }

    public void banNextStep() {
        getBtnNext().setEnabled(false);
        getBtnNext().setTextColor(Color.GRAY);
        getBtnNext().setCompoundDrawablesRelative(null, null,
                Utils.getTintedDrawable(this, R.drawable.ic_chevron_right_black_24dp, Color.GRAY), null);
    }

    public void banForwardStep() {
        getBtnForward().setEnabled(false);
        Utils.setImageWithTint(getBtnForward(), R.drawable.ic_chevron_left_black_24dp, Color.GRAY);
    }

    public void banPageSwitch() {
        getBtnNext().setEnabled(false);
        getBtnForward().setEnabled(false);
        getBtnNext().setTextColor(Color.GRAY);
        getBtnNext().setCompoundDrawablesRelative(null, null,
                Utils.getTintedDrawable(this, R.drawable.ic_chevron_right_black_24dp, Color.GRAY), null);
        Utils.setImageWithTint(getBtnForward(), R.drawable.ic_chevron_left_black_24dp, Color.GRAY);
    }

    public void startPending() {
        setShowProgress(true);
        banPageSwitch();
    }

    public void endPending() {
        setShowProgress(false);
        restoreState();
    }

    public String getSetupPrefPrefix() {
        return setup_pref_prefix;
    }

    public SharedPreferenceUtil getSpf() {
        return spf;
    }

    public String createPrefKey(String action) {
        return setup_pref_prefix + action;
    }

    private void initPrefs() {
        versionCode = (int) spf.get(this, createPrefKey(PREF_ACTION_PREVIOUS_VERSIONCODE), getVersionCode());
        hasRunFullSetup = (boolean) spf.get(this, createPrefKey(PREF_ACTION_HAS_RUN_FULL_SETUP), false);
        startUpType = getIntent().getStringExtra(KEY_SETUP_START_UP_TYPE);
        if(TextUtils.isEmpty(startUpType)){
            startUpType = STARTUP_DEFAULT;
        }
    }

    private void findViews() {
        setup_title = findViewById(R.id.setup_title);
        setup_pager = findViewById(R.id.setup_pager);
        setup_progress = findViewById(R.id.setup_progress);
        setup_forward = findViewById(R.id.setup_btn_forward);
        setup_next = findViewById(R.id.setup_btn_next);
        setup_nav = findViewById(R.id.setup_nav_bar);
        setup_progress_bar_limit = findViewById(R.id.setup_progress_bar_limit);
    }

    private void initViews() {
        findViews();
        setup_nav.setPadding(setup_nav.getPaddingLeft(),
                setup_nav.getPaddingTop(),
                setup_nav.getPaddingRight(),
                setup_nav.getPaddingBottom() + Utils.getNavigationBarHeight(this));
        setup_pager.setPadding(setup_pager.getPaddingLeft(),
                setup_pager.getPaddingTop(),
                setup_pager.getPaddingRight(),
                setup_pager.getPaddingBottom() + Utils.getNavigationBarHeight(this));
        setup_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                setTitle(pages.get(i).getTitle());
                if (i == 0) {
                    setup_forward.setVisibility(View.GONE);
                } else if (i == pages.size() - 1) {
                    setup_next.setText(R.string.setup_step_next_final);
                } else {
                    setup_next.setVisibility(View.VISIBLE);
                    setup_forward.setVisibility(View.VISIBLE);
                    setup_next.setText(R.string.setup_step_next);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        setup_forward.setOnClickListener(view -> {
            if (setup_pager.getCurrentItem() != 0)
                setup_pager.setCurrentItem(setup_pager.getCurrentItem() - 1);
        });
        setup_next.setOnClickListener(view -> {
            if (setup_pager.getCurrentItem() != pages.size() - 1)
                setup_pager.setCurrentItem(setup_pager.getCurrentItem() + 1);
            else {
                if (!hasRunFullSetup)
                    spf.put(SetupWizardBaseActivity.this, createPrefKey(PREF_ACTION_HAS_RUN_FULL_SETUP), true);
                spf.put(SetupWizardBaseActivity.this, createPrefKey(PREF_ACTION_PREVIOUS_VERSIONCODE), getVersionCode());
                onFinishSetup();
            }
        });
    }

    private void initPages() {
        pages.clear();
        if (!hasRunFullSetup || startUpType.equals(STARTUP_FORCE_SHOW_FULL_SETUP)) {
            onSetupPageInit(pages);
            onUpdate(pages);
        } else if (getVersionCode() > versionCode || startUpType.equals(STARTUP_FORCE_SHOW_UPDATE))
            onUpdate(pages);
        if (pages.size() == 0) {
            onFinishSetup();
            return;
        }
        adapter = new SetupPagerAdapter(pages, this);
        setup_pager.setAdapter(adapter);
        setup_forward.setVisibility(View.GONE);
        setTitle(pages.get(0).getTitle());
    }
}
