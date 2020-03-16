package io.alcatraz.audiohq.beans.preset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.alcatraz.audiohq.utils.Utils;

public class ListMuted {
    private String p_muted;
    private String w_muted;

    public void setP_muted(String p_muted) {
        this.p_muted = p_muted;
    }

    public String getP_muted() {
        return p_muted;
    }

    public void setW_muted(String w_muted) {
        this.w_muted = w_muted;
    }

    public String getW_muted() {
        return w_muted;
    }

    public List<String> getPMuted() {
        String[] p_mutes = p_muted.split(";");
        if(Utils.isStringNotEmpty(p_muted)) {
            return Arrays.asList(p_mutes);
        }else
            return new ArrayList<>();
    }

    public List<String> getWMuted() {
        if(Utils.isStringNotEmpty(w_muted)) {
            String[] w_mutes = w_muted.split(";");
            return Arrays.asList(w_mutes);
        }else
            return new ArrayList<>();
    }

}
