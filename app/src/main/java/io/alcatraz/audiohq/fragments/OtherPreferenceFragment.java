package io.alcatraz.audiohq.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import io.alcatraz.audiohq.Constants;
import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.core.utils.AudioHQApis;
import io.alcatraz.audiohq.utils.UpdateUtils;

public class OtherPreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
    private PreferenceScreen check_update;
    private PreferenceScreen clear_profile;
    private PreferenceScreen uninstall_profile;

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        return false;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference_others,rootKey);
        findPreferences();
        bindLinsteners();
    }

    public void findPreferences() {
        check_update = findPreference(Constants.PREF_CHECK_UPDATE);
        clear_profile = findPreference(Constants.PREF_CLEAR_PROFILES);
        uninstall_profile = findPreference(Constants.PREF_UNINSTALL_NATIVE);
    }

    public void bindLinsteners() {
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