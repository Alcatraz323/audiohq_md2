package io.alcatraz.audiohq.fragments;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.List;

import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import io.alcatraz.audiohq.Constants;
import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.core.utils.AudioHQApis;
import io.alcatraz.audiohq.utils.SharedPreferenceUtil;
import io.alcatraz.audiohq.utils.UpdateUtils;

public class OtherPreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
    private CheckBoxPreference exclude_from_recent;
    private PreferenceScreen check_update;
    private PreferenceScreen clear_profile;
    private PreferenceScreen uninstall_profile;

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        return false;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference_others, rootKey);
        findPreferences();
        bindListeners();
    }

    private void findPreferences() {
        exclude_from_recent = findPreference(Constants.PREF_EXCLUDE_FROM_RECENT);
        check_update = findPreference(Constants.PREF_CHECK_UPDATE);
        clear_profile = findPreference(Constants.PREF_CLEAR_PROFILES);
        uninstall_profile = findPreference(Constants.PREF_UNINSTALL_NATIVE);
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
    }
}