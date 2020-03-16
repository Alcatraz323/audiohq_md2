package io.alcatraz.audiohq.beans.preset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.alcatraz.audiohq.utils.Utils;

public class ListProfiles {
    private String p_profiles;
    private String w_profiles;
    public void setP_profiles(String p_profiles) {
        this.p_profiles = p_profiles;
    }
    public String getP_profiles() {
        return p_profiles;
    }

    public void setW_profiles(String w_profiles) {
        this.w_profiles = w_profiles;
    }
    public String getW_profiles() {
        return w_profiles;
    }

    public List<String> getPProfile() {
        if(Utils.isStringNotEmpty(p_profiles)) {
            String[] p_pro = p_profiles.split(";");
            return Arrays.asList(p_pro);
        }else
            return new ArrayList<>();
    }

    public List<String> getWProfile() {
        if(Utils.isStringNotEmpty(w_profiles)) {
            String[] w_mutes = w_profiles.split(";");
            return Arrays.asList(w_mutes);
        }else
            return new ArrayList<>();
    }
}
