package io.alcatraz.audiohq.fragments;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.List;

import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import io.alcatraz.audiohq.Constants;
import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.activities.Quiz2020Activity;
import io.alcatraz.audiohq.activities.SetupActivity;
import io.alcatraz.audiohq.core.utils.AudioHQApis;
import io.alcatraz.audiohq.utils.SharedPreferenceUtil;
import io.alcatraz.audiohq.utils.UpdateUtils;

public class OtherPreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
    private ListPreference language;
    private ListPreference theme;
    private ListPreference dark_mode;
    private CheckBoxPreference anniversary_foreground;
    private CheckBoxPreference exclude_from_recent;
    private PreferenceScreen check_update;
    private PreferenceScreen clear_profile;
    private PreferenceScreen uninstall_profile;
    private PreferenceScreen rerun_setup;
    private PreferenceScreen anniversary_2020_quiz;

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        return false;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference_others, rootKey);
        findPreferences();
        bindListeners();
        updateSummary();
    }

    private void findPreferences() {
        language = findPreference(Constants.PREF_LANGUAGE);
        theme = findPreference(Constants.PREF_THEME);
        dark_mode = findPreference(Constants.PREF_DARK_MODE);
        exclude_from_recent = findPreference(Constants.PREF_EXCLUDE_FROM_RECENT);
        check_update = findPreference(Constants.PREF_CHECK_UPDATE);
        clear_profile = findPreference(Constants.PREF_CLEAR_PROFILES);
        uninstall_profile = findPreference(Constants.PREF_UNINSTALL_NATIVE);
        anniversary_2020_quiz = findPreference(Constants.PREF_SHOW_ANNIVERSARY_2020_QUIZ_CONTENT);
        rerun_setup = findPreference(Constants.PREF_RERUN_SETUP);
        anniversary_foreground = findPreference(Constants.PREF_SHOW_ANNIVERSARY_2020_FOREGROUND);
    }

    private void bindListeners() {
        exclude_from_recent.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferenceUtil spfu = SharedPreferenceUtil.getInstance();
                spfu.put(getContext(), Constants.PREF_EXCLUDE_FROM_RECENT, (boolean) newValue);
                getContext().sendBroadcast(new Intent().setAction(Constants.BROADCAST_ACTION_UPDATE_PREFERENCES));

                ActivityManager activityManager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
                if (activityManager != null) {
                    List<ActivityManager.AppTask> tasks = activityManager.getAppTasks();
                    if (tasks != null && tasks.size() > 0) {
                        tasks.get(0).setExcludeFromRecents((boolean) newValue);
                    }
                }
                return true;
            }
        });

        check_update.setOnPreferenceClickListener(preference -> {
            Toast.makeText(getContext(), R.string.toast_implementing, Toast.LENGTH_SHORT).show();
            UpdateUtils.checkUpdate();
            return true;
        });

        clear_profile.setOnPreferenceClickListener(preference -> {
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.pref_default_silent_warning_title)
                    .setMessage(R.string.pref_clear_profile_confirm)
                    .setNegativeButton(R.string.ad_nb, null)
                    .setPositiveButton(R.string.adjust_confirm, (dialogInterface, i) -> AudioHQApis.clearAllSetting(getContext())).show();
            return true;
        });

        uninstall_profile.setOnPreferenceClickListener(preference -> {
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.pref_3_3)
                    .setMessage(R.string.uninstall_steps)
                    .setNegativeButton(R.string.ad_nb, null).show();
            return true;
        });

        language.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferenceUtil spfu = SharedPreferenceUtil.getInstance();
                spfu.put(getContext(), Constants.PREF_LANGUAGE, (String) newValue);
                restartToApply();
                return true;
            }
        });

        theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferenceUtil spfu = SharedPreferenceUtil.getInstance();
                spfu.put(getContext(), Constants.PREF_THEME, (String) newValue);
                restartToApply();
                return true;
            }
        });

        dark_mode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferenceUtil spfu = SharedPreferenceUtil.getInstance();
                spfu.put(getContext(), Constants.PREF_DARK_MODE, (String) newValue);
                restartToApply();
                return true;
            }
        });

        rerun_setup.setOnPreferenceClickListener(preference -> {
            Intent setup = new Intent(getActivity(), SetupActivity.class);
            setup.putExtra(SetupActivity.KEY_SETUP_START_UP_TYPE, SetupActivity.STARTUP_FORCE_SHOW_FULL_SETUP);
            startActivity(setup);
            return true;
        });

        anniversary_2020_quiz.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(getContext(), Quiz2020Activity.class));
            return true;
        });

        anniversary_foreground.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferenceUtil spfu = SharedPreferenceUtil.getInstance();
                spfu.put(getContext(), Constants.PREF_SHOW_ANNIVERSARY_2020_FOREGROUND, (boolean) newValue);
                restartToApply();
                return true;
            }
        });
    }

    private void updateSummary() {
        SharedPreferenceUtil spfu = SharedPreferenceUtil.getInstance();
        String currentLanguage = (String) spfu.get(getContext(), Constants.PREF_LANGUAGE, Constants.DEFAULT_VALUE_PREF_LANGUAGE);
        String currentTheme = (String) spfu.get(getContext(), Constants.PREF_THEME, Constants.DEFAULT_VALUE_PREF_THEME);
        String currentDark = (String) spfu.get(getContext(), Constants.PREF_DARK_MODE, Constants.DEFAULT_VALUE_PREF_DARK_MODE);
        boolean show2020Quiz = (boolean) spfu.get(getContext(), Constants.PREF_SHOW_ANNIVERSARY_2020_QUIZ_CONTENT, Constants.DEFAULT_VALUE_PREF_SHOW_ANNIVERSARY_2020_QUIZ_CONTENT);
        boolean showThemeSettings = (boolean) spfu.get(getContext(), Constants.PREF_SHOW_THEME_SETTINGS, Constants.DEFAULT_VALUE_PREF_SHOW_THEME_SETTINGS);
        language.setSummary(currentLanguage);
        theme.setSummary(currentTheme);
        dark_mode.setSummary(currentDark);
        theme.setVisible(showThemeSettings);
        dark_mode.setVisible(showThemeSettings);
        anniversary_foreground.setVisible(showThemeSettings);
        anniversary_2020_quiz.setVisible(show2020Quiz);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSummary();
    }

    private void restartToApply() {
        getActivity().finish();
        Intent it = new Intent(getContext(), SetupActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(it);
    }
}