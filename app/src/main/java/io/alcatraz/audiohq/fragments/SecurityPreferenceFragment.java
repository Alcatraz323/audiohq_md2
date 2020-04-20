package io.alcatraz.audiohq.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import io.alcatraz.audiohq.Constants;
import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.activities.DefaultProfileActivity;
import io.alcatraz.audiohq.beans.AudioHQNativeInterface;
import io.alcatraz.audiohq.beans.SwitchConfigurations;
import io.alcatraz.audiohq.core.utils.AudioHQApis;
import io.alcatraz.audiohq.extended.CompatWithPipeActivity;

public class SecurityPreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
    private CheckBoxPreference default_silent;
    private PreferenceScreen default_profile;

    private boolean default_silent_val;

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        return false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_security);
        findPreferences();
        bindLinsteners();
        updateSummary();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    private void findPreferences() {
        default_silent = (CheckBoxPreference) findPreference(Constants.PREF_DEFAULT_SILENT);
        default_profile = (PreferenceScreen) findPreference(Constants.PREF_DEFAULT_PROFILE);
    }

    private void bindLinsteners() {
        default_silent.setOnPreferenceChangeListener((preference, o) -> {
            if (!default_silent_val) {
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.pref_default_silent_warning_title)
                        .setMessage(R.string.pref_default_silent_warning_message)
                        .setNegativeButton(R.string.ad_nb, null)
                        .setPositiveButton(R.string.adjust_confirm, (dialogInterface, i) -> {
                            AudioHQApis.setDefaultSilentState(getContext(), !default_silent_val);
                            default_silent.setChecked(!default_silent_val);
                            default_silent_val = !default_silent_val;
                            Toast.makeText(getContext(), R.string.pref_need_to_restart_playing_process, Toast.LENGTH_LONG).show();
                        }).show();
                return false;
            }
            AudioHQApis.setDefaultSilentState(getContext(), false);
            default_silent.setChecked(!default_silent_val);
            default_silent_val = !default_silent_val;
            Toast.makeText(getContext(), R.string.pref_need_to_restart_playing_process, Toast.LENGTH_LONG).show();
            return false;
        });

        default_profile.setOnPreferenceClickListener(preference -> {
            CompatWithPipeActivity activity = (CompatWithPipeActivity) getActivity();
            assert activity != null;
            activity.startTransition(new Intent(activity, DefaultProfileActivity.class));
            return true;
        });
    }

    private void updateSummary() {
        AudioHQApis.getSwitches(getContext(), new AudioHQNativeInterface<SwitchConfigurations>() {
            @Override
            public void onSuccess(SwitchConfigurations result) {
                default_silent_val = result.isDefaultsilent();
                default_silent.setChecked(default_silent_val);
            }

            @Override
            public void onFailure(String reason) {

            }
        });
    }
}