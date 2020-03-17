package io.alcatraz.audiohq.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import io.alcatraz.audiohq.Constants;
import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.services.FloatPanelService;
import io.alcatraz.audiohq.utils.SharedPreferenceUtil;

public class FloatPreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
    private CheckBoxPreference float_service;
    private ListPreference float_gravity;
    private EditTextPreference float_background;
    private CheckBoxPreference float_foreground_service;
    private EditTextPreference float_dismiss_delay;
    private EditTextPreference float_margin_top;
    private EditTextPreference float_margin_top_landscape;
    private EditTextPreference float_icon_tint;
    private EditTextPreference float_toggle_size;
    private EditTextPreference float_font_color;
    private EditTextPreference float_side_margin;
    private EditTextPreference float_toggle_corner_radius;
    private EditTextPreference float_card_corner_radius;
    private EditTextPreference float_seek_color;
    private CheckBoxPreference float_direct_react;
    private EditTextPreference float_side_margin_landscape;

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        switch (preference.getKey()) {
            case Constants.PREF_FLOAT_WINDOW_BACKGROUND:
            case Constants.PREF_FLOAT_WINDOW_ICON_TINT:
            case Constants.PREF_FLOAT_WINDOW_FONT_COLOR:
            case Constants.PREF_FLOAT_WINDOW_SEEK_COLOR:
                String color_str = (String) o;
                try {
                    Color.parseColor(color_str);
                    SharedPreferenceUtil spfu = SharedPreferenceUtil.getInstance();
                    spfu.put(getContext(), preference.getKey(), (String) o);
                    getContext().sendBroadcast(new Intent().setAction(Constants.BROADCAST_ACTION_UPDATE_PREFERENCES));
                    updateEditTextSummay();
                } catch (Exception e) {
                    Toast.makeText(getContext(), R.string.pref_invalid_color, Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            case Constants.PREF_FLOAT_WINDOW_DISMISS_DELAY:
            case Constants.PREF_FLOAT_WINDOW_MARGIN_TOP:
            case Constants.PREF_FLOAT_WINDOW_MARGIN_TOP_LANDSCAPE:
            case Constants.PREF_FLOAT_WINDOW_TOGGLE_SIZE:
            case Constants.PREF_FLOAT_WINDOW_SIDE_MARGIN:
            case Constants.PREF_FLOAT_WINDOW_TOGGLE_CORNER_RADIUS:
            case Constants.PREF_FLOAT_WINDOW_CARD_RADIUS:
            case Constants.PREF_FLOAT_WINDOW_SIDE_MARGIN_LANDSCAPE:
                String delay = (String) o;
                try {
                    Integer.parseInt(delay);
                    SharedPreferenceUtil spfu = SharedPreferenceUtil.getInstance();
                    spfu.put(getContext(), preference.getKey(), (String) o);
                    getContext().sendBroadcast(new Intent().setAction(Constants.BROADCAST_ACTION_UPDATE_PREFERENCES));
                    updateEditTextSummay();
                } catch (Exception e) {
                    Toast.makeText(getContext(), R.string.pref_invalid_integer, Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            case Constants.PREF_FLOAT_FOREGROUND_SERVICE:
            case Constants.PREF_FLOAT_DIRECT_REACT:
                SharedPreferenceUtil spfu = SharedPreferenceUtil.getInstance();
                spfu.put(getContext(), preference.getKey(), (boolean) o);
                getContext().sendBroadcast(new Intent().setAction(Constants.BROADCAST_ACTION_UPDATE_PREFERENCES));
                return true;
        }
        return false;
    }

    private void bindLinsteners() {
        float_service.setOnPreferenceChangeListener((preference, o) -> {
            if (!Settings.canDrawOverlays(getContext())) {
                Toast.makeText(getContext(), R.string.toast_cant_overlay, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getContext().getPackageName())));
            } else {
                if ((boolean) o) {
                    getContext().startService(new Intent(getContext(), FloatPanelService.class));
                } else {
                    getContext().stopService(new Intent(getContext(), FloatPanelService.class));
                }
            }
            return true;
        });

        float_gravity.setOnPreferenceChangeListener((preference, o) -> {
            SharedPreferenceUtil spfu = SharedPreferenceUtil.getInstance();
            spfu.put(getContext(), Constants.PREF_FLOAT_WINDOW_GRAVITY, (String) o);
            getContext().sendBroadcast(new Intent().setAction(Constants.BROADCAST_ACTION_UPDATE_PREFERENCES));
            updateGravitySummary();
            return true;
        });

        float_background.setOnPreferenceChangeListener(this);
        float_dismiss_delay.setOnPreferenceChangeListener(this);
        float_margin_top.setOnPreferenceChangeListener(this);
        float_margin_top_landscape.setOnPreferenceChangeListener(this);
        float_icon_tint.setOnPreferenceChangeListener(this);
        float_toggle_size.setOnPreferenceChangeListener(this);
        float_font_color.setOnPreferenceChangeListener(this);
        float_side_margin.setOnPreferenceChangeListener(this);
        float_toggle_corner_radius.setOnPreferenceChangeListener(this);
        float_seek_color.setOnPreferenceChangeListener(this);
        float_foreground_service.setOnPreferenceChangeListener(this);
        float_card_corner_radius.setOnPreferenceChangeListener(this);
        float_direct_react.setOnPreferenceChangeListener(this);
        float_side_margin_landscape.setOnPreferenceChangeListener(this);
    }

    private void findPreferences() {
        float_service = findPreference(Constants.PREF_FLOAT_SERVICE);
        float_gravity = findPreference(Constants.PREF_FLOAT_WINDOW_GRAVITY);
        float_background = findPreference(Constants.PREF_FLOAT_WINDOW_BACKGROUND);
        float_foreground_service = findPreference(Constants.PREF_FLOAT_FOREGROUND_SERVICE);
        float_dismiss_delay = findPreference(Constants.PREF_FLOAT_WINDOW_DISMISS_DELAY);
        float_margin_top = findPreference(Constants.PREF_FLOAT_WINDOW_MARGIN_TOP);
        float_margin_top_landscape = findPreference(Constants.PREF_FLOAT_WINDOW_MARGIN_TOP_LANDSCAPE);
        float_icon_tint = findPreference(Constants.PREF_FLOAT_WINDOW_ICON_TINT);
        float_toggle_size = findPreference(Constants.PREF_FLOAT_WINDOW_TOGGLE_SIZE);
        float_font_color = findPreference(Constants.PREF_FLOAT_WINDOW_FONT_COLOR);
        float_side_margin = findPreference(Constants.PREF_FLOAT_WINDOW_SIDE_MARGIN);
        float_toggle_corner_radius = findPreference(Constants.PREF_FLOAT_WINDOW_TOGGLE_CORNER_RADIUS);
        float_card_corner_radius = findPreference(Constants.PREF_FLOAT_WINDOW_CARD_RADIUS);
        float_seek_color = findPreference(Constants.PREF_FLOAT_WINDOW_SEEK_COLOR);
        float_direct_react = findPreference(Constants.PREF_FLOAT_DIRECT_REACT);
        float_side_margin_landscape = findPreference(Constants.PREF_FLOAT_WINDOW_SIDE_MARGIN_LANDSCAPE);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference_float_window,rootKey);
        findPreferences();
        bindLinsteners();
        updateEditTextSummay();
        updateGravitySummary();
    }

    private void updateGravitySummary() {
        SharedPreferenceUtil spfu = SharedPreferenceUtil.getInstance();
        String gravity = (String) spfu.get(getContext(), Constants.PREF_FLOAT_WINDOW_GRAVITY,
                Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_GRAVITY);
        String[] entryvalue = getContext().getResources().getStringArray(R.array.entryvalue_for_float_gravity);
        String[] entry = getContext().getResources().getStringArray(R.array.entries_for_float_gravity);
        for (int i = 0; i < entryvalue.length; i++) {
            if (entryvalue[i].equals(gravity)) {
                float_gravity.setSummary(entry[i]);
            }
        }
    }

    private void updateEditTextSummay() {
        SharedPreferenceUtil spfu = SharedPreferenceUtil.getInstance();
        String f_b_color = (String) spfu.get(getContext(), Constants.PREF_FLOAT_WINDOW_BACKGROUND,
                Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_BACKGROUND);
        String delay = (String) spfu.get(getContext(), Constants.PREF_FLOAT_WINDOW_DISMISS_DELAY,
                Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_DISMISS_DELAY);
        String m_top = (String) spfu.get(getContext(), Constants.PREF_FLOAT_WINDOW_MARGIN_TOP,
                Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_MARGIN_TOP);
        String m_top_l = (String) spfu.get(getContext(), Constants.PREF_FLOAT_WINDOW_MARGIN_TOP_LANDSCAPE,
                Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_MARGIN_TOP_LANDSCAPE);
        String ic_tint = (String) spfu.get(getContext(), Constants.PREF_FLOAT_WINDOW_ICON_TINT,
                Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_ICON_TINT);
        String tg_size = (String) spfu.get(getContext(), Constants.PREF_FLOAT_WINDOW_TOGGLE_SIZE,
                Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_TOGGLE_SIZE);
        String f_color = (String) spfu.get(getContext(), Constants.PREF_FLOAT_WINDOW_FONT_COLOR,
                Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_FONT_COLOR);
        String s_margin = (String) spfu.get(getContext(), Constants.PREF_FLOAT_WINDOW_SIDE_MARGIN,
                Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_SIDE_MARGIN);
        String c_tg_radius = (String) spfu.get(getContext(), Constants.PREF_FLOAT_WINDOW_TOGGLE_CORNER_RADIUS,
                Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_TOGGLE_CORNER_RADIUS);
        String c_radius = (String) spfu.get(getContext(), Constants.PREF_FLOAT_WINDOW_CARD_RADIUS,
                Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_CARD_RADIUS);
        String sk_color = (String) spfu.get(getContext(), Constants.PREF_FLOAT_WINDOW_SEEK_COLOR,
                Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_SEEK_COLOR);
        String s_margin_l = (String) spfu.get(getContext(), Constants.PREF_FLOAT_WINDOW_SIDE_MARGIN_LANDSCAPE,
                Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_SIDE_MARGIN_LANDSCAPE);

        float_background.setSummary(f_b_color);
        float_dismiss_delay.setSummary(delay);
        float_margin_top.setSummary(m_top);
        float_margin_top_landscape.setSummary(m_top_l);
        float_icon_tint.setSummary(ic_tint);
        float_toggle_size.setSummary(tg_size);
        float_font_color.setSummary(f_color);
        float_side_margin.setSummary(s_margin);
        float_toggle_corner_radius.setSummary(c_tg_radius);
        float_card_corner_radius.setSummary(c_radius);
        float_seek_color.setSummary(sk_color);
        float_side_margin_landscape.setSummary(s_margin_l);
    }
}
