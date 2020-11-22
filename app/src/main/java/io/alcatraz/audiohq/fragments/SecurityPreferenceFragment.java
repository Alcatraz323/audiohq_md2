package io.alcatraz.audiohq.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

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
import io.alcatraz.audiohq.core.utils.AudioHQRaw;
import io.alcatraz.audiohq.extended.CompatWithPipeActivity;

public class SecurityPreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
    private CheckBoxPreference default_silent;
    private CheckBoxPreference root_shell;
    private PreferenceScreen default_profile;

    private boolean default_silent_val;

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        return false;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference_security, rootKey);
        findPreferences();
        bindListeners();
        updateSummary();
    }

    private void findPreferences() {
        default_silent = findPreference(Constants.PREF_DEFAULT_SILENT);
        default_profile = findPreference(Constants.PREF_DEFAULT_PROFILE);
        root_shell = findPreference(Constants.PREF_ROOT_SHELL);
    }

    private void bindListeners() {
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

        root_shell.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                AudioHQRaw.AudioHqCmds.FORCE_ROOT_SHELL = (boolean) newValue;
                return true;
            }
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