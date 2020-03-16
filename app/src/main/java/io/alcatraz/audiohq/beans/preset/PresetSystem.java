package io.alcatraz.audiohq.beans.preset;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.beans.AudioHQNativeInterface;
import io.alcatraz.audiohq.core.utils.AudioHQApis;
import io.alcatraz.audiohq.utils.PackageCtlUtils;
import io.alcatraz.audiohq.utils.Utils;

public class PresetSystem {
    private List<String> p_profiles;
    private List<String> w_profiles;
    private List<String> p_muted;
    private List<String> w_muted;
    private ListProfiles listProfiles;
    private ListMuted listMuted;

    private Context context;

    public PresetSystem(Context context) {
        this.context = context;
    }

    public synchronized void update(AudioHQNativeInterface<PresetSystem> nativeInterface) {
        AudioHQApis.listMuted(context, new AudioHQNativeInterface<ListMuted>() {
            @Override
            public void onSuccess(ListMuted result) {
                listMuted = result;
                p_muted = listMuted.getPMuted();
                w_muted = listMuted.getWMuted();
                AudioHQApis.listProfiles(context, new AudioHQNativeInterface<ListProfiles>() {
                    @Override
                    public void onSuccess(ListProfiles result) {
                        listProfiles = result;
                        p_profiles = listProfiles.getPProfile();
                        w_profiles = listProfiles.getWProfile();
                        nativeInterface.onSuccess(PresetSystem.this);
                    }

                    @Override
                    public void onFailure(String reason) {
                        nativeInterface.onFailure(reason);
                    }
                });
            }

            @Override
            public void onFailure(String reason) {
                nativeInterface.onFailure(reason);
            }
        });
    }

    public PackageProfileQuery getPresetStatus(String process) {
        String profile_status = context.getString(R.string.preset_status_no_profile);
        PackageProfileQuery query = new PackageProfileQuery();
        if (w_profiles.contains(process)) {
            query.setColor(context.getColor(R.color.colorAccent));
            profile_status = context.getString(R.string.preset_status_has_profile);
        }
        String muted_status = context.getString(R.string.preset_status_unmuted);
        if (w_muted.contains(process)) {
            query.setColor(context.getColor(R.color.colorAccent));
            muted_status = context.getString(R.string.preset_status_muted);
        }
        query.setText(profile_status + muted_status);
        return query;
    }

    public PackageProfileQuery getPresetStatusProcess(String process) {
        String profile_status = context.getString(R.string.preset_status_no_profile);
        PackageProfileQuery query = new PackageProfileQuery();
        if (p_profiles.contains(process)) {
            query.setColor(context.getColor(R.color.colorAccent));
            profile_status = context.getString(R.string.preset_status_has_profile);
        }
        String muted_status = context.getString(R.string.preset_status_unmuted);
        if (p_muted.contains(process)) {
            query.setColor(context.getColor(R.color.colorAccent));
            muted_status = context.getString(R.string.preset_status_muted);
        }
        query.setText(profile_status + muted_status);
        return query;
    }

    public boolean getMuted(String process, boolean isweakkey){
        if(isweakkey){
            return w_muted.contains(process);
        }else{
            return p_muted.contains(process);
        }
    }

    public List<ControlElement> getSetData() {
        List<ControlElement> output = new ArrayList<>();
        List<String> all_weaks = new ArrayList<>();
        for (String i : w_profiles) {
            PackageProfileQuery query = getPresetStatus(i);
            ControlElement element = new ControlElement(PackageCtlUtils.getLabel(context, i),
                    query.getText(), i, true, PackageCtlUtils.getIcon(context, i), query.getColor());
            output.add(element);
            all_weaks.add(i);
        }
        for (String i : w_muted) {
            if (w_profiles.contains(i))
                continue;
            PackageProfileQuery query = getPresetStatus(i);
            ControlElement element = new ControlElement(PackageCtlUtils.getLabel(context, i),
                    query.getText(), i, true, PackageCtlUtils.getIcon(context, i), query.getColor());
            output.add(element);
            all_weaks.add(i);
        }

        for (String i : p_profiles) {
            PackageProfileQuery query = getPresetStatusProcess(i);
            String pkg = Utils.extractPackageName(i);
            ControlElement element = new ControlElement(i,
                    query.getText(), pkg, false, PackageCtlUtils.getIcon(context, pkg), query.getColor());
            output.add(element);
//            if (all_weaks.contains(pkg)) {
//                element.setConflicted(true);
//            }
        }

        for (String i : p_muted) {
            if (p_profiles.contains(i))
                continue;
            PackageProfileQuery query = getPresetStatusProcess(i);
            String pkg = Utils.extractPackageName(i);
            ControlElement element = new ControlElement(i,
                    query.getText(), pkg, false, PackageCtlUtils.getIcon(context, pkg), query.getColor());
            output.add(element);
//            if (all_weaks.contains(pkg)) {
//                element.setConflicted(true);
//            }
        }

        return output;
    }
}
