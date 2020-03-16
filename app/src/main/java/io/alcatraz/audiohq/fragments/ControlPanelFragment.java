package io.alcatraz.audiohq.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import io.alcatraz.audiohq.AudioHQApplication;
import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.beans.AudioHQNativeInterface;
import io.alcatraz.audiohq.beans.StringProfile;
import io.alcatraz.audiohq.beans.preset.ControlElement;
import io.alcatraz.audiohq.beans.preset.PresetSystem;
import io.alcatraz.audiohq.core.utils.AudioHQApis;

public class ControlPanelFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {
    public static final String CONTROL_PANEL_PARAM_MAIN_KEY = "frag_c_p_key_meta";
    public static final String CONTROL_PANEL_MODE = "frag_c_p_mode";

    SeekBar general_seek;
    TextView general_indicator;
    SeekBar left_seek;
    TextView left_indicator;
    SeekBar right_seek;
    TextView right_indicator;
    LinearLayout mute;
    Switch mute_switch;
    ProgressBar ready_indicator;

    ControlElement dataParam;
    StringProfile profile;
    boolean muted;
    boolean isForDefProfile = false;

    boolean isInitiateSuccessful = true;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getArguments() != null) {
            dataParam = getArguments().getParcelable(CONTROL_PANEL_PARAM_MAIN_KEY);
            isForDefProfile = getArguments().getBoolean(CONTROL_PANEL_MODE);
        } else {
            isInitiateSuccessful = false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.panel_control, container, false);
        findViews(root);
        initViews();
        disablePanel();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isInitiateSuccessful)
            updateProfile();
    }

    public void disablePanel() {
        general_seek.setEnabled(false);
        left_seek.setEnabled(false);
        right_seek.setEnabled(false);
        mute.setEnabled(false);
        mute.setClickable(false);
        mute_switch.setEnabled(false);
        ready_indicator.setVisibility(View.VISIBLE);
    }

    public void enablePanel() {
        general_seek.setEnabled(true);
        left_seek.setEnabled(true);
        right_seek.setEnabled(true);
        mute.setEnabled(true);
        mute.setClickable(true);
        mute_switch.setEnabled(true);
        ready_indicator.setVisibility(View.GONE);
    }

    public void disableMute(){
        mute.setClickable(false);
        mute_switch.setEnabled(false);
    }

    private void findViews(View root) {
        general_seek = root.findViewById(R.id.panel_control_general_general);
        left_seek = root.findViewById(R.id.panel_control_left_seek);
        right_seek = root.findViewById(R.id.panel_control_right_seek);
        general_indicator = root.findViewById(R.id.panel_control_general_indicator);
        left_indicator = root.findViewById(R.id.panel_control_left_indicator);
        right_indicator = root.findViewById(R.id.panel_control_right_indicator);
        mute = root.findViewById(R.id.panel_control_mute);
        mute_switch = root.findViewById(R.id.panel_control_mute_switch);
        ready_indicator = root.findViewById(R.id.panel_control_ready_indicator);
    }

    @SuppressLint("SetTextI18n")
    private void updateIndicators() {
        int g_progress = general_seek.getProgress() / 100;
        int l_progress = left_seek.getProgress() / 100;
        int r_progress = right_seek.getProgress() / 100;
        general_indicator.setText(g_progress + "%");
        left_indicator.setText(l_progress + "%");
        right_indicator.setText(r_progress + "%");
    }

    private void initViews() {
        general_seek.setOnSeekBarChangeListener(this);
        left_seek.setOnSeekBarChangeListener(this);
        right_seek.setOnSeekBarChangeListener(this);
        if(isForDefProfile)
            disableMute();
        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mute_switch.isChecked()) {
                    AudioHQApis.unmuteProcess(getContext(), dataParam.getProcess(), dataParam.isIsweakkey());
                }else {
                    AudioHQApis.muteProcess(getContext(), dataParam.getProcess(), dataParam.isIsweakkey());
                }
                mute_switch.setChecked(!mute_switch.isChecked());
            }
        });
    }

    private void updateProfile(){
        if(!isForDefProfile) {
            AudioHQApis.getProfile(getContext(), dataParam.getProcess(), dataParam.isIsweakkey(),
                    new AudioHQNativeInterface<StringProfile>() {
                        @Override
                        public void onSuccess(StringProfile result) {
                            profile = result;
                            AudioHQApplication application = (AudioHQApplication) getActivity().getApplication();
                            PresetSystem presetSystem = application.getPresetSystem();
                            presetSystem.update(new AudioHQNativeInterface<PresetSystem>() {
                                @Override
                                public void onSuccess(PresetSystem result) {
                                    muted = result.getMuted(dataParam.getProcess(), dataParam.isIsweakkey());
                                    updateSeekBarWidget();
                                    enablePanel();
                                }

                                @Override
                                public void onFailure(String reason) {

                                }
                            });

                        }

                        @Override
                        public void onFailure(String reason) {

                        }
                    });
        }else {
            AudioHQApis.getDefaultProfile(getContext(), new AudioHQNativeInterface<StringProfile>() {
                @Override
                public void onSuccess(StringProfile result) {
                    profile = result;
                    updateSeekBarWidget();
                    enablePanel();
                    disableMute();
                }

                @Override
                public void onFailure(String reason) {

                }
            });
        }
    }

    private void updateSeekBarWidget(){
        general_seek.setProgress((int) (profile.getGeneral()*10000));
        left_seek.setProgress((int) (profile.getLeft()*10000));
        right_seek.setProgress((int) (profile.getRight()*10000));
        updateIndicators();
        mute_switch.setChecked(muted);
    }


    public static ControlPanelFragment newInstance(ControlElement controlElement, boolean isForDefProfile) {
        ControlPanelFragment fragment = new ControlPanelFragment();
        Bundle args = new Bundle();
        args.putParcelable(CONTROL_PANEL_PARAM_MAIN_KEY, controlElement);
        args.putBoolean(CONTROL_PANEL_MODE,isForDefProfile);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (b) {
            updateIndicators();
            if(isForDefProfile){
                AudioHQApis.setDefaultProfile(getContext(),general_seek.getProgress() * 0.0001f,
                        left_seek.getProgress() * 0.0001f, right_seek.getProgress() * 0.0001f, true);
            }else {
                AudioHQApis.setProfile(getContext(), dataParam.getProcess(), general_seek.getProgress() * 0.0001f,
                        left_seek.getProgress() * 0.0001f, right_seek.getProgress() * 0.0001f, true, dataParam.isIsweakkey());
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
